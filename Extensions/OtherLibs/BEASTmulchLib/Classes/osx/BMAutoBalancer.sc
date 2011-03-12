// operates on the speaker list directly
BMAutoBalancer {

	classvar <>running = false, rout, trimList;

	*run {|speakerList, okayFunc, server, in = 0, onlyFullRange = true|
		var target, responders, min, diff;
		running.if({
			"Auto Level Balance already running; ignoring request".warn;
			^this
		});
		trimList = ();
		target = server.asTarget;
		server = target.server;
		responders = ();
		rout = {
			running = true;
			this.sendDef(server);
			server.sync;
			"\n\\\\\\\\\\\\\\\ Auto Level Balance Starting\n".postln;
			speakerList.associationsDo({|speaker, index|
				var synth, array, responder, count = 0, trim;
				if(speaker.isBMSpeaker and: {speaker.spec.fullRange || onlyFullRange.not}, {
					array = Array.new(3);
					responder = OSCresponderNode(server.addr, 'BM-AutoBalance', {|time, resp, msg|
						if(msg[2] == index, {
							count = count + 1;
							array.add(msg[3].sqrt);
							if(count == 3, {
								trim = array.mean.ampdb;
								trimList[speaker.name] = trim;
								"Level for %: % dBFS (RMS)\n".postf(speaker.name, trim);
								responders[speaker.name] = nil;
								resp.remove;
							});
						});
					});
					responders[speaker.name] = responder;
					responder.add;
					3.do({|i|
						synth = Synth("BMAutoBalance", [in: in, out: speaker, id: index]);
						1.wait;
					});

				}, {"% not a normal Speaker, skipping it...\n".postf(speaker.key)});
				
			});
			1.wait;
			"\nChecking results".postln;
			responders.keysValuesDo({|key, value|
				"% failed\n".postf(key);
				value.remove;
			});
			
			(responders.size == 0).if({
				"Results Complete\n".postln;
				"Normalizing".postln;
				min = trimList.minItem; 
				speakerList.do({|speaker| 
					if(speaker.isBMSpeaker and: 
						{speaker.spec.fullRange ? true || onlyFullRange.not}, {
						diff = min - trimList[speaker.name];
						if(diff <= 0, { 
							trimList[speaker.name] = diff;
							"Normalized Autotrim for %: % dBFS\n".postf(speaker.name, diff);
						});
					});
				});
				trimList.keysValuesDo({|name, trim|
					speakerList[name].autoTrim = trim;
				});
				running = false;
				okayFunc.value(speakerList);
			});
			"\n\\\\\\\\\\\\\\\ Auto Level Balance Done\n".postln;
			
		}.fork;
	
	}
	
	// trims only copied at the end, so we can abort safely
	*stop {
		running.if({
			rout.stop;
			running = false;
			"Auto Level Balance aborted".warn;
		});
	}
	
	*sendDef {|server|
		SynthDef("BMAutoBalance", {|out, in = 0, amp = 0.3, id|
			var max, trig;
			trig = Impulse.ar(0);
			// Pink noise from 100 - 4000 Hz
			Out.ar(out, BPF.ar(PinkNoise.ar(amp), 1950, 2) * EnvGen.kr(Env.linen, timeScale: 0.3));
			max = RunningMax.ar(RunningSum.ar(SoundIn.ar(in).squared));
			SendReply.ar(DelayN.ar(trig, 0.3, 0.3), 'BM-AutoBalance', [max], id);
			FreeSelf.kr(DelayN.ar(trig, 0.35, 0.35)); // slightly later
		}).send(server);
	
	}

}

BMAutoBalancerGUI : BMAbstractGUI {
	
	var speakerList, okayFunc, server;
	
	*new {| speakerList, okayFunc, server, name, origin |
		  ^super.new.init(speakerList, okayFunc, server, name)
		  	.makeWindow(origin ? (400@600));
	}
	
	init {|argspeakerList, argokayFunc, argserver, argname|
		speakerList = argspeakerList;
		okayFunc = argokayFunc;
		server = argserver ? Server.default;
		name = argname;
	}
	
	makeWindow {|origin|
		var inChan, onlyFull, startButt;
		okayFunc = okayFunc.addFunc({ {startButt.value = 0;}.defer});
		window = Window.new(name ? "Autobalance Speakers", Rect(origin.x, origin.y, 300, 60), false);
		window.addFlowLayout;
		inChan = EZNumber(window, 290@20, "Microphone Input Channel ", [1, server.options.numInputBusChannels, \lin, 1, 1].asSpec, numberWidth: 40);
		window.view.decorator.nextLine.nextLine;
		SCStaticText(window, 60@20);
		onlyFull = RoundButton(window, 142.5@20)
			.extrude_(false)
			.canFocus_(false)
			.states_([
				[ "Only Full Range", Color.black,  Color.white.alpha_(0.8) ],
				[ "Only Full Range", Color.black,  Color.clear ]
			]);
		startButt = RoundButton(window, 80@20)
			.extrude_(false)
			.canFocus_(false)
			.states_([
				["Start", Color.black, Color.green.alpha_(0.2)],
				["Stop", Color.white, Color.red.alpha_(0.2)]
			])
			.action_({|butt|
				if(butt.value == 1, {
					BMAutoBalancer.run(speakerList, okayFunc, server, inChan.value - 1, onlyFull.value.booleanValue.not);
				}, { BMAutoBalancer.stop });
			});
		window.onClose = onClose.addFunc({ BMAutoBalancer.stop });
		window.front;
	}

}