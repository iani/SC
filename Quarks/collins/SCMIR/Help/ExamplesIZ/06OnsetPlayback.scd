/*

Adding buffer playback with found onsets.
Based on 01ONsetDetectorComparison examples of iz.drafts SoundFeature quark

*/

SCMIRAudioBuffer.load;
w = SCMIRAudioBuffer.get('weddell1');
w.buffer.play;
w.plotFeatures;
w.extractOnsets;
w.onsetdata.differentiate;
w.numonsets;

w.buffer.numFrames / 44100;

// Original func playback usage for SynthDef: 

		{ | onsets, numOnsets = 5, buf | // num onsets will be used as arg to Dseries
			var trig, env;
			trig = TDuty.kr(
					Dbufrd(onsets, Dseries(0, 1, numOnsets), 1), // 
					0,
					1
			);
			env = Decay.kr(trig);
			[
				WhiteNoise.ar(0.1),
				PlayBuf.ar(1, buf, BufRateScale.kr(buf), loop: 0, doneAction: 2)
			] * env * 0.5
		
		}.play(args: [numOnsets: index + 1, onsets: \jensenOnsets.buffer, \buf, 'a11wlk01'.buffer]);

//:Storing onset data 
b = Buffer.loadCollection(Server.default, w.onsetdata.differentiate);
b.updateInfo;
b.numFrames;

//:Test playing
{ | onsets, numOnsets = 5, buf | // num onsets will be used as arg to Dseries
	var trig, env;
	trig = TDuty.kr(
			Dbufrd(onsets, Dseries(0, 1, numOnsets), 1), // 
			0,
			1
	);
	env = Decay.kr(trig);
	[
		WhiteNoise.ar(0.1),
		PlayBuf.ar(1, buf, BufRateScale.kr(buf), loop: 0, doneAction: 2) * 2
	] * env * 0.5

}.play(args: [numOnsets: b.numFrames, onsets: b.bufnum, \buf, w.buffer]);


