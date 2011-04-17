//:! boot default server

if (Server.default.serverRunning.not) { Server.default.boot; };

// //:a make a spectrogram window;

// a = SpectrogramWindow.new;

//:b try out a sine
b.free;
b = { SinOsc.ar(LFNoise2.kr(1).range(100, 10000), 0, 0.1) }.spectrogram;

//:c move the sine more slowly
b.free;
b = { SinOsc.ar(LFNoise2.kr(0.1).range(100, 10000), 0, 0.1) }.spectrogram;

//:d saw: aliasing
b.free;
b = { LFSaw.ar(LFNoise1.kr(0.1).range(100, 10000), 0, 0.1) }.spectrogram;

//:e saw lower range
b.free;
b = { LFSaw.ar(LFNoise1.kr(0.1).range(100, 1000), 0, 0.1) }.spectrogram;

//:f pulse lower range
b.free;
b = { LFPulse.ar(LFNoise1.kr(0.1).range(100, 1000), 0, 0.5, 0.1) }.spectrogram;

//:g LFNoise0 greater range
b.free;
b = { LFNoise0.ar(LFNoise1.kr(0.1).range(100, 10000), 0.1) }.spectrogram;

//:h LFNoise1 greater range
b.free;
b = { LFNoise1.ar(LFNoise1.kr(0.1).range(100, 10000), 0.1) }.spectrogram;

//:i LFNoise2 greater range
b.free;
b = { LFNoise2.ar(LFNoise1.kr(0.1).range(100, 10000), 0.1) }.spectrogram;
