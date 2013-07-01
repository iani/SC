



m = BasicMixer.new;
m.makeWindow;
m.start;


a = { | amp = 0.1 | WhiteNoise.ar(amp) }.play;

m.synth.set(\amp1, 0.1);

m.stop;

NodeWatcher.register(a);

\test.addNotifier(a, \n_end, { "ended".postln; });

a.free;


a = SynthDef("test", { | amp = 0.1 | WhiteNoise.ar(amp) });
a.add;

SynthDescLib.global[\test].controls.first.inspect;

Dictionary;


d = (hello: 9, whello: 77, z: 99);
d.getPairs;
d.getPairs([\z, \hello]);

d.keys