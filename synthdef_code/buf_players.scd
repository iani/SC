/* 
O.startup;
Synth(\blfn1, [\bufnum, O@\weddellb])


Synth(\blfn3, [\bufnum, O@\weddellb])

SynthDescLib.global.browse;

*/

SynthDef("bphasor", { 
	| 
		out = 0, bufnum = 0, rate = 1, trigger = 0, start = 0, stop = 1, reset = 0, 
		vol = 1, vollag = 0.1, ratelag = 0.0
	|
	var source;
	source = BufRd.ar(1, bufnum, 
		Phasor.ar(
			trigger, 
			Lag.kr(rate, ratelag) * BufRateScale.kr(bufnum), 
			start * BufFrames.kr(bufnum), 
			stop * BufFrames.kr(bufnum),
			reset * BufFrames.kr(bufnum)
		)
	);
	Out.ar(out, Lag.kr(vol, vollag) * source);
}).store;

// racing through the buffer in varying speeds with LFDNoise3

SynthDef("blfn3", { | out = 0, bufnum = 0, rate = 0.045, vol = 1, vollag = 0.1 |
	Out.ar(out, Lag.kr(vol, vollag) * BufRd.ar(1, bufnum, LFDNoise3.ar(rate) * BufFrames.kr(bufnum)));
}).store;


// As above but with LFDNoise1. Speed will be constant during each time interval
SynthDef("blfn1", { | out = 0, bufnum = 0, rate = 0.045, vol = 1, vollag = 0.1  |
	Out.ar(out, Lag.kr(vol, vollag) * BufRd.ar(1, bufnum, LFDNoise1.ar(rate) * BufFrames.kr(bufnum)));
}).store;

/* 	Simple linear reading through a buffer with Phasor. To be developed for combination
	with subtler controls added on top of the linear movement. 
*/


/*
Phasor.ar(trig, rate, start, end, resetPos)

a = Synth(\bphasor, [\bufnum, O@\weddellb]);

a.set(\rate, 0.1);

r = Bus.control;
r set: 1;

a.map(\rate, r);

r set: 2;

r set: 0.5;
r set: 1.5;
r set: 3.5;

m = { Out.kr(r.index, LFNoise1.kr(0.2)) }.play;

Line.kr(start, end, dur, mul, add, doneAction)

m.free;

m = { Out.kr(r.index, Line.kr(-5, 5, 100, doneAction: 2)) }.play;

m = { Out.kr(r.index, XLine.kr(0.1, 2, 20, doneAction: 2)) }.play;


a = Synth(\bphasor, [\bufnum, O@\weddellb, \rate, 1]);
a.free;


a = { | i | Synth(\bphasor, [\out, i.postln, \bufnum, O@\weddellb, \rate, i / 1000 + 1]); } ! 43 

b = { | i | Synth(\bphasor, [\out, i.postln, \bufnum, O@\swallowsa, \rate, i / 1000 + 1]); } ! 43 

a do: _.free;

c = { | i | Synth(\bphasor, [\out, i.postln, \bufnum, O@\dfd, \rate, i / 100 + 1]); } ! 43


Synth(\bphasor, [\out, 0, \bufnum, O@\weddellb, \rate, 1]);
Synth(\bphasor, [\out, 1, \bufnum, O@\weddellb, \rate, 1]);
Synth(\bphasor, [\out, 2, \bufnum, O@\weddellb, \rate, 1]);
Synth(\bphasor, [\out, 3, \bufnum, O@\weddellb, \rate, 1]);
Synth(\bphasor, [\out, 4, \bufnum, O@\weddellb, \rate, 1]);


// m.free;
*/

