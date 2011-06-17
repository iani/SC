
/*

FadeOut {

	*ar { | attack = 0.01, curve = -3, doneAction = 2 |
		^EnvGen.ar(Env.linen(attack, 1, 1, 1, curve);
			\gate.kr(1), 
			1, 1, \fadeout.kr(1), doneAction
		)
	}
	*kr { | attack = 0.01, curve = -3, doneAction = 2 |
		^EnvGen.ar(Env.linen(attack, 1, 1, 1, curve);
			\gate.kr(1), 
			1, 1, \fadeout.kr(1), doneAction
		)
	}
	
	*test {
		^{  WhiteNoise.ar(this.kr) * 0.1 }.play;
	}
	
}

*/

/*
//:---
\test.playFunc({ | gate = 1 |
	var env;
	env = Env.linen(1, 1, 10, 1, -3);
	Out.ar(0, WhiteNoise.ar(0.1)
		* EnvGen.kr(env, gate, 1, 1, 1, 2)
	)
});

1.wait;
\test.synth.object.set(\gate, 0).postln;

//:---
*/