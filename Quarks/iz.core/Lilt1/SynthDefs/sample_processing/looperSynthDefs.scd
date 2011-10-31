/* iz Saturday; October 25, 2008: 12:18 AM
Looping with Phasor. 
Can loop forwards and backwards with positive - negative rate values. 
mark1 and mark2 are freely settable between 0 (beginning of file) and 1 (end of file). 
If mark1 is smaller than mark2, then mark1 is the start of the loop and mark2 the end. Otherwise the reverse is the case. 

looper2, looper3, looper4, and looper5,
loop multiply over the same buffer, with the same mechanism but with separate rate, mark1 and mark2 inputs. This should enable to control each of these separately to weave them polyphonically. 


*/
[
SynthDef("looper", { | out = 0, bufnum = 0, rate = 1, mark1 = 0, mark2 = 1 |
	var framesInBuffer, start, end, loop;
	framesInBuffer = BufFrames.kr(bufnum);
	start = (mark1 min: mark2) * framesInBuffer;
	end = (mark1 max: mark2) * framesInBuffer;
	loop = BufRd.ar(1, bufnum, Phasor.ar(
			// retrigger when buffer or any mark changes: 
			Slope.kr(bufnum).abs + Slope.kr(mark1).abs + Slope.kr(mark2).abs,
			BufRateScale.kr(bufnum) * rate, start, end, start));
	Out.ar(out, loop);
}),
// looperx just adds amplitude control
SynthDef("looperx", { | out = 0, bufnum = 0, rate = 1, mark1 = 0, mark2 = 1, amp = 0.1 |
	var framesInBuffer, start, end, loop;
	framesInBuffer = BufFrames.kr(bufnum);
	start = (mark1 min: mark2) * framesInBuffer;
	end = (mark1 max: mark2) * framesInBuffer;
	loop = BufRd.ar(1, bufnum, Phasor.ar(
			// retrigger when buffer or any mark changes: 
			Slope.kr(bufnum).abs + Slope.kr(mark1).abs + Slope.kr(mark2).abs,
			BufRateScale.kr(bufnum) * rate, start, end, start));
	Out.ar(out, loop * amp);
}),
SynthDef("looper2", { | out = 0, bufnum = 0,
	rate_1 = 1, mark1_1 = 0, mark2_1 = 1,
	rate_2 = 1, mark1_2 = 0, mark2_2 = 1 |
	var framesInBuffer, loop, bufrate, bufchanged;
	var start1, end1;
	var start2, end2;
	framesInBuffer = BufFrames.kr(bufnum);
	bufrate = BufRateScale.kr(bufnum);
	start1 = (mark1_1 min: mark2_1) * framesInBuffer;
	end1 = (mark1_1 max: mark2_1) * framesInBuffer;
	start2 = (mark1_2 min: mark2_2) * framesInBuffer;
	end2 = (mark1_2 max: mark2_2) * framesInBuffer;
	bufchanged = Slope.kr(bufnum).abs;
	loop = BufRd.ar(1, bufnum, Phasor.ar(
		[
			// retrigger when buffer or any mark changes: 
			bufchanged + Slope.kr(mark1_1).abs + Slope.kr(mark2_1).abs,
			bufchanged + Slope.kr(mark1_2).abs + Slope.kr(mark2_2).abs
		],
		[bufrate * rate_1, bufrate * rate_2],
		[start1, start2], [end1, end2], [start1, start2]
	));
	Out.ar(out, loop);
})
]

/* // versions without retriggering whenever marks or bufnum change: 
[
SynthDef("looper", { | out = 0, bufnum = 0, rate = 1, mark1 = 0, mark2 = 1 |
	var framesInBuffer, start, end, loop;
	framesInBuffer = BufFrames.kr(bufnum);
	start = (mark1 min: mark2) * framesInBuffer;
	end = (mark1 max: mark2) * framesInBuffer;
	loop = BufRd.ar(1, bufnum, Phasor.ar(1, BufRateScale.kr(bufnum) * rate, start, end, start));
	Out.ar(out, loop);
}), 
SynthDef("looper2", { | out = 0, bufnum = 0,
	rate_1 = 1, mark1_1 = 0, mark2_1 = 1,
	rate_2 = 1, mark1_2 = 0, mark2_2 = 1 |
	var framesInBuffer, loop, bufrate;
	var start1, end1;
	var start2, end2;
	framesInBuffer = BufFrames.kr(bufnum);
	bufrate = BufRateScale.kr(bufnum);
	start1 = (mark1_1 min: mark2_1) * framesInBuffer;
	end1 = (mark1_1 max: mark2_1) * framesInBuffer;
	start2 = (mark1_2 min: mark2_2) * framesInBuffer;
	end2 = (mark1_2 max: mark2_2) * framesInBuffer;
	loop = BufRd.ar(1, bufnum, Phasor.ar(1, [bufrate * rate_1, bufrate * rate_2],
		[start1, start2], [end1, end2], [start1, start2]
	));
	Out.ar(out, loop);
})
]
*/
