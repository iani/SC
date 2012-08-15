Rclock.start;

//:=---

~c1 = { | out = 0, amp = 1, modr = 3, inr = 1, kr = 1, rotr = 0.2 |
	var input, kernel, source;	
	input = PlayBuf.ar(1, \weddell9min.buffer.bufnum, loop: 1, rate: inr);
	kernel = PlayBuf.ar(1, \swallows10minL.buffer.bufnum, loop: 1, rate: kr);
	source = Convolution.ar(input, kernel, 1024, LFDNoise1.kr(modr));
	// must have power of two framesize
	Out.ar(out, amp * PanAz.ar(2, source * Lag.kr(amp, 3), LFDNoise1.kr(rotr).range(-1, 1)));
}.play(args: [\inr, -0.25, \modr, 0.1]);

//:-----
~c1.set(\inr, -0.5);
~c1.set(\inr, 1);
~c1.set(\inr, 3);
~c1.set(\inr, -0.25);
~c1.set(\modr, 0.1);
~c1.set(\modr, 1.1);
~c1.set(\modr, 1.1);
~c1.set(\modr, 11);
~c1.set(\rotr, 0.1);
~c1.set(\modr, 10.1);
~c1.set(\inr, 1.25);
~c1.set(\kr, 1.5);
~c1.set(\amp, 1.5);
~c1.set(\amp, 2.5);
~c1.set(\amp, 0.5);
~c1.set(\amp, 0.1);
~c1.set(\amp, 0.01);
~c1.free;
//:---

~c2 = { | out = 0, amp = 1, modr = 3, inr = 1, kr = 1, rotr = 0.2 |
	var input, kernel, source;	
	input = PlayBuf.ar(1, \swallows10minL.buffer.bufnum, loop: 1, rate: inr);
	kernel = PlayBuf.ar(1, \weddell9min.buffer.bufnum, loop: 1, rate: kr);
	source = Convolution.ar(input, kernel, 1024, LFDNoise1.kr(modr));
	// must have power of two framesize
	Out.ar(out, amp * PanAz.ar(2, source * Lag.kr(amp, 3), LFDNoise1.kr(rotr).range(-1, 1)));
}.play(args: [\inr, 1, \kr, -0.25, \modr, 0.1]);

//:----
~c2.set(\kr, -0.5);
~c2.set(\kr, 1);
~c2.set(\kr, 3);
~c2.set(\kr, -0.5);
~c2.set(\kr, 1);
~c2.set(\kr, 3);
~c2.set(\inr, -0.25);
~c2.set(\modr, 0.1);
~c2.set(\modr, 1.1);
~c2.set(\modr, 1.1);
~c2.set(\modr, 11);
~c2.set(\rotr, 0.1);
~c2.set(\rotr, 10.1);
~c2.set(\modr, 10.1);
~c2.set(\inr, 1.25);
~c2.set(\kr, 1.5);
~c2.set(\amp, 1.5);
~c2.set(\amp, 2.5);
~c2.set(\amp, 0.5);
~c2.set(\amp, 0.1);
~c2.set(\amp, 0.01);
~c2.free;

//:---

~w1 = { | out = 0, amp = 1, modr = 3, inr = 1, kr = 1, rotr = 0.2, wipe = 0, wipelag = 5 |
	var input, chain, source;	
	input = PlayBuf.ar(1, \weddell9min.buffer.bufnum, loop: 1, rate: inr);
	chain = FFT(LocalBuf(2048), input);
	source = IFFT(PV_BrickWall(chain, Lag.kr(wipe, wipelag)));
	// must have power of two framesize
	Out.ar(out, PanAz.ar(2, source * Lag.kr(amp, 3), LFDNoise1.kr(rotr).range(-1, 1)));
}.play(args: [\inr, 1, \wipe, 0.35, \rotr, 0.5]);

//:-----
~w1.set(\wipelag, 40, \wipe, -0.25);
~w1.set(\wipelag, 30, \wipe, -0.99);
~w1.set(\wipelag, 60, \wipe, 0.25);
~w1.set(\wipe, 0.15);
~w1.set(\amp, 0.15);
~w1.set(\amp, 0.5);
//:-----
~w1.set(\wipelag, 3, \wipe, -0.98);
//:-----
~w1.set(\wipelag, 3, \wipe, 0.250);
//:-----
~w1.set(\amp, 0.25);
//:-----
~w1.set(\amp, 0.098);
~w1.set(\amp, 0.01);
~w1.free;
//:-------
~s1 = { | out = 0, amp = 1, modr = 3, inr = 1, kr = 1, rotr = 0.2, wipe = 0, wipelag = 5 |
	var input, chain, source;	
	input = PlayBuf.ar(1, \swallows10minL.buffer.bufnum, loop: 1, rate: inr);
	chain = FFT(LocalBuf(2048), input);
	source = IFFT(PV_BrickWall(chain, Lag.kr(wipe, wipelag)));
	// must have power of two framesize
	Out.ar(out, PanAz.ar(2, source * Lag.kr(amp, 3), LFDNoise1.kr(rotr).range(-1, 1)));
}.play(args: [\inr, 1, \wipe, 0.25, \amp, 0.2]);

//:-----
~s1.set(\wipelag, 60, \wipe, -0.95);
~s1.set(\wipelag, 60, \wipe, 0.25);
//:-----
~s1.set(\wipe, -0.98);
~s1.set(\wipelag, 120, \wipe, 0.25);
//:-----
~s1.set(\amp, 0.25);
//:-----
~s1.set(\amp, 0.098);
//:-----
~s1.set(\rotr, 0.25);
//:-----
~s1.set(\rotr, 1.25);
//:-----
~s1.set(\rotr, 0.098);
//:-----
~s1.free;

//:-------
~q1 = { | out = 0, amp = 1, modr = 3, inr = 1, kr = 1, rotr = 0.2, wipe = 0 |
	var input, chain, source;	
	var inA, chainA, inB, chainB;
	inA = PlayBuf.ar(1, \swallows10minL.buffer.bufnum, loop: 1, rate: inr);	
	inB = PlayBuf.ar(1, \weddell9min.buffer.bufnum, loop: 1, rate: kr);
	chainA = FFT(LocalBuf(2048), inA);
	chainB = FFT(LocalBuf(2048), inB);
	chain = PV_CopyPhase(chainA, chainB); 
	source = IFFT(chain);
	// must have power of two framesize
	Out.ar(out, PanAz.ar(2, source * Lag.kr(amp, 3), LFDNoise1.kr(rotr).range(-1, 1)));
}.play(args: [\inr, 1]);

//:-----
~q1.set(\wipe, 0.25);
//:-----
~q1.set(\wipe, -0.98);
//:-----
~q1.set(\amp, 0.25);
//:-----
~q1.set(\amp, 0.098);
//:-----
~q1.set(\rotr, 0.25);
//:-----
~q1.set(\rotr, 0.098);
//:-----
~q1.free(\amp, 0.25);

//:=---


