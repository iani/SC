/* IZ 20120414  

Play back a single segment of a buffer: 

From time point \from
For duration \dur
At rate \rate

*/

//:Load some default sound files into buffers to work with
BufferResource.loadSCdefaults; // loads the a11wlk files from SC sounds folder
Server.default.boot;

//:Play just one second, starting from 0

'a11wlk01'.playBuf({ | buf = 0, from = 1.9, dur = 0.9, rate = 1 |
	var attack = 0.01, release = 0.01, env, envgen;
	env = Env([0, 1, 1, 0], [attack, dur - attack - release max: 0.01, release]);
	envgen = EnvGen.kr(env, doneAction: 2);
	PlayBuf.ar(1, buf, BufRateScale.kr(buf) * rate, 1, from * BufSampleRate.kr(buf), 0) * envgen;
});



//:Trying BufGrain from JoshUgens, from SC3plugins RepQuark from Collins.
/*
BufGrain : JoshGrain 
	*ar  arg trigger = 0, dur = 1, sndbuf, rate = 1, pos = 0, interp = 2, mul = 1, add = 0;
*/

'a11wlk01'.playBuf({ | buf = 0 | BufGrain.ar(Impulse.kr(1), 0.5, buf) });

//:2nd try

'a11wlk01'.playBuf({ | buf = 0 | 
	BufGrain.ar(
		Impulse.kr(LFSaw.kr(0.02, pi).range(1, 20)), 
		0.5, 
		buf,
		1,
		LFSaw.kr(0.05, pi).range(0, 1)
	)
});

//:3d try - overlap time stretch

'a11wlk01'.playBuf({ | buf = 0 | 
	BufGrain.ar(
		Impulse.kr(10), 
		0.1, 
		buf,
		1,
		LFSaw.kr(0.05, pi).range(0, 1)
	)
});



//:4th try - random rates

'a11wlk01'.playBuf({ | buf = 0 |
	var trig, rate;
	trig = Impulse.kr(4.reciprocal);
	rate = Demand.kr(trig, 0, Dseq([0.5, 2.0, 2.5, 3, 0.25, 10], inf));
	BufGrain.ar(
		trig, 
		4, 
		buf,
		rate,
		0,
//		LFSaw.kr(0.05, pi).range(0, 1),
		mul: 1
	)
});

//:5th try - using TGrains2 (has linear envelope)
'a11wlk01'.playBuf({ | buf = 0 |
	var trig, rate, pos;
	trig = TDuty.kr(Dseq((0.1, 0.2 .. 20).reciprocal.sqrt, inf));
	rate = Demand.kr(trig, 0, Dseq([0.5, 1, 1.5], inf));
	pos = Demand.kr(trig, 0, Dser([1, 2], inf));
	TGrains2.ar(
		2,		// at least 2 output channels erforderlich
		trig,	// trigger for grain production 
		buf,		// buffer
		rate,	// rate
		pos,		// center position of buffer where grain env will reach max amp
		1.9, 	// duration
		0,		// pan
		1,		// amp
		0.01,	// attack time of grain in seconds
		0.01,	// decay time of rain in seconds
//		LFSaw.kr(0.05, pi).range(0, 1),
//		mul: 1
	)
});


//:6th try: What happens to the center and beginning of the grain when the rate changes?
'a11wlk01'.playBuf({ | buf = 0 |
	var trig, rate, pos = 2;
	trig = Impulse.kr(0.5);
	rate = Demand.kr(trig, 0, Dseq((0.2, 0.3 .. 2.5), inf));
	TGrains2.ar(
		2,		// at least 2 output channels erforderlich
		trig,	// trigger for grain production 
		buf,		// buffer
		rate,	// rate
		pos,		// center position of buffer where grain env will reach max amp
		1.5, 	// duration
		0,		// pan
		1,		// amp
		0.01,	// attack time of grain in seconds
		0.01,	// decay time of rain in seconds
//		LFSaw.kr(0.05, pi).range(0, 1),
//		mul: 1
	)
});

// Next see: 04BufferGrainTrains
