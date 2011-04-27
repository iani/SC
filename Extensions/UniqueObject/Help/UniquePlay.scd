/*
To run an example, go anywhere after the //: mark that starts it, and press Command-Shift-x

To select the next example, press Command-shift-j
To select the previous example, press Command-shift-k

*/

//:p Uplay(function)

p = Uplay({ WhiteNoise.ar(0.1) }).onEnd({ | x | x.post; " ended".postln; });
p.wait(1);
p.release;

//:P function.uplay

a = { | v = 1 | SinOsc.ar(LFPulse.kr(v).range(400, 440), 0, 0.1) }.uplay(fadeTime: 0.3, args: [\v, 15]);
a.wait(0.2);
a.synth.set(\v, 1);
1.5.wait;
a.release;

//:f fun

"hello ".post;
a = { WhiteNoise.ar(EnvGen.kr(Env.perc(0.001, 0.7, 0.3), doneAction: 2)) }.uplay;

a.wait(0.2);
a = { WhiteNoise.ar(LFTri.kr(10 + LFNoise1.kr(2, 3), 0, 0.2)) }.uplay(fadeTime: 4, dur: 5);

\default.play.rsynca({ | s |
	220 do: { | i | (i + 11 / 5).reciprocal.wait; 
		s.set(\freq, ((4..16) * 110) @@ ((i % 4).rand + (i / 20 % 60))); ".".post;
	};
	1.wait;
	s.release;
	" there".postln;
});
