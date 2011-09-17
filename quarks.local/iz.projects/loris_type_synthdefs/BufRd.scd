

//: ! read a whole sound into memory
// note: not *that* columbia, the first one
Server.default.waitForBoot({
	b = Buffer.read(path: "sounds/a11wlk01.wav", bufnum: 0).postln;
});

//:a test the buffer

b.play;

//:x load some extra sounds: 
Buffers.load(*[ 'tcp_d1_01_the_swedish_rhapsody_irdial', 'tcp_d1_03_counting_control_irdial', 'tcp_d1_02_counting_cia_irdial', 'tcp_d1_04_phonetic_alphabet_nato_irdial', 'tcp_d1_05_5_dashes_irdial', 'tcp_d1_06_the_lincolnshire_poacher_mi5_irdial' ])

//:b reading parts of the buffer with demand / TDuty ugens driving BufRd + Phasor. 

// BufRd.ar(numChannels, bufnum, phase, loop, interpolation)
// 	Phasor.ar(trig, rate, start, end, resetPos)


/*
a = { Out.ar(0, BufRd.ar(1, b.bufnum, Phasor.ar(0, BufRateScale.kr(b), 0, BufFrames.kr(b)), 1, 0)) }.play; 
*/

a = { Out.ar(0, BufRd.ar(1, b.bufnum, 
	Phasor.ar(TDuty.ar(
		Dseq([0.1, 0.2, 0.4, 0.3] * 5, inf)), // timing of the reset of the phasor: restart loop
		BufRateScale.kr(b), 				 // rate of playback
		0, 								 // start from the beginning
		BufFrames.kr(b)					 // play till the end (unless reset)
	), 1, 0)) 
}.play; 

// {ÊTDuty.ar(Dseq([0.1, 0.2, 0.4, 0.3], inf)) }.play; // demand ugen as durations

//:c extending the above to change rate and points of start and end at each retrigger. 

// Demand.ar(trig, reset, [..ugens..])


c = Buffers.at(\tcp_d1_03_counting_control_irdial);
a = {
	var trig, rate, rate_pattern, start, start_pattern, patterns;
	trig = TDuty.ar(Dseq([0.1, 0.2, 0.4, 0.3] * 25, inf));
	rate_pattern = Dseq([1, 1.5, -1, -1.5, 1, 1.9, -0.5], inf);
	start_pattern = Dseq([0, 0.1, 0.2, 0.3], inf);
	patterns = Demand.ar(trig, 0, [rate_pattern, start_pattern]);
	rate = patterns[0];
	start =  patterns[1];
	Out.ar(0, BufRd.ar(1, c.bufnum.postln, 
	Phasor.ar(
		trig, 							// timing of the reset of the phasor: restart loop
		rate * BufRateScale.kr(c), 			// rate of playback
		start * BufFrames.kr(c), 			// start controlled by start_pattern
		(start + 0.9).fold(1), BufFrames.kr(c)	// play end is start offset by 0.9
	), 1, 0)) 
}.play; 

/*
What form could a playlist-score take for the above?
[
	[dur, rate, startpoint],
	[dur, rate, startpoint],
	[dur, rate, startpoint],
]

or:

[
	[startpoint, endpoint, rate],
	[startpoint, endpoint, rate],
	[startpoint, endpoint, rate],
]

*/

//:e playing random extracts. 

// Demand.ar(trig, reset, [..ugens..])


c = Buffers.at(\tcp_d1_05_5_dashes_irdial);
a = {
	var trig, rate, rate_pattern, start, start_pattern;
	trig = TDuty.ar(Dseq([2, 3, 4, 5, 6] / 20, inf));
	rate_pattern = Dseq([1, 1, -1], inf);
	start_pattern = Dseq((0, 0.01..1).reverse, inf);
	#rate, start = Demand.ar(trig, 0, [rate_pattern, start_pattern]);
	Out.ar(0, BufRd.ar(1, c.bufnum.postln, 
	Phasor.ar(
		trig, 							// timing of the reset of the phasor: restart loop
		rate * BufRateScale.kr(c), 			// rate of playback
		start * BufFrames.kr(c), 			// start controlled by start_pattern
		(start + 0.9).fold(1), BufFrames.kr(c)	// play end is start offset by 0.9
	), 1, 0)) 
}.play; 
