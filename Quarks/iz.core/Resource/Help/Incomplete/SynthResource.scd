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

postf("Synths on server are: %\n", SynthResource.onServer);


//:3
[\default, \default2, \default3] do: _.releaseSynth(1.5);

//:1a
\default.play.onStart({ | s | postf("% started\n", s.synth); });

1.wait;

\default2.playDef(\default, [\freq, 550]).onStart({ | s | postf("% started\n", s.synth); });

1.wait;

\default3.playDef(\default, [\freq, 660]).onStart({ | s | postf("% started\n", s.synth); });

[\default, \default2, \default3] do: { | s | 0.5.wait; s.releaseSynth; };

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
	s.release(5);
});

//:WRONG!
\default.play;

{ inf do: { \default.set(\freq, 400 rrand: 4000); 0.025.wait; }; }.fork;

2.wait;

\default.release(3);

//:Right!
\default.play.rsynca({ inf do: { \default.set(\freq, 400 rrand: 4000); 0.025.wait; }; });

2.wait;

\default.release(3);

//:More examples: 
\default.play;
0.1.wait;
\default.set(\freq, 440 * 3/2);
0.4.wait;
15 do: { \default.set(\freq, ((4..12) * 110).choose); 0.05.wait };
(16..4) * 110 do: { | i | \default.set(\freq, i); 0.1.wait };
0.5.wait;
\default.release; 

//:On start. 
// This example also shows the combined use of Symbol:play with Function:uplay. 

\test.play({ LFNoise2.ar(4000, 0.1) }).onStart({ "hello".postln; });
1.wait;
\test.release;

\default.play.rsyncs({ | synth | 
	0.05.wait;
	50 do: {
		0.1.wait;
		synth.set(\freq, 200 rrand: 400);
	};
	synth.release(2);
});

2.wait;

{ | freq = 400 | LFNoise0.ar(freq, 0.1) }.play.rsyncs({ | synth |
	50 do: { | i |
		(i + 1).reciprocal.wait;
		synth.set(\freq, 400 rrand: 10000);
	};
	synth.free;	
});
