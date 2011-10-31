/* iz Saturday; October 25, 2008: 10:13 AM
Like looper except it plays simultaneously 2 or more loops based on the same Phasor but offsetting the read position by a controllable interval \offset. 

The plain difflooper outputs each loop on a separate channel. The difflooperM variant mixes all loops to 1 output channel. 

Offsetting the read can also be done delaying the loop signal with DelayN. But using DelayN would require to initialize the maximum possible duration of the delay and entail creation of a buffer (done by DelayN internally). So this is not as flexible as offsetting the read position.  Therefore DelayN implementation not done here. 

*/

[
SynthDef("difflooper", { | out = 0, bufnum = 0, rate = 1, mark1 = 0, mark2 = 1, offset = 0 |
	var framesInBuffer, start, end, loop, readPos;
	framesInBuffer = BufFrames.kr(bufnum);
	start = (mark1 min: mark2) * framesInBuffer;
	end = (mark1 max: mark2) * framesInBuffer;
	readPos = Phasor.ar(1, BufRateScale.kr(bufnum) * rate, start, end, start);
	offset = offset * framesInBuffer;
	loop = BufRd.ar(1, bufnum, [readPos, readPos + offset]);
	Out.ar(out, loop);
}), 
SynthDef("difflooper3", { | out = 0, bufnum = 0, rate = 1, mark1 = 0, mark2 = 1, offset = 0 |
	var framesInBuffer, start, end, loop, readPos;
	framesInBuffer = BufFrames.kr(bufnum);
	start = (mark1 min: mark2) * framesInBuffer;
	end = (mark1 max: mark2) * framesInBuffer;
	readPos = Phasor.ar(1, BufRateScale.kr(bufnum) * rate, start, end, start);
	offset = offset * framesInBuffer;
	loop = BufRd.ar(1, bufnum, [readPos, offset + readPos, offset * 2 + readPos]);
	Out.ar(out, loop);
}),

SynthDef("difflooperM", { | out = 0, bufnum = 0, rate = 1, mark1 = 0, mark2 = 1, offset = 0 |
	var framesInBuffer, start, end, loop, readPos;
	framesInBuffer = BufFrames.kr(bufnum);
	start = (mark1 min: mark2) * framesInBuffer;
	end = (mark1 max: mark2) * framesInBuffer;
	readPos = Phasor.ar(1, BufRateScale.kr(bufnum) * rate, start, end, start);
	offset = offset * framesInBuffer;
	loop = BufRd.ar(1, bufnum, [readPos, readPos + offset]);
	Out.ar(out, Mix.ar(loop));
}),
SynthDef("difflooper3M", { | out = 0, bufnum = 0, rate = 1, mark1 = 0, mark2 = 1, offset = 0 |
	var framesInBuffer, start, end, loop, readPos;
	framesInBuffer = BufFrames.kr(bufnum);
	start = (mark1 min: mark2) * framesInBuffer;
	end = (mark1 max: mark2) * framesInBuffer;
	readPos = Phasor.ar(1, BufRateScale.kr(bufnum) * rate, start, end, start);
	offset = offset * framesInBuffer;
	loop = BufRd.ar(1, bufnum, [readPos, offset + readPos, offset * 2 + readPos]);
	Out.ar(out, Mix.ar(loop));
}), 
SynthDef("difflooper4M", { | out = 0, bufnum = 0, rate = 1, mark1 = 0, mark2 = 1, offset = 0 |
	var framesInBuffer, start, end, loop, readPos;
	framesInBuffer = BufFrames.kr(bufnum);
	start = (mark1 min: mark2) * framesInBuffer;
	end = (mark1 max: mark2) * framesInBuffer;
	readPos = Phasor.ar(1, BufRateScale.kr(bufnum) * rate, start, end, start);
	offset = offset * framesInBuffer;
	loop = BufRd.ar(1, bufnum, [readPos, offset + readPos,
		offset * 2 + readPos, offset * 3 + readPos
	]);
	Out.ar(out, Mix.ar(loop));
}), 
SynthDef("difflooper5M", { | out = 0, bufnum = 0, rate = 1, mark1 = 0, mark2 = 1, offset = 0 |
	var framesInBuffer, start, end, loop, readPos;
	framesInBuffer = BufFrames.kr(bufnum);
	start = (mark1 min: mark2) * framesInBuffer;
	end = (mark1 max: mark2) * framesInBuffer;
	readPos = Phasor.ar(1, BufRateScale.kr(bufnum) * rate, start, end, start);
	offset = offset * framesInBuffer;
	loop = BufRd.ar(1, bufnum, [readPos, offset + readPos,
		offset * 2 + readPos, offset * 3 + readPos, offset * 4 + readPos
	]);
	Out.ar(out, Mix.ar(loop));
}), 
SynthDef("difflooper6M", { | out = 0, bufnum = 0, rate = 1, mark1 = 0, mark2 = 1, offset = 0 |
	var framesInBuffer, start, end, loop, readPos;
	framesInBuffer = BufFrames.kr(bufnum);
	start = (mark1 min: mark2) * framesInBuffer;
	end = (mark1 max: mark2) * framesInBuffer;
	readPos = Phasor.ar(1, BufRateScale.kr(bufnum) * rate, start, end, start);
	offset = offset * framesInBuffer;
	loop = BufRd.ar(1, bufnum, [readPos, offset + readPos,
		offset * 2 + readPos, offset * 3 + readPos, offset * 4 + readPos, offset * 5 + readPos
	]);
	Out.ar(out, Mix.ar(loop));
})
]


