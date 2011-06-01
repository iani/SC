/* Draft of simple class for synthdefs for a project 


TransBeings1SynthDefs.load;

*/

TransBeings1SynthDefs { 

	
	*load {

		var s;
		
		s = Server.default;	

//Effects SynyhDefs
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
 


	}
}