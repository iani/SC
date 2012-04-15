/* IZ 20120415

Towards triggering sequences of grains whose parameters are stored in buffers

7 parameters that can be controlled with buffer data: 

onsets 		: dtime between onsets of grains
rates		: rate of each grain (constant within the grain)
centers		: centers of the grains
durations		: durations of the grains
pan			: pan "
amp			: amp "
att			: attack time of grains
dec			: decay time of grains

In the example below only the first 4 parameters are variable. 

*/

//:Prepare buffers
// Load default sound buffers
BufferResource.loadSCdefaults;
//Prepare buffer for rates
[\onsets, \centers, \durations, \rates] do: { | b |
	BufferResource(b, nil, 10000).sendCollection(Array.rand(10000, 0.9, 1.1));
};

//:1: trigger a single grain
a = 'a11wlk01'.playBuf({ | buf = 0, length = 1, 
	onsets = 0, centers = 0, durations = 0, rates = 0, reset = 0 |
	var trig;
	reset = Slope.kr(reset) > 1;
	trig = TDuty.kr(Dseq([0, Dbufrd(onsets, Dseries(0, 1, length - 1), 1)], 1), reset, 1, 0, 1);

	TGrains2.ar(
		2,		// at least 2 output channels required
		trig,	// trigger for grain production 
		buf,		// buffer
		Demand.kr(trig, reset, Dbufrd(rates, Dseries(0, 1, length), 1)), // rate
		Demand.kr(trig, reset, Dbufrd(centers, Dseries(0, 1, length), 1)), // grain center
		Demand.kr(trig, reset, Dbufrd(durations, Dseries(0, 1, length), 1)), // duration
		0,		// pan
		1,		// amp
		0.01,	// attack time of grain in seconds
		0.01,	// decay time of grain in seconds
	)
}, args: [length: 5, onsets: \onsets.buffer, centers: \centers.buffer, rates: \rates.buffer]);

//:retrigger with new length
a.set(\length, 12.rand + 1, \reset, UniqueID.next);

//:Verify that length of 1 is valid:
a.set(\length, 1, \reset, UniqueID.next);

//:set the centers to move along a sine curve
\centers.sendCollection((0, 0.01 .. 2pi)


