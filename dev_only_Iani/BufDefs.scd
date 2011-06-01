

Udef('playbuf0', { | buf | Out.ar(0, PlayBuf.ar(1, buf)) });


Udef('playbuf', { | buf, rate = 1, trigger = 1, startPos = 0, loop = 1 |
	var source;
	source = PlayBuf.ar(1, buf, rate, trigger, startPos, loop, 2);
	Out.ar(0, source);
});

