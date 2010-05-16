/* IZ Saturday, May 17, 2008 
moved several PV synthdefs here and added 8-channel panning versions
*/

[

SynthDef("PV_BinScramble", { | in=0, out=0, bufnum=0, 
	wipe = 0.5, width = 0.2, trigger = 0.0, 
	source_level = 0, fx_level = 1 |
	var source, chain;
	source = In.ar(in);
	chain = FFT(bufnum, source);
	chain = PV_BinScramble(chain, wipe , width,  trigger ); 
	Out.ar(out, Mix.ar([IFFT(chain) * fx_level, source * source_level]));
}, [\kr, \kr, \kr, \kr, \kr, \tr])
,
SynthDef("PV_BinScramble8", { arg in=0, out=0, bufnum=0, 
	wipe = 0.5, width = 0.2, trigger = 0.0,
	source_level = 0, fx_level = 1,
	pos = 0, level = 1, panwidth = 2, orientation = 0.5;
//numChans, in, pos = 0, level = 1, width = 2, orientation = 0.5
	var source, chain;
	source = In.ar(in);
	chain = FFT(bufnum, source);
	chain = PV_BinScramble(chain, wipe , width,  trigger ); 
	Out.ar(out, PanAz.ar(8, Mix.ar([IFFT(chain) * fx_level, source * source_level]),
		pos, level, panwidth, orientation));
}, [\kr, \kr, \kr, \kr, \kr, \tr])
,

SynthDef("PV_BinShift", { | in=0, out=0, bufnum=0,
	// stretch: 0.25 - 4, shift: -128 - 128
	stretch = 1.5, shift = 100,
	source_level = 0, fx_level = 1 |
	var chain, source;
	source = In.ar(in);
	chain = FFT(bufnum, source);
	chain = PV_BinShift(chain, stretch , shift );
	Out.ar(out, Mix.ar([IFFT(chain) * fx_level, source * source_level]))
})
,
SynthDef("PV_BinShift8", { | in=0, out=0, bufnum=0,
	// stretch: 0.25 - 4, shift: -128 - 128
	stretch = 1.5, shift = 100,
	source_level = 0, fx_level = 1,
	pos = 0, level = 1, panwidth = 2, orientation = 0.5 |
	var chain, source;
	source = In.ar(in);
	chain = FFT(bufnum, source);
	chain = PV_BinShift(chain, stretch , shift );
	Out.ar(out, PanAz.ar(8, Mix.ar([IFFT(chain) * fx_level, source * source_level]),
		pos, level, panwidth, orientation));
})
,

SynthDef("PV_ConformalMap", { arg in=0, out=0, bufnum=0, 
	// real: -1.0, 1.0    imag: -1.0, 1.0
	real= 0.5, imag = 0.2;
	var chain;
	chain = FFT(bufnum, In.ar(in));
	chain = PV_ConformalMap(chain, real, imag); 
	Out.ar(out, IFFT(chain));
})
,

SynthDef("PV_LocalMax", { | in=0, out=0, bufnum=0,
	// Threshold: 0 - 50
	threshold=25,
	source_level = 0, fx_level = 1 |
	var chain, source;
	source = In.ar(in);
	chain = FFT(bufnum, source);
	chain = PV_LocalMax(chain,  threshold);
	Out.ar(out, Mix.ar([IFFT(chain) * fx_level, source * source_level]))
}),

SynthDef("PV_LocalMax8", { | in=0, out=0, bufnum=0,
	// Threshold: 0 - 50
	threshold=25,
	source_level = 0, fx_level = 1, 
	pos = 0, level = 1, panwidth = 2, orientation = 0.5 |
	var chain, source;
	source = In.ar(in);
	chain = FFT(bufnum, source);
	chain = PV_LocalMax(chain,  threshold);
	Out.ar(out, PanAz.ar(8, Mix.ar([IFFT(chain) * fx_level, source * source_level]),
		pos, level, panwidth, orientation));
}),

SynthDef("PV_MagAbove", { | in=0, out=0, bufnum=0,
	threshold = 5, // threshold: 0 - 10
	source_level = 0, fx_level = 1 |
	var chain, source;
	source = In.ar(in);
	chain = FFT(bufnum, source);
	chain = PV_BinShift(chain, threshold);
	Out.ar(out, Mix.ar([IFFT(chain) * fx_level, source * source_level]))
}),

SynthDef("PV_MagAbove8", { | in=0, out=0, bufnum=0,
	threshold = 5, // threshold: 0 - 10
	source_level = 0, fx_level = 1,
	pos = 0, level = 1, panwidth = 2, orientation = 0.5 |
	var chain, source;
	source = In.ar(in);
	chain = FFT(bufnum, source);
	chain = PV_BinShift(chain, threshold);
	Out.ar(out, PanAz.ar(8, Mix.ar([IFFT(chain) * fx_level, source * source_level]),
		pos, level, panwidth, orientation));
}),


SynthDef("PV_MagFreeze", { | in = 0, out=0, bufnum = 0, trigger = 0 |
	var chain;
	chain = FFT(bufnum, In.ar(in));
	chain = PV_MagFreeze(chain, trigger); 
	Out.ar(out, IFFT(chain));
}),

SynthDef("PV_MagFreeze8", { | in = 0, out=0, bufnum = 0, trigger = 0,
	pos = 0, level = 1, panwidth = 2, orientation = 0.5 |
	var chain;
	chain = FFT(bufnum, In.ar(in));
	chain = PV_MagFreeze(chain, trigger); 
	Out.ar(out, PanAz.ar(8, IFFT(chain), pos, level, panwidth, orientation));
}),

SynthDef("PV_MagMul", { arg in = 0, out = 0, bufnumA = 0, bufnumB = 1;
	var inA, chainA, inB, chainB, chain;
	inA = In.ar(in);
	chainA = FFT(bufnumA, inA);
	chain = PV_MagMul(chainA, bufnumB); 
	Out.ar(out, 1.5 * IFFT(chain));
}),

SynthDef("PV_MagMul8", { arg in = 0, out = 0, bufnumA = 0, bufnumB = 1,
	pos = 0, level = 1, panwidth = 2, orientation = 0.5;
	
	var inA, chainA, inB, chainB, chain;
	inA = In.ar(in);
	chainA = FFT(bufnumA, inA);
	chain = PV_MagMul(chainA, bufnumB); 
	Out.ar(out, PanAz.ar(8, 1.5 * IFFT(chain), pos, level, panwidth, orientation));
//	Out.ar(out, 1.5 * IFFT(chain));
}),

SynthDef("PV_MagSmear", { arg in=0, out=0, bufnum=0, 
	// bins - (0 - 100) number of bins to average on each side of bin. As this number rises, so will CPU usage.
	bins=50;
	
	var chain;
	chain = FFT(bufnum, In.ar(in));
	chain = PV_MagSmear(chain, bins ); 
	Out.ar(out, IFFT(chain));
}),

SynthDef("PV_MagSmear8", { arg in=0, out=0, bufnum=0, 
	// bins - (0 - 100) number of bins to average on each side of bin. As this number rises, so will CPU usage.
	bins=50,
	pos = 0, level = 1, panwidth = 2, orientation = 0.5;
	var chain;
	chain = FFT(bufnum, In.ar(in));
	chain = PV_MagSmear(chain, bins ); 
	Out.ar(out, PanAz.ar(8, IFFT(chain), pos, level, panwidth, orientation));
}),

SynthDef("PV_RectComb", { | in=0, out=0, bufnum=0,
	// numTeeth: 0- 32, phase:          , width:
	numTeeth = 15, phase = 0.5, width=0.2,
	source_level = 0, fx_level = 1 |
	var chain, source;
//	in = {WhiteNoise.ar(0.2)}.dup;
//	chain = FFT(bufnum, in);
	source = In.ar(in);
 	chain = FFT(bufnum, source);
	chain = PV_RectComb(chain,  numTeeth, phase, width);
	Out.ar(out, Mix.ar([IFFT(chain) * fx_level, source * source_level]))
}),

SynthDef("PV_RectComb8", { | in=0, out=0, bufnum=0,
	// numTeeth: 0- 32, phase:          , width:
	numTeeth = 15, phase = 0.5, width=0.2,
	source_level = 0, fx_level = 1,
	pos = 0, level = 1, panwidth = 2, orientation = 0.5 |
	
	var chain, source;
//	in = {WhiteNoise.ar(0.2)}.dup;
//	chain = FFT(bufnum, in);
	source = In.ar(in);
 	chain = FFT(bufnum, source);
	chain = PV_RectComb(chain,  numTeeth, phase, width);
	Out.ar(out, PanAz.ar(8, Mix.ar([IFFT(chain) * fx_level, source * source_level]),
		pos, level, panwidth, orientation));
})


]