//:Buffers
BufferResource.loadPaths(
	"/Users/iani2/Music/sounds/osmosis_sounds_wav/seals_normalized/*.wav".pathMatch
);
BufferResource.loadPaths(
	"/Users/iani2/Music/sounds/osmosis_sounds_wav/swallows/*.wav".pathMatch
);
BufferResource.loadPaths(
	"/Users/iani2/Music/sounds/osmosis_sounds_wav/numbers/*.aiff".pathMatch
);

//:List of buffers loaded

BufferResource.postNames;

/* 

'weddell3L', 'weddell3R', 'weddell2L', 'strich', 'swallows', 
'weddell4R', 'weddell1R', 'weddell2R', 'russian', 'weddell1L', 'weddell4L', 'counting' 

*/

//:proxyspacetest3
{ 
	|
		buf, ampThreshold = 0.02, transpose = 2, srcAmp = 0, fxAmp = 1, 
		srcpos = 0, srcwidth = 2, 
		fxpos = 0, fxwidth = 2,
		clar = 0
	|
	var in, amp, freq, hasFreq, out;
	in = PlayBuf.ar(1, \swallows.buffer, loop: 1);
	amp = Amplitude.kr(in, 0.05, 0.05);
	#freq, hasFreq = Pitch.kr(in, ampThreshold: ampThreshold, median: 7, clar: clar);
	out = CombC.ar(LPF.ar(in, 1000), 0.1, (transpose * freq).reciprocal, -6).distort * 0.05;
	out = out * hasFreq;
	6.do({ out = AllpassN.ar(out, 0.040, Rand(0.0001, 0.04), 2) });
	
	[out * fxAmp, in * srcAmp];
}
//:
~proxyspacetest3.set(\ampThreshold, 1);
~proxyspacetest3.set(\ampThreshold, 5);
~proxyspacetest3.set(\fxAmp, 5);
~proxyspacetest3.set(\clar, 5);


//:proxyspacetest3
{ 
	|
		buf, ampThreshold = 0.02, transpose = 2, srcAmp = 0, fxAmp = 1, 
		srcpos = 0, srcwidth = 2, 
		fxpos = 0, fxwidth = 2,
		clar = 0
	|
	var in, amp, freq, hasFreq, out;
//	in = SinOsc.ar(400, 0, 0.1);
	in = PlayBuf.ar(1, \swallows.buffer);	
	amp = Amplitude.kr(in, 0.05, 0.05);
	#freq, hasFreq = Pitch.kr(in, median: 1, clarity: 1);
	out = LFSaw.ar(transpose * freq, 0, 0.1);
	out = out * hasFreq;
	
	[out, in];
}
//:
~proxyspacetest3.set(\ampThreshold, 1);
~proxyspacetest3.set(\ampThreshold, 5);
~proxyspacetest3.set(\fxAmp, 5);
~proxyspacetest3.set(\clar, 5);


