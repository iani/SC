
//:s synth
SynthDef(\test, { Out.ar(0, SinOsc.ar(Rand(4, 10) * 100, 0, 0.1)) }).load(Server.default);

0.1.wait;

5 do: {
	a = \test.usynth;
	a.onRemove({ | x |  postf("% stopped\n", x); });
	
	0.2.wait;
	
	a.free;
	0.1.wait;
};

//:S stringOrSymbol.usynth

\test.usynth

//:p Uplay(function)

p = Uplay({ WhiteNoise.ar(0.1) });
p.onEnd({ | x | x.post; " ended".postln; });

//:P function.uplay

a = { | v = 1 | SinOsc.ar(LFPulse.kr(v).range(400, 440), 0, 0.1) }.uplay(args: [\v, 15]);

{ a.synth.set(\v, 1); }.defer(1);

