
BMSoundFilePlayer : BMAbstractAudioSource {
	
	var maxNumChannels, <>bus;
	var <buffer, <synth, watcher, <rate = 1;
	var <sampleDur = 2.2675736961451e-05;
	var blockPlay = false;
	var resp, trigID;
	var <loading = false;
	
	*new {|maxNumChannels = 2, target, addAction = \addToHead, name|
		^super.new.init(maxNumChannels, target, addAction, name);
	}
	
	init { |argMaxNumChannels, argTarget, argAddAction, argName|
		
		this.initNameAndTarget(argTarget, argAddAction, argName);
		maxNumChannels = argMaxNumChannels;
		bus = Bus.audio(server, maxNumChannels);
		BMTimeReferences.addReference(this);
		// we check by node ID but this should be good enough to avoid conflicts with others
		// if they don't
		trigID = this.hash & 65535; // need 16 bit
	}
	
	startListening {
		resp = OSCresponderNode(this.server.addr,'/tr',{ arg time,responder,msg;
			if(msg[1] == synth.nodeID, {
				this.changed(\time, msg.last * this.sampleDur, rate, time);
			});
		}).add;
	}
	
	read {|path, action|
		
		Routine.run {
			var condition, bundle;
			loading = true;
			this.changed(\loading);
			
			// create a condition variable to control execution of the Routine
			condition = Condition.new;
			this.stop;
			BMOptions.crossfade.wait;
			
			bundle = server.makeBundle(false, { buffer.free });
			server.sync(condition, bundle);
			"Old Buffer Freed".postln;
			buffer = Buffer.read(server, path, action: {(path + "loaded").postln;
			sampleDur = buffer.sampleRate.reciprocal;
			loading = false;
			this.changed(\loaded);
			this.changed(\base);
			this.sendDef; action.value(this) });
		};

		
		
//		var oldBuffer;
//		oldBuffer = buffer;
//		this.stop;
//		buffer = Buffer.read(server, path, action: {(path + "loaded").postln;
//			this.sendDef; action.value });
//		server.makeBundle(releaseTime, {oldBuffer.free});
	}
	
//	sendDef { // only called after Buffer vars updated
//		SynthDef(this.hash.asString, { arg out, gate = 1, rate = 1, loop = 0, updateRate = 1;
//			var player, phasor;
////			phasor = Phasor.ar(0, BufRateScale.kr(buffer.bufnum) * rate, 
////				0, BufFrames.kr(buffer.bufnum) * 1.1);
////			phasor = Phasor.ar(0, BufRateScale.kr(buffer.bufnum) * rate, 
////				0, buffer.numFrames);
//			//phasor = LFSaw.ar(BufDur.ir(buffer.bufnum).reciprocal * rate, add: 1).range(0, BufFrames.ir(buffer.bufnum) + 1);
//			phasor = LFSaw.ar(BufDur.ir(buffer.bufnum).reciprocal * rate, 1).range(0, BufFrames.ir(buffer.bufnum));
//			//phasor = Line.ar(0, buffer.numFrames, BufDur.ir(buffer.bufnum));
//			SendTrig.kr(Impulse.kr(updateRate.reciprocal), this.hash, phasor);
//			//player =	BufRd.ar(buffer.numChannels, buffer.bufnum, phasor, loop, 4); 
//			//player =	BufRd.ar(buffer.numChannels, buffer.bufnum, phasor, loop, 1); 
//			player = PlayBuf.ar(buffer.numChannels, buffer.bufnum,
//				BufRateScale.kr(buffer.bufnum),1.0);
//			FreeSelfWhenDone.kr(player);//(BufFrames.kr(buffer.bufnum) * 0.5));
//			player = player * Linen.kr(gate, releaseTime: releaseTime, doneAction:2); 
//			Out.ar(out, player); 
//		}).send(server);
//	}

	sendDef { // only called after Buffer vars updated
		SynthDef(this.hash.asString, { arg out, gate = 1, rate = 1, loop = 0, updateRate = 0.1, 
				startPos = 0, t_trig = 1;
			var player, freeEnv, pauseEnv;
			
			player = PlayBufSendIndex.ar(buffer.numChannels, buffer.bufnum,
				BufRateScale.kr(buffer.bufnum) * rate,t_trig, startPos, loop, 
				updateRate.reciprocal, 
				trigID);
			FreeSelfWhenDone.kr(player);
			freeEnv = Linen.kr(gate, 0, releaseTime: BMOptions.crossfade, doneAction:2);
			// avoid DC offset by fading in and out
			pauseEnv = Linen.kr(rate, releaseTime: 0.01, doneAction:0);
			player = player * freeEnv * pauseEnv; 
			Out.ar(out, player); 
		}).send(server);
	}
	
	// startTime only works if we're not already playing
	play { |startTime = 0, out|
		this.rate_(1.0);
		(synth.isPlaying.not && blockPlay.not && synth.isNil && buffer.notNil).if({
			this.startListening;
			blockPlay = true;
			server.makeBundle(nil, { // bundle guarantees node watcher registered
				synth = Synth.head(group, this.hash.asString, 
					[\out, out ? bus, \rate, rate, 
					\startPos, startTime * buffer.sampleRate]);
				watcher = NodeWatcher.register(synth);
				synth.addDependant(this);
			});
			
			SystemClock.sched(0.1, {blockPlay = false;});
		}, {buffer.isNil.if({this.changed(\playFailed); ^this})});
		this.changed(\play);
	}
	
	stop { 
		synth.isPlaying.if({ this.stopAndCleanUp }); 
	}
	
	togglePlay {
		synth.isPlaying.if({ if(rate != 0, {this.pause}, {this.play}) }, {this.play });
	}
	
	stopAndCleanUp {
		resp.remove;
		blockPlay = true;
		watcher.stop;
		watcher = nil;
		synth.isPlaying.if({synth.release; }); 
		synth = nil; 
		rate = 1; 
		this.changed(\stop); 
		this.changed(\time, 0, 0, Main.elapsedTime); // not sure about this
		SystemClock.sched(0.1, {blockPlay = false;});
	}
	
	pause { synth.isNil.not.if({ this.rate = 0; this.changed(\pause);}) } // this will continue to ping time vals
	
	freeBuffer { this.stop;  server.makeBundle(BMOptions.crossfade, {buffer.free;}); buffer = nil;
		this.changed(\bufferFreed);
	}
	
	free { // after this I'm dead
		this.stop;  
		server.makeBundle(BMOptions.crossfade, {buffer.free; group.free;}); 
		buffer = nil;
		group = nil;
		bus.free;
		BMTimeReferences.removeReference(this);
		allChainElements[name] = nil;
		this.changed(\bufferFreed);
	}
	
	// maybe a controller better?
	update { arg changed, what; 
		if(what == \n_end, {this.stopAndCleanUp});
		this.changed(what);
	}
	
	setTime {|time|
		synth.isPlaying.if({
			synth.set(\startPos, time * buffer.sampleRate, \t_trig, 1);
		}, {
			// start and pause
			(buffer.notNil && (time != 0)).if({
				this.play(time);
				this.pause;
			});
		});
	}
	
//	getInputArray {|name = "Player"|
//		^BMInOutArray.fill(maxNumChannels, {|i| (name ++ (i + 1)).asSymbol -> (bus.index + i)});
//			
//	}
	
	asBMInOutArray {
		^BMInOutArray.fill(maxNumChannels, {|i| (name.asString ++ "-" ++ (i + 1)).asSymbol -> (bus.index + i)});
	}
	
	path { ^buffer.notNil.if({buffer.path}, {nil}) }
	
	rate_ { |newRate| 
		rate = newRate;
		synth.set(\rate, rate, \loop, 0);
	}
	
//	makeGroup {
//		group = Group.head(server);
//	}
	
//	cmdPeriod { blockPlay = false; this.makeGroup }
	
	
	// this provides a hook for multiple players by first checking by name
	// but then loading plain old path for simple case
	// undocumented
	loadPiece {|pieceEvent|
		var playerSpecificInfo;
		if(pieceEvent.soundFilePlayers.notNil, {
			playerSpecificInfo = pieceEvent.soundFilePlayers[name];
			if(playerSpecificInfo.notNil, {
				this.read(playerSpecificInfo.path);
			});
		}, {
			// in this case assume only one player and load path
			pieceEvent.path.notNil.if({
				this.read(pieceEvent.path);
			});
		});
	}
}


