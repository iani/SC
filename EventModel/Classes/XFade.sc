/*
Enclose a ugen-output in a basic envelope, send it through Out.ar,
and provide controls:
out
fadeTime
gate

Note: fadeOut time is provided by synth.release(fadeOut)

This little utility is not for use with SynthDef(...).add
or { }.asSynthDef.add

For doing this with functions, use:
{ ... }.asFlexSynthDef

/////////// Examples

a = { XFade(WhiteNoise.ar(0.1)) }.play(args: [fadeTime: 20]);
// a = { XFade(WhiteNoise.ar(0.1)) }.play;

a.set(\amp, 0.1);

a.release(0.1);
a.release(10);
a.set(\out, 1);
a release: 1;
a.release;
*/

XFade {
	*new { | input |
		var env;
		env = Env([0, 1, 0], [1, 1], releaseNode: 1);
		^Out.ar(\out.kr(0),
			input * \amp.kr(1) *
			EnvGen.kr(env, \gate.kr(1), timeScale: \fadeTime.kr(0.1), doneAction: 2)
		)
	}
}
