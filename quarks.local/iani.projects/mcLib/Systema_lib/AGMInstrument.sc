AGMInstruments { // Ancient Greek Music Instruments
	*initClass {
		this.default3;
		this.logln("*initClass done", 3);
	}
	*default2 {
		SynthDef(\AGMI, { arg out=0, freq=440, amp=0.1, pan=0, gate=1;
			var z;
			z = LPF.ar(
					Mix.new(VarSaw.ar(freq + [0, Rand(-0.4,0.0), Rand(0.0,0.4)], 0, 0.3)),
					XLine.kr(Rand(4000,5000), Rand(2500,3200), 1)
				) * Linen.kr(gate, 0.01, amp * 0.7, 0.3, 2);
			Out.ar(out, Pan2.ar(z, pan));
		}, [\ir]).store;
	}
	*default {
		SynthDef(\AGMI, { arg out=0, freq=220, amp=0.1, pan=0, gate=1, gDetune=0;
			var z;
			z = LPF.ar(
				Mix.new(VarSaw.ar(freq + gDetune, 0, 0.3)),
				XLine.kr(Rand(4000,5000), Rand(2500,3200), 1)) 
					* Linen.kr(gate, 0.01, amp * 0.7, 0.3, 2);
			Out.ar(out, Pan2.ar(z, pan));
		}).store;
	}
	*default1 {
		SynthDef(\AGMI, { arg out=0, freq=220, amp=0.1, pan=0, gate=1, gDetune=0;
			var z;
			z = LPF.ar(
				Mix.new(SinOsc.ar(freq + gDetune, 0, 0.3)),
				XLine.kr(Rand(4000,5000), Rand(2500,3200), 1)) 
					* Linen.kr(gate, 0.01, amp * 0.7, 0.3, 2);
			Out.ar(out, Pan2.ar(z, pan));
		}).store;
	}
	
	*default3 {
	(
		SynthDef(\AGMI, { arg out=0, freq=220, amp=0.7, pan=0, gDetune=0, silence=1.0;
			//var z, t, sustain = Rand(0.9, 1.5), env, tt; //jrh
			var z, t, sustain = Rand(1.2, 1.8), env, tt;

			freq = freq + gDetune;
			amp = amp * AmpComp.kr(freq, 220, 0.9);
			
			//amp = amp * 0.7;
			
			tt = Impulse.ar(0);
			tt = CombL.ar(tt, 0.1, 0.01, 0.05);
			t = Decay2.ar(
				tt, 
				Rand(0.002, 0.0005), 
				0.05, 
				WhiteNoise.ar(Rand(0.1, 0.4), 0.2)
			);
			//freq = freq * Decay2.ar(tt, 0.009, Rand(0.01, 0.02), 0.2, 1); // jrh
			freq = freq; // * Decay2.ar(tt, 0.009, Rand(0.001, 0.002), 0.2, 1);


			z = CombC.ar(t, 1/30, 1/freq, sustain);
			z = LPF.ar(z, freq * 1.5) + BPF.ar(t, freq * 4, 0.2, Rand(0.05, 0.1));
			z = LeakDC.ar(z);
			env = EnvGen.kr(Env([amp, amp, 0], [sustain, 0.1]), doneAction: 2);
			OffsetOut.ar(out, (Pan2.ar(z, pan, env * 10)) * silence);
		}).store;
	 );
	}
/*
_________________________________________
(
Pdef(\x,
	Pbind(
		\instrument, \AGMI,
		\midinote, Prand((0..22), inf) + 50,
		\dur, Prand([1, 0.5, 2], inf)
	)
).play;
)
_________________________________________
(

	Pbind(\degree, Prand([ -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7 ], inf) + Prand([0, 0, 0, [0, 3], [0, 5]], inf), \strum, Pwhite(0, 0.005, inf), \dur, 0.3, *a.pat).play;
)

SynthDescLib.global.browse;

*/
}