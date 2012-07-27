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

//:Synthdefs
Udef("pitchFollowPan",{ 
	|
		buf, ampThreshold = 0.02, transpose = 2, srcAmp = 0, fxAmp = 1, 
		srcpos = 0, srcwidth = 2, 
		fxpos = 0, fxwidth = 2, 
		vol = 1
	|
	var in, amp, freq, hasFreq, out;
	in = PlayBuf.ar(1, buf, loop: 1);
	amp = Amplitude.kr(in, 0.05, 0.05);
	#freq, hasFreq = Pitch.kr(in, ampThreshold: ampThreshold, median: 7);
	out = CombC.ar(LPF.ar(in, 1000), 0.1, (transpose * freq).reciprocal, -6).distort * 0.05;
	out = out * hasFreq;
	6.do({ out = AllpassN.ar(out, 0.040, Rand(0.0001, 0.04), 2) });
	
	
	Out.ar(0, [out * fxAmp * vol, in * srcAmp * vol]);
});

//:TIMER
var startTime, window, timer, timeRout;
startTime = Date.getDate.bootSeconds;
window = Window("TIME!", Rect(1240, 50, 190, 100)).front;
timer = NumberBox(window, window.view.bounds);
timer.font = Font("Helvetica", 72);
timeRout = {
	var dt;
	loop {
		1.wait;
		dt = Date.getDate.bootSeconds - startTime;
		timer.value = (dt / 60).floor(1) + (dt / 60 mod: 1 * 60 / 100).round(0.01);
	};
}.fork(AppClock);	

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


