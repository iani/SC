/*

ChReverb.load;

*/

ChReverb {
	*load {
		var s;
		s = Server.default;
		
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
						4, 						// numChans
						ses, 					// in
						SinOsc.kr(0.01, -0.1,0.1), 	// pos
						0.5,						// level
						2.5						// width
					);
				Out.ar(out, ses );
			}).send(s);
			
			
	//REVERB
	
	//tog
			/*~togFlow= OSCresponderNode(nil, '/outs/togRev', { |t,r,m| 
				if (~rev.isNil) {
				~rev = Synth.after(~piges,"reverb", 
					[
					\in,  ~revBus, \out, 0, 
					\amp, 0.5
					]
				);
				}{
					~rev.free;
					~rev = nil;
				}
			}).add;*/
	
			~roomF = OSCresponderNode(nil, '/outs/room', { |t,r,m| 
				var n1;
				n1 = (m[1]*300);
				~rev.set(\roomsize, n1);
				}).add;
	
			~room2 = OSCresponderNode(nil, '/outs/room2', { |t,r,m| 
				var n1;
				n1 = (m[1]*3);
				~rev.set(\roomsize, n1);
				}).add;
	
			~revtimeF = OSCresponderNode(nil, '/outs/revtime', { |t,r,m| 
				var n1;
				n1 = (m[1]*100);
				~rev.set(\revtime, n1);
				}).add;
	
			~dampF = OSCresponderNode(nil, '/outs/damp', { |t,r,m| 
				var n1;
				n1 = (m[1]);
				~rev.set(\damping, n1);
				}).add;			
	
			~revampF = OSCresponderNode(nil, '/outs/revamp', { |t,r,m| 
				var n1;
				n1 = (m[1]) ;
				~rev.set(\amp, n1);
			}).add;
			
			~revmain = OSCresponderNode(nil, '/outs/revMain', { |t,r,m|
				~rev.set(
					\revtime, 20, \roomsize, 120, \damping, 0.9, 
					\inputbw, 0.3, \drylevel -9, 
					\earlylevel, -10, \taillevel, -10.1, \amp, 0.005
				);
			}).add;
			~bath = OSCresponderNode(nil, '/outs/bath', { |t,r,m|
				~rev.set(
					\roomsize, 5, \revtime, 0.6, \damping, 0.62,
					\earlylevel, -11, \taillevel, -13
				);
			}).add;
			~church = OSCresponderNode(nil, '/outs/church', { |t,r,m|
					~rev.set(
						\roomsize, 80, \revtime, 4.85, \damping, 0.41, 
						\inputbw, 0.19, \drylevel -3, 
						\earlylevel, -9, \taillevel, -11
					);
			}).add;
			~cathedral = OSCresponderNode(nil, '/outs/cath', { |t,r,m|
					~rev.set(
						\roomsize, 243, \revtime, 1, \damping, 0.1, 
						\inputbw, 0.34, \drylevel -3, 
						\earlylevel, -11, \taillevel, -9
					);
			}).add;
			~canyon = OSCresponderNode(nil, '/outs/canyon', { |t,r,m|
					~rev.set(
						\roomsize, 300, \revtime, 103, \damping, 0.43, 
						\inputbw, 0.51, \drylevel -5, 
						\earlylevel, -26, \taillevel, -20
					);
			}).add;
	
	
	
	}
	
	*play{
		
		~rev = Synth.after(~piges,"reverb", 
					[
					\in,  ~revBus, \out, 0, 
					\amp, 0.0
					]
				);
	
	
	}
	
}


/*
(
			~rev.set(
				\roomsize, 5, \revtime, 0.6, \damping, 0.62,
				\earlylevel, -11, \taillevel, -13
			);
)
*/