//: Load all buffers
(Platform.userAppSupportDir +/+ "sounds/osmosis_sounds_wav/*").pathMatch do: { | p |
	BufferResource.loadPaths((p +/+ "*").pathMatch); 	
};

//: Set Global fadeTime
currentEnvironment.fadeTime = 3;

//:Buffer names: 
BufferResource.postNames;

/* // selecting alternately left and right tracks for 4 weddell seals
[ counting, russian, strich, swallows, weddell1L, weddell2R, weddell3L, weddell4R ]

*/


~counting.set(\amp, 0.0);

// ==================== COUNTING =====================
//:counting
currentEnvironment.fadeTime = 3; // initialize global fading time at this stage

{ | amp = 0.2 | PlayBuf.ar(1, \counting.buffer, loop: 1) * amp };

//:countingpan
{ | pos = 0, level = 1, width = 2 | PanAz.ar(2, ~counting.ar, pos, level, width); };

//:countingmove
{ LFNoise2.kr(0.1) }

//:map countingpan to countingmove
~countingpan.map(\pos, ~countingmove);

//:countingbshift PV_BinShift
{ | amp = 1, stretch = 1, shift = 0 |
	var in, chain;
	in = ~counting.ar;
	chain = FFT(LocalBuf(2048), in);
	chain = PV_BinShift(chain, stretch, shift);
	amp * IFFT(chain);
}
//:countingbshiftstretch
{ LFNoise2.kr(2).range(0.05, 0.4) }
//:-----
~countingbshift.map(\stretch, ~countingbshiftstretch);


//================ SWALLOWS ================

// ====== BASIC ======
//:swallows
{ | amp = 0.2 | PlayBuf.ar(1, \swallows.buffer, loop: 1) * amp };

// ====== PAN ======
//:swallowspan
{ | pos = 0, level = 1, width = 2 | PanAz.ar(2, ~swallows.ar, pos, level, width); };

//:swallowsmove
{ LFNoise2.kr(0.1) }

//:map countingpan to countingmove
~swallowspan.map(\pos, ~swallowsmove);

// ======= PITCH + PAN =========
//:pswallows with PanAz, amp, freqlag, fxAmplag, srcAmplag
{ 	|
		buf, ampThreshold = 0.02, transpose = 1, srcAmp = 0, fxAmp = 1,
		flag = 0, srcAmplag = 1, fxAmplag = 1,
		fpos = 0, fwidth = 2, spos = 0, swidth = 2
	|
	var in, amp, freq, hasFreq, out;
//	in = PlayBuf.ar(1, \swallows.buffer, loop: 1);
	in = ~swallows.ar * 5;

	amp = Amplitude.kr(in, 0.05, 0.05);
	#freq, hasFreq = Pitch.kr(in, ampThreshold: ampThreshold, median: 7);
	freq = Lag.kr(freq, flag);
Ê	out = CombC.ar(LPF.ar(in, 1000), 0.1, (transpose * freq).reciprocal, -6).distort * 0.05;
	out = out * amp * 150;
	6.do({ out = AllpassN.ar(out, 0.040, 0.040.rand, 2) });
	PanAz.ar(2, out * Lag.kr(fxAmp, fxAmplag), fpos, 1, fwidth);
};

//:pswallowsmove
{ LFNoise2.kr(0.2) }

//:map countingpan to countingmove
~pswallows.map(\fpos, ~pswallowsmove);

//:pswallowstpose
{ LFNoise0.kr(0.2).range(0.2, 2) }

//:map swallowstranspose
~pswallows.map(\transpose, ~pswallowstpose);

//================ SEALS ================

//==== PLAIN ====
//:weddell
{ | amp = 0.5 | PlayBuf.ar(1, \weddell.buffer, loop: 1) * amp };


//:pweddell /// panning
{ | amp = 0.5, speed = 0.2 |
	var src;
	src = PlayBuf.ar(1, \weddell.buffer, loop: 1) * amp;
	PanAz.ar(2, src, LFNoise2.kr(speed));
};

//:
~pweddell.set(\speed, 1);

//:wedabove magabove
{ | amp = 0.3, speed = 0.2, above = 0.1 |
	var in, chain;
	in = PlayBuf.ar(1, \weddell.buffer, loop: 1);
	chain = FFT(LocalBuf(2048), in);
	chain = PV_MagAbove(chain, above);
	PanAz.ar(2, IFFT(chain) * amp, LFNoise2.kr(speed));
};

//:
~wedabove.set(\above, 0.2);

//:wedbelow magbelow
{ | amp = 0.8, speed = 0.2, below = 0.1 |
	var in, chain;
	in = PlayBuf.ar(1, \weddell.buffer, loop: 1);
	chain = FFT(LocalBuf(2048), in);
	chain = PV_MagBelow(chain, below);
	PanAz.ar(2, IFFT(chain) * amp, LFNoise2.kr(speed));
};


//:
~wedbelow.set(\below, 0.5);

//=========== ADDING MAGABOVE + MAGBELOW SWALLOWS + COUNTING


//:swalabove magabove
{ | amp = 0.3, speed = 0.2, above = 0.1 |
	var in, chain;
	in = PlayBuf.ar(1, \swallows.buffer, loop: 1);
	chain = FFT(LocalBuf(2048), in);
	chain = PV_MagAbove(chain, above);
	PanAz.ar(2, IFFT(chain) * amp, LFNoise2.kr(speed));
};

//:
~swalabove.set(\above, 1.5);

//:
~swalabove.set(\above, 10.5);

//:
~swalabove.set(\amp, 0.25);


//:swalbelow magbelow
{ | amp = 0.8, speed = 0.2, below = 0.1 |
	var in, chain;
	in = PlayBuf.ar(1, \swallows.buffer, loop: 1);
	chain = FFT(LocalBuf(2048), in);
	chain = PV_MagBelow(chain, below);
	PanAz.ar(2, IFFT(chain) * amp, LFNoise2.kr(speed));
};


//:
~swalbelow.set(\below, 0.25);


//:countabove magabove
{ | amp = 0.3, speed = 0.2, above = 50 |
	var in, chain;
	in = PlayBuf.ar(1, \counting.buffer, loop: 1);
	chain = FFT(LocalBuf(2048), in);
	chain = PV_MagAbove(chain, above);
	PanAz.ar(2, IFFT(chain) * amp, LFNoise2.kr(speed));
};

//:
~countlabove.set(\above, 1.5);

//:
~countabove.set(\above, 50.5);

//:
~countabove.set(\amp, 0.25);


//:countbelow magbelow
{ | amp = 0.8, speed = 0.2, below = 1 |
	var in, chain;
	in = PlayBuf.ar(1, \counting.buffer, loop: 1);
	chain = FFT(LocalBuf(2048), in);
	chain = PV_MagBelow(chain, below);
	PanAz.ar(2, IFFT(chain) * amp, LFNoise2.kr(speed));
};


//:
~countbelow.set(\below, 0.25);
//:
~countbelow.set(\amp, 0.25);

