//: Create a class for auto send 
MySynths {
	*sendToServer {
	//:Use microphone
	Udef("pure_clarinet",	
		{
			var in;
			in = In.ar(8);
			Out.ar(0, in);
		}		
	);
	//:Play Buffer v.1
	Udef(\foubuf, {| out = 0, bufnum = 0, rate = 1, trigger = 1, loop = 1, pos = 0, level = 1, windowSize = 0.5, pitchRatio = 1, in , cutoffFreq = 400|
		in = Pan2.ar(PitchShift.ar(                  
	          		PlayBuf.ar(1, bufnum, rate, trigger, 0, loop),
	                  	windowSize,
	                  	pitchRatio
	                 ),
	          	pos,
	             	level
	        	);
	      in = LPF.ar(in, cutoffFreq);
	      Out.ar(out, in);
	});

	//:Play Buffer
	Udef(\foubuf_0, {| out = 0, bufnum = 0, rate = 1, trigger = 1, loop = 1, pos = 0, level = 1, windowSize = 0.5, pitchRatio = 1 |
	        Out.ar(out,
	                Pan2.ar( 
	                        PitchShift.ar(                  
	                                PlayBuf.ar(1, 
	                                        bufnum, 
	                                        rate, 
	                                        trigger, 
	                                        0, 
	                                        loop
	                                ),
	                                windowSize,
	                                pitchRatio
	                        ),
	                        pos,
	                        level
	                )
	        )
	});

	//:PV start synth
	Udef(\pv0, {| out = 0, bufnum = 0, rate = 1, trigger = 1, loop = 1, pos = 0, level = 1, in |
		in =  PlayBuf.ar(1, bufnum, rate, trigger, 0, loop);
	     Out.ar(out,Pan2.ar( in, pos,level))
	});
	//:PV_MagSquared
	Udef(\pv_magSquared, {  |out=0, bufnum=0, soundBufnum=2|
		var in, chain;
		in = PlayBuf.ar(1, soundBufnum, BufRateScale.kr(soundBufnum), loop: 1);
		chain = FFT(LocalBuf(2048), in);
		chain = PV_MagSquared(chain); 
		Out.ar(out, 0.003 * IFFT(chain).dup); 
	});
	
	//:PV_RectComb synth with SinOsc Buffer as in
	Udef(\pv_rectcombSinBuf, {
		|out =0, in, bufnum = 0, rate = 1, trigger = 1, loop = 1, chain, pos = 0, level = 1, numTeeth = 1, sinPhaseFreq = 0, sinPhaseMul = 0, sinWidthMul = 0, sinWidthFreq = 0, sinPhasePhase = 0, sinWidthPhase = 0, sinPhaseAdd = 0.5, sinWidthAdd = 0.5 | 
		in = PlayBuf.ar(1, bufnum, rate, trigger, 0, loop);
		in = WhiteNoise.ar(0.2);
		chain = FFT(LocalBuf(2048), in);
		chain = PV_RectComb(chain, 
							numTeeth, 
							SinOsc.kr(sinPhaseFreq, sinPhasePhase, sinPhaseMul, 0.5), 
							SinOsc.kr(sinWidthFreq, sinWidthPhase, sinWidthMul, 0.5)
		);
	     Out.ar(out,Pan2.ar( IFFT(chain).dup, pos,level));	 
	
	});

	
	//:PV_RectComb synth with SinOsc
	Udef(\pv_rectcombSin, {
		|out =0, in, chain, pos = 0, level = 1, numTeeth = 1, sinPhaseFreq = 0, sinPhaseMul = 0.4, sinWidthMul = -0.5, sinWidthFreq = 0.24, sinPhasePhase = 0, sinWidthPhase = 0, sinPhaseAdd = 0.5, sinWidthAdd = 0.5 | 
		in = WhiteNoise.ar(0.2);
		chain = FFT(LocalBuf(2048), in);
		chain = PV_RectComb(chain, 
							numTeeth, 
							SinOsc.kr(sinPhaseFreq, sinPhasePhase, sinPhaseMul, 0.5), 
							SinOsc.kr(sinWidthFreq, sinWidthPhase, sinWidthMul, 0.5)
		);
	     Out.ar(out,Pan2.ar( IFFT(chain).dup, pos,level));	 
	
	});
	 
	//:PV_RectComb synth with LFTri
	Udef(\pv_rectcombLFTri, {
		|out =0, in, chain, pos = 0, level = 1, numTeeth = 8, lftPhaseFreq = 0.097, lftPhaseMul = 0.4, lftWidthMul = -0.5, lftWidthFreq = 0.24, lftPhasePhase = 0, lftWidthPhase = 0 | 
		in = WhiteNoise.ar(0.2);
		chain = FFT(LocalBuf(2048), in);
		chain = PV_RectComb(chain, 
							numTeeth, 
							LFTri.kr(lftPhaseFreq, lftPhasePhase, lftPhaseMul, 0.5), 
							LFTri.kr(lftWidthFreq, lftWidthPhase, lftWidthMul, 0.5)
		);
	     Out.ar(out,Pan2.ar( IFFT(chain).dup, pos,level));	 
	
	});
	 
	}
}