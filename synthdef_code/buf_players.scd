

// racing through the buffer in varying speeds with LFDNoise3

SynthDef("Blfn3", { | out = 0, bufnum = 0, rate = 0.045 |
	Out.ar(out, BufRd.ar(1, bufnum, LFDNoise3.ar(rate) * BufFrames.ir(bufnum));
}).load(Server.default);


// As above but with LFDNoise1. Speed will be constant during each time interval
SynthDef("Blfn1", { | out = 0, bufnum = 0, rate = 0.045 |
	Out.ar(out, BufRd.ar(1, bufnum, LFDNoise1.ar(rate) * BufFrames.ir(bufnum));
}).load(Server.default);

