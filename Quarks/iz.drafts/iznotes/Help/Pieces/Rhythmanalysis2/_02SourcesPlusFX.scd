//: Load all buffers
"/Users/iani2/Music/sounds/osmosis_sounds_wav/*".pathMatch do: { | p |
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
{ | amp = 0.2 | PlayBuf.ar(1, \counting.buffer, loop: 1) * amp };

//:countingpan
{ | pos = 0, level = 1, width = 2 | PanAz.ar(6, ~counting.ar, pos, level, width); };

//:countingbshift PV_BinShift
{ | amp = 1, stretch = 1, shift = 0 |
	var in, chain;
	in = ~counting.ar;
	chain = FFT(LocalBuf(2048), in);
	 chain = PV_BinShift(chain, stretch, shift);
	amp * IFFT(chain);
}

//:
~countingbshift.set(\stretch, 2, \shift, 10);


//:countingbshiftstretch
{ LFNoise2.kr(2).range(0.05, 0.4) }


//:

~countingbshift.map(\stretch, ~countingbshiftstretch);

//:swallows without pan, without amp

{ | buf, ampThreshold = 0.02, transpose = 2, srcAmp = 0, fxAmp = 1 |
	var in, amp, freq, hasFreq, out;
	in = PlayBuf.ar(1, \swallows.buffer, loop: 1);
	amp = Amplitude.kr(in, 0.05, 0.05);
	#freq, hasFreq = Pitch.kr(in, ampThreshold: ampThreshold, median: 7);
	out = CombC.ar(LPF.ar(in, 1000), 0.1, (transpose * freq).reciprocal, -6).distort * 0.05;
	out = out * hasFreq;
	6.do({ out = AllpassN.ar(out, 0.040, [0.040.rand,0.040.rand], 2) });
	[out * Lag.kr(fxAmp, 1), in * Lag.kr(srcAmp, 1)];
};

//:swallows
~swallows.set(\fxAmp, 0.4);
~swallows.set(\srcAmp, 0.005);
~swallows.set(\srcAmp, 0.000);

//:---

~swallows.end;

//:weddell without pan, with amp

{ | buf, ampThreshold = 0.02, transpose = 0.2, srcAmp = 0, fxAmp = 1, flag = 0 |
	var in, amp, freq, hasFreq, out;
	in = PlayBuf.ar(1, \weddell1L.buffer, loop: 1);
	amp = Amplitude.kr(in, 0.05, 0.05);
	#freq, hasFreq = Pitch.kr(in, ampThreshold: ampThreshold, median: 7);
	freq = Lag.kr(freq, flag);
Ê	out = CombC.ar(LPF.ar(in, 1000), 0.1, (transpose * freq).reciprocal, -6).distort * 0.05;
	out = out * amp;
	6.do({ out = AllpassN.ar(out, 0.040, 0.040.rand, 2) });
	[out * fxAmp, in * srcAmp];
};

//:aswallows without pan, with amp

{ | buf, ampThreshold = 0.02, transpose = 1, srcAmp = 0, fxAmp = 1 |
	var in, amp, freq, hasFreq, out;
	in = PlayBuf.ar(1, \swallows.buffer, loop: 1);
	amp = Amplitude.kr(in, 0.05, 0.05);
	#freq, hasFreq = Pitch.kr(in, ampThreshold: ampThreshold, median: 7);
	freq = Lag.kr(freq, 0);
Ê	out = CombC.ar(LPF.ar(in, 1000), 0.1, (transpose * freq).reciprocal, -6).distort * 0.05;
	out = out * amp * 10;
	6.do({ out = AllpassN.ar(out, 0.040, 0.040.rand, 2) });
	[out * fxAmp, in * srcAmp];
};

//:aswallows without pan, with amp, freqlag, fxAmplag, srcAmplag

{ 	|
		buf, ampThreshold = 0.02, transpose = 1, srcAmp = 0, fxAmp = 1,
		flag = 0, srcAmplag = 1, fxAmplag = 1
	
	|
	var in, amp, freq, hasFreq, out;
	in = PlayBuf.ar(1, \swallows.buffer, loop: 1);
	amp = Amplitude.kr(in, 0.05, 0.05);
	#freq, hasFreq = Pitch.kr(in, ampThreshold: ampThreshold, median: 7);
	freq = Lag.kr(freq, flag);
Ê	out = CombC.ar(LPF.ar(in, 1000), 0.1, (transpose * freq).reciprocal, -6).distort * 0.05;
	out = out * amp * 10;
	6.do({ out = AllpassN.ar(out, 0.040, 0.040.rand, 2) });
	[out * Lag.kr(fxAmp, fxAmplag), in * Lag.kr(srcAmp, srcAmplag];
};

//:pswallows with PanAz, amp, freqlag, fxAmplag, srcAmplag

{ 	|
		buf, ampThreshold = 0.02, transpose = 1, srcAmp = 0, fxAmp = 1,
		flag = 0, srcAmplag = 1, fxAmplag = 1,
		fpos = 0, fwidth = 2, spos = 0, swidth = 2
	|
	var in, amp, freq, hasFreq, out;
	in = PlayBuf.ar(1, \swallows.buffer, loop: 1);
	amp = Amplitude.kr(in, 0.05, 0.05);
	#freq, hasFreq = Pitch.kr(in, ampThreshold: ampThreshold, median: 7);
	freq = Lag.kr(freq, flag);
Ê	out = CombC.ar(LPF.ar(in, 1000), 0.1, (transpose * freq).reciprocal, -6).distort * 0.05;
	out = out * amp * 10;
	6.do({ out = AllpassN.ar(out, 0.040, 0.040.rand, 2) });
	PanAz.ar(6, out * Lag.kr(fxAmp, fxAmplag), fpos, 1, fwidth);
};

//:setting pos
~pswallows.set(\fpos, 0.15);


//:p1transpose // mapping freq

{ LFNoise0.kr(1).range(0.2, 3) }

//: map it here

~pswallows.map(\transpose, ~p1transpose);

//:counting with PanAz, amp, freqlag, fxAmplag, srcAmplag

{ 	|
		buf, ampThreshold = 0.02, transpose = 1, srcAmp = 0, fxAmp = 1,
		flag = 0, srcAmplag = 1, fxAmplag = 1,
		fpos = 0, fwidth = 2, spos = 0, swidth = 2
	|
	var in, amp, freq, hasFreq, out;
	in = PlayBuf.ar(1, \counting.buffer, loop: 1);
	amp = Amplitude.kr(in, 0.05, 0.05);
	#freq, hasFreq = Pitch.kr(in, ampThreshold: ampThreshold, median: 7);
	freq = Lag.kr(freq, flag);
Ê	out = CombC.ar(LPF.ar(in, 1000), 0.1, (transpose * freq).reciprocal, -6).distort * 0.05;
	out = out * amp * 10;
	6.do({ out = AllpassN.ar(out, 0.040, 0.040.rand, 2) });
	PanAz.ar(6, out * Lag.kr(fxAmp, fxAmplag), fpos, 1, fwidth);
};
//:

~counting.set(\flag, 5);


//:counting with PanAz, amp, freqlag, fxAmplag, srcAmplag

{ 	|
		buf, ampThreshold = 0.02, transpose = 1, srcAmp = 0, fxAmp = 1,
		flag = 0, srcAmplag = 1, fxAmplag = 1,
		fpos = 0, fwidth = 2, spos = 0, swidth = 2
	|
	var in, amp, freq, hasFreq, out;
	in = PlayBuf.ar(1, \counting.buffer, loop: 1);
	amp = Amplitude.kr(in, 0.05, 0.05);
	#freq, hasFreq = Pitch.kr(in, ampThreshold: ampThreshold, median: 7);
	freq = Lag.kr(freq, flag);
Ê	out = CombC.ar(LPF.ar(in, 1000), 0.1, (transpose * freq).reciprocal, -6).distort * 0.05;
	out = out * amp * 10;
	6.do({ out = AllpassN.ar(out, 0.040, 0.040.rand, 2) });
	PanAz.ar(6, out * Lag.kr(fxAmp, fxAmplag), fpos, 1, fwidth);
};



