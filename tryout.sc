(1..16).reciprocal
(1..32).pow(0.01).normalize.postln.plot

(1..32).log10.plot;
(1..32).pow(0.001).log10.plot;

//:s // start spectrogram  // auto ! disabled ! 
if (a.isKindOf(Spectrogram2)) {  a.start } {
	{ a = Spectrogram2.new; }.defer(1);
};
//:a 
if (b.notNil) { b.free; };
b = { SinOsc.ar(LFNoise0.kr(LFNoise1.kr(0.1).range(0.2, 4)).range(100, 19000).lag(0.1), 0, 0.1) }.play;

//:b

// { SinOsc.ar(LFNoise2.ar(0.5).range(70, 15000), 0, 0.1) }.play;

{ HPF.ar(LFNoise2.ar(LFNoise1.kr(0.5).range(100, 15000), 0.5), LFNoise2.ar(0.3).range(10, 15000)) }.play;

//:x
// problems here ...
// a.window.fullScreen; // fullScreen messes things up? 
a.toggleMaxScreen; // OK
a.stop;