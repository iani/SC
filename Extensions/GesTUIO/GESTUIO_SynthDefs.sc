/* 110406

This is the GESTUIO_SynthDef subclass. It enables all SynthDefs created for the Multitouch Gestus Surface to be loaded from here. "at least is a try :)"

*/

GESTUIO_SynthDef : Parametric_GESTUIO {
	
	var <>synthDefs;
	
	init { 
		
// Here load all SynthDefs

synthDefs = [
	
	SynthDef(\Grain02, {| in = 10, dur = 0.5, freq = 80  |
	Out.ar(0,
		FreeVerb.ar(FMGrain.ar(LFDNoise3.ar(in), dur, 40, freq, 
			PinkNoise.kr([10, 150]).range(10, 200),
			EnvGen.kr(
				Env([0, 0.1, 0], [2, 2], \sine, 1), gate: 1, levelScale: 1, doneAction: 2)
			), room: (0.7), damp: (0.9)))
		
	}),
		
	SynthDef("blobBorn2", { |freq = 200|
		Out.ar(0, FreeVerb.ar(PMOsc.ar(LFNoise0.kr([10 ,10], freq ,freq), Line.kr(30, 40, 6), 0.5), room: (0.2), mix: (0.2), damp:(0.2)))
	}),
	
	SynthDef("Rythym01", {
		Out.ar(0, FreeVerb.ar(Streson.ar(PMOsc.ar(LFNoise0.kr([30, 40]), Line.kr(30, 40, 100), 8), Line.kr(LFCub.kr(0.5, 0.5), LFCub.kr				(0.5, 0.5), 1, 50, 90).reciprocal, 0.9, 0.5), room: (0.5), mix: (0.4), damp: (0.5)))

	}),

	SynthDef("Rythym02", { 
		Out.ar(0, Streson.ar(LFPulse.ar([10, 5], 0, EnvGen.kr(Env.asr(5, 0.5, 2), 1.0) * 0.3), LFCub.kr(LFCub.kr(0.1, 0.5), -1, 				1, 50, 90).reciprocal, 0.9, 0.3)) 
	}),
	
	SynthDef("Rythym03", { | freq1 = 30, freq2 = 40, decay = 0.1|
		Out.ar(0, AllpassN.ar( Streson.ar(Blip.ar(BrownNoise.kr([freq1, freq2]), Line.kr(LFCub.kr(0.3, 20), LFCub.kr(0.3, 30), 1, 500, 				900).reciprocal, 1, 0.1), 0.1, decay, 1)))
	}),
	
	SynthDef("blobBorn3", { |bufnum = 0, rate = 1, room = 0.1|
		Out.ar(0, FreeVerb.ar(PlayBuf.ar(2, bufnum, BufRateScale.kr(bufnum) * rate, doneAction:2), room))	}),
		
		SynthDef("MG_Rythym", { |bufnum = 0, winsize = 0.01, grainrate = 10, rate = 1|
		Out.ar(0, FreeVerb.ar(MonoGrain.ar(PlayBuf.ar(2, bufnum, BufRateScale.kr(bufnum) * rate, loop: 1, doneAction:2),
		winsize, grainrate, 0, 1, 1), damp: (3)))	
	}),

	SynthDef("Rythym04", { |bufnum = 0, rate = 1,  freq =0 |
		Out.ar(0, FreeVerb.ar(LPF.ar(PlayBuf.ar(2, bufnum, BufRateScale.kr(bufnum) * rate, loop: 1, doneAction:2), 
		mul:(0.8) ,freq:(freq)), damp: (0.5)))
		
	});

]; 

		
	}
}
