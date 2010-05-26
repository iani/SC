/* 8 - channel output test for Tascam DM 32000
	
IZ 100520
*/


a = { | out = 0, amp = 0.1 | Out.ar(out, amp * WhiteNoise.ar(Decay.kr(Impulse.kr(5)))) }.play;

a.set(\amp, 0.02);

(
r = {  
	inf do: { | c |
		a.set(\out, c % 8); 
		1.wait;
	}
}.fork
)

r.stop;
a.set(\amp, 0);

a.free