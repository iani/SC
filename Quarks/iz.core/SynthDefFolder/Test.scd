
Udef("test", { OUT(WhiteNoise.ar(0.1)) });


Udef("test2", { Out.ar(0, WhiteNoise.ar(LFNoise0.kr(15, 0.1))) });


Udef("test3", { Out.ar(0, WhiteNoise.ar(LFNoise0.kr(LFNoise0.kr(1).range(1, 30), 0.1))) });


Udef("test4", {
	var source;
	source = WhiteNoise.ar(LFNoise0.kr(LFNoise0.kr(1).range(1, 30), 0.1));
	Out.ar(0, Resonz.ar(source, LFNoise0.kr(0.5).range(100, 10000), LFNoise1.kr(0.2).range(0.01, 3)))
});

Udef("test5", {
	var source;
	source = WhiteNoise.ar(LFNoise0.kr(LFNoise0.kr(1).range(1, 30), 0.1));
	Out.ar(0, Resonz.ar(source, LFNoise0.kr(LFNoise0.kr(0.1).range(0.01, 10)).range(100, 10000), 
		LFNoise1.kr(0.2).range(0.01, 3))
	)
});

Udef("test6", {
	var source, resonance, reverb;
	source = WhiteNoise.ar(LFNoise0.kr(LFNoise0.kr(1).range(1, 30), 0.1));
	resonance = Resonz.ar(source, LFNoise0.kr(LFNoise0.kr(0.1).range(0.01, 10)).range(100, 10000), 
		LFNoise1.kr(0.2).range(0.01, 3));
	reverb = AllpassL.ar(resonance, 0.1, 0.05, 3);
	Out.ar(0, reverb);
});

Udef("test7", {
	var source, resonance;
	source = WhiteNoise.ar(LFNoise0.kr(LFNoise0.kr(1).range(1, 30), 0.1));
	resonance = Resonz.ar(source, LFNoise0.kr(LFNoise0.kr(0.1).range(0.01, 10)).range(100, 10000), 
		LFNoise1.kr(0.2).range(0.01, 3));
	5 do: { resonance = AllpassL.ar(resonance, 0.1, 
			LFNoise0.kr(LFNoise1.kr(0.1).range(0.1, 20).dup).range(0.0001, 0.099), 
			LFNoise1.kr(0.2).range(0.1, 5)) };
	Out.ar(0, resonance);
});
