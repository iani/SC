//:! boot+open scope 
Server.default = Server.internal;
if (Server.default.serverRunning.not) { 
   Server.default.waitForBoot({
	Server.default.scope;
});
};

//:a first load a bigger sample to play with: 

DefaultBuffer loadDialog: _.play; // plays new buffer

//:b free the synth just created by default buffer: 

~synth.free;

//:c frozen samples on scope

//    BufRd.ar(numChannels, bufnum, phase, loop, interpolation)
//  BufRd.ar(1, b, Phasor.ar(0, BufRateScale.kr(b), 0, BufFrames.kr(b))) 
// Phasor: *ar(trig, rate, start, end, resetPos)

DefaultBuffer.play({ | bufnum |
	// a stupid function: scan a single sample in the buffer and output it. 
	{ | bufnum = 0, samplePos = 0 |
		Out.ar(0, BufRd.ar(1, bufnum, K2A.ar(samplePos), 1)); // 1 means loop on
	}.play;
})

//:e try setting the position somewhere else

~synth.set(\samplePos, 100000);

//:l loop random sample positions and watch level change

r = { 
	100 do: {	
		~synth.set(\samplePos, 100000.rand);
		0.25.wait;
	}
}.fork

//:p now use phasor

// Basic: just a read from start to end. 

DefaultBuffer.play({ | bufnum |
	{ | bufnum = 0, samplePos = 0 |
		Out.ar(0, BufRd.ar(1, bufnum, 
			Phasor.ar(
				0,	// trigger (resets to start when triggered). Not used.
				BufRateScale.kr(bufnum), // speed of movement. BufRateScale means fit to rate of buffer
				0,		// start at the first sample frame of the buffer
				BufFrames.kr(bufnum) // end at the last sample frame of the buffer
			)
		)); 
	}.play;
});

//:r change the rate

DefaultBuffer.play({ | bufnum |
	{ | bufnum = 0, samplePos = 0, rate = 1 |
		Out.ar(0, BufRd.ar(1, bufnum, 
			Phasor.ar(
				0,	// trigger not used here
				rate * BufRateScale.kr(bufnum), // added multiplier to change the rate
				0,		// start at the first sample frame of the buffer
				BufFrames.kr(bufnum) // end at the last sample frame of the buffer
			)
		)); 
	}.play;
});


r = { 
	100 do: {	
		~synth.set(\rate, -2 rrand: 2.0);
		3.wait;
	}
}.fork;

//:t start retrigger phasor: loop between points / durations

DefaultBuffer.play({ | bufnum |
	{ | bufnum = 0, samplePos = 0, trig = 0, start = 0, end = 1 |
		Out.ar(0, BufRd.ar(1, bufnum, 
			Phasor.ar(
				Impulse.ar(0.2),	// triggers every 5 seconds
				BufRateScale.kr(bufnum), // speed is constant
				start * BufFrames.kr(bufnum), // start scaled to fit entire buffer at 1
				end * BufFrames.kr(bufnum), // end scaled to fit entire buffer at 1
				1						// loop on
			)
		));
	}.play;
});

// gradually change the start point: 
r = { 
	(0, 0.1..1) do: {	| startPoint |
		~synth.set(\start, startPoint);
		15.wait; // change start point every 15 seconds
	}
}.fork;

//:d use TDuty ugen to control the time points of triggering

// DefaultBuffer.reset; // use the default allwk sound of SC

DefaultBuffer.play({ | bufnum |
	{ | bufnum = 0, samplePos = 0, start = 0.5, end = 1 |
		Out.ar(0, BufRd.ar(1, bufnum, 
			Phasor.ar(
				// TDuty.ar(dur, reset,  level, doneAction) 
				TDuty.ar(Dseq([0.1, 0.5, 0.2, 0.2, 0.2, 0.3], inf), 0, 1),
				BufRateScale.kr(bufnum), // speed is constant
				start * BufFrames.kr(bufnum), // start scaled to fit entire buffer at 1
				end * BufFrames.kr(bufnum), // end scaled to fit entire buffer at 1
				1						// loop on
			)
		));
	}.play;
});

//:m change other parameters at each triggering

/* Here we use the TDuty to provide a trigger for several parameters at once. */

DefaultBuffer.play({ | bufnum |
	{ | bufnum = 0, samplePos = 0, end = 1 |
		var trig, start, rate;
		trig = TDuty.ar(Dseq(({ | i | 1 + i / 2 * ([0.1, 0.5, 0.2, 0.2, 0.2, 0.3] ! 3).flat } ! 15).flat, inf), 0, 1);
		// Demand.ar(trig, reset, [..ugens..])
		#start, rate = Demand.ar(trig, 0, [Dseq([0, 0.1, 0.0, 0.2, 0.0], inf), Dseq([-1, 1, 2, -2, 0.5], inf)]); 
		Out.ar(0, BufRd.ar(1, bufnum, 
			Phasor.ar(
				// TDuty.ar(dur, reset,  level, doneAction) 
				trig,
				rate * BufRateScale.kr(bufnum), // speed is constant
				start * BufFrames.kr(bufnum), // start scaled to fit entire buffer at 1
				end * BufFrames.kr(bufnum), // end scaled to fit entire buffer at 1
				1						// loop on
			)
		));
	}.play;
});



//:_ rest

DefaultBuffer.play({ | bufnum | 
	{ | bufnum |
		Out.ar(0, BufRd.ar(1, bufnum, Phasor.ar(0, BufRateScale.kr(bufnum), 0, BufFrames.kr(bufnum))))
	}.play;
})

DefaultBuffer.play({ | bufnum | 
	{ | bufnum |
		Out.ar(0, BufRd.ar(1, bufnum, Phasor.ar(0, SinOsc.ar(0.1) * BufRateScale.kr(bufnum), 0, BufFrames.kr(bufnum))))
	}.play;
})


DefaultBuffer.play({ | bufnum | 
	{ | bufnum |
		Out.ar(0, BufRd.ar(1, bufnum, Phasor.ar(0, SinOsc.ar(0.1) * BufRateScale.kr(bufnum), 0, BufFrames.kr(bufnum))))
	}.play;
})

DefaultBuffer.play({ | bufnum | 
	{ | bufnum |
		Out.ar(0, BufRd.ar(1, bufnum, Phasor.ar(0, SinOsc.ar(0.1) * BufRateScale.kr(bufnum), 0, BufFrames.kr(bufnum))))
	}.play;
})


