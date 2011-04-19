//:@  ! // start spectrogram  // auto ! disabled ! 
if (a.isKindOf(Spectrogram2)) {  a.start } {
	{ 	1.wait; a = Spectrogram2.new; 
		1.wait; x = SpectrogramDataTest(a);
	}.fork(AppClock);
};

//: nothing particular
a.binfreqs.asCompileString;

a.binfreqs.size;

x = SpectrogramDataTest(a);
/* 
x.stop;
x.start;
a.colorScaleExp = 2;
*/
//:Q test tone again
r = { | freq = 400 | SinOsc.ar(freq, 0, 0.1); }.play;

//:` modulate above by hand

r.set(\freq, 1000);
r.set(\freq, 2000);
r.set(\freq, 4000);

//: 

q = { | m = 40 | SinOsc.ar(m.midicps, 0, 0.1) }.play;

q.set(\m, 41);
q.set(\m, 42);
q.set(\m, 82);
q.set(\m, 84);
q.set(\m, 45);
q.set(\m, 48);

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

{ WhiteNoise.ar(30) }.play;

(1..16).reciprocal
(1..32).pow(0.01).normalize.postln.plot

(1..32).log10.plot;
(1..32).pow(0.001).log10.plot;

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