(

		SynthDef( \lypat01, { |out = 0, vol = 1, amp = 1, sustain = 1.1, freq = 60, 
			sin1 = 0, sin2 = 0.1, brown = 0.1, saw = 400, 
			att = 0.01, rls = 1.5, pan = 0 |
			var in, osc, env, ses;
			env =  EnvGen.ar(Env.perc(att, rls), doneAction: 2, levelScale: 0.8, timeScale: sustain);
			in = SinOsc.ar(FSinOsc.ar(freq, 0, brown), 0.4);
			ses = SinOsc.ar(0, in, 0.01) ;
			ses = RLPF.ar(ses, freq, 1.4, 1.6, 0.4 );
			ses = ses.sin/8 + SinOsc.ar(freq, Decay.ar(SinOsc.ar(sin1.log2/8, sin2.log*4), 8.2.abs*8, Saw.ar(saw.log10*32)));
			ses = Pan2.ar(ses, pan, amp);
			
			Out.ar(out, ses *vol *env );
		}).add;



		
		SynthDef("limiter",{ arg out=0, in = 0, lvl = 0.9, durt = 0.01;
			ReplaceOut.ar( out, Limiter.ar( In.ar(in, 2), lvl, durt) )
		}).send(s);
		
		SynthDef("reverb", { | out, in = 0, amp=0.05, roomsize = 10, revtime = 1, damping = 0.2, inputbw = 0.19, spread = 15,
				drylevel = -3, earlylevel = -9, taillevel = -11 |
			var input;
			input = In.ar(in, 2);
			Out.ar(out, GVerb.ar(
				input,
				roomsize,
				revtime,
				damping,
				inputbw,
				spread,
				drylevel.dbamp,
				earlylevel.dbamp,
				taillevel.dbamp,
				roomsize, amp) + input
			)
		}).send(s);
			
		SynthDef("delay", { |out = 0, in = 0, maxdelay = 0.25,  delay = 1.0, decay = 0.05, pan = 0, amp =0.5|
			var ses, filt;
			ses =  In.ar(in, 2);
			filt = CombN.ar(
					ses,
					maxdelay,
					delay,
					decay, 
					amp
				);
			Out.ar(out,  Pan2.ar(filt, pan));
		}).send(s);
		
		SynthDef("rlpf",{ |out = 0, amp = 0.8 in = 0, ffreq = 600, rq = 0.1, pan = 0|
			Out.ar( out, Pan2.ar(RLPF.ar( In.ar(in), ffreq, rq, amp), pan))
		}).send(s);
		
		
		SynthDef("wah", { arg out = 0, in = 0, rate = 0.5, amp = 1, pan = 0, cfreq = 1400, mfreq = 1200, rq=0.1, dist = 0.15;
			var zin, zout;
			zin = In.ar(in, 2);
			cfreq = Lag3.kr(cfreq, 0.1);
			mfreq = Lag3.kr(mfreq, 0.1);
			rq   = Ramp.kr(rq, 0.1);
			zout = RLPF.ar(zin, LFNoise1.kr(rate, mfreq, cfreq), rq, amp).distort * dist;
			Out.ar( out , Pan2.ar(zout, pan) ); 
		}).send(s);
		
)

(		
		~piges = Group.head(s);
		~effe = Group.tail(s);

		~limBus = Bus.new(\audio, 20, 2);
		~revBus = Bus.new(\audio, 22, 2);
		~dlyBus = Bus.new(\audio, 24, 2);
		~rlpBus = Bus.new(\audio, 26, 2);
		~wahBus = Bus.new(\audio, 28, 2);

		Ia1Osc.load;
)		
		
		
(
			
			~rev = Synth.tail(~effe,"reverb", 
				[\in,  ~revBus, \out, ~limBus, \amp, 0.5
				]
			);
			~dly = Synth.tail(~effe,"delay", 
				[\in,  ~dlyBus, \out, ~limBus, \amp, 0.8
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

			Pdef(\lypat0).play( quant: [0, 0, 0]);
)

(

			Pdef(\lypat0, Pbind(
				\instrument, \lypat01,
				\scale, Pfunc({ Scale.phrygian }, inf),
				\octave, 3,
				\dur, Pseq([
						Pseq([1/8], 8), 1/4, 1/2, 1/2, 1/4, 1, 1/2, 
						1/2, 1, 1/4, 1/4, 1/2, 1, 1/2, 
						1/2, 1, 1/2, 1
					]/2 , inf),
				\amp, Pseq([
						Pseq([1,0.8], 8), Pseq([ 0.8, 0.25, 0.3, 0.8, 0.7, 0.5, 0.0 ], 1),
						Pseq([ 0.6, 0.3 ], 5)  
						
					] , inf),
				\degree, Pseq([
						0
					], inf),
				\brown, Pseq([
						0				
					],inf) ,
				\saw, Pseq([
						118.sin.cos
					],inf),
				\sin1, 100.sin*8,
				\sin2, 100.0.tanh,
				\group, ~piges,
				\out, Pseq([~limBus], inf)			
			));


		
)

(

			Pdef(\lypat0, Pbind(
				\instrument, \lypat01,
				\scale, Pfunc({ Scale.phrygian }, inf),
				\octave, 3,
				\dur, Pseq([
						Pseq([1/8], 8), 1/4, 1/2, 1/2, 1/4, 1, 1/2, 
						1/2, 1, 1/4, 1/4, 1/2, 1, 1/2, 
						1/2, 1, 1/2, 1
					]/2 , inf),
				\amp, Pseq([
						Pseq([1,0.8], 8), Pseq([ 0.8, 0.25, 0.0, 0.8, 0.7, 0.5, 0.0 ], 1),
						Pseq([ 0.6, 0.3 ], 5)  
						
					] , inf),
				\degree, Pseq([
						3
					], inf),
				\brown, Pseq([
						0				
					],inf) ,
				\saw, Pseq([
						108.sin
					],inf),
				\sin1, 100.cos*18,
				\sin2, 100.0.tanh,
				\group, ~piges,
				\out, Pseq([~limBus], inf)			
			));


		
)

(

			Pdef(\lypat0, Pbind(
				\instrument, \lypat01,
				\scale, Pfunc({ Scale.phrygian }, inf),
				\octave, 3,
				\dur, Pseq([
						Pseq([1/8], 8), 1/4, 1/2, 1/2, 1/4, 1, 1/2, 
						1/2, 1, 1/4, 1/4, 1/2, 1, 1/2, 
						1/2, 1, 1/2, 1
					]/2 , inf),
				\amp, Pseq([
						Pseq([1,0.8], 8), Pseq([ 0.8, 0.25, 0.0, 0.8, 0.7, 0.5, 0.0 ], 1),
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
						118.sin*8
					],inf),
				\sin1, 100.sin*28,
				\sin2, 90.0.tanh,
				\group, ~piges,
				\out, Pseq([~limBus], inf)			
			));


		
)

(

			Pdef(\lypat0, Pbind(
				\instrument, \lypat01,
				\scale, Pfunc({ Scale.phrygian }, inf),
				\octave, 3,
				\dur, Pseq([
						Pseq([1/8], 8), 1/4, 1/2, 1/2, 1/4, 1, 1/2, 
						1/2, 1, 1/4, 1/4, 1/2, 1, 1/2, 
						1/2, 1, 1/2, 1
					]/2 , inf),
				\amp, Pseq([
						Pseq([1,0.8], 8), Pseq([ 0.8, 0.25, 0.0, 0.8, 0.7, 0.5, 0.0 ], 1),
						Pseq([ 0.6, 0.3 ], 5)  
						
					] , inf),
				\degree, Pseq([
						1
					], inf),
				\att, 0.1,
				\rls, 3,
				\brown, Pseq([
						0				
					],inf) ,
				\saw, Pseq([
						118.sin*5
					],inf),
				\sin1, 100.sin*28,
				\sin2, 90.0.cos.sin,
				\group, ~piges,
				\out, Pseq([~limBus], inf)			
			));


		
)
(

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


		
)

