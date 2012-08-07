/*

IZ Fri 03 August 2012  6:25 PM EEST

Using a Phasor to play back a control line made of a series of values and time intervals.
The series may of any length, because the values and time intervals are stored in a buffer. 

Principles:
 
- Use 2 buffers for storing the values and the durations (Dbufrd, needed to read from the buffers, cannot read from multichannel buffers). 

- Use Phasor to interpolate linearly between subsequent values of channel 0.

- Use TDuty, Dbufrd and Dseries to read subsequent values from channels 0 and 1.

- Calculate he rate of change of Phasor from the time distance between two subsequent values and the difference between those values. 

Example of calculation: 

if the initial value (value1) is 100 and the final value (value2) is 200 and the duration interval between them is 1 second, then: 

Given that a rate of 1 with a sampling-rate of 44100 would go from 1 to 44100 in one second, and we want to go from 100 to 200 in one second, and the difference of 200 to 100 is 100, consider this: 

At the Phasor rate of 1 we increment 44100 unit values in one second. If we moved at a rate of 1 / 44100 then it would take 1 second to increment by 1 value. So to increment by 100 in the same time interval, we multiply the rate by 100, which makes: 100 / 44100. Now if we want to increment by the same amount in 2 seconds, we need to move at a proportionally slower rate, so we need to divide the rate by 2. And so on. 

So the formula for calculating the correct rate for Phasor so that it reaches from value1 to value2 during a time interval dt is: 

	(value2 - value1) / (44100 * dt)
 
Note 1: The actual sample rate of the server is given by the info-UGen SampleRate. 
If we are using the Phasor at kr then we should use the UGen ControlRate instead to do the conversion. 

Note 2: The synthdef developed here, when measured with Poll, yields an error of ca 1/100000. That is, the values at each vertex of the line are off by less than 0.01 in 100. The causes of this error are unknown and no better algorithm is available here so far. 

Note 2: A buffer of 1 MB storing a new control value at each control-rate sample can hold 
1000000 / 44100 * 64 / 60 = 24.18.. minutes of data. At an average of 10 control data per second, the same buffer would hold: 1000000 / 44100 * 4410 / 60 = 1666.66.. minutes of data. 

*/


//:simplephasor Lets try the above formula here: 

{ | startVal = 500, endVal = 600, dt = 1 |
	var phasor;
	phasor = Phasor.ar(1, (endVal - startVal) / (SampleRate.ir * dt), startVal, endVal, 0);
	SinOsc.ar(phasor, 0, 0.1);
};

//:changing duration, start and end: 

~simplephasor.set(\startVal, 600, \endVal, 500);
~simplephasor.set(\dt, 2);
~simplephasor.set(\dt, 0.2);
~simplephasor.set(\dt, 0.5);
~simplephasor.set(\dt, 0.25);

//:tduty1 - Now how to do the triggering that gets the next value at the right time.
// first check that the triggering works
{ | startVal = 500, endVal = 600, dt = 1 |
	var phasor, tduty, checkTick;
	phasor = Phasor.ar(1, (endVal - startVal) / (SampleRate.ir * dt), startVal, endVal, 0);
	tduty = TDuty.ar(Impulse.ar(dt.reciprocal), level: 0.1);
	checkTick = WhiteNoise.ar(Decay.ar(tduty, 0.1));
	[SinOsc.ar(phasor, 0, 0.1), checkTick];
};

//:tduty2 - different durations at each beat 
{ | startVal = 500, endVal = 600 |
	var phasor, tduty, checkTick, trigdurpattern, phasdurpattern, durvalues;
	// we need two identical generators, one for the Tduty and one for the Demand ugen
	// that provides the durations for the Phasor: 
	#trigdurpattern, phasdurpattern = { Dseq([0.5, 1, 2], inf) }.dup;
	tduty = TDuty.ar(trigdurpattern, level: 0.1);
	durvalues = Demand.ar(tduty, 0, phasdurpattern);
	phasor = Phasor.ar(1, (endVal - startVal) / (SampleRate.ir * durvalues), startVal, endVal, 0);
	checkTick = WhiteNoise.ar(Decay.ar(tduty, 0.1));
	[SinOsc.ar(phasor, 0, 0.1), checkTick];
};

//:tduty3 - different frequency values at each beat. Using Tduty to trigger the transition to the
// new value starting the new line in the phasor.  
{ | startVal = 500, endVal = 600 |
	var phasor, tduty, checkTick, trigdurpattern, phasdurpattern, duration;
	var freqarray, freq1, freq2;
	#trigdurpattern, phasdurpattern = { Dseq([5, 5, 1, 1, 0.5, 0.5, 0.1], inf) }.dup;
	tduty = TDuty.ar(trigdurpattern, level: 0.1);
	freqarray = [400, 500, 300, 600];
	#duration, freq1, freq2 = Demand.ar(tduty, 0, 
		[phasdurpattern, Dseq(freqarray, inf), Dseq(freqarray rotate: -1, inf)]
	);
	phasor = Phasor.ar(tduty, (freq2 - freq1) / (SampleRate.ir * duration), freq1, freq2, freq1);
	Poll.ar(tduty, phasor);
	checkTick = WhiteNoise.ar(Decay.ar(tduty, 0.1));
//	checkTick = WhiteNoise.ar(Decay.ar(Slope.ar(phasor > (freq1 + 20)), 0.1));
	[SinOsc.ar(phasor, 0, 0.1), checkTick];
};

//:kr !
{ | startVal = 500, endVal = 600 |
	var phasor, tduty, checkTick, trigdurpattern, phasdurpattern, duration;
	var freqarray, freq1, freq2;
	#trigdurpattern, phasdurpattern = { Dseq([5, 5, 1, 1, 0.5, 0.5, 0.1], inf) }.dup;
	tduty = TDuty.kr(trigdurpattern, level: 0.1);
	freqarray = [400, 500, 300, 600];
	#duration, freq1, freq2 = Demand.kr(tduty, 0, 
		[phasdurpattern, Dseq(freqarray, inf), Dseq(freqarray rotate: -1, inf)]
	);
	phasor = Phasor.kr(tduty, (freq2 - freq1) / (ControlRate.ir * duration), freq1, freq2, freq1);
	Poll.kr(tduty, phasor);
	checkTick = WhiteNoise.ar(Decay.ar(K2A.ar(tduty), 0.1));
	[SinOsc.ar(phasor, 0, 0.1), checkTick];
};

// ================== Using buffers to store the level and duration values

// Just durations first: 
//:Prepare a buffer for durations

a = [5, 5, 1, 1, 0.5, 0.5, 0.1];
d = Buffer.sendCollection(Server.default, a);
n = a.size;

//:durbuf Getting the durations from a buffer
/* Note: When measured with Poll, the values reached by Phasor at each end point in the line show an error of ca 1/100000. That is, the values at each vertex of the line are off by less than 0.01 in 100. The causes of this error are unknown and no better algorithm is available here so far. 
*/
{ | startVal = 500, endVal = 600 |
	var phasor, tduty, checkTick, trigdurpattern, phasdurpattern, duration;
	var freqarray, freq1, freq2;
	#trigdurpattern, phasdurpattern = { Dbufrd(d, Dseries(0, 1, inf) % n, 1) }.dup;
	tduty = TDuty.ar(trigdurpattern, level: 0.1);
	freqarray = [400, 500, 300, 600];
	#duration, freq1, freq2 = Demand.ar(tduty, 0, 
		[phasdurpattern, Dseq(freqarray, inf), Dseq(freqarray rotate: -1, inf)]
	);
	phasor = Phasor.ar(tduty, (freq2 - freq1) / (SampleRate.ir * duration), freq1, freq2, freq1);
	Poll.ar(tduty, phasor); // post the phasor values reached at each trigger point, for checking
	checkTick = WhiteNoise.ar(Decay.ar(tduty, 0.1));
	[SinOsc.ar(phasor, 0, 0.1), checkTick];
};

// Now durations + values
//:Prepare the buffers for durations and values

a = [5, 5, 1, 1, 0.5, 0.5, 0.1];
d = Buffer.sendCollection(Server.default, a);
n = a.size;
b = [400, 500, 300, 600];
v = Buffer.sendCollection(Server.default, b);

//:durvalbuf Getting durations and values from buffers
{ | startVal = 500, endVal = 600 |
	var phasor, tduty, checkTick, trigdurpattern, phasdurpattern, duration;
	var freqarray, freq1, freq2;
	#trigdurpattern, phasdurpattern = { Dbufrd(d, Dseries(0, 1, inf) % n, 1) }.dup;
	tduty = TDuty.ar(trigdurpattern, level: 0.1);
	freqarray = [400, 500, 300, 600];
	#duration, freq1, freq2 = Demand.ar(tduty, 0, 
		[phasdurpattern, Dseq(freqarray, inf), Dseq(freqarray rotate: -1, inf)]
	);
	phasor = Phasor.ar(tduty, (freq2 - freq1) / (SampleRate.ir * duration), freq1, freq2, freq1);
	Poll.ar(tduty, phasor); // post the phasor values reached at each trigger point, for checking
	checkTick = WhiteNoise.ar(Decay.ar(tduty, 0.1));
	[SinOsc.ar(phasor, 0, 0.1), checkTick];
};


// ================== The final solution as SynthDef ======================

//: kr version - for use as control signal for any synth parameter
SynthDef("krline", {
	
}).add;

//: ar version - for use in micro-montage from audio sample buffers
/* 
Note: This is hardly very useful, because it means going to-and-fro through an audio file. 
A more useful version would use three parameter buffers as follows: 
1. startTimes : an array of start times in the audio buffer for each successive sound-bite
2. durations : array of durations of each sound-bite
3. rates : array of rates of playback for each sound-bite

Implementation forthcoming ...

*/

SynthDef("arline", {
	
}).add;

