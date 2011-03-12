


{ 
	var a;
	a = { | out = 0 | Out.ar(out, WhiteNoise.ar(0.1)) }.play;
	0.1.wait;
	inf do: { | i |
		1.wait;
		a.set(\out, i % 24);
	}
}.fork;


a = { | out = 0 | Out.ar(out, WhiteNoise.ar(0.1)) }.play;
a.set(\out, 5);