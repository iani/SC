/*


Ia1MainSA.load;
Ia1PatSA.load;
Ia1FxSA.load;
Ia1IndustSA.load;
~part1 = SyncSender(Pbind.new);
~part1.start;
~part1.stop;
6
Ia1MainSA.unLoad;

*/


/*


*/


Ia1MainSA {
	classvar <action;
	*load {
		var s;
		
		s = Server.default;
		
		action = SyncAction(\beats, { | beat ... otherStuff |
		beat.postln;
		
		if (beat == 0) {

			Ia1Groups.load;
			Ia1SynthDefs.load;
			Ia1Buffers.load;
			Ia1Busses.load;
			Ia1Osc.load;

	
		};
		if (beat == 1) {

			~xor1 = Synth.head( ~piges, \xorInt, [\out, [~revBus, ~rlpBus]]);
	
		};
		if (beat == 16) {
			~xor1.set(
				\pan, 0.00001, \cos, 0.00001,  
				\lfn1a, ~a2, \lfn2a, ~c3,
				\lfn1b, ~e3, \lfn2b, ~a3, 
				\lfbeat1, 0.5, \lfbeat2, 0.5
			);
		};
		if (beat == 36) { 
			~xor1.set(
				\pan, 0.001, \cos, 0.001,  
				\lfn1a, ~a2, \lfn2a, ~c3,
				\lfn1b, ~e3, \lfn2b, ~a3, 
				\lfbeat1, 0.5, \lfbeat2, 1
			);
		};
		if (beat == 46) {		
			~xor1.set(
				\pan, 0.01, \cos, 0.001,  
				\lfn1a, ~c2, \lfn2a, ~e4, 
				\lfn1b, ~e2, \lfn2b, ~g1,
				\lfbeat1, 0.5, \lfbeat2, 2
			);
		};
		if (beat == 56) {
			~xor1.set(
				\pan, 0.08, \cos, 1.93, 
				\lfn1a, ~a2, \lfn2a, ~c4, 
				\lfn1b, ~e3, \lfn2b, ~a2, 
				\lfbeat1, 2, \lfbeat2, 2
			);
		};
		if (beat == 66) {
			~xor1.set(
				\vol, 1.5, \amp, 1.2,
				\pan, 0.08, \cos, 1.93, 
				\lfn1a, ~a3, \lfn2a, ~d4, 
				\lfn1b, ~c2, \lfn2b, ~f2, 
				\lfbeat1, 2, \lfbeat2, 2
			);
		};
		if (beat == 76) {
			~xor1.set(
				\pan, 0.08, \cos, 1.96, 
				\lfn1a, ~a2, \lfn2a, ~c4, 
				\lfn1b, ~e3, \lfn2b, ~a1, 
				\lfbeat1, 2, \lfbeat2, 2
			);
		};
		if (beat == 86) {
			~xor1.set(
				\vol, 1, \amp, 1.0,
				\pan, 0.8, \cos, 1.9, 
				\lfn1a, ~a2, \lfn2a, ~c3, 
				\lfn1b, ~e4, \lfn2b, ~a2, 
				\lfbeat1, 2, \lfbeat2, 2
			);
		};
		//
		if (beat == 96) {
			~xor1.set(
				\pan, 0.1, \cos, 1.9,  
				\lfn1a, ~f3, \lfn2a, ~c3, 
				\lfn1b, ~a3, \lfn2b, ~d2,
				\lfbeat1, 2, \lfbeat2, 4
			);
		};
		
		//
		if (beat == 101) {
			~xor1.set(
				\pan, 0.1, \cos, 1.9,  
				\lfn1a, ~g3, \lfn2a, ~d3, 
				\lfn1b, ~b3, \lfn2b, ~e3,
				\lfbeat1, 4, \lfbeat2, 2
			);
		};
		//
		if (beat == 106) {
			~xor1.set(
				\pan, 0.1, \cos, 1.9,  
				\lfn1a, ~a3, \lfn2a, ~d3, 
				\lfn1b, ~f3, \lfn2b, ~e3,
				\lfbeat1, 4, \lfbeat2, 2
			);
		};
		//
		if (beat == 116) {
			~xor1.set(
				\vol, 1.2, \amp, 1.3,
				\pan, 0.15, \cos, 1.9, 
				\lfn1a, ~a3, \lfn2a, ~c4, 
				\lfn1b, ~a5, \lfn2b, ~a2, 
				\lfbeat1, 4, \lfbeat2, 4
			);
		};
		//
		if (beat == 126) {
			~xor1.set(
				\vol, 1.2, \amp, 1.3,
				\pan, 0.15, \cos, 1.9, 
				\lfn1a, ~g3, \lfn2a, ~b4, 
				\lfn1b, ~g5, \lfn2b, ~g2, 
				\lfbeat1, 4, \lfbeat2, 4
			);
		};
		//
		if (beat == 134) {
			~xor1.set(
				\vol, 1.2, \amp, 1.3,
				\pan, 0.15, \cos, 1.9, 
				\lfn1a, ~a3, \lfn2a, ~e4, 
				\lfn1b, ~a5, \lfn2b, ~a2, 
				\lfbeat1, 4, \lfbeat2, 4
			);
		};
		//
		if (beat == 158) {
			~xor1.set(
				\vol, 1.2, \amp, 1.3,
				\pan, 0.15, \cos, 1.9, 
				\lfn1a, ~c3, \lfn2a, ~g4, 
				\lfn1b, ~c5, \lfn2b, ~c2, 
				\lfbeat1, 4, \lfbeat2, 4
			);
		};
		//
		if (beat == 166) {
			~xor1.set(
				\vol, 1.2, \amp, 1.3,
				\pan, 0.15, \cos, 1.9, 
				\lfn1a, ~c3, \lfn2a, ~f4, 
				\lfn1b, ~c5, \lfn2b, ~c2, 
				\lfbeat1, 4, \lfbeat2, 4
			);
			
		};
		
		if (beat == 176) {
					   ///////////////O S C 1////////////
			~xorOsc= Synth.head( ~piges, \xorIntOsc, [\out, [~revBus, ~dlyBus, ~wahBus]]); 
			~xor1.set(
				\vol, 1, \amp, 1,
				\pan, 0.15, \cos, 1.9, 
				\lfn1a, ~a3, \lfn2a, ~c4, 
				\lfn1b, ~a5, \lfn2b, ~a2, 
				\lfbeat1, 14, \lfbeat2, 4
			);

		};
		
	switch (beat,
		196, {  
				~xorOsc.set(
				\pan, 6.15, \cos, 1.9, 
				\lfn1a, ~a3, \lfn2a, ~c4,
				\lfn1b, ~a4, \lfn2b, ~a2, 
				\lfbeat1, 4, \lfbeat2, 2
			);
				~xor1.set(
					\vol, 1.2,
					\pan, 0.15, \cos, 1.9, 
				\lfn1a, ~a3, \lfn2a, ~c4, 
				\lfn1b, ~a4, \lfn2b, ~a2, 
				\lfbeat1, 4, \lfbeat2, 4
			);


		},
		246, {  
				~xorOsc.set(
				\pan, 6.15, \cos, 1.9, 
				\lfn1a, ~a3, \lfn2a, ~c4,
				\lfn1b, ~a4, \lfn2b, ~a3, 
				\lfbeat1, 6, \lfbeat2, 4
			);
				~xor1.set(
					\vol, 1.2,
					\pan, 0.15, \cos, 1.9, 
				\lfn1a, ~a3, \lfn2a, ~c4, 
				\lfn1b, ~a4, \lfn2b, ~c3, 
				\lfbeat1, 4, \lfbeat2, 4
			);

		},
		286, { 
				~xorOsc.set(
				\pan, 6.15, \cos, 1.9, 
				\lfn1a, ~a3, \lfn2a, ~c4,
				\lfn1b, ~a4, \lfn2b, ~a3, 
				\lfbeat1, 4, \lfbeat2, 4
			);
				~xor1.set(
					\vol, 1.2,
					\pan, 2.65, \cos, 1.9, 
				\lfn1a, ~a3, \lfn2a, ~c4, 
				\lfn1b, ~a4, \lfn2b, ~a2, 
				\lfbeat1, 4, \lfbeat2, 4
			);

 
		},
		306, { 
				~xorOsc.set(
				\pan, 6.15, \cos, 1.9, 
				\lfn1a, ~e3, \lfn2a, ~c4,
				\lfn1b, ~a4, \lfn2b, ~a2, 
				\lfbeat1, 4, \lfbeat2, 4
			);
				~xor1.set(
					\vol, 1.2,
					\pan, 2.65, \cos, 1.9, 
				\lfn1a, ~a3, \lfn2a, ~c4, 
				\lfn1b, ~e4, \lfn2b, ~a2, 
				\lfbeat1, 4, \lfbeat2, 8
			);

 
		},
		346, {  
				~xorOsc.set(
				\pan, 6.15, \cos, 1.9, 
				\lfn1a, ~a3, \lfn2a, ~c4,
				\lfn1b, ~a4, \lfn2b, ~a2, 
				\lfbeat1, 4, \lfbeat2, 2
			);
				~xor1.set(
					\vol, 1.2,
					\pan, 0.15, \cos, 1.9, 
				\lfn1a, ~a3, \lfn2a, ~c4, 
				\lfn1b, ~a4, \lfn2b, ~a2, 
				\lfbeat1, 8, \lfbeat2, 8
			);


		},
		386, {  
				~xorOsc.set(
				\pan, 6.15, \cos, 1.9, 
				\lfn1a, ~a3, \lfn2a, ~c4,
				\lfn1b, ~a4, \lfn2b, ~a3, 
				\lfbeat1, 6, \lfbeat2, 4
			);
				~xor1.set(
					\vol, 1.2,
					\pan, 0.15, \cos, 1.9, 
				\lfn1a, ~a3, \lfn2a, ~c4, 
				\lfn1b, ~a4, \lfn2b, ~c3, 
				\lfbeat1, 4, \lfbeat2, 6
			);

		},
		406, { 
				~xorOsc.set(
				\pan, 6.15, \cos, 1.9, 
				\lfn1a, ~a3, \lfn2a, ~c4,
				\lfn1b, ~a4, \lfn2b, ~a3, 
				\lfbeat1, 4, \lfbeat2, 4
			);
				~xor1.set(
					\vol, 1.2,
					\pan, 2.65, \cos, 1.9, 
				\lfn1a, ~a3, \lfn2a, ~c4, 
				\lfn1b, ~a4, \lfn2b, ~a2, 
				\lfbeat1, 4, \lfbeat2, 4
			);

 
		},
		426, { 
				~xorOsc.set(
				\pan, 6.15, \cos, 1.9, 
				\lfn1a, ~e3, \lfn2a, ~c4,
				\lfn1b, ~a4, \lfn2b, ~a2, 
				\lfbeat1, 4, \lfbeat2, 4
			);
				~xor1.set(
					\vol, 1.2,
					\pan, 2.65, \cos, 1.9, 
				\lfn1a, ~a3, \lfn2a, ~c4, 
				\lfn1b, ~e4, \lfn2b, ~a2, 
				\lfbeat1, 4, \lfbeat2, 4
			);
 
		},

		436, { 
				~xorOsc.set(
				\pan, 6.15, \cos, 1.9, 
				\lfn1a, ~e2, \lfn2a, ~c4,
				\lfn1b, ~a4, \lfn2b, ~a3, 
				\lfbeat1, 2, \lfbeat2, 2
			);
				~xor1.set(
					\vol, 1.2,
					\pan, 2.65, \cos, 1.9, 
				\lfn1a, ~e3, \lfn2a, ~c4, 
				\lfn1b, ~e4, \lfn2b, ~a2, 
				\lfbeat1, 4, \lfbeat2, 4
			);

 
		},
		446, { 
				~xorOsc.set(
				\pan, 2.15, \cos, 1.9, 
				\lfn1a, ~d2, \lfn2a, ~d4,
				\lfn1b, ~a4, \lfn2b, ~c5, 
				\lfbeat1, 4, \lfbeat2, 4
			);
				~xor1.set(
					\vol, 1.2,
					\pan, 2.65, \cos, 1.9, 
				\lfn1a, ~d3, \lfn2a, ~c4, 
				\lfn1b, ~a5, \lfn2b, ~a2, 
				\lfbeat1, 4, \lfbeat2, 4
			);

 
		},
		456, { 
				~xorOsc.set(
				\pan, 6.15, \cos, 1.9, 
				\lfn1a, ~e2, \lfn2a, ~c4,
				\lfn1b, ~a4, \lfn2b, ~a3, 
				\lfbeat1, 2, \lfbeat2, 2
			);
				~xor1.set(
					\vol, 1.2,
					\pan, 2.65, \cos, 1.9, 
				\lfn1a, ~e3, \lfn2a, ~c4, 
				\lfn1b, ~e4, \lfn2b, ~a2, 
				\lfbeat1, 4, \lfbeat2, 4
			);

 
		},
		466, { 
				~xorOsc.set(
				\pan, 2.15, \cos, 1.9, 
				\lfn1a, ~d2, \lfn2a, ~d4,
				\lfn1b, ~a4, \lfn2b, ~c5, 
				\lfbeat1, 4, \lfbeat2, 4
			);
				~xor1.set(
					\vol, 1.2,
					\pan, 2.65, \cos, 1.9, 
				\lfn1a, ~d3, \lfn2a, ~c4, 
				\lfn1b, ~a5, \lfn2b, ~a2, 
				\lfbeat1, 4, \lfbeat2, 4
			);

 
		},
		476, { 
				~xorOsc.set(
				\pan, 2.15, \cos, 1.9, 
				\lfn1a, ~e2, \lfn2a, ~e4,
				\lfn1b, ~b4, \lfn2b, ~d5, 
				\lfbeat1, 4, \lfbeat2, 4
			);
				~xor1.set(
					\vol, 1.2,
					\pan, 2.65, \cos, 1.9, 
				\lfn1a, ~d3, \lfn2a, ~b4, 
				\lfn1b, ~d5, \lfn2b, ~d2, 
				\lfbeat1, 4, \lfbeat2, 4
			);

 
		},
		486, { 
				~xorOsc.set(
				\pan, 6.15, \cos, 8.9, 
				\lfn1a, ~a3, \lfn2a, ~c4,
				\lfn1b, ~a4, \lfn2b, ~a2, 
				\lfbeat1, 4, \lfbeat2, 8
			);
				~xor1.set(
					\vol, 1.2,
					\pan, 12.15, \cos, 8.9, 
				\lfn1a, ~a3, \lfn2a, ~c4, 
				\lfn1b, ~a4, \lfn2b, ~a2, 
				\lfbeat1, 8, \lfbeat2, 4
			);

		},
		496, { 
				~xorOsc.set(
				\pan, 16.15, \cos, 8.9, 
				\lfn1a, ~a3, \lfn2a, ~c4,
				\lfn1b, ~a4, \lfn2b, ~a2, 
				\lfbeat1, 4, \lfbeat2, 8
			);
				~xor1.set(
					\vol, 1.2,
					\pan, 12.15, \cos, 8.9, 
				\lfn1a, ~a3, \lfn2a, ~c4, 
				\lfn1b, ~a4, \lfn2b, ~a2, 
				\lfbeat1, 8, \lfbeat2, 4
			);


		},
		506, {  
				~xorOsc.set(
					\pan, 6.15, \cos, 1.9, 
					\lfn1a, ~a3, \lfn2a, ~c4,
					\lfn1b, ~a4, \lfn2b, ~a2, 
					\lfbeat1, 4, \lfbeat2, 8
				);
				~xor1.set(
					\vol, 1.2,
					\pan, 0.15, \cos, 1.9, 
					\lfn1a, ~a3, \lfn2a, ~c4, 
					\lfn1b, ~a4, \lfn2b, ~a2, 
					\lfbeat1, 4, \lfbeat2, 4
				);
		}

	);
		
		
		//
		if (beat == 546) {
			~xor1.set(
				\vol, 1.2,
				\pan, 2.65, \cos, 1.9, 
				\lfn1a, ~d3, \lfn2a, ~c4, 
				\lfn1b, ~a5, \lfn2b, ~a2, 
				\lfbeat1, 4, \lfbeat2, 4
			);


		
		};
		
		
		
		
		//
		if (beat == 588) {
			~xor1.set(
				\vol, 1.2, \amp, 1.3,
				\pan, 0.15, \cos, 1.9, 
				\lfn1a, ~a3, \lfn2a, ~e4, 
				\lfn1b, ~a5, \lfn2b, ~a2, 
				\lfbeat1, 4, \lfbeat2, 4
			);
		

				
		};
		
		//
		if(beat == 608) {
			

		
		};
		
		//
		if (beat == 648) {
			
		~xor1.set(
			\vol, 1.0, \amp, 1.3,
			\pan, 0.15, \cos, 1.9, 
			\lfn1a, ~g3, \lfn2a, ~b4, 
			\lfn1b, ~g5, \lfn2b, ~g2, 
			\lfbeat1, 4, \lfbeat2, 4);
		};
		//
		if (beat == 666) {
			
		~xor1.set(
			\vol, 1.2, \amp, 1.3,
			\pan, 0.15, \cos, 1.9, 
			\lfn1a, ~a3, \lfn2a, ~e4, 
			\lfn1b, ~a5, \lfn2b, ~a2, 
			\lfbeat1, 4, \lfbeat2, 4);
			

		
		};
		if (beat == 676) {
		};
		//////////ESS//////////
		if (beat == 716) {
			
			~xor1.free;
			~xorOsc.free;
			

};
		
		
								//@720//
		
		
		
		
		switch (beat,
		717, {
			//tsEvnts
			
			~tsdur1= PatternProxy( 
				Pseq([
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([1/2], 7),
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([0.5], 7)
				], inf)
			);
			~tsamp1 = PatternProxy( 
				 Pseq([ 
					Pseq([0.1, 0.0, 0.9, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0], 1) 
					]/4, inf) 
			);
			~tsdeg1 = PatternProxy(20);			
			~tsoct = PatternProxy(0.8);
			~tsscale1 = PatternProxy(  Pfunc({  Scale.phrygian  }, inf) );
			~tsattime1 = PatternProxy( 
				Pseq([
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([1/2], 7),
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([0.5], 7)
				]/8, inf) 
			);
			~tsrls1 = PatternProxy (
				Pseq([
					Pseq([1], 1), Pseq([1/4], 1), Pseq([1/2], 7),
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([0.5], 7)
				]*2, inf)
			);
			~tsbrown1 = PatternProxy( 1000 );
			~tsfsin = PatternProxy ( 1.5 );
			~tsvol1 = PatternProxy ( 0.9 );
			//lyEvnts
			~lydur1= PatternProxy( 
				Pseq([
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([1/2], 7),
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([0.5], 7)
				]/4, inf)
			);
			~lyamp1 = PatternProxy( 
				Pseq([ 
					Pseq([1, 0.0, 0.0, 0, 0.0, 0.4, 0, 0.6, 0] , 7),
					Prand([0.0, 0, 0.1, 0, 0.4, 0.8, 0, 0.6, 0] , 1)
				] , inf) 
			);
			~lyfreq1 = PatternProxy(20);
			~lyfreqlp1 = PatternProxy(
				Pseq([
						Pseq( [~e2], 8), Pseq( [~f4], 1)
					], inf)
			);
			~lybrown1 = PatternProxy( 10 );
			~lysaw1 = PatternProxy( 0.1 );
			~lysin1a = PatternProxy ( 1000 );
			~lysin2a = PatternProxy ( 100 );
			~lyvol1 = PatternProxy ( 0.9 );		
		},
		718, {


		},
		
		719, {
			Pdef(\taskDef1).play(quant: [0, 0, 0]);		
			
			Pdef(\lypat1).play( quant: [0, 0, 0]);
			
		},
		
		720, {
//1
			
			//Pdef(\taskBas1).stop;
			

			Pdef(\taskBas1, Pbind(
				\instrument, \tascaleBass,
				\scale, Pfunc({  Scale.phrygian }, inf),
				\octave, 2,
				\dur, Pseq([
						1
					]/4 , inf),
				\amp, Pseq([ 
					0.7, 0.3, 0.1, 0.0, 0.0, 0.3, 0.2, 0.0
					]/3.6, inf),
				\degree, Pseq([
					0
					] , inf),
				\brown, Pseq([ 
					1.8
					], inf),
				\rls, Pseq([0.5]*2, inf),
				\fSin, 2,
				\pan, Prand([-0.1, 0.3, 0.2, 0.02 ], inf)
				
			));
			
			Pdef(\taskDef1, Pbind(
				\vol, ~tsvol1,
				\instrument, \tascale,
				\scale, ~tsscale1,
				\octave, ~tsoct,
				\mtraspose, 30,
				\dur, ~tsdur1,
				\amp, ~tsamp1,
				\degree,~tsdeg1,
				\attime, ~tsattime1,
				\rls, ~tsrls1,
				\brown, ~tsbrown1,
				\fsin, ~tsfsin,
				\pan, Pseq([-0.1, 0.1, 0.2, -0.2 ], inf),
				\out,Pseq([ [~dlyBus, 0, ~revBus] ], inf)
				
			));




			Pdef(\lypat1, Pbind(
				\vol, ~lyvol1,
				\instrument, \lypat01,
				\dur,~lydur1 ,
				\amp, ~lyamp1 ,
				\freq, ~lyfreq1,
				\freqlp, ~lyfreqlp1,
				\brown, ~lybrown1,
				\saw, ~lysaw1,
				\sin1, ~lysin1a,
				\sin2, ~lysin2a,
				\out, Pseq([ [~dlyBus, 0, ~revBus] ], inf)			
			));

			
			~dly.set(\delay, 1.0, \decay, 1.0, \amp, 0.07);
			~rev.set(
				\revtime, 10.1, \roomsize, 40, \damping, 0.08, 
				\inputbw, 0.88, \drylevel -9, 
				\earlylevel, 4, \taillevel, -10.1, \amp, 0.05
			);
			
			},
		736, {
			Pdef(\taskDef1, Pbind(
				\instrument, \tascale,
				\scale, Pfunc({  Scale.phrygian  }, inf),
				\octave, 0.7,
				\mtraspose, 0,
				\dur, Pseq([
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([1/2], 7),
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([0.5], 7)
				], inf),
				\amp, Pseq([ 
					Pseq([0.1, 0.0, 0.9, 0.0, 0.0, 0.0, 0.0, 0.5, 0.0], 1), 
					]/2, inf),
				\degree, Pseq([ 
					Pseq([0], 9) 
					], inf),
				\attime, Pseq([
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([1/2], 7),
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([0.5], 7)
				]/6, inf),
				\rls, Pseq([
					Pseq([1], 1), Pseq([1/4], 1), Pseq([1/2], 7),
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([0.5], 7)
				]*2, inf),
				\brown, Pseq([ 
					Pseq([990], 9) 
					], inf),
				\pan, Pseq([-0.1, 0.1, 0.2, -0.2 ], inf),
				\out, Pseq([ [~dlyBus, 0, ~revBus] ], inf)
				
			));


			Pdef(\lypat1, Pbind(
				\instrument, \lypat01,
				\dur, ~lydur1,
				\amp, Pseq([
						1, Pseq([ 0, 0.01, 0.1, 0.0, 0.0, 0.07, 0.3 ], 1), Pseq([0.0], 1)
					] , inf),
				\freq, Pseq([
						Pseq( [~e1], 8), Pseq( [~f2], 1)
					], inf),
				\freqlp, Pseq([
						Pseq( [~g3], 8), Pseq( [~a4], 1)
					], inf),
				\brown, Pseq([
						8				
					],inf) ,
				\saw, Pseq([
						Pseq([0.1], 9)
					],inf),
				\sin1, 1000,
				\sin2, 100,
				\out, Pseq([ [~dlyBus, 0, ~revBus] ], inf)			
			));
			
			~dly.set(\delay, 1.0, \decay, 0.5, \amp, 1.7);
			~rev.set(
				\revtime, 10.1, \roomsize, 0.1, \damping, 0.8, 
				\inputbw, 0.88, \drylevel -9, 
				\earlylevel, 4, \taillevel, -10.1, \amp, 1.5
			);			
			},		
		752, { 
//3333333
			Pdef(\taskDef1, Pbind(
				\instrument, \tascale,
				\scale, Pfunc({  Scale.phrygian  }, inf),
				\octave, 0.99,
				\mtraspose, 0,
				\dur, Pseq([
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([1/2], 7),
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([0.5], 7)
				], inf),
				\amp, Pseq([ 
					Pseq([0.1, 0.0, 0.09, 0.0, 0.09, 0.0, 0.4, 0.0, 0.0], 1), 
					]/2, inf),
				\degree, Pseq([ 
					Pseq([0], 9) 
					], inf),
				\attime, Pseq([
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([1/2], 7),
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([0.5], 7)
				]/8, inf),
				\rls, Pseq([
					Pseq([1], 1), Pseq([1/4], 1), Pseq([1/2], 7),
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([0.5], 7)
				], inf),
				\brown, Pseq([ 
					Pseq([980], 9) 
					], inf),
				\pan, Pseq([-0.1, 0.1, 0.2, -0.2 ], inf),
				\out, Pseq([ [~dlyBus, 0, ~revBus] ], inf)
				
			));


			Pdef(\lypat1, Pbind(
				\instrument, \lypat01,
				\dur, Pseq([
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([1/2], 7),
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([0.5], 7)
				]/4, inf),
				\amp, Pseq([
						1, Pseq([ 0, 0.05, 0.5, 0.0, 0.5, 0.7, 0.0 ], 1), Pseq([0.4], 1)
					] , inf),
				\freq, Pseq([
						Pseq( [~e1], 8), Pseq( [~f2], 1)
					], inf),
				\freqlp, Pseq([
						Pseq( [~e3], 8), Pseq( [~f4], 1)
					], inf),
				\brown, Pseq([
						8				
					],inf) ,
				\saw, Pseq([
						Pseq([0.1], 9)
					],inf),
				\sin1, 1000,
				\sin2, 100,
				\out, Pseq([ [~dlyBus, 0, ~revBus] ], inf)			
			));

			},
		768, {
//4
			Pdef(\taskDef1, Pbind(
				\instrument, \tascale,
				\scale, Pfunc({  Scale.phrygian  }, inf),
				\octave, 0.2,
				\mtraspose, 0,
				\dur, Pseq([
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([1/2], 7),
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([0.5], 7)
				], inf),
				\amp, Pseq([ 
					Pseq([0.1, 0.5, 0.0, 0.0, 0.7, 0.0, 0.4, 0.2, 0.0], 1), 
					]/2, inf),
				\degree, Pseq([
					Pseq([1], 1), Pseq([5], 1), Pseq([0], 7),
					Pseq([7], 1), Pseq([3], 1), Pseq([0], 7)
				], inf),
				\attime, Pseq([
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([1/2], 7),
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([0.5], 7)
				]/6, inf),
				\rls, Pseq([
					Pseq([1], 1), Pseq([1/4], 1), Pseq([1/2], 7),
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([0.5], 7)
				], inf),
				\brown, Pseq([ 
					Pseq([800], 9) 
					], inf),
				\pan, Pseq([-0.1, 0.1, 0.2, -0.2 ], inf),
				\out, Pseq([ [~dlyBus, 0, ~revBus] ], inf)
				
			));


			Pdef(\lypat1, Pbind(
				\instrument, \lypat01,
				\dur, Pseq([
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([1/2], 7),
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([0.5], 7)
				]/4, inf),
				\amp, Pseq([
						1, Pseq([ 0, 0.25, 0.5, 0.0, 0.8, 0.7, ], 1),Pseq([0.8], 1), Pseq([0.4], 1)
					] , inf),
				\freq, Pseq([
						Pseq( [~e1], 8),  Pser( [~f2], 1)
					], inf),
				\freqlp, Pseq([
						Pseq( [~e3], 7), Pseq( [~d2], 1), Pseq( [~f4], 1)
					], inf),
				\brown, Pseq([
						10				
					],inf) ,
				\saw, Pseq([
						Pseq([0.1], 9)
					],inf),
				\sin1, 1000,
				\sin2, 100,
				\out, Pseq([ [~dlyBus, 0, ~revBus] ], inf)			
			));
			},
		782, {
//5,782

			Pdef(\taskDef1, Pbind(
				\instrument, \tascale,
				\scale, Pfunc({  Scale.phrygian  }, inf),
				\octave, 0.1,
				\mtraspose, 0,
				\dur, Pseq([
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([1/2], 7),
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([0.5], 7)
				], inf),
				\amp, Pseq([ 
					Pseq([0.1, 0.5, 0.8, 0.0, 0.7, 0.0, 0.4, 0.2, 0.0], 1), 
					]/3, inf),
				\degree, Pseq([
					Pseq([1], 1), Pseq([5], 1), Pseq([5], 7),
					Pseq([7], 1), Pseq([3], 1), Pseq([3], 7)
				], inf),
				\attime, Pseq([
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([1/2], 7),
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([0.5], 7)
				]/6, inf),
				\rls, Pseq([
					Pseq([1], 1), Pseq([1/4], 1), Pseq([1/2], 7),
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([0.5], 7)
				], inf),
				\brown, Pseq([ 
					Pseq([800], 9) 
					], inf),
				\pan, Pseq([-0.1, 0.1, 0.2, -0.2 ], inf),
				\out, Pseq([[~dlyBus, 0, ~revBus]], inf)
				
			));


			Pdef(\lypat1, Pbind(
				\instrument, \lypat01,
				\dur, Pseq([
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([1/2], 7),
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([0.5], 7)
				]/4, inf),
				\amp, Pseq([
						1, Pseq([ 0, 0.25, 0.5, 0.0, 0.8, 0.7, ], 1),Pseq([0.8], 1), Pseq([0.4], 1)
					] , inf),
				\freq, Pseq([
						Pseq( [~e1], 8),  Pseq( [~f2], 1)
					], inf),
				\freqlp, Pseq([
						Pseq( [~g3], 7), Pseq( [~e2], 1), Pseq( [~a3], 1)
					], inf),
				\brown, Pseq([
						10				
					],inf) ,
				\saw, Pseq([
						Pseq([0.1], 9)
					],inf),
				\sin1, 1000,
				\sin2, 100,
				\out, Pseq([[~dlyBus, 0, ~revBus]], inf)			
			));
			},		
		//
		798, { 
//6,798

			Pdef(\taskDef1, Pbind(
				\instrument, \tascale,
				\scale, Pfunc({  Scale.phrygian  }, inf),
				\octave, 0.1,
				\mtraspose, 0,
				\dur, Pseq([
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([1/2], 7),
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([0.5], 7)
				], inf),
				\amp, Pseq([ 
					Pseq([0.1, 0.0, 0.8, 0.0, 0.7, 0.0, 0.4, 0.2, 0.0], 1), 
					], inf),
				\degree, Pseq([
					Pseq([1], 1), Pseq([5], 1), Pseq([0], 7),
					Pseq([7], 1), Pseq([3], 1), Pseq([0], 7)
				], inf),
				\attime, Pseq([
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([1/2], 7),
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([0.5], 7)
				]/6, inf),
				\rls, Pseq([
					Pseq([1], 1), Pseq([1/4], 1), Pseq([1/2], 7),
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([0.5], 7)
				], inf),
				\brown, Pseq([ 
					Pseq([800], 9) 
					], inf),
				\pan, Pseq([-0.1, 0.1, 0.2, -0.2 ], inf),
				\out, Pseq([[~dlyBus, 0, ~revBus]], inf)
				
			));


			Pdef(\lypat1, Pbind(
				\instrument, \lypat01,
				\dur, Pseq([
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([1/2], 7),
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([0.5], 7)
				]/4, inf),
				\amp, Pseq([
						1, Pseq([ 0, 0.5, 0.0, 0.3, 0.08, 0.7, ], 1),Pseq([0.8], 1), Pseq([0.4], 1)
					] , inf),
				\freq, Pseq([
						Pseq( [~e1], 8),  Pseq( [~f2], 1)
					], inf),
				\freqlp, Pseq([
						Pseq( [~e3], 7), Pseq( [~d2], 1), Pseq( [~f4], 1)
					], inf),
				\brown, Pseq([
						10				
					],inf) ,
				\saw, Pseq([
						Pseq([0.6], 9)
					],inf),
				\sin1, 1000,
				\sin2, 100,
				\out, Pseq([[~dlyBus, 0, ~revBus]], inf)			
			));
			},
		814, { 
//7, 814

			Pdef(\taskDef1, Pbind(
				\instrument, \tascale,
				\scale, Pfunc({  Scale.phrygian  }, inf),
				\octave, 0.1,
				\mtraspose, 0,
				\dur, Pseq([
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([1/2], 7),
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([0.5], 7)
				], inf),
				\amp, Pseq([ 
					Pseq([0.1, 0.5, 0.8, 0.0, 0.7, 0.0, 0.4, 0.2, 0.0], 1), 
					], inf),
				\degree, Pseq([
					Pseq([1], 1), Pseq([5], 1), Pseq([0], 7),
					Pseq([7], 1), Pseq([3], 1), Pseq([0], 7)
				], inf),
				\attime, Pseq([
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([1/2], 7),
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([0.5], 7)
				]/6, inf),
				\rls, Pseq([
					Pseq([1], 1), Pseq([1/4], 1), Pseq([1/2], 7),
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([0.5], 7)
				], inf),
				\brown, Pseq([ 
					Pseq([800], 9) 
					], inf),
				\pan, Pseq([-0.1, 0.1, 0.3, -0.2 ], inf),
				\out, Pseq([[~dlyBus, 0, ~revBus]], inf)
				
			));


			Pdef(\lypat1, Pbind(
				\instrument, \lypat01,
				\dur, Pseq([
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([1/2], 7),
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([0.5], 7)
				]/4, inf),
				\amp, Pseq([
						1, Pseq([ 0, 0.25, 0.5, 0.3, 0.8, 0.7, ], 1),Pseq([0.8], 1), Pseq([0.4], 1)
					] , inf),
				\freq, Pseq([
						Pseq( [~e1], 8),  Pseq( [~f2], 1)
					], inf),
				\freqlp, Pseq([
						Pseq( [~e3], 7), Pseq( [~d2], 1), Pseq( [~f4], 1)
					], inf),
				\brown, Pseq([
						10				
					],inf) ,
				\saw, Pseq([
						Pseq([0.1], 9)
					],inf),
				\sin1, 1000,
				\sin2, 100,
				\out, Pseq([[~dlyBus, 0, ~revBus]], inf)			
			));
			},
		830, { 
			Pdef(\taskDef7).stop;
			Pdef(\taskDef8).play(quant: [0, 0, 0]);		
			
			Pdef(\lypat7).stop;
			Pdef(\lypat8).play( quant: [0, 0, 0]);
			
			Pdef(\taskBas1, Pbind(
				\instrument, \tascaleBass,
				\scale, Pfunc({  Scale.phrygian }, inf),
				\octave, 2,
				\dur, Pseq([
						1
					]/2 , inf),
				\amp, Pseq([ 
					0.0, 0.2, 0.1, 0.0, 0.0, 0.4, 0.2, 0.0
					]/1.6, inf),
				\degree, Pseq([
					4
					] , inf),
				\brown, Pseq([ 
					1.8
					], inf),
				\rls, Pseq([0.5]*2, inf),
				\fSin, 2,
				\pan, Prand([-0.1, 0.3, 0.2, 0.02 ], inf)
				
			));
			
			},
		846, { 
//8
			Pdef(\taskDef1, Pbind(
				\instrument, \tascale,
				\scale, Pfunc({  Scale.phrygian  }, inf),
				\octave, 0.1,
				\mtraspose, 0,
				\dur, Pseq([
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([1/2], 7),
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([0.5], 7)
				], inf),
				\amp, Pseq([ 
					Pseq([0.1, 0.5, 0.8, 0.0, 0.7, 0.0, 0.4, 0.2, 0.0], 1), 
					], inf),
				\degree, Pseq([
					Pseq([0], 1), Pseq([3], 1), Pseq([5], 7),
					Pseq([7], 1), Pseq([5], 1), Pseq([3], 7)
				], inf),
				\attime, Pseq([
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([1/2], 7),
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([0.5], 7)
				]/6, inf),
				\rls, Pseq([
					Pseq([1], 1), Pseq([1/4], 1), Pseq([1/2], 7),
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([0.5], 7)
				], inf),
				\brown, Pseq([ 
					Pseq([800], 9) 
					], inf),
				\pan, Pseq([-0.1, 0.1, 0.2, -0.2 ], inf),
				\out, Pseq([[~dlyBus, 0, ~revBus]], inf)
				
			));


			Pdef(\lypat1, Pbind(
				\instrument, \lypat01,
				\dur, Pseq([
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([1/2], 7),
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([0.5], 7)
				]/4, inf),
				\amp, Pseq([
						1, Pseq([ 0, 0.25, 0.5, 0.3, 0.8, 0.7, ], 1),Pseq([0.8], 1), Pseq([0.4], 1)
					] , inf),
				\freq, Pseq([
						Pseq( [~e1], 8),  Pseq( [~f2], 1)
					], inf),
				\freqlp, Pseq([
						Pseq( [~e2], 6), Pseq( [~d2, ~a3], 1), Pseq( [~c4], 1)
					], inf),
				\brown, Pseq([
						10				
					],inf) ,
				\saw, Pseq([
						Pseq([0.1], 9)
					],inf),
				\sin1, 1000,
				\sin2, 100,
				\out, Pseq([[~dlyBus, 0, ~revBus]], inf)			
			));		
			},
		862, { 
//9,830

			Pdef(\taskDef1, Pbind(
				\instrument, \tascale,
				\scale, Pfunc({  Scale.phrygian  }, inf),
				\octave, 0.1,
				\mtraspose, 0,
				\dur, Pseq([
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([1/2], 7),
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([0.5], 7)
				], inf),
				\amp, Pseq([ 
					Pseq([0.1, 0.5, 0.8, 0.0, 0.7, 0.0, 0.4, 0.2, 0.0], 1), 
					], inf),
				\degree, Pseq([
					Pseq([1], 1), Pseq([5], 1), Pseq([0], 7),
					Pseq([7], 1), Pseq([5], 1), Pseq([1], 7)
				], inf),
				\attime, Pseq([
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([1/2], 7),
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([0.5], 7)
				]/6, inf),
				\rls, Pseq([
					Pseq([1], 1), Pseq([1/4], 1), Pseq([1/2], 7),
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([0.5], 7)
				], inf),
				\brown, Pseq([ 
					Pseq([800], 9) 
					], inf),
				\pan, Pseq([-0.1, 0.1, 0.2, -0.2 ], inf),
				\out, Pseq([[~dlyBus, 0, ~revBus]], inf)
				
			));


			Pdef(\lypat1, Pbind(
				\instrument, \lypat01,
				\dur, Pseq([
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([1/2], 7),
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([0.5], 7)
				]/4, inf),
				\amp, Pseq([
						1, Pseq([ 0, 0.25, 0.5, 0.3, 0.8, 0.7, ], 1),Pseq([0.8], 1), Pseq([0.4], 1)
					] , inf),
				\freq, Pseq([
						Pseq( [~e1], 8),  Pseq( [~f2], 1)
					], inf),
				\freqlp, Pseq([
						Pseq( [~e2], 6), Pseq( [~d2, ~b3], 1), Pseq( [~f4], 1)
					], inf),
				\brown, Pseq([
						10				
					],inf) ,
				\saw, Pseq([
						Pseq([0.1], 9)
					],inf),
				\sin1, 1000,
				\sin2, 100,
				\out, Pseq([[~dlyBus, 0, ~revBus]], inf)			
			));		
			},
		878, { 
//10,846
			Pdef(\taskDef1, Pbind(
				\instrument, \tascale,
				\scale, Pfunc({  Scale.phrygian  }, inf),
				\octave, 0.1,
				\mtraspose, 0,
				\dur, Pseq([
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([1/2], 7),
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([0.5], 7)
				], inf),
				\amp, Pseq([ 
					Pseq([0.1, 0.5, 0.8, 0.0, 0.7, 0.0, 0.4, 0.2, 0.0], 1), 
					], inf),
				\degree, Pseq([
					Pseq([1], 1), Pseq([5], 1), Pseq([0], 7),
					Pseq([7], 1), Pseq([3], 1), Pseq([3], 7)
				], inf),
				\attime, Pseq([
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([1/2], 7),
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([0.5], 7)
				]/6, inf),
				\rls, Pseq([
					Pseq([1], 1), Pseq([1/4], 1), Pseq([1/2], 7),
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([0.5], 7)
				], inf),
				\brown, Pseq([ 
					Pseq([800], 9) 
					], inf),
				\pan, Pseq([-0.1, 0.1, 0.2, -0.2 ], inf),
				\out, Pseq([[~dlyBus, 0, ~revBus]], inf)
				
			));


			Pdef(\lypat1, Pbind(
				\instrument, \lypat01,
				\dur, Pseq([
					Pseq([1/4], 2), Pseq([1/4], 2), Pseq([1/2], 6),
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([0.5], 7)
				]/4, inf),
				\amp, Pseq([
						1, Pseq([ 0, 0.25, 0.5, 0.3, 0.8, 0.7, ], 1),Pseq([0.8], 1), Pseq([0.4], 1)
					] , inf),
				\freq, Pseq([
						Pseq( [~e1], 8),  Pseq( [~f2], 1)
					], inf),
				\freqlp, Pseq([
						Pseq( [~d2], 6), Pseq( [~d2, ~b3], 1), Pseq( [~f4], 1)
					], inf),
				\brown, Pseq([
						10				
					],inf) ,
				\saw, Pseq([
						Pseq([0.1], 9)
					],inf),
				\sin1, 980,
				\sin2, 100,
				\out, Pseq([[~dlyBus, 0, ~revBus]], inf)			
			));		
			},
		894, { 
//11,862
			Pdef(\taskDef1, Pbind(
				\instrument, \tascale,
				\scale, Pfunc({  Scale.phrygian  }, inf),
				\octave, 0.1,
				\mtraspose, 0,
				\dur, Pseq([
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([1/2], 7),
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([0.5], 7)
				], inf),
				\amp, Pseq([ 
					Pseq([0.1, 0.5, 0.8, 0.0, 0.7, 0.0, 0.4, 0.2, 0.0], 1), 
					]/4, inf),
				\degree, Pseq([
					Pseq([1], 1), Pseq([5], 1), Pseq([0], 7),
					Pseq([7], 1), Pseq([3], 1), Pseq([3], 7)
				], inf),
				\attime, Pseq([
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([1/2], 7),
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([0.5], 7)
				]/6, inf),
				\rls, Pseq([
					Pseq([1], 1), Pseq([1/4], 1), Pseq([1/2], 7),
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([0.5], 7)
				]/2, inf),
				\brown, Pseq([ 
					Pseq([800], 9) 
					], inf),
				\pan, Pseq([-0.1, 0.1, 0.2, -0.2 ], inf),
				\out, Pseq([[~dlyBus, 0, ~revBus]], inf)
				
			));


			Pdef(\lypat1, Pbind(
				\instrument, \lypat01,
				\dur, Pseq([
					Pseq([1/2], 1), Pseq([1/8], 4), Pseq([1/2], 5), Pseq([1/4], 2),
					Pseq([1/4], 1), Pseq([1/4], 1), Pseq([0.5], 7)
				]/4, inf),
				\amp, Pseq([
						1, Pseq([ 0, 0.25, 0.5, 0.3, 0.8, 0.7, ], 1),Pseq([0.8], 1), Pseq([0.4], 1)
					] , inf),
				\freq, Pseq([
						Pseq( [~g1], 6),  Pseq( [~e3], 3)
					], inf),
				\freqlp, Pseq([
						Pseq( [~e2], 6), Pseq( [~c4, ~e3], 1), Pseq( [~d4], 1)
					]/2, inf),
				\brown, Pseq([
						10				
					],inf) ,
				\saw, Pseq([
						Pseq([1.1], 9)
					],inf),
				\sin1, 1000,
				\sin2, 100,
				\out, Pseq([[~dlyBus, 0, ~revBus]], inf)			
			));		
			},
										//910//		
		910, { 
			Pdef(\taskDef1, Pbind(
				\vol, ~tsvol1,
				\instrument, \tascale,
				\scale, ~tsscale1,
				\octave, ~tsoct,
				\mtraspose, 30,
				\dur, ~tsdur1,
				\amp, ~tsamp1,
				\degree,~tsdeg1,
				\attime, ~tsattime1,
				\rls, ~tsrls1,
				\brown, ~tsbrown1,
				\fsin, ~tsfsin,
				\pan, Pseq([-0.1, 0.1, 0.2, -0.2 ], inf),
				\out,Pseq([ [~dlyBus, 0, ~revBus] ], inf)
				
			));
			
			Pdef(\lypat1, Pbind(
				\vol, ~lyvol1,
				\instrument, \lypat01,
				\dur,~lydur1 ,
				\amp, ~lyamp1 ,
				\freq, ~lyfreq1,
				\freqlp, ~lyfreqlp1,
				\brown, ~lybrown1,
				\saw, ~lysaw1,
				\sin1, ~lysin1a,
				\sin2, ~lysin2a,
				\out, Pseq([ [~dlyBus, 0, ~revBus] ], inf)			
			));
			},
		926, { 

			},
		942, { 


			},
		1160, { 
			Pdef(\takBasDef1).stop;
			//DAKIS
			},
		1220, { 
		
			},
		1300, { 
		
			},


		1740, { 
			Pdef(\takBasDef1).stop; 
			Pdef(\lypat1).stop;
			Pdef(\taskDef1).stop;
			},
		
		//1760//
		
		
		1760, {
			
			~ea = Pproto({
				~newgroup = (type:	\group).yield;
				~gl1 =  SoundFile("sounds/galliko01.aif").asEvent.yield;
				~gl2 =  SoundFile("sounds/galliko02.aif").asEvent.yield;
				~gl3 =  SoundFile("sounds/galliko03.aif").asEvent.yield;
				~kz1 =  SoundFile("sounds/kazanaki01.aif").asEvent.yield;
				~kz2 =  SoundFile("sounds/kazanaki02.aif").asEvent.yield;
				~kz3 =  SoundFile("sounds/kazanaki03.aif").asEvent.yield;
				~gc1 =  SoundFile("sounds/gic01.aif").asEvent.yield;
				~gc2 =  SoundFile("sounds/gic02.aif").asEvent.yield;
				~gc3 =  SoundFile("sounds/gic03.aif").asEvent.yield;
				~gc4 =  SoundFile("sounds/gic04.aif").asEvent.yield;
				~kt1 =  SoundFile("sounds/krot01.aif").asEvent.yield;
				~kt2 =  SoundFile("sounds/krot02.aif").asEvent.yield;
			//	(type: \on).yield
			},	
				Pbind(*[
					\instrument,	\buf,
					\dur,		Prand([1,0.1,0.001], inf).sin**9*4,
					\legato,		Pseq([ (1..6),2 ], inf).sin*2,
					\startPos,	0,
					\rate,		Pwhite(0.1, 18).abs/8,
					\sustain,		Pseq([ (4..1),2 ], inf),
					\pan,  		Pseq([ (-4..4),inf ], inf).tanh,
					\group,		Pkey(\newgroup),
					\bufnum,		Pkey(\gl1)
				
				])
			);
			Pdef(\galliko, ~ea).play;
			
			
			
			
			 
			Pdef(\lypat1).stop;
			Pdef(\taskDef1).stop;
						
		
			},
		2098, { 
			Pdef(\galliko, ~ea).stop;
		},
		2100, {
			
			Synth(\kick).play;
			//~part1.start;
			
			/*~rev.free;
			~dly.free	;
			~wah.free	;					
			
			~mdlyFdr.remove;
			~dlyFdr.remove;
			~dcyFdr.remove;
			~combAmpFdr.remove;
			~revtimeFdr.remove;
			~roomsizeFdr.remove;
			~dampingFdr.remove;
			~drylevFdr.remove;*/
			
			}
	)  });
		
	}
	
	*unLoad{
	
	action.deactivate;
	
	}
	
}