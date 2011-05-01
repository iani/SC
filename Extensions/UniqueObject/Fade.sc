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

