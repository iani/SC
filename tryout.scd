//:a
WaitForServer.new;

SynthDef("test", { | out = 0 |
	var fadeout;

	fadeout = FadeOut.kr;

	Out.ar(out, SinOsc.ar(440, 0, 0.1) * fadeout);	
	
}).send(Server.default);
0.1.wait;
\test.play;
0.5.wait;
\test.set(\fadeOut, 5, \gate, 0);



