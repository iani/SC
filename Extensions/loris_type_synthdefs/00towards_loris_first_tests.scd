
//:a basic Demand

SynthDef("test", { 
	var source, controls;
	var trig, freq, freq_pattern;
	trig = TDuty.ar(Dseq([1], 6), doneAction: 2);
	freq_pattern = Dseq([400, 800], inf);
	controls = Demand.ar(trig, 0, [freq_pattern]);
//	source = SinOsc.ar(440, 0, 0.1);
	source = SinOsc.ar(controls, 0, 0.1);
	
	Out.ar(0, source);
}).load(Server.local);


//:b test it
a = Synth("test");

//:c with phasor

SynthDef("test", { 
	var source, controls;
	var trig, freq, freq_pattern;
	trig = TDuty.ar(Dseq([1], 6), doneAction: 2);
	freq_pattern = Dseq([400, 800], inf);
	controls = Demand.ar(trig, 0, [freq_pattern]);
//	source = SinOsc.ar(440, 0, 0.1);
	source = SinOsc.ar(controls, 0, 0.1);
	
	Out.ar(0, source);
}).load(Server.local);




/*
	Phasor.ar(
		trig, 							// timing of the reset of the phasor: restart loop
		rate * BufRateScale.kr(c), 			// rate of playback
		start * BufFrames.kr(c), 			// start controlled by start_pattern
		(start + 0.9).fold(1), BufFrames.kr(c)	// play end is start offset by 0.9
	), 1, 0)) 
*ar(trig, rate, start, end, resetPos)

*/

//:d test it
a = Synth("test");
