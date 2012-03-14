/* IZ 20120312 
Comparing onset detection algorithms in SC: 

Onsets, PV_HainsworthFoote, PV_JensenAndersen
*/

//:Load the default buffers and boot
BufferResource.loadSCdefaults;
Server.default.boot;

//:Onsets
//Use SendReply to store the onset values in an array

o = [];	// this is the array that will store the onsets
OSCresponder(nil, 'bufpos', { | t, r, m | o = o add: m[3]; m[3..].postln; }).add;

'a11wlk01'.playBuf({ | buf |
	var sig, fft, onsets, bufpos, pips;
	var fftbuflen = 2048;
	bufpos = Phasor.ar(1, BufRateScale.kr(buf), 0, BufFrames.kr(buf) - (BufFrames.kr(buf) % fftbuflen));
	// we use the bufpos phasor only to send index values. To play sound, we use PlayBuf instead.
	// This makes it possible to stop when the buffer is played once
	sig = PlayBuf.ar(1, buf, loop: 0, doneAction: 2);
	fft = FFT(LocalBuf(fftbuflen, 1), sig);
	onsets = Onsets.kr(fft, 0.01, \rcomplex);
	SendReply.kr(onsets, \bufpos, [bufpos, bufpos / 44100]);
	pips = WhiteNoise.ar(EnvGen.kr(Env.perc(0.001, 0.1, 0.2), onsets));
	Out.ar(0, Pan2.ar(sig, -0.75, 0.2) + Pan2.ar(pips, 0.75, 1));
});

//:Hainsworth-Foote

h = [];	// this is the array that will store the onsets
OSCresponder(nil, 'bufpos', { | t, r, m | h = h add: m[3]; m[3..].postln; }).add;

'a11wlk01'.playBuf({ | buf |
	var sig, onsets, bufpos, pips;
	var fftbuflen = 2048;
	bufpos = Phasor.ar(1, BufRateScale.kr(buf), 0, BufFrames.kr(buf) - (BufFrames.kr(buf) % fftbuflen));
	// we use the bufpos phasor only to send index values. To play sound, we use PlayBuf instead.
	// This makes it possible to stop when the buffer is played once
	sig = PlayBuf.ar(1, buf, loop: 0, doneAction: 2);
	// Settings used here are the defaults from the PV_HainsworthFoote help file:
	onsets = PV_HainsworthFoote.ar(FFT(LocalBuf(2048), sig), 1.0, 0.0);
	SendReply.kr(A2K.kr(onsets), \bufpos, [bufpos, bufpos / 44100]);
	pips = WhiteNoise.ar(EnvGen.kr(Env.perc(0.001, 0.1, 0.2), onsets));
	Out.ar(0, Pan2.ar(sig, -0.75, 0.2) + Pan2.ar(pips, 0.75, 1));
});

//:PV_JensenAndersen

j = [];	// this is the array that will store the onsets
OSCresponder(nil, 'bufpos', { | t, r, m | j = j add: m[3]; m[3..].postln; }).add;

'a11wlk01'.playBuf({ | buf |
	var sig, onsets, bufpos, pips;
	var fftbuflen = 2048;
	bufpos = Phasor.ar(1, BufRateScale.kr(buf), 0, BufFrames.kr(buf) - (BufFrames.kr(buf) % fftbuflen));
	// we use the bufpos phasor only to send index values. To play sound, we use PlayBuf instead.
	// This makes it possible to stop when the buffer is played once
	sig = PlayBuf.ar(1, buf, loop: 0, doneAction: 2);
	// Settings used here are the defaults from the PV_HainsworthFoote help file:
	onsets = PV_JensenAndersen.ar(FFT(LocalBuf(2048), sig), threshold: 0.1, waittime: 0.2);
	SendReply.kr(A2K.kr(onsets), \bufpos, [bufpos, bufpos / 44100]);
	pips = WhiteNoise.ar(EnvGen.kr(Env.perc(0.001, 0.1, 0.2), onsets));
	Out.ar(0, Pan2.ar(sig, -0.75, 0.2) + Pan2.ar(pips, 0.75, 1));
});

//:compare the accumulated onset indices:

o.postln;
h.postln;
j.postln;

//:Use a chain to post found indices after the playback is done: 

Chain(Pseq([
{ 	j = [];
	OSCresponder(nil, 'bufpos', { | t, r, m | j = j add: m[3]; m[3..].postln; }).add;
}.chain,
{
\jensen.playFunc({ | buf |
	var sig, onsets, bufpos, pips;
	var fftbuflen = 2048;
	// Since we are not looping, no need to clip the Phasor's values to multiples of fftbuflen
	bufpos = Phasor.ar(1, BufRateScale.kr(buf), 0, BufFrames.kr(buf));
	// we use the bufpos phasor only to send index values. To play sound, we use PlayBuf instead.
	// This makes it possible to stop when the buffer is played once
	sig = PlayBuf.ar(1, buf, loop: 0, doneAction: 2);
	// Settings used here are the defaults from the PV_HainsworthFoote help file:
	onsets = PV_JensenAndersen.ar(FFT(LocalBuf(2048), sig), threshold: 0.1, waittime: 0.2);
	SendReply.kr(A2K.kr(onsets), \bufpos, [bufpos, bufpos / 44100]);
	pips = WhiteNoise.ar(EnvGen.kr(Env.perc(0.001, 0.1, 0.2), onsets));
	Out.ar(0, Pan2.ar(sig, -0.75, 0.2) + Pan2.ar(pips, 0.75, 1));
}, args: [\buf, 'a11wlk01'.buffer])
},
{ "done".postln; j.postln; }.chain]));

/* 	collect the onsets, then play them back with a simple percussive sound
	We store the onsets directly in a buffer, and use the buffer data for
	direct timing
*/

//:1-First create a buffer to store the onsets in: 
BufferResource('jensenOnsets', Server.default, 100000);

//:2-Then Collect onsets in buffer and play them with a percussive sound
{
	var index = 0; // share variables in the scope of OSCresponder and Chain functions ...
	var jensenOnsets = [];
	Chain(Pseq([
	{ 	var onset = 0;
		OSCresponder(nil, 'bufpos', { | t, r, m |
			jensenOnsets = jensenOnsets add: m[3]; m[3..]; // keeping this for reference
	// store the DIFFERENCE (DT!) from the previous onset time point, in seconds, for Duty
			'jensenOnsets'.buffer.set(index, onset.neg + (onset = m[4]));		index = index + 1;			// increment the index
		}).add;
	}.chain,
	{
		\jensen.playFunc({ | buf, onsetbuf |
			var sig, onsets, bufpos, pips;
			var fftbuflen = 2048;
	// Since we are not looping, no need to clip the Phasor's values to multiples of fftbuflen
			bufpos = Phasor.ar(1, BufRateScale.kr(buf), 0, BufFrames.kr(buf));
	// we use the bufpos phasor only to send index values. To play sound, we use PlayBuf instead.
	// This makes it possible to stop when the buffer is played once
			sig = PlayBuf.ar(1, buf, loop: 0, doneAction: 2);
	// Settings used here are the defaults from the PV_HainsworthFoote help file:
			onsets = PV_JensenAndersen.ar(FFT(LocalBuf(2048), sig), threshold: 0.1, waittime: 0.2);
			SendReply.kr(A2K.kr(onsets), \bufpos, [bufpos, bufpos / 44100]);
			pips = WhiteNoise.ar(EnvGen.kr(Env.perc(0.001, 0.1, 0.2), onsets));
			Out.ar(0, Pan2.ar(sig, -0.75, 0.2) + Pan2.ar(pips, 0.75, 1));
		}, args: [\buf, 'a11wlk01'.buffer, \onsetbuf, 'jensenOnsets'.buffer])
	},
	{
		"starting onset playback".postln;
		postf("onsets are: %\n", jensenOnsets.asCompileString);
		{ | onsets, numOnsets = 5 | // num onsets will be used as arg to Dseries
			var trig;
			trig = Duty.kr(
					Dbufrd(onsets, Dseries(0, 1, inf), 1), // 
					0, 
					Dseries(1, 1, numOnsets)
				); 
			WhiteNoise.ar(Decay.kr(Slope.kr(DelayN.kr(trig, 0.001, 0.001)) > 0)) * 0.1
		
		}.play(args: [numOnsets: index + 1, onsets: \jensenOnsets.buffer]);
	
	}.chain]));
}.value;



