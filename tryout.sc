
a = Spectrogram2.new;
a.start;
a.stop;
{ SinOsc.ar(LFNoise2.ar(0.5).range(1300, 9000), 0, 0.1) }.play;

{ SinOsc.ar(LFNoise0.ar(2 + 2.0.rand).range(1300, 9000), 0, 0.1) }.play;

b = Spectrogram2(bounds: Rect(0, 600, 700, 200));


{ SinOsc.ar(LFNoise2.ar(0.5).range(7000, 15000), 0, 0.1) }.play;

a.intensity = 3

{ HPF.ar(LFNoise2.ar(15000, 0.5), 5000) }.play;
