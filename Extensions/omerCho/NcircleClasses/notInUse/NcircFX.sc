/* Draft of simple class for synthdefs for a project 


NcircFX.load;

*/

NcircFX { 

	
	*load {

		var s;
		
		s = Server.default;	

//Effects SynyhDefs

		SynthDef("limiter",{ arg out=0, in = 0, lvl = 0.9, durt = 0.01;
			var src;
			src = Limiter.ar( In.ar(in, 2), lvl, durt);
			src = PanAz.ar(
					2, 						// numChans
					src, 					// in
					SinOsc.kr(0.01, -0.1,0.1), 	// pos
					0.5,						// level
					2.5						// width
				);
			ReplaceOut.ar( out, src )
		}).send(s);
		
		
		SynthDef("reverb", { | out, in = 0, amp=0.8, pan=0.0, 
			roomsize = 10, revtime = 1, damping = 0.2, inputbw = 0.19, spread = 15,
			drylevel = -3, earlylevel = -9, taillevel = -11,
			lvl = 0.9, durt = 0.01 |
			
			var input, ses;
			
			input = In.ar(in, 2);
			ses = GVerb.ar(
				input,
				roomsize,
				revtime,
				damping,
				inputbw,
				spread,
				drylevel.dbamp,
				earlylevel.dbamp,
				taillevel.dbamp,
				roomsize, 
				amp
			);
			ses = Limiter.ar( ses, lvl, durt);
			ses = PanAz.ar(
					2, 						// numChans
					ses, 					// in
					SinOsc.kr(0.01, -0.1,0.1), 	// pos
					0.5,						// level
					2.5						// width
				);
			ReplaceOut.ar(out, ses );
		}).send(s);
			
		SynthDef("delay", { |out = 0, in = 0, pan = 0, amp =0.8,
			maxdelay = 0.25,  delay = 1.0, decay = 0.05, 
			lvl = 0.9, durt = 0.01|
			var ses;
			ses =  In.ar(in, 2);
			ses = CombN.ar(
					ses,
					maxdelay,
					delay,
					decay, 
					amp
				);
			ses = Limiter.ar( ses, lvl, durt);
			ses = PanAz.ar(
					2, 						// numChans
					ses, 					// in
					SinOsc.kr(0.01, -0.1,0.1), 	// pos
					0.5,						// level
					2.5						// width
				);
			ReplaceOut.ar(out,  ses);
		}).send(s);
		
		SynthDef("rlpf",{ |out = 0, amp = 0.8 in = 0, pan = 0,
			ffreq = 600, rq = 0.1, 
			lvl = 0.9, durt = 0.01|
			var ses;
			ses =  In.ar(in, 2);
			ses = RLPF.ar( ses, ffreq, rq, amp);
			ses = Limiter.ar( ses, lvl, durt);
			ses = PanAz.ar(
					2, 						// numChans
					ses, 					// in
					SinOsc.kr(0.01, -0.1,0.1), 	// pos
					0.5,						// level
					2.5						// width
				);
			
			ReplaceOut.ar( out, ses);
		}).send(s);
		
		
		SynthDef("wah", { |out = 0, in = 0, amp = 1, pan = 0, 
			rate = 0.5, cfreq = 1400, mfreq = 1200, rq=0.1, dist = 0.15,
			lvl = 0.9, durt = 0.01|
			
			var zin, zout, ses;
			
			zin = In.ar(in, 2);
			cfreq = Lag3.kr(cfreq, 0.1);
			mfreq = Lag3.kr(mfreq, 0.1);
			rq   = Ramp.kr(rq, 0.1);
			zout = RLPF.ar(zin, LFNoise1.kr(rate, mfreq, cfreq), rq, amp).distort * dist;
			ses = Limiter.ar(zout, lvl, durt);
			ses = PanAz.ar(
					2, 						// numChans
					ses, 					// in
					SinOsc.kr(0.01, -0.1,0.1), 	// pos
					0.5,						// level
					2.5						// width
				);
			
			ReplaceOut.ar( out , ses); 
		}).send(s);


// 	LEVELS


		SynthDef("lvlA", { | out, in = 0, amp=0.8, pan=0.0, 
			ffreq = 800, rq = 0.1,
			roomsize = 20, revtime = 0.01, damping = 0.2, inputbw = 0.39, spread = 15,
			drylevel = -6, earlylevel = -11, taillevel = -13 |
			var input, ses;
			
			input = In.ar(in, 2);
			//input = Saw.ar(1);
			
			ses = GVerb.ar(
				input,
				roomsize,
				revtime,
				damping,
				inputbw,
				spread,
				drylevel.dbamp,
				earlylevel.dbamp,
				taillevel.dbamp,
				roomsize, 
				amp
			);
			ses = RLPF.ar( ses, ffreq, rq, 1.0);
			ses = PanAz.ar(
				2, 						// numChans
				ses, 					// in
				SinOsc.kr(0.01, -0.1,0.1), 	// pos
				0.5,						// level
				2.5						// width
			);
			
			ReplaceOut.ar(out, ses );
		}).send(s);




	}
}