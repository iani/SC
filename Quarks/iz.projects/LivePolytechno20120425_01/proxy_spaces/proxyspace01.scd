

~out.play;

~out = { Dust.ar(100, 0.5) };

~out = { LFNoise0.ar(LFNoise0.kr(0.25).range(100, 1000), 0.1) };

~out release: 3;