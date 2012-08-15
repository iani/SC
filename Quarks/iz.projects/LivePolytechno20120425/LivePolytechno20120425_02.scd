

\dust playFunc: { Dust.ar(10) };


\dust playFunc: { Dust.ar(100) };

z = { Dust.ar(10) * 1 }.play;

z.free;

a = { Dust.ar(3) * 1 }.play;

a.free;


b.free;

b = { Mix(Ringz.ar(Dust.ar(3), [150, 160, 170, 180, 190, 200])* 0.6 }.play;

y = { Ringz.ar(Dust.ar(3.4), 200) * 0.6 }.play;

y.free;

c = { Ringz.ar(Dust.ar(20), 640) * 0.6 }.play;

d = { Ringz.ar(Dust.ar(30), 620) * 0.6 }.play;

b.free

b.free;

c.free

d.free


a.free

//:-------------

e = { Ringz.ar(Dust.ar(3), LFNoise0.kr([55, 66]).range(50, 250)) * 0.5 }.play;

f = { Ringz.ar(Dust.ar(1), LFNoise0.kr([3, 4]).range(250, 500)) * 2 }.play;


g = { Ringz.ar(Dust.ar(1), LFNoise1.kr([0.3, 0.4]).range(250, 5000)) * 0.5 }.play;



g.free;


f.free;
e.free

h = { Mix(Ringz.ar(Dust.ar(1), LFNoise1.kr([0.3, 0.4]).range(25, 200) * [1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 1.8])) * 0.5 }.play;

h.free;


i = { Mix(Ringz.ar(Dust.ar(10), LFNoise0.kr([30, 40]).range(250, 2000) * [1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 1.8])) * 0.5 }.play;


