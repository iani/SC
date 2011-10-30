/* (IZ 2005-09-22) { 
Modified 081025 to use PinkNoise as this makes locating the source in space easier. 
} */

SynthDef("multichanneltest", { | out = 0, amp = 0.1, rate = 1 |
	Out.ar(out, PinkNoise.ar(Decay2.kr(Impulse.kr(rate, 0, amp))));
});


