/*
Provide a simple way to fade synths at start and end, in order to avoid "click" sounds. 

Decay is set by release: 

Server.default.boot;

SynthDef(\test, { Out.ar(0, Fade.kr * SinOsc.ar(Rand(400, 4000), 0, 0.2)) }).send(Server.default);

a = Synth(\test);

a.release(3);


*/

Fade {
	*ar { | attack = 0.01, doneAction = 2 |
		^Linen.ar(\gate.kr(1), attack, 1, 0.02, doneAction)
	}
	*kr { | attack = 0.01, doneAction = 2 |
		^Linen.kr(\gate.kr(1), attack, 1, 0.02, doneAction)
	}
}

// A curved fadeout sounds better than a linear one 

FadeOut {
	*ar { | attack = 0.01, doneAction = 2 |
		var env;
		env = Env.new([0, 1, 0], [attack, 1], -5, 1);
		^EnvGen.ar(env, \gate.kr(1), timeScale: \fadeout.kr(1), doneAction: doneAction)
	}
	*kr { | attack = 0.01, doneAction = 2 |
		var env;
		env = Env.new([0, 1, 0], [attack, 1], -5, 1);
		^EnvGen.kr(env, \gate.kr(1), timeScale: \fadeout.kr(1), doneAction: doneAction)
	}
}

