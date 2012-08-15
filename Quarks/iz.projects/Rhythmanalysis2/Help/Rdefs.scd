

Udef('playbuf', { | buf, rate = 1, trigger = 1, startPos = 0, loop = 0, out = 0, amp = 1 |
	var source;
	source = PlayBuf.ar(1, buf, rate, trigger, startPos, loop, 2);
	Rout.ar(out, source * Lag.kr(amp, 3));
});

Udef('playbuf24', { | buf, rate = 1, trigger = 1, startPos = 0, loop = 0, amp = 1 |
	var source;
	source = PlayBuf.ar(1, buf, rate, trigger, startPos, loop, 2);
	Rout.ar((0..23), source * Lag.kr(amp, 3));
});



"=========================== Udefs loaded ===========================".postln;