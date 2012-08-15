From: http://www.fredrikolofsson.com/f0blog/?q=node/531

on: 12 07 26

//:
SynthDef("recur", { | freq = 60 |
	f={|o,i|if(i>0,{SinOsc.ar([i,i+1e-4]**2*f.(o,i-1),f.(o,i-1)*1e-4,f.(o,i-1))},o)};
	
	Out.ar(0, f.(freq,6)/freq);
}).add;

//:
a = Synth('recur');
//:
a.set(\freq, 5);


a = Synth('recur', [\freq, 5.01]);
a = Synth('recur', [\freq, 6.02]);

a = Synth('recur', [\freq, 4.02]);

a = Synth('recur', [\freq, 100.02]);
