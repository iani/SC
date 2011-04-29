/*
To run an example, go anywhere after the //: mark that starts it, and press Command-Shift-x

To select the next example, press Command-shift-j
To select the previous example, press Command-shift-k

*/

//:Can be started when the server is not running
\default.play.onStart({ | s | postf("% started\n", s.synth); });

//:2
\default2.playDef(\default, [\freq, 550]).onStart({ | s | postf("% started\n", s.synth); });

\default3.playDef(\default, [\freq, 660]).onStart({ | s | postf("% started\n", s.synth); });

UniqueSynth.onServer.postln;


//:3
[\default, \default2, \default3] do: _.release;

//:1a
\default.play.onStart({ | s | postf("% started\n", s.synth); });

\default.wait(1);

\default2.playDef(\default, [\freq, 550]).onStart({ | s | postf("% started\n", s.synth); });

1.wait;

\default3.playDef(\default, [\freq, 660]).onStart({ | s | postf("% started\n", s.synth); });

[\default, \default2, \default3] do: { | s | 0.5.wait; s.release; };

//:4

\default.play.dur(1).rsyncs({
	inf do: { \default.set(\freq, 80 rrand: 1800); 0.01.wait; };
});

//:4a

\default.play.dur(1.5).rsyncs({
	100 do: { \default.set(\freq, 80 rrand: 1800); 0.01.wait; };
});

//:4b

\default.play.rsyncs({ | s |
	20 do: { s.set(\freq, 80 rrand: 1800); 0.05.wait; };
	(1..40) do: { | i | 0.1.wait; s.set(\freq, i * 100) }; 
	1.wait;
	s.release;
});

//:WRONG!
\default.play;

{ inf do: { \default.set(\freq, 400 rrand: 4000); 0.25.wait; }; }.fork;

\default.wait(1);

\default.release;

//:Right!
\default.play.rsynca({ inf do: { \default.set(\freq, 400 rrand: 4000); 0.25.wait; }; });

\default.wait(1);

\default.release;

//:More examples: 
 

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
\test.free;

\default.play.rsyncs({ | synth | 
	0.25.wait;
	10 do: {
		0.1.wait;
		synth.set(\freq, 200 rrand: 400);
	};
	synth.release;
});

2.wait;

{ | freq = 400 | LFNoise0.ar(freq, 0.1) }.uplay.rsyncs({ | synth |
	50 do: { | i |
		(i + 1).reciprocal.wait;
		synth.set(\freq, 400 rrand: 10000);
	};
	synth.free;	
});
