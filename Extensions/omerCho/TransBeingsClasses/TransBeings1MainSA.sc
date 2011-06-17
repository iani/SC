/*

Ia1MainSA.load;
~part1 = SyncSender(Pbind.new);
~part1.start;
~part1.stop;

Ia1MainSA.unLoad;

*/


/*


*/


TransBeings1MainSA {
	classvar <action;
	*load {
		var s;
		
		s = Server.default;
		
		action = SyncAction(\beats, { | beat ... otherStuff |
		beat.postln;
		
		if (beat == 0) {

				~piges = Group.head(s);
				~effe = Group.tail(s);	
				TransBeings1SynthDefs.load;
				TransBeings1Buffers.load;
				TransBeings1Busses.load;
				TransBeings1Osc.load;

	
		};
		if (beat == 1) {

		~xor1 = Synth.head( ~piges, \xorInt, [\out, [~revBus, ~rlpBus]]
		);
	

		~rev = Synth.tail(~effe,"reverb", 
			[\in,  ~revBus, \out, ~limBus, \amp, 0.5
			]
		);
		~dly = Synth.tail(~effe,"delay", 
			[\in,  ~dlyBus, \out, ~limBus, \amp, 0.5
			]
		);
		~rlp = Synth.tail(~effe,"rlpf", 
			[\in,  ~rlpBus, \out, ~limBus, 
			\ffreq, 220, \rq, 1.5, \amp, 0.1
			]
		);
		~wah = Synth.tail(~effe,"wah", 
			[\in,  ~wahBus, \out, ~limBus
			]
		);
		~lim = Synth.tail(~effe, "limiter",
			[ \in ,~limBus, \out, 0,  
			\lvl, 0.6, \durt, 0.01
			]
		);	
				

				
				Ia1Pattern.at(\indust1).play( quant: [0, 0, 0]);


	
		};

		//
		if (beat == 16) {
			~ind = Synth(\indust1, [\rlstime, 16, \amp, 0.09, \dist, 0.45, \addAction, \addToHead ]); 
			~ind.set(\dist, 1.45);
			~xor1.set(
			\pan, 0.00001, \cos, 0.00001,  
			\lfn1a, ~a2, \lfn2a, ~c3,
			\lfn1b, ~e3, \lfn2b, ~a3, 
			\lfbeat1, 0.5, \lfbeat2, 0.5);
 
		};
		//
		if (beat == 36) { 
			~ind = Synth(\indust1, [\rlstime, 16, \amp, 0.09, \dist, 0.45, \addAction, \addToHead ]);
			~xor1.set(
			\pan, 0.001, \cos, 0.001,  
			\lfn1a, ~a2, \lfn2a, ~c3,
			\lfn1b, ~e3, \lfn2b, ~a3, 
			\lfbeat1, 0.5, \lfbeat2, 1);
 
		};
		//
		if (beat == 46) {
			Pdef(\buf, Pbind(
				\instrument,	\buf,
				\amp,		Pseq([0.8], inf),
				\dur,		Pseq([50], 1),
				\startPos,	0,
				\rate,		Pseq([1], inf),
				\sustain,		Pseq([60 ], inf),
				\pan,  		Pseq([ 0.3], inf),
				\bufnum,		 Pseq([~indbuf], inf,
				\out, ~revBus
				)			
			)).play( quant: [0, 0, 0]);			
			
			~xor1.set(
			\pan, 0.01, \cos, 0.001,  
			\lfn1a, ~c2, \lfn2a, ~e4, 
			\lfn1b, ~e2, \lfn2b, ~g1,
			\lfbeat1, 0.5, \lfbeat2, 2);
			~rlp.set(\ffreq, ~a2, \rq, 1.65, \amp, 0.6);
			~wah.set(\rq, 0.95, \dist, 1.0, \mfreq, 1200,  \cfreq, 1800);
		};
		//
		if (beat == 56) {
			~wah.set(\rq, 0.45, \dist, 2.0, \mfreq, 1200,  \cfreq, 1300, \amp, 1);
			~rlp.set(\ffreq, ~d1, \rq, 4.165, \amp, 1.9);
			~xor1.set(
			\pan, 0.08, \cos, 1.93, 
			\lfn1a, ~a2, \lfn2a, ~c4, 
			\lfn1b, ~e3, \lfn2b, ~a2, 
			\lfbeat1, 2, \lfbeat2, 2);
			
		};
		//
		if (beat == 66) {
			~ind = Synth(\indust1, [\rlstime, 16, \amp, 0.09, \dist, 0.45, \addAction, \addToHead ]);
			~wah.set(\rq, 0.45, \dist, 2.0, \mfreq, 100,  \cfreq, 1300, \amp, 1);
			~rlp.set(\ffreq, ~d2, \rq, 2.165, \amp, 1.9);
			~xor1.set(
				\vol, 1.5, \amp, 1.2,
			\pan, 0.08, \cos, 1.93, 
			\lfn1a, ~a3, \lfn2a, ~d4, 
			\lfn1b, ~c2, \lfn2b, ~f2, 
			\lfbeat1, 2, \lfbeat2, 2);
		};
		//
		if (beat == 76) {
			~xor1.set(
			\pan, 0.08, \cos, 1.96, 
			\lfn1a, ~a2, \lfn2a, ~c4, 
			\lfn1b, ~e3, \lfn2b, ~a1, 
			\lfbeat1, 2, \lfbeat2, 2);
		};

		//
		if (beat == 86) {
			~rlp.set(\ffreq, ~f2, \rq, 4.165, \amp, 1.9);
			~xor1.set(
				\vol, 1, \amp, 1.0,
			\pan, 0.8, \cos, 1.9, 
			\lfn1a, ~a2, \lfn2a, ~c3, 
			\lfn1b, ~e4, \lfn2b, ~a2, 
			\lfbeat1, 2, \lfbeat2, 2);
		};

		//
		if (beat == 96) {
			~rlp.set(\ffreq, ~f2, \rq, 1.65, \amp, 1.4);
			~ind = Synth(\indust1, [\rlstime, 16, \amp, 0.09, \dist, 0.45, \addAction, \addToHead ]);
			~xor1.set(
			\pan, 0.1, \cos, 1.9,  
			\lfn1a, ~f3, \lfn2a, ~c3, 
			\lfn1b, ~a3, \lfn2b, ~d2,
			\lfbeat1, 2, \lfbeat2, 4);
		};
		
		//
		if (beat == 101) {
			
			~wah.set(\rq, 0.45, \dist, 2.0, \mfreq, 100,  \cfreq, 1300, \amp, 1);
			~rlp.set(\amp, 1.0, \ffreq, ~g4, \rq, 4.5);
			~xor1.set(
			\pan, 0.1, \cos, 1.9,  
			\lfn1a, ~g3, \lfn2a, ~d3, 
			\lfn1b, ~b3, \lfn2b, ~e3,
			\lfbeat1, 4, \lfbeat2, 2);
		};
		//
		if (beat == 106) {
		~wah.set(\mul, 5, \rq, 2.45);
			
		~rlp.set(\amp, 1.3, \ffreq, ~a6, \rq, 4.5);
		~xor1.set(
			\pan, 0.1, \cos, 1.9,  
			\lfn1a, ~a3, \lfn2a, ~d3, 
			\lfn1b, ~f3, \lfn2b, ~e3,
			\lfbeat1, 4, \lfbeat2, 2);
		};
		//
		if (beat == 116) {
		~wah.set(\rq, 0.45, \dist, 2.0, \mfreq, 100,  \cfreq, 1300, \amp, 1.1);
			~rlp.set(\ffreq, ~d2, \rq, 2.165, \amp, 1.9);
		~xor1.set(
			\vol, 1.2, \amp, 1.3,
			\pan, 0.15, \cos, 1.9, 
			\lfn1a, ~a3, \lfn2a, ~c4, 
			\lfn1b, ~a5, \lfn2b, ~a2, 
			\lfbeat1, 4, \lfbeat2, 4);
		};
		//
		if (beat == 126) {
		~wah.set(\rq, 0.45, \dist, 2.0, \mfreq, 100,  \cfreq, 1300, \amp, 1.1);
			~rlp.set(\ffreq, ~d2, \rq, 2.165, \amp, 1.9);
		~xor1.set(
			\vol, 1.2, \amp, 1.3,
			\pan, 0.15, \cos, 1.9, 
			\lfn1a, ~g3, \lfn2a, ~b4, 
			\lfn1b, ~g5, \lfn2b, ~g2, 
			\lfbeat1, 4, \lfbeat2, 4);
		};
		//
		if (beat == 134) {
		~wah.set(\rq, 0.45, \dist, 2.0, \mfreq, 100,  \cfreq, 1300, \amp, 1.1);
			~rlp.set(\ffreq, ~d2, \rq, 2.165, \amp, 1.9);
		~xor1.set(
			\vol, 1.2, \amp, 1.3,
			\pan, 0.15, \cos, 1.9, 
			\lfn1a, ~a3, \lfn2a, ~e4, 
			\lfn1b, ~a5, \lfn2b, ~a2, 
			\lfbeat1, 4, \lfbeat2, 4);
		};
		//
		if (beat == 158) {
		~wah.set(\rq, 0.45, \dist, 2.0, \mfreq, 100,  \cfreq, 1300, \amp, 1.1);
			~rlp.set(\ffreq, ~d2, \rq, 2.165, \amp, 1.9);
		~xor1.set(
			\vol, 1.2, \amp, 1.3,
			\pan, 0.15, \cos, 1.9, 
			\lfn1a, ~c3, \lfn2a, ~g4, 
			\lfn1b, ~c5, \lfn2b, ~c2, 
			\lfbeat1, 4, \lfbeat2, 4);
		};
		//
		if (beat == 166) {
			~wah.set(\rq, 0.45, \dist, 2.0, \mfreq, 100,  \cfreq, 1300, \amp, 1.1);
			~rlp.set(\ffreq, ~d2, \rq, 2.165, \amp, 1.9);
		~xor1.set(
			\vol, 1.2, \amp, 1.3,
			\pan, 0.15, \cos, 1.9, 
			\lfn1a, ~c3, \lfn2a, ~f4, 
			\lfn1b, ~c5, \lfn2b, ~c2, 
			\lfbeat1, 4, \lfbeat2, 4);
			
		};
		
		
		
		if (beat == 176) {
					   ///////////////O S C 1////////////
			
					///////////////////////////////////////
			~wah.set(\rq, 0.45, \dist, 2.95, \mfreq, 100,  \cfreq, ~c3, \amp, 1.5);
			~rlp.set(\ffreq, ~d2, \rq, 2.165, \amp, 1.9);
			~xor1.set(
				\vol, 1.2, \amp, 1.3,
			\pan, 0.15, \cos, 1.9, 
			\lfn1a, ~a3, \lfn2a, ~c4, 
			\lfn1b, ~a5, \lfn2b, ~a2, 
			\lfbeat1, 4, \lfbeat2, 4);

			

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
				~rev.set(
				\amp, 0.03,
				\roomsize, 10,  \revtime, 1.25,
				\damping, 0.1, \inputbw, 0.19,
				\spread, 15, \drylevel, -3,
				\earlylevel, -9, \taillevel, -11 
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
				~rev.set(
				\amp, 0.03,
				\roomsize, 20,  \revtime, 1.25,
				\damping, 0.2, \inputbw, 0.29,
				\spread, 15, \drylevel, -3,
				\earlylevel, -9, \taillevel, -11 
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
				~rev.set(
				\amp, 0.03,
				\roomsize, 20,  \revtime, 1.15,
				\damping, 0.1, \inputbw, 0.19,
				\spread, 15, \drylevel, -1,
				\earlylevel, -6, \taillevel, -11 
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
				\lfbeat1, 4, \lfbeat2, 4
			);
				~rev.set(
				\amp, 0.03,
				\roomsize, 20,  \revtime, 1.55,
				\damping, 0.1, \inputbw, 0.19,
				\spread, 15, \drylevel, -3,
				\earlylevel, -6, \taillevel, -11 
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
				\lfbeat1, 4, \lfbeat2, 4
			);
				~rev.set(
				\amp, 0.03,
				\roomsize, 10,  \revtime, 1.25,
				\damping, 0.1, \inputbw, 0.19,
				\spread, 15, \drylevel, -3,
				\earlylevel, -9, \taillevel, -11 
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
				\lfbeat1, 4, \lfbeat2, 4
			);
				~rev.set(
				\amp, 0.03,
				\roomsize, 20,  \revtime, 1.25,
				\damping, 0.2, \inputbw, 0.29,
				\spread, 15, \drylevel, -3,
				\earlylevel, -9, \taillevel, -11 
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
				~rev.set(
				\amp, 0.03,
				\roomsize, 20,  \revtime, 1.15,
				\damping, 0.1, \inputbw, 0.19,
				\spread, 15, \drylevel, -1,
				\earlylevel, -6, \taillevel, -11 
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
				~rev.set(
				\amp, 0.03,
				\roomsize, 20,  \revtime, 1.55,
				\damping, 0.1, \inputbw, 0.19,
				\spread, 15, \drylevel, -3,
				\earlylevel, -6, \taillevel, -11 
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
				~rev.set(
				\amp, 0.03,
				\roomsize, 20,  \revtime, 1.55,
				\damping, 0.1, \inputbw, 0.19,
				\spread, 15, \drylevel, -3,
				\earlylevel, -6, \taillevel, -11 
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
				~rev.set(
				\amp, 0.03,
				\roomsize, 20,  \revtime, 1.55,
				\damping, 0.1, \inputbw, 0.19,
				\spread, 15, \drylevel, -3,
				\earlylevel, -6, \taillevel, -11 
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
				~rev.set(
				\amp, 0.03,
				\roomsize, 20,  \revtime, 1.55,
				\damping, 0.1, \inputbw, 0.19,
				\spread, 15, \drylevel, -3,
				\earlylevel, -6, \taillevel, -11 
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
				~rev.set(
				\amp, 0.03,
				\roomsize, 20,  \revtime, 1.55,
				\damping, 0.1, \inputbw, 0.19,
				\spread, 15, \drylevel, -3,
				\earlylevel, -6, \taillevel, -11 
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
				~rev.set(
				\amp, 0.03,
				\roomsize, 20,  \revtime, 1.55,
				\damping, 0.1, \inputbw, 0.19,
				\spread, 15, \drylevel, -3,
				\earlylevel, -6, \taillevel, -11 
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
				\lfbeat1, 4, \lfbeat2, 4
			);
				~rev.set(
				\amp, 0.03,
				\roomsize, 10,  \revtime, 1.25,
				\damping, 0.1, \inputbw, 0.19,
				\spread, 15, \drylevel, -3,
				\earlylevel, -9, \taillevel, -11 
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
				~rev.set(
				\amp, 0.03,
				\roomsize, 10,  \revtime, 1.25,
				\damping, 0.01, \inputbw, 0.19,
				\spread, 10, \drylevel, -1,
				\earlylevel, -1, \taillevel, -11 
			);

		},
		506, {  
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
				~rev.set(
				\amp, 0.03,
				\roomsize, 10,  \revtime, 1.25,
				\damping, 0.1, \inputbw, 0.19,
				\spread, 15, \drylevel, -3,
				\earlylevel, -9, \taillevel, -11 
			);

		}/*,
		516, {
				~xorOsc.set(
				\pan, 6.15, \cos, 1.9, 
				\lfn1a, ~a3, \lfn2a, ~c4,
				\lfn1b, ~a4, \lfn2b, ~a2, 
				\lfbeat1, 4, \lfbeat2, 2
			);
				~xor1.set(
					\vol, 1.0,
					\pan, 0.15, \cos, 1.9, 
				\lfn1a, ~a3, \lfn2a, ~c4, 
				\lfn1b, ~a3, \lfn2b, ~a2, 
				\lfbeat1, 4, \lfbeat2, 4
			);
				~rev.set(
				\amp, 0.03,
				\roomsize, 10,  \revtime, 1.25,
				\damping, 0.1, \inputbw, 0.19,
				\spread, 15, \drylevel, -3,
				\earlylevel, -9, \taillevel, -11 
			);
		
		},
		526, {
				~xorOsc.set(
				\pan, 6.15, \cos, 1.9, 
				\lfn1a, ~a3, \lfn2a, ~c3,
				\lfn1b, ~a3, \lfn2b, ~a2, 
				\lfbeat1, 4, \lfbeat2, 2
			);
				~xor1.set(
					\vol, 0.4,
					\pan, 0.15, \cos, 1.9, 
				\lfn1a, ~a2, \lfn2a, ~c3, 
				\lfn1b, ~a4, \lfn2b, ~a1, 
				\lfbeat1, 4, \lfbeat2, 4
			);
				~rev.set(
				\amp, 0.01,
				\roomsize, 20,  \revtime, 1.25,
				\damping, 0.1, \inputbw, 0.19,
				\spread, 15, \drylevel, -3,
				\earlylevel, -9, \taillevel, -11 
			);
		}*/


	);
		
		
		//
		if (beat == 546) {
			~wah.set(\rq, 0.45, \dist, 2.0, \mfreq, 100,  \cfreq, 1300, \amp, 1.1);
			~rlp.set(\ffreq, ~cS2, \rq, 2.165, \amp, 1.9);
				~xor1.set(
					\vol, 1.2,
					\pan, 2.65, \cos, 1.9, 
				\lfn1a, ~d3, \lfn2a, ~c4, 
				\lfn1b, ~a5, \lfn2b, ~a2, 
				\lfbeat1, 4, \lfbeat2, 4
			);

		Pdef(\taskBas1).play(quant: [0, 0, 0]);
		Pdef(\taskBas1, Pbind(
				\instrument, \tascaleBass,
				\scale, Pfunc({  Scale.ionian }, inf),
				\octave, 2,
				\dur, Pseq([
						1
					] , inf),
				\amp, Pseq([ 
					0.4, 0.0, 0.0, 0.3, 0.0, 0.0, 0, 0
					]/14, inf),
				\degree, Pseq([
						0
					] , inf),
				\brown, Pseq([ 
					0.8
					], inf),
				\rls, Pseq([0.5]*6, inf),
				\fSin, 0.5,
				\pan, Pseq([-0.3, 0.3, 0.2, -0.2 ], inf)
				
			));
		
		};
		
		
		
		
		//
		if (beat == 588) {
			~wah.set(\rq, 0.45, \dist, 2.0, \mfreq, 100,  \cfreq, 1300, \amp, 1.1);
			~rlp.set(\ffreq, ~d2, \rq, 2.165, \amp, 1.9);
		~xor1.set(
			\vol, 1.2, \amp, 1.3,
			\pan, 0.15, \cos, 1.9, 
			\lfn1a, ~a3, \lfn2a, ~e4, 
			\lfn1b, ~a5, \lfn2b, ~a2, 
			\lfbeat1, 4, \lfbeat2, 4);
		
		
		Pdef(\taskBas1, Pbind(
				\instrument, \tascaleBass,
				\scale, Pfunc({  Scale.ionian }, inf),
				\octave, 2,
				\dur, Pseq([
						1
					] , inf),
				\amp, Pseq([ 
					0.7, 0.0, 0.0, 0.5, 0.0, 0.0, 0, 0
					]/19, inf),
				\degree, Pseq([
						5
					] , inf),
				\brown, Pseq([ 
					0.8
					], inf),
				\rls, Pseq([0.5]*9, inf),
				\fSin, 0.5,
				\pan, Pseq([-0.3, 0.3, 0.2, -0.2 ], inf)
				
			));
				
		};
		
		//
		if(beat == 608) {
			
			
			Pdef(\taskBas1, Pbind(
				\instrument, \tascaleBass,
				\scale, Pfunc({  Scale.ionian }, inf),
				\octave, 2,
				\dur, Pseq([
						1
					] , inf),
				\amp, Pseq([ 
					0.7, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0
					]/18, inf),
				\degree, Pseq([
						3
					] , inf),
				\brown, Pseq([ 
					0.8
					], inf),
				\rls, Pseq([0.5]*9, inf),
				\fSin, 1.0,
				\pan, Pseq([-0.3, 0.3, 0.2, -0.2 ], inf)
				
			));
		
		};
		
		//
		if (beat == 648) {
			
			Pdef(\taskBas1, Pbind(
				\instrument, \tascaleBass,
				\scale, Pfunc({  Scale.ionian }, inf),
				\octave, 2,
				\dur, Pseq([
						1
					] , inf),
				\amp, Pseq([ 
					0.7, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0
					]/18, inf),
				\degree, Pseq([
						0
					] , inf),
				\brown, Pseq([ 
					0.6
					], inf),
				\rls, Pseq([0.5]*9, inf),
				\fSin, 1.0,
				\pan, Pseq([-0.3, 0.3, 0.2, -0.2 ], inf)
				
			));
			
			
				~wah.set(\rq, 0.45, \dist, 2.0, \mfreq, 100,  \cfreq, 1300, \amp, 1.1);
			~rlp.set(\ffreq, ~d2, \rq, 2.165, \amp, 1.9);
		~xor1.set(
			\vol, 1.2, \amp, 1.3,
			\pan, 0.15, \cos, 1.9, 
			\lfn1a, ~g3, \lfn2a, ~b4, 
			\lfn1b, ~g5, \lfn2b, ~g2, 
			\lfbeat1, 4, \lfbeat2, 4);
		};
		//
		if (beat == 666) {
			
			~wah.set(\rq, 0.45, \dist, 2.0, \mfreq, 100,  \cfreq, 1300, \amp, 1.1);
			~rlp.set(\ffreq, ~d2, \rq, 2.165, \amp, 1.9);
		~xor1.set(
			\vol, 1.2, \amp, 1.3,
			\pan, 0.15, \cos, 1.9, 
			\lfn1a, ~a3, \lfn2a, ~e4, 
			\lfn1b, ~a5, \lfn2b, ~a2, 
			\lfbeat1, 4, \lfbeat2, 4);
			
			Pdef(\lypat0).play( quant: [0, 0, 0]);
			Pdef(\lypat0, Pbind(
				\instrument, \lypat01,
				\octave, 4,
				\dur, Pseq([
						Pseq([1/8], 8), 1/4, 1/2, 1/2, 1/4, 1, 1/2, 
						1/2, 1, 1/4, 1/4, 1/2, 1, 1/2, 
						1/2, 1, 1/2, 1
					]/2 , inf),
				\amp, Pseq([
						1, Prand([ 0.8, 0.25, 0.0, 0.8, 0.7, 0.5 ], 1) 
						
					] , inf)/15,
				\degree, Pseq([
						-10, 0, 0, 0, 1
					], inf),
				\brown, Pseq([
						100				
					],inf) ,
				\saw, Pseq([
						0.4
					],inf),
				\sin1, 1.1,
				\sin2, 0.1,
				\out, Pseq([0], inf)			
			));
		
		};
		//
		//
		if (beat == 676) {
			Pdef(\lypat0, Pbind(
				\instrument, \lypat01,
				\octave, 4,
				\dur, Pseq([
						Pseq([1/8], 8), 1/4, 1/2, 1/2, 1/4, 1, 1/2, 
						1/2, 1, 1/4, 1/4, 1/2, 1, 1/2, 
						1/2, 1, 1/2, 1
					]/2 , inf),
				\amp, Pseq([
						1, Prand([ 0.8, 0.25, 0.0, 0.8, 0.7, 0.5 ], 1) 
						
					] , inf)/3,
				\degree, Pseq([
						-10, 0, 0, 0, 1
					], inf),
				\brown, Pseq([
						100				
					],inf) ,
				\saw, Pseq([
						0.7
					],inf),
				\sin1, 1.1,
				\sin2, 0.1,
				\out, Pseq([0], inf)			
			));
		
		};
		//////////ESS//////////
		if (beat == 716) {
			Pdef(\lypat0, Pbind(\instrument, \lypat01, \amp, 0));
			Pdef(\lypat0).stop;
			~rlp.set(\amp, 0.0, \ffreq, ~fS1, \rq, 4, \pan, -1);
			~ind = Synth.head(~piges, \indust1, [\rlstime, 12, \amp, 0.9, \dist, 0.65]);
			~xor1.free;
			
			Pdef(\buf, Pbind(
				\instrument,	\buf,
				\amp,		Pseq([0.9], inf),
				\dur,		Pseq([48], 1),
				\startPos,	0,
				\rate,		Pseq([0.5], inf),
				\sustain,		Pseq([6], inf),
				\pan,  		Pseq([ 0.3], inf),
				\bufnum,		 Pseq([~indbuf], inf)
			)).play( quant: [0, 0, 0]);
	
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
			
			//~audBus = Bus.new(\audio, 20, 2);
			//~revBus = Bus.new(\audio, 22, 2);
			//~dlyBus = Bus.new(\audio, 24, 2);

			//~dly = Synth("aDelay", [\in,  ~dlyBus, \out, 0, \amp]);
			//~rev = Synth("reverb1", [\in,  ~revBus, \out, 0, \amp]);
		
			~dly = Synth("aDelay", [\in,  ~dlyBus, \out, 0, \amp, 0.01]);
			~rev = Synth("reverb1", [\in,  ~revBus, \out, 0, \amp, 0.01]);

		},
		
		719, {
			Pdef(\taskDef1).play(quant: [0, 0, 0]);		
			
			Pdef(\lypat1).play( quant: [0, 0, 0]);
			
			Pdef(\taskBas1).play( quant: [0, 0, 0]);		
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