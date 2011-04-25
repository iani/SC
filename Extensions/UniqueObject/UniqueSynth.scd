/*
To run an example, go anywhere after the //: mark that starts it, and press Command-Shift-x

To select the next example, press Command-shift-j
To select the previous example, press Command-shift-k

*/

//:a runs also with non-booted server

\default.play;
\default.wait(0.1); 				// make sure to wait for server boot also
\default.set(\freq, 440 * 3/2);
0.4.wait;
15 do: { \default.set(\freq, ((4..12) * 110).choose); 0.05.wait };
(16..4) * 110 do: { | i | \default.set(\freq, i); 0.1.wait };
0.5.wait;
\default.release; 

//:s synth
SynthDef(\test, { Out.ar(0, SinOsc.ar(Rand(4, 10) * 100, 0, 0.1)) }).load(Server.default);

0.3.wait;

5 do: {
	a = \test.play.onStart;
	a.onRemove({ | x |  postf("% stopped\n", x); });
	0.2.wait;
	\test.free;
	0.1.wait;
};

//:o on start

\test.play.onStart({ "hello".postln; });
\test.wait(1);
\test.usynth.free;

\default.play.rsyncs({ | synth | 
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
