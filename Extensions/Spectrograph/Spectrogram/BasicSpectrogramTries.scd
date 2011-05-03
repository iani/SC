//:a b ! // start spectrogram  // auto ! disabled ! 
if (a.isKindOf(Spectrogram2)) {  a.start } {
	{ 	0.1.wait; a = Spectrogram2(bounds: Rect(0, 0, 1200, 200)); 
		1.wait; x = ImageDrawTest(a);
	}.fork(AppClock);
};


//:Q test tone
r = { | freq = 400 | SinOsc.ar(freq, 0, 0.1); }.play;

//:` modulate above by hand

r.set(\freq, 1000);
r.set(\freq, 2000);
r.set(\freq, 4000);

//: midi tone

q = { | m = 40 | SinOsc.ar(m.midicps, 0, 0.1) }.play;

//:q test tone max freq detection
q = {
	SinOsc.ar(
		LFSaw.kr(0.1, pi).range(60, 110).round(1).midicps,
		0,
		0.1
	)
}.play;


//:~ stuff
a.colorScaleExp = 0.5;
a.intensity = 2;
ff10.ceil(5);

a.respondsTo('colorScaleExp_');


(0..10).clip(0, 5);

{ WhiteNoise.ar(0.30) }.play;

(1..16).reciprocal
(1..32).pow(0.01).normalize.postln.plot

(1..32).log10.plot;
(1..32).pow(0.001).log10.plot;

//:s modulating sine
if (b.notNil) { b.free; };
b = { SinOsc.ar(LFNoise0.kr(LFNoise1.kr(0.1).range(0.2, 4)).range(100, 19000).lag(0.1), 0, 0.1) }.play;

//:b

// { SinOsc.ar(LFNoise2.ar(0.5).range(70, 15000), 0, 0.1) }.play;

{ HPF.ar(LFNoise2.ar(LFNoise1.kr(0.5).range(100, 15000), 0.5), LFNoise2.ar(0.3).range(10, 15000)) }.play;
