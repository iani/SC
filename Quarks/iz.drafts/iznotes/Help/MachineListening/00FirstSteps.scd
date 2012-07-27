

Server.default.boot;

FreqScope.new.front


FreqScope.new(400, 200, 0);

{ WhiteNoise.ar(0.1) }.play;


{ PinkNoise.ar(0.1) }.play;

{ GrayNoise.ar(0.1) }.play;

{ BrownNoise.ar(0.1) }.play;

{ LFNoise0.ar(MouseX.kr(100, 10000), 0.1) }.play;

{ SinOsc.ar(MouseX.kr(100, 10000), 0, 0.1) }.play;

GUI.qt;

SpecPcile

////

b = Buffer.alloc(Server.default, 2048, 1);

(
{
var in, chain, realcutoff, estcutoff;
realcutoff = MouseX.kr(0.00001,22050);
in = LPF.ar(WhiteNoise.ar(0.2), realcutoff);
chain = FFT(b, in);
estcutoff = Lag.kr(SpecPcile.kr(chain, 0.9), 1);
realcutoff.poll(Impulse.kr(1), "real cutoff");
estcutoff.poll(Impulse.kr(1), "estimated cutoff");
Out.ar(0, in);
Out.kr(0, estcutoff * 22050.0.reciprocal);
}.scope;
)

