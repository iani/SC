/* IZ Friday, May 16, 2008 
Adapted from help-magMul and from example sent by Scott Wilson for filtering a sound via a buffer containing frequency band (bin) amplitudes. 
*/
/*
SynthDef("PV_MagMul", { arg in = 0, out = 0, bufnumA = 0, bufnumB = 1;
	var inA, chainA, inB, chainB, chain;
	inA = In.ar(in);
	chainA = FFT(bufnumA, inA);
	chain = PV_MagMul(chainA, bufnumB); 
	Out.ar(out, 1.5 * IFFT(chain));
})
*/