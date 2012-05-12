Rclock.start;

//:=---

~c1 = { | out = 0, amp = 1, modr = 3, inr = 1, kr = 1, rotr = 0.2 |
	var input, kernel, source;	
	input = PlayBuf.ar(1, \weddell9min.buffer.bufnum, loop: 1, rate: inr);
	kernel = PlayBuf.ar(1, \swallows10minL.buffer.bufnum, loop: 1, rate: kr);
	source = Convolution.ar(input, kernel, 1024, LFDNoise1.kr(modr));
	// must have power of two framesize
	Out.ar(out, PanAz.ar(2, source * Lag.kr(amp, 3), LFDNoise1.kr(rotr).range(-1, 1)));
}.play(args: [\inr, -0.25, \modr, 0.1]);


//:----
~c1.set(\inr, -0.5);
//:----
~c1.set(\inr, 1);
//:----
~c1.set(\inr, 3);
//:---
~c1.set(\inr, -0.25);
//:---
~c1.set(\modr, 0.1);
//:---
~c1.set(\modr, 1.1);
//:---
~c1.set(\rotr, 0.1);
//:---
~c1.set(\modr, 10.1);
//:----
~c1.set(\inr, 1.25);
//:----
~c1.set(\kr, 1.5);
//:---- 
~c1.set(\amp, 0.5);
//:---- 
~c1.set(\amp, 0.1);
//:---- 
~c1.set(\amp, 0.01);
//:----
~c1.free;


//:=---

~c2 = { | out = 0, amp = 1, modr = 3, inr = 1, kr = 1, rotr = 0.2 |
	var input, kernel, source;	
	 kernel = PlayBuf.ar(1, \weddell9min.buffer.bufnum, loop: 1, rate: inr);
	input = PlayBuf.ar(1, \swallows10minL.buffer.bufnum, loop: 1, rate: kr);
	source = Convolution.ar(input, kernel, 1024, LFDNoise1.kr(modr));
	// must have power of two framesize
	Out.ar(out, PanAz.ar(2, source * Lag.kr(amp, 3), LFDNoise1.kr(rotr).range(-1, 1)));
}.play(args: [\inr, -0.25, \modr, 0.1]);


//:----
~c2.set(\inr, -0.5);
//:----
~c2.set(\inr, 1);
//:----
~c2.set(\inr, 3);
//:---
~c2.set(\inr, -0.25);
//:---
~c2.set(\modr, 0.1);
//:---
~c2.set(\rotr, 0.1);
//:---
~c2.set(\modr, 10.1);
//:----
~c2.set(\inr, 1.25);
//:----
~c2.set(\kr, 1.5);
//:---- 
~c2.set(\amp, 0.5);
//:---- 
~c2.set(\amp, 0.1);
//:---- 
~c2.set(\amp, 0.01);
//:----
~c2.free;




//:---

~w1 = { | out = 0, amp = 1, modr = 3, inr = 1, kr = 1, rotr = 0.2, wipe = 0, wipelag = 5 |
	var input, chain, source;	
	input = PlayBuf.ar(1, \weddell9min.buffer.bufnum, loop: 1, rate: inr);
	chain = FFT(LocalBuf(2048), input);
	source = IFFT(PV_BrickWall(chain, Lag.kr(wipe, wipelag)));
	// must have power of two framesize
	Out.ar(out, PanAz.ar(2, source * Lag.kr(amp, 3), LFDNoise1.kr(rotr).range(-1, 1)));
}.play(args: [\inr, 1, \wipe, 0.35]);

//:-----
~w1.set(\wipelag, 10, \wipe, -0.25);
~w1.set(\wipe, 0.15);
//:-----
~w1.set(\wipe, -0.98);
//:-----
~w1.set(\amp, 0.25);
//:-----
~w1.set(\amp, 0.098);

//:-------
~s1 = { | out = 0, amp = 1, modr = 3, inr = 1, kr = 1, rotr = 0.2, wipe = 0, wipelag = 5 |
	var input, chain, source;	
	input = PlayBuf.ar(1, \swallows10minL.buffer.bufnum, loop: 1, rate: inr);
	chain = FFT(LocalBuf(2048), input);
	source = IFFT(PV_BrickWall(chain, Lag.kr(wipe, wipelag)));
	// must have power of two framesize
	Out.ar(out, PanAz.ar(2, source * Lag.kr(amp, 3), LFDNoise1.kr(rotr).range(-1, 1)));
}.play(args: [\inr, 1, \wipe, -0.95, \amp, 0.2]);

//:-----
~s1.set(\wipe, -0.95);
//:-----
~s1.set(\wipe, -0.98);
//:-----
~s1.set(\amp, 0.25);
//:-----
~s1.set(\amp, 0.098);
//:-----
~s1.set(\rotr, 0.25);
//:-----
~s1.set(\rotr, 0.098);
//:-----
~s1.free(\amp, 0.25);

//:------------------

~w1 = { | out = 0, amp = 1, modr = 3, inr = 1, kr = 1, rotr = 0.2, wipe = 0, wipelag = 5 |
	var input, chain, source;	
	input = PlayBuf.ar(1, \weddell9min.buffer.bufnum, loop: 1, rate: inr);
	chain = FFT(LocalBuf(2048), input);
	source = IFFT(PV_BrickWall(chain, Lag.kr(wipe, 5)));
	// must have power of two framesize
	Out.ar(out, PanAz.ar(2, source * Lag.kr(amp, 3), LFDNoise1.kr(rotr).range(-1, 1)));
}.play(args: [\inr, 1]);

//:-----
~w1.set(\wipe, 0.25);
//:-----
~w1.set(\wipe, -0.98);
//:-----
~w1.set(\amp, 0.25);
//:-----
~w1.set(\amp, 0.098);

//:-------
~s1 = { | out = 0, amp = 1, modr = 3, inr = 1, kr = 1, rotr = 0.2, wipe = 0 |
	var input, chain, source;	
	input = PlayBuf.ar(1, \swallows10minL.buffer.bufnum, loop: 1, rate: inr);
	chain = FFT(LocalBuf(2048), input);
	source = IFFT(PV_BrickWall(chain, Lag.kr(wipe, 5)));
	// must have power of two framesize
	Out.ar(out, PanAz.ar(2, source * Lag.kr(amp, 3), LFDNoise1.kr(rotr).range(-1, 1)));
}.play(args: [\inr, 1]);

//:-----
~s1.set(\wipe, 0.25);
//:-----
~s1.set(\wipe, -0.98);
//:-----
~s1.set(\amp, 0.25);
//:-----
~s1.set(\amp, 0.098);
//:-----
~s1.set(\rotr, 0.25);
//:-----
~s1.set(\rotr, 0.098);
//:-----
~s1.free(\amp, 0.25);

//:=---

~c1 = { | out = 0, amp = 1, modr = 3, inr = 1, kr = 1, rotr = 0.2 |
	var input, kernel, source;	
	input = PlayBuf.ar(1, \weddell9min.buffer.bufnum, loop: 1, rate: inr);
	kernel = PlayBuf.ar(1, \swallows10minL.buffer.bufnum, loop: 1, rate: kr);
	source = Convolution.ar(input, kernel, 1024, LFDNoise1.kr(modr));
	// must have power of two framesize
	Out.ar(out, PanAz.ar(2, source * Lag.kr(amp, 3), LFDNoise1.kr(rotr).range(-1, 1)));
}.play(args: [\inr, -0.25, \modr, 0.1]);


//:----
~c1.set(\inr, -0.5);
//:----
~c1.set(\inr, 1);
//:----
~c1.set(\inr, 3);
//:---
~c1.set(\inr, -0.25);
//:---
~c1.set(\modr, 0.1);
//:---
~c1.set(\rotr, 0.1);

//:---
~c1.set(\modr, 10.1);

//:----
~c1.set(\inr, 1.25);

//:----
~c1.set(\kr, 1.5);


//:----- 



//:-----


//
~c1 = {
	var input, kernel;	
	input = PlayBuf.ar(1, \weddell1.buffer.bufnum, loop: 1);
	kernel = PlayBuf.ar(1, \swallows10minL.buffer.bufnum, loop: 1);

	// must have power of two framesize
	Convolution.ar(input, kernel, 1024, LFNoise0.kr(3));
}.play;

//:------
~convn03 = {
	var input, kernel;	
	input = PlayBuf.ar(1, \weddell1.buffer.bufnum, loop: 1);
	kernel = PlayBuf.ar(1, \swallows10minL.buffer.bufnum, loop: 1);

	// must have power of two framesize
	Convolution.ar(input, kernel, 1024, LFNoise1.kr(0.3));
}.play;

//:------
~convn03 = {
	var input, kernel;	
	input = PlayBuf.ar(1, \weddell1.buffer.bufnum, loop: 1);
	kernel = PlayBuf.ar(1, \swallows10minL.buffer.bufnum, loop: 1);

	// must have power of two framesize
	Convolution.ar(input, kernel, 1024, LFNoise1.kr(10));
}.play;


//:------
~convn03inv = {
	var input, kernel;	
	input = PlayBuf.ar(1, \weddell1.buffer.bufnum, loop: 1, rate: -1);
	kernel = PlayBuf.ar(1, \swallows10minL.buffer.bufnum, loop: 1);

	// must have power of two framesize
	Convolution.ar(input, kernel, 1024, LFNoise1.kr(10));
}.play;


//:------
~convn03inv = {
	var input, kernel;	
	input = PlayBuf.ar(1, \weddell1.buffer.bufnum, loop: 1, rate: -1);
	kernel = PlayBuf.ar(1, \swallows10minL.buffer.bufnum, loop: 1);

	// must have power of two framesize
	Convolution.ar(input, kernel, 1024, LFNoise1.kr(5));
}.play;


