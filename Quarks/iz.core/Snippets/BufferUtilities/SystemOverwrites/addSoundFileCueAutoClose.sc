/* iz 

Add option to SoundFile:cue for automatically freeing the buffer when the synth that is playing it ends. 

*/

+ SoundFile {
	
	cue { | ev, playNow = false, closeWhenDone = true |
		var server, packet, defname = "diskIn" ++ numChannels, condition, onClose;
		ev = ev ? ();
		if (this.numFrames == 0) { this.info };
		fork {
			ev.use {
				server = ~server ?? { Server.default};
				if(~instrument.isNil) {
					SynthDef(defname, { | out, amp = 1, bufnum, sustain, ar = 0, dr = 0.01 gate = 1 |
						Out.ar(out, VDiskIn.ar(numChannels, bufnum, BufRateScale.kr(bufnum) )
						* Linen.kr(gate, ar, 1, dr, 2)
						* EnvGen.kr(Env.linen(ar, sustain - ar - dr max: 0 ,dr),1, doneAction: 2) * amp)
					}).add;
					~instrument = defname;
					condition = Condition.new;
					server.sync(condition);
				};
				ev.synth;	// set up as a synth event (see Event)
				~bufnum =  server.bufferAllocator.alloc(1);
				~bufferSize = 0x10000;
				~firstFrame = ~firstFrame ? 0;
				~lastFrame = ~lastFrame ? numFrames;
				~sustain = (~lastFrame - ~firstFrame)/(sampleRate ?? {server.options.sampleRate ? 44100});
				~close = { | ev |
						server.bufferAllocator.free(ev[\bufnum]);
						server.sendBundle(server.latency, ["/b_close", ev[\bufnum]],
							["/b_free", ev[\bufnum] ]  )
				};
				~setwatchers = { |ev|
					OSCFunc({ 
						server.sendBundle(server.latency, ["/b_close", ev[\bufnum]],
							["/b_read", ev[\bufnum], path, ev[\firstFrame], ev[\bufferSize], 0, 1]);
					}, "/n_end", server.addr, nil, ev[\id][0]).oneShot;
				};
				if (playNow) {
					packet = server.makeBundle(false, {ev.play})[0];
						// makeBundle creates an array of messages
						// need one message, take the first
				} {
					packet = [];
				};
				server.sendBundle(server.latency,["/b_alloc", ~bufnum, ~bufferSize, numChannels,
							["/b_read", ~bufnum, path, ~firstFrame, ~bufferSize, 0, 1, packet]
						]);
			};
		};
		if (closeWhenDone) {
			onClose = SimpleController(ev).put(\n_end, { 
	 			ev.close;
	 			onClose.remove;
			});
			ev.addDependant(onClose)
		};
		^ev;
	}
}

