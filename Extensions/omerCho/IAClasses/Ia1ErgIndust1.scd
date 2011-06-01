(

		SynthDef(\indust1, {|out = 0, amp = 0.8 attime = 0.01, sustain = 1.1, rlstime = 51.1, dist = 0.5 |
			var  env, mix, ses, outdc, rek, n=8;
			env =  EnvGen.ar(Env.perc(attime, rlstime), doneAction: 2, levelScale: 0.8, timeScale: sustain);
			rek = LFNoise0.kr(rrand(0.2,1.0), 1,1).squared * MouseX.kr;
			mix = Klank.ar(`[
					Array.fill(n, { exprand(1.0,20.0) }),
					nil,
					Array.fill(n, { 0.2.rand })
				],
				Blip.ar(rek, [rrand(2,5),rrand(2,5)], 0.1)
			).fold2(0.2).cubed * 12;
			mix = Mix.arFill(3, { CombL.ar(mix, 0.1, 0.03.linrand, 4.0.linrand) });
			ses = mix.distort * dist;
			8.do({ses = AllpassN.ar(ses, 0.05, [0.05.rand, 0.05.rand], 3) }) ;
			outdc = LeakDC.ar(ses);
			Out.ar(out, outdc *amp * env);
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
		}).add;
			
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
		}).add;
		
		SynthDef("rlpf",{ |out = 0, amp = 0.8 in = 0, ffreq = 600, rq = 0.1, pan = 0|
			Out.ar( out, Pan2.ar(RLPF.ar( In.ar(in), ffreq, rq, amp), pan))
		}).add;
		
		
		SynthDef("wah", { arg out = 0, in = 0, rate = 0.5, amp = 1, pan = 0, cfreq = 1400, mfreq = 1200, rq=0.1, dist = 0.15;
			var zin, zout;
			zin = In.ar(in, 2);
			cfreq = Lag3.kr(cfreq, 0.1);
			mfreq = Lag3.kr(mfreq, 0.1);
			rq   = Ramp.kr(rq, 0.1);
			zout = RLPF.ar(zin, LFNoise1.kr(rate, mfreq, cfreq), rq, amp).distort * dist;
			Out.ar( out , Pan2.ar(zout, pan) ); 
		}).add;
		
)

(		
			~piges = Group.head(s);
			~effe = Group.tail(s);

		~limBus = Bus.new(\audio, 20, 2);
		~revBus = Bus.new(\audio, 22, 2);
		~dlyBus = Bus.new(\audio, 24, 2);
		~rlpBus = Bus.new(\audio, 26, 2);
		~wahBus = Bus.new(\audio, 28, 2);
)		
		
		
(

		~ind = Synth.head(~piges, \indust1, [ \out, [~revBus, ~wahBus, ~dlyBus], \rlstime, 26, \amp, 0.9, \dist, 0.65 ]); 

			
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


)

(
		~xor1.set(
			\vol, 1.2, \amp, 1.3,
			\pan, 0.15, \cos, 1.9, 
			\lfn1a, ~a3, \lfn2a, ~e4, 
			\lfn1b, ~a5, \lfn2b, ~a2, 
			\lfbeat1, 4, \lfbeat2, 4);

		~rev.set( 
			\revtime, 5.21,
			\roomsize, 4.312,
			\damping, 0.82
		);


		~wah.set(
			\rq, 0.45, \dist, 2.0, \rate, 0.5,
			\mfreq, 1200,  \cfreq, 1300, \amp, 1.1
		);
		~dly.set(
			\delay, 0.0, \decay, 0.5, 
			\amp, 1.0
		);
		~rlp.set(
			\ffreq, ~c8, \rq, 1.165, 
			\amp, 2.0
		);
		~lim.set(\lvl, 0.8, \durt, 0.1);
)