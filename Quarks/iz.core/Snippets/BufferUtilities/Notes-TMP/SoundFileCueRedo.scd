/* iz Mon 24 September 2012  6:38 PM EEST

rewriting SoundFile:cue to close the buffer automatically when done playing.

*/


//:
SynthDef("test", { Out.ar(0, SinOsc.ar(400, 0, 0.01)) }).add;


//:

a = Synth("test").doOnEnd({ "ended".postln; });


//:
a.free;

//:

p = Platform.resourceDir +/+ "sounds/a11wlk01.wav";

//:

e = ();

SoundFile:cue

//:

f = SoundFile(Platform.resourceDir +/+ "sounds/a11wlk01.wav");
//:
e = f.play;
e[\close] = { | ev |
						"CRUUUUUUUUUUUUUUUUFFFFFFFFFFTTTTTTTTT".postln;
						Server.default.bufferAllocator.free(ev[\bufnum]);
						Server.default.bufferAllocator.sendBundle(Server.default.latency, ["/b_close", ev[\bufnum]],
							["/b_free", ev[\bufnum] ]  )
				};
e.addDependant({ | me, whatHappened | if (whatHappened === \n_end) {
	me.close;
	me.releaseDependants;
}; });
//:
e.close;
e.dependants;