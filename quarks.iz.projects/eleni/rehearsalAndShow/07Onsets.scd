
(
s.boot.doWhenBooted {
	// Prepare the buffers
	b = Buffer.alloc(s, 512);
	// Feel free to load a more interesting clip!
	// a11wlk01 is not an ideal example of musical onsets.
	d = Buffer.read(s, "sounds/a11wlk01.wav");
};
)

////////////////////////////////////////////////////////////////////////////////////////////////
// Move the mouse to vary the threshold
(
x = { | threshold = 0.5, out = 100 |
	var sig, chain, onsets, pips;
	
	// A simple generative signal
//	sig = LPF.ar(Pulse.ar(TIRand.kr(63,75,Impulse.kr(2)).midicps), LFNoise2.kr(0.5).exprange(100, 10000)) * Saw.ar(2).range(0, 1);
	// or, uncomment this line if you want to play the buffer in
	//sig = PlayBuf.ar(1, d, BufRateScale.kr(d), loop: 1);

	sig = In.ar(8);	
	chain = FFT(b, sig);

	onsets = Onsets.kr(chain, threshold, \rcomplex);
	SendReply.kr(onsets, \onset);
	// You'll hear percussive "ticks" whenever an onset is detected
	pips = WhiteNoise.ar(EnvGen.kr(Env.perc(0.001, 0.1, 0.2), onsets));
	Out.ar(5, Pan2.ar(pips, 0.75, 1));

}.play;
)

x.set(\threshold, 0.5);
x.set(\threshold, 0.9);

x.free; // Free the synth

r = OSCresponder(nil, \onset, {
	"onset".postln;
	NetAddr("192.0.0.21", 57120).sendMsg(\onset);
}).add;

NetAddr("192.0.0.21", 57110).sendMsg(\onset);
