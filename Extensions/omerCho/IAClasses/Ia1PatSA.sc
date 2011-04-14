/*

Ia1PatSA.load;
~part1 = SyncSender(Pbind.new);
~part1.start;
~part1.stop;
6
Ia1MainSA.unLoad;

*/

Ia1PatSA {
	classvar <action;
	*load {
		var s;
		
		s = Server.default;
		
		action =  SyncAction(\beats, { | beat ... otherStuff |
		//beat.postln;
		
			switch (beat,
				0, {
		
		
				},
				1, {},
				36, {},
				546, {

		Pdef(\taskBas1, Pbind(
			\instrument, \tascaleBass,
			\scale, Pfunc({  Scale.ionian }, inf),
			\octave, 2,
			\dur, Pseq([
					1
				] , inf),
			\amp, Pseq([ 
				0.4, 0.0, 0.0, 0.3, 0.0, 0.0, 0, 0
				], inf),
			\degree, Pseq([
					0
				] , inf),
			\brown, Pseq([ 
				0.8
				], inf),
			\rls, Pseq([0.5]*6, inf),
			\fSin, 0.5,
			\pan, Pseq([-0.3, 0.3, 0.2, -0.2 ], inf),
			\group, ~piges,
			\out, ~limBus
			
		));
		Pdef(\taskBas1).play(quant: [0, 0, 0]);
				},
				588, {
		Pdef(\taskBas1, Pbind(
			\instrument, \tascaleBass,
			\scale, Pfunc({  Scale.ionian }, inf),
			\octave, 2,
			\dur, Pseq([
					1
				] , inf),
			\amp, Pseq([ 
				0.4, 0.0, 0.0, 0.3, 0.0, 0.0, 0, 0
				], inf),
			\degree, Pseq([
					3
				] , inf),
			\brown, Pseq([ 
				0.8
				], inf),
			\rls, Pseq([0.5]*6, inf),
			\fSin, 0.5,
			\pan, Pseq([-0.3, 0.3, 0.2, -0.2 ], inf),
			\group, ~piges,
			\out, ~limBus
			
		));
	
				},
				
				608, {
		Pdef(\taskBas1, Pbind(
			\instrument, \tascaleBass,
			\scale, Pfunc({  Scale.ionian }, inf),
			\octave, 2,
			\dur, Pseq([
					1
				] , inf),
			\amp, Pseq([ 
				0.4, 0.0, 0.0, 0.3, 0.0, 0.0, 0, 0
				]*2, inf),
			\degree, Pseq([
					0
				] , inf),
			\brown, Pseq([ 
				0.8
				], inf),
			\rls, Pseq([0.5]*6, inf),
			\fSin, 0.5,
			\pan, Pseq([-0.3, 0.3, 0.2, -0.2 ], inf),
			\group, ~piges,
			\out, ~limBus
			
		));	
				},
				
				666, {
		Pdef(\lypat0).play( quant: [0, 0, 0]);
		Pdef(\lypat0, Pbind(
			\instrument, \lypat01,
			\scale, Pfunc({ Scale.phrygian }, inf),
			\octave, 3,
			\dur, Pseq([
					Pseq([1/8], 8), 1/4, 1/2, 1/2, 1/4, 1, 1/2, 
					1/2, 1, 1/4, 1/4, 1/2, 1, 1/2, 
					1/2, Pseq([1/8], 8), 1/2, 1
				]/2 , inf),
			\amp, Pseq([
					Pseq([1,0.8], 8), Pseq([ 0.8, 0.25, 0.0, 0.8, 0.7, 0.5, 0.0 ], 1),
					1, Pseq([0.4], 8),
					Pseq([ 0.6, 0.3 ], 5)  
					
				] , inf),
			\degree, Pseq([
					0
				], inf),
			\att, 0.1,
			\rls, 3,
			\brown, Pseq([
					0				
				],inf) ,
			\saw, Pseq([
					18.cos.sin/8
				],inf),
			\sin1, 100.sin*18.cos,
			\sin2, 90.0.cos.sin*8,
			\group, ~piges,
			\out, Pseq([~limBus], inf)			
		));
				},
				
				716, {
		Pdef(\taskBas1).stop;
		Pdef(\lypat0).stop;
				}
				
			)  
		})
	}
	*unLoad{
	
	action.deactivate;
	
	}
	
}