a = { | freq = 400, gate = 1 |
	SinOsc.ar(freq, 0, EnvGen.kr(Env([0, 0.1, 0.05, 0.15, 0.02, 0], [0.01, 0.1, 0.3, 0.1, 1.0], 1, 1), gate, doneAction: 2));
}.play(args: [\freq, 500 rrand: 2000]).register;

a addDependant: { | ... args | args.postln; }



a.isRunning

a.free;