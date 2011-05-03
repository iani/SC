\default.play;
\test.play;

Udef(\test, { Out.ar(0, LFNoise0.ar(SinOsc.kr(5).range(400, 4000), 0.1)) });

\test.play;

\test.play(\test);

