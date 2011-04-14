(

		SynthDef(\xorInt,{|out = 0, gate = 1,  vol = 0.8, amp = 0.5, 
			pan = 0.001, cos = 0.0001, lfbeat1 = 0.25, lfbeat2 = 0.25,
			lfn1a = 110, lfn1b = 324, lfn2a = 130.8,  lfn2b = 146,  
			rls = 1 |
			var ses, env;
			var in1, in2, comb1, combin1, comb2, combin2;
				
			in1 = Blip.ar([lfn1a, lfn1b], 2,  0.8);
			in2 = Blip.ar([lfn2a, lfn2b], 2,  1.9);
			
			comb1 = BPF.ar(	in1, 4**LFNoise0.kr(lfbeat1) * 680, 0.1,  0.4);
			comb2 = BPF.ar(	in2, 8**LFNoise0.kr(lfbeat2) * 680, 0.1,  0.4);
				
			ses = Rotate2.ar(comb1, comb2, LFSaw.kr(0.1));
			ses = {comb1+comb2};
			
			env = EnvGen.ar(Env.new([0, 1.0, 2, 0], [0.1, 0.5, rls], [1, -3, -1], 2 ), gate,  doneAction:2);
			ses = Pan2.ar(ses *SinOsc.ar( cos/pan.cos, 0, 0.4),FSinOsc.kr(pan), 0.5,ses);
			
			Out.ar(out, ses *vol/1.1 );
		}).add;
		
		SynthDef("limiter",{ arg out=0, in = 0, lvl = 0.9, durt = 0.01;
			ReplaceOut.ar( out, Limiter.ar( In.ar(in, 2), lvl, durt) )
		}).add;
		
		
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

		~xor1 = Synth.head( ~piges, \xorInt, [
				\out, ~revBus,
			\vol, 1.2, \amp, 1.3,
			\pan, 0.15, \cos, 1.9, 
			\lfn1a, ~a3, \lfn2a, ~c4, 
			\lfn1b, ~a5, \lfn2b, ~a2, 
			\lfbeat1, 4, \lfbeat2, 4]
		 );
			
		~lim = Synth.tail(~effe, "limiter",
			[ \in ,~limBus, \out, 0,  
			\lvl, 0.6, \durt, 0.01
			]
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