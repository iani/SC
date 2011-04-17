//:! boot default server
if (Server.default.serverRunning.not) { Server.default.boot; };

//:0 custom spectrogram size

a = Spectrogram(bounds: Rect(0, 0, 1100, 300)).start;
b = { SinOsc.ar(LFNoise2.kr(1).range(100, 10000), 0, 0.1) }.play;

//:a from Spectrogram quark help example 1
if (w.notNil) { w.close; w = nil };
w = Window.new("testing spectrogram", Rect(10,10, 600, 300)).front;
a = Spectrogram.new(w, Rect(10, 10, 580, 280));
a.start;
{ SinOsc.ar(LFNoise1.kr(1).range(20, 2250)) }.play

//:b half window height
if (w.notNil) { w.close; w = nil };
w = Window.new("testing spectrogram", Rect(10,10, 600, 300)).front;
~height = w.view.bounds.height;
~width = w.view.bounds.width - 20;
a = Spectrogram.new(w, Rect(10, ~height / 2 - 2, ~width, ~height / 2 - 4));
a.start;
{ SinOsc.ar(LFNoise1.kr(1).range(20, 2250), mul: 0.1) }.play

//:c half window height +
if (w.notNil) { w.close; w = nil };
w = Window.new("testing spectrogram", Rect(10,10, 600, 300)).front;
~height = w.view.bounds.height;
~width = w.view.bounds.width - 20;
a = Spectrogram.new(w, Rect(10, ~height / 2 - 2, ~width, ~height / 2 - 4));
a.start;
{ SinOsc.ar(LFNoise1.kr(1).range(20, 2250), mul: 0.1) }.play

