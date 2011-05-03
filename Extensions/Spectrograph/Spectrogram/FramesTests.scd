//:! boot server
if (Server.default.serverRunning.not) { Server.default.boot };
//:a start spectrogram
if (a.notNil) {
	a = Spectrogram2(bounds: Rect(0, 0, 200, 200)).start;
};
b.free;
b = { SinOsc.ar(LFNoise2.kr(1).range(100, 10000), 0, 0.1) }.play;


//:g shorter test plot 
Frames.new.add(10000).plot({ | view, frame | postf("plotting: %\n", frame) });


//:f pre-test plot 
{
	var min, frames;
	"starting".postln;
	frames = Frames.new;
	10 do: { | i | frames add: i; 0.1.wait };
	min = frames.flop.first.minItem;
	frames do: { | f | f[0] = f[0] - min };
	"done".postln;
	frames.postln;
	frames.plot({ | view, frame | postf("plotting: %\n", frame) });
}.fork;



//:e test timerange frames 
{
	var min, frames;
	"starting".postln;
	frames = Frames.new;
	10 do: { | i | frames add: i; 0.1.wait };
	min = frames.flop.first.minItem;
	frames do: { | f | f[0] = f[0] - min };
	"done".postln;
	frames.postln;
	frames.timerange(0.3, 0.8).postln;
}.fork;


//:d test timerangeDo frames 
{
	var min, frames;
	"starting".postln;
	frames = Frames.new;
	10 do: { | i | frames add: i; 0.1.wait };
	min = frames.flop.first.minItem;
	frames do: { | f | f[0] = f[0] - min };
	"done".postln;
	frames.postln;
	frames.timerangeDo({ | ... args | args.postln; }, 0.3, 0.8);
}.fork;


//:! boot server
if (Server.default.serverRunning.not) { Server.default.boot };
//:a start spectrogram 
a = Spectrogram2(bounds: Rect(0, 0, 200, 200)).start;
b = { SinOsc.ar(LFNoise2.kr(1).range(100, 10000), 0, 0.1) }.play;

//:b stop sound
b.free;

//:x other sound
b.free;
b = { SinOsc.ar(LFNoise2.kr(1).range(100, 10000), 0, 0.1) }.play;
