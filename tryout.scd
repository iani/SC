
Udef(\simple, { | freq = 440 |
	Out.ar(0, Fade.kr * SinOsc.ar(freq, 0, 0.1.dup))
}).play;

