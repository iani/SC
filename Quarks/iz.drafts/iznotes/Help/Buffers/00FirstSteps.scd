/* IZ 2012 03 ff


First steps

Start with getting a list of onsets detected by Onsets.kr

How to get the frame number of the buffer at the point when an onset was detected? A try here: 

*/

//:Load the default buffers and boot
BufferResource.loadSCdefaults;
Server.default.boot;

//:Test a buffer
'a11wlk01'.playBuf({ | buf | PlayBuf.ar(1, buf); });

//:Onset detection on same buffer

'a11wlk01'.playBuf({ | buf |
	var sig, chain, onsets, pips;
	sig = PlayBuf.ar(1, buf, BufRateScale.kr(buf), loop: 1);
	chain = FFT(LocalBuf(2048, 1), sig);
	onsets = Onsets.kr(chain, 0.2, \rcomplex);
	pips = WhiteNoise.ar(EnvGen.kr(Env.perc(0.001, 0.1, 0.2), onsets));
	Out.ar(0, Pan2.ar(sig, -0.75, 0.2) + Pan2.ar(pips, 0.75, 1));
})

//:Post the frame number of the detected onsets

'a11wlk01'.playBuf({ | buf |
	var sig, fft, onsets, bufpos, pips;
	var fftbuflen = 2048;
	bufpos = Phasor.ar(1, BufRateScale.kr(buf), 0, BufFrames.kr(buf));
	sig = BufRd.ar(1, buf, bufpos);
	fft = FFT(LocalBuf(fftbuflen, 1), sig);
	onsets = Onsets.kr(fft, 0.01, \rcomplex);
	Poll.kr(onsets, bufpos, \bufpos);
	pips = WhiteNoise.ar(EnvGen.kr(Env.perc(0.001, 0.1, 0.2), onsets));
	Out.ar(0, Pan2.ar(sig, -0.75, 0.2) + Pan2.ar(pips, 0.75, 1));
})

/* 	Note: At each repetition of the buffer playback, 
	the onset numbers posted change. 
	This is because the fft window size overlaps between the end and the beginning of the
	looped buffer repetition. 
	To correct this, reduce the buffer playback length to a multiple of the fft window length:
*/

//:Reduce buffer playback size to multiple of fft window size

'a11wlk01'.playBuf({ | buf |
	var sig, fft, onsets, bufpos, pips;
	var fftbuflen = 2048;
	bufpos = Phasor.ar(1, BufRateScale.kr(buf), 0, BufFrames.kr(buf) - (BufFrames.kr(buf) % fftbuflen));
	sig = BufRd.ar(1, buf, bufpos);
	fft = FFT(LocalBuf(fftbuflen, 1), sig);
	onsets = Onsets.kr(fft, 0.01, \rcomplex);
	Poll.kr(onsets, bufpos, \bufpos);
	pips = WhiteNoise.ar(EnvGen.kr(Env.perc(0.001, 0.1, 0.2), onsets));
	Out.ar(0, Pan2.ar(sig, -0.75, 0.2) + Pan2.ar(pips, 0.75, 1));
})

//:Use SendReply to store the onset values in an array
o = [];	// this is the array that will store the onsets
OSCresponder(nil, 'bufpos', { | t, r, m | o = o add: m[3]; m[3..].postln; }).add;

'a11wlk01'.playBuf({ | buf |
	var sig, fft, onsets, bufpos, pips;
	var fftbuflen = 2048;
	bufpos = Phasor.ar(1, BufRateScale.kr(buf), 0, BufFrames.kr(buf) - (BufFrames.kr(buf) % fftbuflen));
	sig = BufRd.ar(1, buf, bufpos);
	fft = FFT(LocalBuf(fftbuflen, 1), sig);
	onsets = Onsets.kr(fft, 0.01, \rcomplex);
	SendReply.kr(onsets, \bufpos, [bufpos, bufpos / 44100]);
	pips = WhiteNoise.ar(EnvGen.kr(Env.perc(0.001, 0.1, 0.2), onsets));
	Out.ar(0, Pan2.ar(sig, -0.75, 0.2) + Pan2.ar(pips, 0.75, 1));
});

