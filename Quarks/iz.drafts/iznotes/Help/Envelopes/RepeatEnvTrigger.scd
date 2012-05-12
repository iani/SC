

//:--
\test.playFunc({ WhiteNoise.ar(EnvGen.kr(Env.perc(0.001, 0.1, 0.2), Dust.kr(3).dup)) });

//:--
\test.release(1);

//:--
\test.playFunc({
	LFTri.ar(
		EnvGen.kr(Env([400, 300, 600, 500], [1, 0.5, 0.25]), Dust.kr(0.5).dup, timeScale: 0.2³),
		0.0, 0.05
	)
});

//:--
\test.release(1);

//:Different shapes are possible (in contrast to Decay)
\test.playFunc({ WhiteNoise.ar(EnvGen.kr(
	Env([0, 0.1, 0.02, 0.05, 0], [0.05, 0.5, 0.1, 0.7]),
	Dust.kr(0.5).dup)) 
});

//:--
\test.release(1);

//:Compare with Decay: Simpler, but little control over the shape of the envelope
\test.playFunc({ WhiteNoise.ar(Decay.kr(Dust.kr(3, 0.1).dup)) });

//:--
\test.release(1);
