(

		SynthDef( \tascaleBass, { |out = 0, freq = 369.92, amp = 1, attime = 0.001, rls = 0.05, pan = 0, brown = 0.1, fSin = 0.5  |
			var in, osc, env, ses;
			env =  EnvGen.ar(Env.perc(attime, rls, 0.5, 2), doneAction: 2);
			in = FSinOsc.ar(freq, 0, brown);
			osc = SinOsc.ar(0, in, 1.8) * env;
			ses = Pan2.ar(osc, pan, FSinOsc.kr(fSin));
			Out.ar(0, ses *3 *amp);
		}).send(s);



		
		SynthDef("limiter",{ arg out=0, in = 0, lvl = 0.9, durt = 0.01;
			ReplaceOut.ar( out, Limiter.ar( In.ar(in, 2), lvl, durt) )
		}).send(s);
		

		
)

(		
		~piges = Group.head(s);
		~effe = Group.tail(s);

		~limBus = Bus.new(\audio, 20, 2);

)		
		
		
(


			
		~lim = Synth.tail(~effe, "limiter",
			[ \in ,~limBus, \out, 0,  
			\lvl, 0.6, \durt, 0.01
			]
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
				\pan, Pseq([-0.3, 0.3, 0.2, -0.2 ], inf),
				\group, ~piges,
				\out, ~limBus
				
			));


)

(

		~lim.set(\lvl, 0.8, \durt, 0.1);
)