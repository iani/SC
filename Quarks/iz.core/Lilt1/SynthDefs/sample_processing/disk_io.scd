/* IZ Monday, May 12, 2008 
Sound file reading and writing
*/

[
SynthDef("diskin_1", { | out = 0, bufnum = 0, loop = 0, level = 1 |
	Out.ar(out, DiskIn.ar(1, bufnum, loop) * level);
}),
SynthDef("diskin_2", { | out = 0, bufnum = 0, loop = 0, level = 1 |
	Out.ar(out, DiskIn.ar(2, bufnum, loop) * level);
}),
SynthDef("diskin_3", { | out = 0, bufnum = 0, loop = 0, level = 1 |
	Out.ar(out, DiskIn.ar(3, bufnum, loop) * level);
}),
SynthDef("diskin_4", { | out = 0, bufnum = 0, loop = 0, level = 1 |
	Out.ar(out, DiskIn.ar(4, bufnum, loop) * level);
}),
SynthDef("diskin_5", { | out = 0, bufnum = 0, loop = 0, level = 1 |
	Out.ar(out, DiskIn.ar(5, bufnum, loop) * level);
}),
SynthDef("diskin_6", { | out = 0, bufnum = 0, loop = 0, level = 1 |
	Out.ar(out, DiskIn.ar(6, bufnum, loop) * level);
}),
SynthDef("diskin_7", { | out = 0, bufnum = 0, loop = 0, level = 1 |
	Out.ar(out, DiskIn.ar(7, bufnum, loop) * level);
}),
SynthDef("diskin_8", { | out = 0, bufnum = 0, loop = 0, level = 1 |
	Out.ar(out, DiskIn.ar(8, bufnum, loop) * level);
}),
SynthDef("diskout_1", { | in = 0, bufnum = 0 |
	DiskOut.ar(bufnum, In.ar(in, 1));
}),
SynthDef("diskout_2", { | in = 0, bufnum = 0 |
	DiskOut.ar(bufnum, In.ar(in, 2));
}),
SynthDef("diskout_3", { | in = 0, bufnum = 0 |
	DiskOut.ar(bufnum, In.ar(in, 3));
}),
SynthDef("diskout_4", { | in = 0, bufnum = 0 |
	DiskOut.ar(bufnum, In.ar(in, 4));
}),
SynthDef("diskout_5", { | in = 0, bufnum = 0 |
	DiskOut.ar(bufnum, In.ar(in, 5));
}),
SynthDef("diskout_6", { | in = 0, bufnum = 0 |
	DiskOut.ar(bufnum, In.ar(in, 6));
}),
SynthDef("diskout_7", { | in = 0, bufnum = 0 |
	DiskOut.ar(bufnum, In.ar(in, 7));
}),
SynthDef("diskout_8", { | in = 0, bufnum = 0 |
	DiskOut.ar(bufnum, In.ar(in, 8));
})

]