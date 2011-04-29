/*
To run an example, go anywhere after the //: mark that starts it, and press Command-Shift-x

To select the next example, press Command-shift-j
To select the previous example, press Command-shift-k

*/


//:play a function with uplay: starts also when server is not booted: 
{ SinOsc.ar(400 + 400.rand, 0, 0.1) }.uplay;

//:symbol + playFunc
\def.playFunc({ SinOsc.ar(400 + 400.rand, 0, 0.1) });

//:play a function multiple times, (server-boot-safe), for specified durations: 

WaitForServer.new;

100 do: { 
	{ SinOsc.ar(400 + 400.rand, 0, 0.1) }.mplay(dur: rrand(0.01, 1));
	0.1.wait;
};

//:f fun

"hello ".post;
a = { WhiteNoise.ar(EnvGen.kr(Env.perc(0.001, 0.7, 0.3), doneAction: 2)) }.uplay;

a.wait(0.2);
a = { WhiteNoise.ar(LFTri.kr(10 + LFNoise1.kr(2, 3), 0, 0.2)) }.uplay(fadeTime: 4).dur(5);

\default.play.rsynca({ | s |
	220 do: { | i | (i + 11 / 5).reciprocal.wait; 
		s.set(\freq, ((4..16) * 110) @@ ((i % 4).rand + (i / 20 % 60))); ".".post;
	};
	1.wait;
	s.release;
	" there".postln;
});
