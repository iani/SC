/*


GtrReceive.load;


*/



GtrReceive {
	*load {
///////////////////////////////////////
var s;
s = Server.default;

SynthDef(\bufPat, { | out=0, vol = 0.5, bufnum = 0, gate = 1.0, rate = 1, startPos = 0, amp = 1.0, 
	att = 0.1, dec = 0.5, sus = 1, rls = 0.5, lvl=0.8,
	pan = 0, wid = 2, loop = 0|
	var audio, env;
	env =  EnvGen.kr(Env.perc(att, rls, dec), gate, doneAction:2);

	rate = rate * BufRateScale.kr(bufnum);
	startPos = startPos * BufFrames.kr(bufnum);
	
	audio = BufRd.ar(1, bufnum, Phasor.ar(1, rate, startPos, BufFrames.ir(bufnum)), loop, 4);
	audio =  audio;
	audio = Pan2.ar(audio, pan, amp);
	//audio = PanAz.ar( 8, audio, pan, amp*4, width: wid);
	Out.ar(out, env *audio);
}).add;

~gtr1 = Buffer.read(s, "sounds/_Evfer/gtr1.aiff");

//	Tags
~klankA01 = Preceive(
	
	\gtr1 -> {
		Pdef(\bufRand,  
			Pbind(*[
				\instrument,	\bufPat,
				\dur,		Pseq([~yurukSemaiDur, ~yurukSemaiDur, ~yurukSemaiDur], inf),
				\amp, 		Pseq([~yurukSemaiAmp, ~yurukSemaiAmp, ~yurukSemaiAmp]/2, inf),
				\legato,		Pseq([ (0.8..2.8) ], inf),
				\startPos,	Pseq([ (0.5..0.0) ], inf),
				\rate,	Pseq([
							Prand([
								Pseq([~rastRateC1, ~rastRateD2, ~rastRateC6, ~rastRateC4, ~rastRateC5, ~rastRateB7, ~rastRateC7, ~rastRateB7 ]),
								Pseq([~rastRateC1, ~rastRateC2, ~rastRateC3, ~rastRateC4, ~rastRateD2, ~rastRateD4, ~rastRateC7, ~rastRateB7 ])
							]),
							Pseq([ ~rastRateC1, Prand([~rastRateB2, ~rastRateB6]), ~rastRateC5, Prand([~rastRateB5, ~rastRateB7, ~rastRateC3]) ]),
							Pseq([ ~rastRateC3, Prand([~rastRateB2, ~rastRateB6]), ~rastRateC7, Prand([~rastRateB5, ~rastRateB7, ~rastRateC3]) ])
						], inf)/2,
						
				
				\att,		Pseq([ (0.3..0.05) ], inf),
				\rls,		Prand([ (1.0..3.8) ], inf),
				\pan,  		Prand([ (-3.0..3.0) ], inf),
				\group,		~piges,
				\out, 		Pseq([~mainBus], inf),
				\bufnum,		Pseq([ ~gtr1 ], inf)
			
			])
		);
		},
	\gtr2 -> {
		Pdef(\bufRand,  
			Pbind(*[
				\instrument,	\bufPat,
				\dur,		Pseq([~yurukSemaiDur, ~yurukSemaiDur, ~yurukSemaiDur]*1.5, inf),
				\amp, 		Pseq([~yurukSemaiAmp, ~yurukSemaiAmp, ~yurukSemaiAmp]/2, inf),
				\legato,		Pseq([ (0.8..2.8) ], inf),
				\startPos,	0.1,
				\rate,	Pseq([
							Prand([
								Pseq([~rastRateC1, ~rastRateD2, ~rastRateC6, ~rastRateC4, ~rastRateC5, ~rastRateB7, ~rastRateC7, ~rastRateB7 ]),
								Pseq([~rastRateC1, ~rastRateC2, ~rastRateC3, ~rastRateC4, ~rastRateD2, ~rastRateD4, ~rastRateC7, ~rastRateB7 ])
							]),
							Pseq([ ~rastRateC1, Prand([~rastRateB2, ~rastRateB6]), ~rastRateC5, Prand([~rastRateB5, ~rastRateB7, ~rastRateC3]) ]),
							Pseq([ ~rastRateC3, Prand([~rastRateB2, ~rastRateB6]), ~rastRateC7, Prand([~rastRateB5, ~rastRateB7, ~rastRateC3]) ])
						], inf)/2,
						
				
				\att,		Pseq([ (0.3..0.05) ], inf),
				\rls,		Prand([ (1.0..3.8) ], inf),
				\pan,  		Prand([ (-3.0..3.0) ], inf),
				\group,		~piges,
				\out, 		Pseq([~mainBus], inf),
				\bufnum,		Pseq([ ~gtr1 ], inf)
			
			])
		);
		},
	\gtr3 -> {
		Pdef(\bufRand,  
			Pbind(*[
				\instrument,	\bufPat,
				\dur,		Pseq([~yurukSemaiDur, ~yurukSemaiDur, ~yurukSemaiDur], inf),
				\amp, 		Pseq([~yurukSemaiAmp, ~yurukSemaiAmp, ~yurukSemaiAmp]/2, inf),
				\legato,		Pseq([ (0.8..2.8) ], inf),
				\startPos,	Pseq([ (0.3..0.05) ], inf),
				\rate,	Pseq([
							Prand([
								Pseq([~rastRateC1, ~rastRateD2, ~rastRateC6, ~rastRateC4, ~rastRateC5, ~rastRateB7, ~rastRateC7, ~rastRateB7 ]),
								Pseq([~rastRateC1, ~rastRateC2, ~rastRateC3, ~rastRateC4, ~rastRateD2, ~rastRateD4, ~rastRateC7, ~rastRateB7 ])
							]),
							Pseq([ ~rastRateC1, Prand([~rastRateB2, ~rastRateB6]), ~rastRateC5, Prand([~rastRateB5, ~rastRateB7, ~rastRateC3]) ]),
							Pseq([ ~rastRateC3, Prand([~rastRateB2, ~rastRateB6]), ~rastRateC7, Prand([~rastRateB5, ~rastRateB7, ~rastRateC3]) ])
						], inf),
						
				
				\att,		Pseq([ (0.3..0.05) ], inf),
				\rls,		Prand([ (1.0..3.8) ], inf),
				\pan,  		Prand([ (-3.0..3.0) ], inf),
				\group,		~piges,
				\out, 		Pseq([~mainBus], inf),
				\bufnum,		Pseq([ ~gtr1 ], inf)
			
			])
		);
		},
	\gtr4 -> {
		Pdef(\bufRand,  
			Pbind(*[
				\instrument,	\bufPat,
				\dur,		Pseq([~yurukSemaiDur, ~yurukSemaiDur, ~yurukSemaiDur]*1.5, inf),
				\amp, 		Pseq([~yurukSemaiAmp, ~yurukSemaiAmp, ~yurukSemaiAmp]/2, inf),
				\legato,		Pseq([ (0.8..2.8) ], inf),
				\startPos,	Pseq([ (0.3..0.5) ], inf),
				\rate,	Pseq([
							Prand([
								Pseq([~rastRateC1, ~rastRateD2, ~rastRateC6, ~rastRateC4, ~rastRateC5, ~rastRateB7, ~rastRateC7, ~rastRateB7 ]),
								Pseq([~rastRateC1, ~rastRateC2, ~rastRateC3, ~rastRateC4, ~rastRateD2, ~rastRateD4, ~rastRateC7, ~rastRateB7 ])
							]),
							Pseq([ ~rastRateC1, Prand([~rastRateB2, ~rastRateB6]), ~rastRateC5, Prand([~rastRateB5, ~rastRateB7, ~rastRateC3]) ]),
							Pseq([ ~rastRateC3, Prand([~rastRateB2, ~rastRateB6]), ~rastRateC7, Prand([~rastRateB5, ~rastRateB7, ~rastRateC3]) ])
						], inf)/2,
						
				
				\att,		Pseq([ (0.3..0.05) ], inf),
				\rls,		Prand([ (1.0..3.8) ], inf),
				\pan,  		Prand([ (-3.0..3.0) ], inf),
				\group,		~piges,
				\out, 		Pseq([~mainBus], inf),
				\bufnum,		Pseq([ ~gtr1 ], inf)
			
			])
		);
		},
	\gtr5 -> {
		Pdef(\bufRand,  
			Pbind(*[
				\instrument,	\bufPat,
				\dur,		Pseq([~yurukSemaiDur, ~yurukSemaiDur, ~yurukSemaiDur], inf),
				\amp, 		Pseq([~yurukSemaiAmp, ~yurukSemaiAmp, ~yurukSemaiAmp]/2, inf),
				\legato,		Pseq([ (0.8..2.8) ], inf),
				\startPos,	Pseq([ (0.3..0.05) ], inf),
				\rate,	Pseq([
							Prand([
								Pseq([~rastRateC1, ~rastRateD2, ~rastRateC6, ~rastRateC4, ~rastRateC5, ~rastRateB7, ~rastRateC7, ~rastRateB7 ]),
								Pseq([~rastRateC1, ~rastRateC2, ~rastRateC3, ~rastRateC4, ~rastRateD2, ~rastRateD4, ~rastRateC7, ~rastRateB7 ])
							]),
							Pseq([ ~rastRateC1, Prand([~rastRateB2, ~rastRateB6]), ~rastRateC5, Prand([~rastRateB5, ~rastRateB7, ~rastRateC3]) ]),
							Pseq([ ~rastRateC3, Prand([~rastRateB2, ~rastRateB6]), ~rastRateC7, Prand([~rastRateB5, ~rastRateB7, ~rastRateC3]) ])
						], inf),
						
				
				\att,		Pseq([ (0.3..0.05) ], inf),
				\rls,		Prand([ (1.0..3.8) ], inf),
				\pan,  		Prand([ (-3.0..3.0) ], inf),
				\group,		~piges,
				\out, 		Pseq([~mainBus], inf),
				\bufnum,		Pseq([ ~gtr1 ], inf)
			
			])
		);
		}
).play;

//////////////////////////////////////
	}
	*unLoad { 
	}
}

