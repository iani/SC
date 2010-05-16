// IZ 080817

SynthDef('pan4xy', { | out = 0, in = 0, xpos = 0, ypos = 0, level = 1 |
	var src;
	src = Pan4.ar(In.ar(in), xpos, ypos, level);
	Out.ar(out, src);
});
