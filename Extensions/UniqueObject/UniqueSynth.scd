/*
To run an example, go anywhere after the //: mark that starts it, and press Command-Shift-x
*/

//:f fun

"hello ".post;
{ WhiteNoise.ar(EnvGen.kr(Env.perc(0.001, 0.7, 0.3), doneAction: 2)) }.uplay;

0.5.wait;

\default.usynth.rsynca({ | s |
	50 do: { | i | (i + 2).reciprocal.wait; s.set(\freq, 200 rrand: 1500); ".".post; };
	s.free;
	" there".postln;
});

//:s synth
SynthDef(\test, { Out.ar(0, SinOsc.ar(Rand(4, 10) * 100, 0, 0.1)) }).load(Server.default);

0.3.wait;

5 do: {
	a = \test.usynth;
	a.onRemove({ | x |  postf("% stopped\n", x); });
	0.2.wait;
	a.free;
	0.1.wait;
};

//:S stringOrSymbol.usynth

\test.usynth; 1.wait; \test.usynth.free;

//:p Uplay(function)

p = Uplay({ WhiteNoise.ar(0.1) }).onEnd({ | x | x.post; " ended".postln; });
1.wait;
p.free;

//:P function.uplay

a = { | v = 1 | SinOsc.ar(LFPulse.kr(v).range(400, 440), 0, 0.1) }.uplay(args: [\v, 15]);
1.wait;
a.synth.set(\v, 1);
1.5.wait;
a.free;

//:o on start

\test.usynth.onStart({ "hello".postln; });
1.wait;
\test.usynth.free;

//:r routine sync

\default.usynth.rsyncs({ | synth | 
	0.25.wait;
	10 do: {
		0.1.wait;
		synth.set(\freq, 200 rrand: 400);
	};
	synth.free;
});

2.wait;

{ | freq = 400 | LFNoise0.ar(freq, 0.1) }.uplay.rsyncs({ | synth |
	50 do: { | i |
		(i + 1).reciprocal.wait;
		synth.set(\freq, 400 rrand: 10000);
	};
	synth.free;	
});
