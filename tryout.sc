
a = Spectrogram2(bounds: Rect(0, 0, 200, 200));
a.start;
a.stop;
{ SinOsc.ar(LFNoise2.ar(0.5).range(1300, 9000), 0, 0.1) }.play;

a.intensity = 3

{ HPF.ar(LFNoise2.ar(15000, 0.5), 5000) }.play;
