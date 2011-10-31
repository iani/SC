/* iz Saturday; October 25, 2008: 11:53 AM
4 plain control-rate lf-noise generators: LFDNoise0, LFDNoise1, LFDNoise3, LFDClipNoise
+ some fixed shape generators: 
sine, saw, triangular, pulse, step

In 2 versions: (1) range limited between hi and lo and (2) range set by mul / amp

/// TESTS: 
a = { | freq = 440 | SinOsc.ar(freq, 0, 0.1) }.play;
b = Synth("k_stepR", [\k_out, 0, \rate, 5, \lo, 440, \hi, 500]);

a.map(\freq, 0);
Server.local.queryAllNodes


a = { | freq = 440 | SinOsc.ar(freq + 50, 0, 0.1) }.play;
a.map(\freq, 0);
Server.local.queryAllNodes

b = SynthDef("help-Stepper",{ arg out=0;
	var ctl;
	ctl = Stepper.kr(Impulse.kr(10), 0, 4, 16, 1) * 100;
	Out.kr(0, ctl);
	Out.ar(out,
		SinOsc.ar(
			ctl, 
			0, 0.05
		)
	)
}).play;

c = Synth("k_stepR", [\lo, 300, \hi, 700, \rate, 10, \step, 10])
c.set(\lo, 300);
c.set(\hi, 700);
c.set(\rate, 2);
c.set(\step, 50);

*/

[
/* versions using hi-lo to define the range of the output (spares having to infer it from mul and add)
Note 1: If lo is larger than hi, then it becomes hi and hi becomes lo!
Note 2: The versions using mul and add directly are at the end of this file.
*/
SynthDef('k_lfdnoise0R', { | k_out = 0, freq = 1, lo = 0, hi = 1 |
	Out.kr(k_out, LFDNoise0.kr(freq).range(lo, hi));
}),
SynthDef('k_lfdnoise1R', { | k_out = 0, freq = 1, lo = 0, hi = 1 |
	Out.kr(k_out, LFDNoise1.kr(freq).range(lo, hi));
}),
SynthDef('k_lfdnoise3R', { | k_out = 0, freq = 1, lo = 0, hi = 1 |
	Out.kr(k_out, LFDNoise3.kr(freq).range(lo, hi));
}),
SynthDef('k_lfdclipnoiseR', { | k_out = 0, freq = 1, lo = 0, hi = 1 |
	Out.kr(k_out, LFDClipNoise.kr(freq).range(lo, hi));
}),
SynthDef('k_sinR', { | k_out = 0, freq = 1, phase = 0, lo = 0, hi = 1 |
	Out.kr(k_out, SinOsc.kr(freq, phase).range(lo, hi));
}),
SynthDef('k_sawR', { | k_out = 0, freq = 1, phase = 0, lo = 0, hi = 1 |
	Out.kr(k_out, LFSaw.kr(freq, phase).range(lo, hi));
}),
SynthDef('k_triR', { | k_out = 0, freq = 1, phase = 0, lo = 0, hi = 1 |
	Out.kr(k_out, LFTri.kr(freq, phase).range(lo, hi));
}),
SynthDef('k_pulseR', { | k_out = 0, freq = 1, phase = 0, width = 0.5, lo = 0, hi = 1 |
	Out.kr(k_out, LFPulse.kr(freq, phase, width).range(lo, hi));
}),
SynthDef('k_stepR', { | k_out = 0, rate = 1, step = 0.01, lo = 0, hi = 1 |
	// only has an R version since Stepper UGen comes directly with min - max inputs
	var realLo, realHi;
	realLo = lo min: hi;
	realHi = lo max: hi;
	Out.kr(k_out, Stepper.kr(Impulse.kr(rate), 0, realLo, realHi, step));
}),

// ------------------------- versions using mul and add directly: -------------------------
SynthDef('k_lfdnoise0', { | k_out = 0, freq = 1, mul = 1, add = 0 |
	Out.kr(k_out, LFDNoise0.kr(freq, mul, add));
}),
SynthDef('k_lfdnoise1', { | k_out = 0, freq = 1, mul = 1, add = 0 |
	Out.kr(k_out, LFDNoise1.kr(freq, mul, add));
}),
SynthDef('k_lfdnoise3', { | k_out = 0, freq = 1, mul = 1, add = 0 |
	Out.kr(k_out, LFDNoise3.kr(freq, mul, add));
}),
SynthDef('k_lfdclipnoise', { | k_out = 0, freq = 1, mul = 1, add = 0 |
	Out.kr(k_out, LFDClipNoise.kr(freq, mul, add));
}),
SynthDef('k_sin', { | k_out = 0, freq = 1, phase = 0, mul = 1, add = 0 |
	Out.kr(k_out, SinOsc.kr(freq, phase, mul, add));
}),
SynthDef('k_saw', { | k_out = 0, freq = 1, phase = 0, mul = 1, add = 0 |
	Out.kr(k_out, LFSaw.kr(freq, phase, mul, add));
}),
SynthDef('k_tri', { | k_out = 0, freq = 1, phase = 0, mul = 1, add = 0 |
	Out.kr(k_out, LFTri.kr(freq, phase, mul, add));
}),
SynthDef('k_pulse', { | k_out = 0, freq = 1, phase = 0, width = 0.5, mul = 1, add = 0 |
	Out.kr(k_out, LFPulse.kr(freq, phase, width, mul, add));
})
]
