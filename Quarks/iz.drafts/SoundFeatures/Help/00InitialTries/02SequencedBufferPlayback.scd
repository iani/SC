/* IZ 20120314 

Using Duty + Phasor to playback sections of a buffer

*/

//:Load some default sound files into buffers to work with
BufferResource.loadSCdefaults; // loads the a11wlk files from SC sounds folder
Server.default.boot;

//:Play just one second, starting from 0

'a11wlk01'.playBuf({ | buf, amp = 0.5 |
	var trig, pos;
	trig = Impulse.kr(1);
	pos = Phasor.ar(trig, BufRateScale.kr(buf), 0, BufFrames.ir(buf), 0);
	BufRd.ar(1, buf, pos, 0, 1) * amp;
});

//:Play snippets starting from an arbitrary position each time

a = 'a11wlk01'.playBuf({ | buf, amp = 0.5, beat = 10 |
	var trig, pos, start;
	trig = Impulse.kr(beat);
	start = Demand.kr(trig, 0, Dwhite(0, BufFrames.ir(buf), inf));
	pos = Phasor.ar(trig, BufRateScale.kr(buf), start, BufFrames.ir(buf), start * [1, 1.05]);
	BufRd.ar(1, buf, pos, 0, 1) * amp;
});

r = {
	loop { 2.wait; a.set(\beat, 1 exprand: 20) }	
}.fork;


//:Using an envelope to prevent clicks

a = 'a11wlk01'.playBuf({ | buf, amp = 0.5, beat = 5 |
	var trig, pos, start, env;
	trig = Impulse.kr(beat);
	start = Demand.kr(trig, 0, Dwhite(0, BufFrames.ir(buf), inf));
	env = EnvGen.kr(Env([0, 1, 1, 0], [0.1, 0.8, 0.1]), trig, timeScale: beat.reciprocal);
	pos = Phasor.ar(trig, BufRateScale.kr(buf), start, BufFrames.ir(buf), start);
	BufRd.ar(1, buf, pos, 0, 1) * amp * env;
});

r = {
	loop { 
	5.wait; a.set(\beat, 0.2 exprand: 20) }	
}.fork;

//:Introducing variation in the rate

a = 'a11wlk01'.playBuf({ | buf, amp = 0.5, beat = 5 |
	var trig, pos, start, env, rate;
	trig = Impulse.kr(beat);
//	trig = Dust.kr(beat);	
	start = Demand.kr(trig, 0, Dwhite(0, BufFrames.ir(buf), inf));
	env = EnvGen.kr(Env([0, 1, 1, 0], [0.1, 0.8, 0.1]), trig, timeScale: beat.reciprocal);
	rate = Demand.kr(trig, 0, Dwhite(-2.0, 2.0, inf));
	pos = Phasor.ar(trig, BufRateScale.kr(buf) * rate, start, BufFrames.ir(buf), start * [1, 1.1]);
	BufRd.ar(1, buf, pos, 0, 1) * amp * env;
});

r = {
	loop { 
	5.wait; a.set(\beat, 0.2 exprand: 20) }	
}.fork;

//:Trying out a different sound file. Must be loaded from buffer list previously ... 
/*
BufferResource.load; // evaluate this line to open a file dialog
// Then see the name(s) of the loaded buffer(s) from the post window
// and copy one of these to the example below before the .playBuf: 
*/

a = 'Belugawhales'.playBuf({ | buf, amp = 0.5, beat = 5 |
	var trig, pos, start, env, rate;
	trig = Impulse.kr(beat);
//	trig = Dust.kr(beat);	
	start = Demand.kr(trig, 0, Dwhite(0, BufFrames.ir(buf), inf));
	env = EnvGen.kr(Env([0, 1, 1, 0], [0.1, 0.8, 0.1]), trig, timeScale: beat.reciprocal);
	rate = Demand.kr(trig, 0, Dwhite(-2.0, 2.0, inf));
	pos = Phasor.ar(trig, BufRateScale.kr(buf) * rate, start, BufFrames.ir(buf), start * [1, 1.1]);
	BufRd.ar(1, buf, pos, 0, 1) * amp * env;
});

r = {
	loop { 
	5.wait; a.set(\beat, 0.2 exprand: 20) }	
}.fork;

/*Individual control of each parameter for each snippet. Using TGrains!
Parameters: 
- onsets for onset of next snippet
- centers: centers position of each grain on buffer
- durations: duration of each grain
- rates: rate of each grain
- amplitude scale (mul)

TGrains provides all the controls needed and therefore tested here. 
TODO: Write a variant of TGrains that uses startPos instead of centerPos and has no pan

*/

//:TGrains basic test: controlling the rate of playback and the center position of grains
a = 'a11wlk01'.playBuf({ | buf, trate = 2, centerpos = 0 |
	var duration, rate, trig;
	trate = trate * 6;
	trig = Impulse.kr(trate);
	duration = trate.reciprocal;
	rate = Dseq([1, 1.2, 0.75, -0.75, -1, -1.2], inf);
	TGrains.ar(2, trig, buf, rate, centerpos, duration, Dseq([-0.5, 0.5], inf), 0.5);
});

r = { 
	inf do: { | i | 1.wait; a.set(\centerpos, (i / 20) % 4) }
}.fork;

//:Control of onsets, rates, centers and durations
a = 'a11wlk01'.playBuf({ | buf |
	var onsets, rates, centers, durations;
	onsets = TDuty.kr(Dseq([0.1, 0.2, 0.3, 0.1, 0.1, 0.2] / 3, inf), 0, 1);
	durations = Dseq([0.05, 0.1, 0.15], inf) + Dseq([0, 0, 0.0, 0.0, 0.3], inf);
	rates = Dseq([1, 1.2, 0.75, -0.75, -1, -1.2, 1], inf);
	centers = Dseq([0, 1, 2, 3, 2, 1], inf) + Dstutter(6, Dseries(0, 0.01, inf));
	TGrains.ar(2, onsets, buf, rates, centers, durations, Dseq([-0.5, 0.5], inf), 0.5);
});

//:Variation of above with gradual shifting of center positions by 0.001 seconds at each new grain
a = 'a11wlk01'.playBuf({ | buf |
	var onsets, rates, centers, durations;
	onsets = TDuty.kr(Dseq([0.1, 0.2, 0.3, 0.1, 0.1, 0.2] / 3, inf), 0, 1);
	durations = Dseq([0.05, 0.1, 0.15], inf) + Dseq([0, 0, 0.0, 0.0, 0.3], inf);
	rates = Dseq([1, 1.2, 0.75, -0.75, -1, -1.2, 1], inf);
//	centers = Dseq([0, 1, 2, 3, 2, 1], inf) + Dstutter(6, Dseries(0, 0.01, inf));
	centers = Dseq([0, 1, 2, 3, 2, 1], inf) + Dseries(0, 0.001, inf);
	TGrains.ar(2, onsets, buf, rates, centers, durations, Dseq([-0.5, 0.5], inf), 0.5);
});

//==================== Using buffers to store the series of parameter values ==================
//:1. Prepare buffers
{ 
	[\onsets, \rates, \centers, \durations] do: { | b |
		BufferResource(b, Server.default, 100);
		0.2.wait; // slower systems need some time for the buffer to initialize
		b.buffer.sendCollection(1 ! 100, 0);
		0.2.wait;
	};
	"BUFFERS INITIALIZED".postln;
}.fork;

//:1b. Test to see that the contents have been initialized
[\onsets, \rates, \centers, \durations] do: { | b |
	b.buffer.getn(0, 100, { | vals | b.post; ":".postln; vals.postln });
}

//:2. Play
a = 'a11wlk01'.playBuf({  // tried also with 'Belugawhales'
	| buf, onsets, rates, centers, durations, numons = 4, numrat = 4, numcen = 4, numdur = 4 |
	onsets = TDuty.kr(Dbufrd(onsets, Dseries(0, 1, inf) % numons, 1), 0, 1);
	rates = Dbufrd(rates, Dseries(0, 1, inf) % numrat, 1);
	centers = Dbufrd(centers, Dseries(0, 1, inf) % numcen, 1);
	durations = Dbufrd(durations, Dseries(0, 1, inf) % numdur, 1);
	TGrains.ar(2, onsets, buf, rates, centers, durations, Dseq([-0.5, 0.5], inf), 0.5);
}, args: [
	\onsets, \onsets.buffer,
	\rates, \rates.buffer,
	\centers, \centers.buffer,
	\durations, \durations.buffer
]);

//:3. Change the contents of the buffers
\rates.buffer.set(1, -1);

//:4 different rates 
\rates.buffer.setn(0, [1, 1.2, 1.3, 1.4]);

//:4 different onsets
\onsets.buffer.setn(0, [0.1, 0.2, 0.3, 0.4]);
\durations.buffer.setn(0, [0.1, 0.2, 0.4, 0.4]);	// also shorten the durations

//:Different centers: 
\centers.buffer.setn(0, [0, 0.5, 1, 1.5]);

//:random variations of above
r = {
	var onsets;
	inf do: { | i |
		4.wait;
		onsets = [0.1, 0.2, 0.3, 0.4].scramble + [0.1, 0.2, 0.0, -0.05].choose;
		\onsets.buffer.setn(0, onsets);
		\durations.buffer.setn(0, onsets);
		\centers.buffer.setn(0, Array.rand(4, 0.0, 4)); // 4, 0, 20.0 for belugawhales
		\rates.buffer.setn(0, [1, 1.2, 1.3, 1.4] * [-1, 1.0, 1.1, 1.2, 1.5, 0.75].choose);
	};
}.fork;

//:--
r.stop;





