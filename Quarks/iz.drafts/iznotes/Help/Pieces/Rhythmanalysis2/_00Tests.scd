a = SynthDef("pitchFollow2", { | buf, ampThreshold = 0.02, transpose = 2, srcAmp = 0, fxAmp = 1 |
Ê Ê 	var in, amp, freq, hasFreq, out;
Ê Ê in = PlayBuf.ar(1, buf, loop: 1);
Ê Ê amp = Amplitude.kr(in, 0.05, 0.05);
Ê Ê # freq, hasFreq = Pitch.kr(in, ampThreshold: ampThreshold, median: 7);
Ê Ê out = CombC.ar(LPF.ar(in, 1000), 0.1, (transpose * freq).reciprocal, -6).distort * 0.05;
	out = out * hasFreq;
Ê Ê 6.do({
Ê Ê Ê Ê out = AllpassN.ar(out, 0.040, [0.040.rand,0.040.rand], 2)
Ê Ê });
Ê Ê Out.ar(0, [out * fxAmp, in * srcAmp]);
//Ê Ê Out.ar(0, PlayBuf.ar(1, buf, loop: 1));
	
}).play(Server.default, args: [\buf, \swallows.buffer]);


//:

{ | buf, ampThreshold = 0.02, transpose = 2, srcAmp = 0, fxAmp = 1 |
Ê Ê 	var in, amp, freq, hasFreq, out;
Ê Ê in = PlayBuf.ar(1, \swallows.buffer, loop: 1);
Ê Ê amp = Amplitude.kr(in, 0.05, 0.05);
Ê Ê # freq, hasFreq = Pitch.kr(in, ampThreshold: ampThreshold, median: 7);
Ê Ê out = CombC.ar(LPF.ar(in, 1000), 0.1, (transpose * freq).reciprocal, -6).distort * 0.05;
	out = out * hasFreq;
Ê Ê 6.do({
Ê Ê Ê Ê out = AllpassN.ar(out, 0.040, [0.040.rand,0.040.rand], 2)
Ê Ê });
Ê Ê Out.ar(0, [out * fxAmp, in * srcAmp]);
//Ê Ê Out.ar(0, PlayBuf.ar(1, buf, loop: 1));
	
};


//:Back to the synthdef

SynthDef("pitchFollow2",{
	var in, amp, freq, hasFreq, out;
//Ê Ê in = Mix.new(SoundIn.ar([0,1]));
	in = PlayBuf.ar(1, \swallows.buffer);
	amp = Amplitude.kr(in, 0.05, 0.05);
	#freq, hasFreq = Pitch.kr(in, ampThreshold: 0.02, median: 7);
	out = CombC.ar(LPF.ar(in, 1000), 0.1, (2 * freq).reciprocal, -6).distort * 0.05;
	6.do({
		out = AllpassN.ar(out, 0.040, [0.040.rand,0.040.rand], 2)
	});
	Out.ar(0,out);
}).play;


//:Back to the synthdef

{ 
	|
		buf, ampThreshold = 0.02, transpose = 2, srcAmp = 0, fxAmp = 1, 
		srcpos = 0, srcwidth = 2, 
		fxpos = 0, fxwidth = 2, 
		vol = 1
	|
	var in, amp, freq, hasFreq, out;
	in = PlayBuf.ar(1, \swallows.buffer, loop: 1);
	amp = Amplitude.kr(in, 0.05, 0.05);
	#freq, hasFreq = Pitch.kr(in, ampThreshold: ampThreshold, median: 7);
	out = CombC.ar(LPF.ar(in, 1000), 0.1, (transpose * freq).reciprocal, -6).distort * 0.05;
	out = out * hasFreq;
	6.do({ out = AllpassN.ar(out, 0.040, Rand(0.0001, 0.04), 2) });
	
	
	Out.ar(0, [out * fxAmp * vol, in * srcAmp * vol]);
}


//:Test
Synth(\pitchFollowPan, [\buf, \swallowsa.buffer, \vol, 20]);

//:proxyspacetest
(instrument: \pitchFollowPan, buf: \swallowsa.buffer);

//:proxyspacetest2
Pbind(\instrument, \pitchFollowPan, \buf, \swallowsa.buffer);

//:proxyspacetest2
~proxyspacetest2.set(\fxAmp, 10);


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
//	out = CombC.ar(LPF.ar(in, 1000), 0.1, (transpose * freq).reciprocal, -6).distort * 0.05;
	out = WhiteNoise.ar(1);	
//	out = out * hasFreq;
//	6.do({ out = AllpassN.ar(out, 0.040, Rand(0.0001, 0.04), 2) });
	
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


