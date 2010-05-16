/* IZ Monday, May 12, 2008 
Live recording into buffer.

NOT YET DONE!
*/

[
SynthDef("bufrecord_1", { | in = 0, bufnum = 0, offset = 0, recLevel = 1, preLevel = 0, run = 1, loop = 1, trigger = 1 |
	var recording;
	recording = RecordBuf.ar(In.ar(in, 1), bufnum, offset, recLevel, preLevel, run, loop, trigger);
	FreeSelfWhenDone.kr(recording);
}),
SynthDef("bufrecord_2", { | in = 0, bufnum = 0, offset = 0, recLevel = 1, preLevel = 0, run = 1, loop = 1, trigger = 1 |
	var recording;
	recording = RecordBuf.ar(In.ar(in, 2), bufnum, offset, recLevel, preLevel, run, loop, trigger);
	FreeSelfWhenDone.kr(recording);
}),
SynthDef("bufrecord_3", { | in = 0, bufnum = 0, offset = 0, recLevel = 1, preLevel = 0, run = 1, loop = 1, trigger = 1 |
	var recording;
	recording = RecordBuf.ar(In.ar(in, 3), bufnum, offset, recLevel, preLevel, run, loop, trigger);
	FreeSelfWhenDone.kr(recording);
}),
SynthDef("bufrecord_4", { | in = 0, bufnum = 0, offset = 0, recLevel = 1, preLevel = 0, run = 1, loop = 1, trigger = 1 |
	var recording;
	recording = RecordBuf.ar(In.ar(in, 4), bufnum, offset, recLevel, preLevel, run, loop, trigger);
	FreeSelfWhenDone.kr(recording);
}),
SynthDef("bufrecord_5", { | in = 0, bufnum = 0, offset = 0, recLevel = 1, preLevel = 0, run = 1, loop = 1, trigger = 1 |
	var recording;
	recording = RecordBuf.ar(In.ar(in, 5), bufnum, offset, recLevel, preLevel, run, loop, trigger);
	FreeSelfWhenDone.kr(recording);
}),
SynthDef("bufrecord_6", { | in = 0, bufnum = 0, offset = 0, recLevel = 1, preLevel = 0, run = 1, loop = 1, trigger = 1 |
	var recording;
	recording = RecordBuf.ar(In.ar(in, 6), bufnum, offset, recLevel, preLevel, run, loop, trigger);
	FreeSelfWhenDone.kr(recording);
}),
SynthDef("bufrecord_7", { | in = 0, bufnum = 0, offset = 0, recLevel = 1, preLevel = 0, run = 1, loop = 1, trigger = 1 |
	var recording;
	recording = RecordBuf.ar(In.ar(in, 7), bufnum, offset, recLevel, preLevel, run, loop, trigger);
	FreeSelfWhenDone.kr(recording);
}),
SynthDef("bufrecord_8", { | in = 0, bufnum = 0, offset = 0, recLevel = 1, preLevel = 0, run = 1, loop = 1, trigger = 1 |
	var recording;
	recording = RecordBuf.ar(In.ar(in, 8), bufnum, offset, recLevel, preLevel, run, loop, trigger);
	FreeSelfWhenDone.kr(recording);
})
]