/*

ChReverb.load;

*/

ChReverb {
	classvar <action;
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
					2, 						// numChans
					ses, 					// in
					SinOsc.kr(0.01, -0.1,0.1), 	// pos
					0.5,						// level
					2.5						// width
				);
			Out.ar(out, ses );
		}).send(s);
		
		
//REVERB
		~roomF =ÊOSCresponderNode(nil,Ê'/outs/room', {Ê|t,r,m|Ê
			varÊn1;
			n1Ê= (m[1]*300);
			~rev.set(\roomsize, n1);
			}).add;

		~room2 =ÊOSCresponderNode(nil,Ê'/outs/room2', {Ê|t,r,m|Ê
			varÊn1;
			n1Ê= (m[1]*3);
			~rev.set(\roomsize, n1);
			}).add;

		~revtimeF =ÊOSCresponderNode(nil,Ê'/outs/revtime', {Ê|t,r,m|Ê
			varÊn1;
			n1Ê= (m[1]*100);
			~rev.set(\revtime, n1);
			}).add;

		~dampF =ÊOSCresponderNode(nil,Ê'/outs/damp', {Ê|t,r,m|Ê
			varÊn1;
			n1Ê= (m[1]);
			~rev.set(\damping, n1);
			}).add;			

		~revampF =ÊOSCresponderNode(nil,Ê'/outs/revamp', {Ê|t,r,m|Ê
			varÊn1;
			n1Ê= (m[1]) ;
			~rev.set(\amp, n1);
		}).add;
		
		~revmain = OSCresponderNode(nil,Ê'/outs/revMain', {Ê|t,r,m|
			~rev.set(
				\revtime, 20, \roomsize, 120, \damping, 0.9, 
				\inputbw, 0.3, \drylevel -9, 
				\earlylevel, -10, \taillevel, -10.1, \amp, 0.0005
			);
		}).add;
		~bath = OSCresponderNode(nil,Ê'/outs/bath', {Ê|t,r,m|
			~rev.set(
				\roomsize, 5, \revtime, 0.6, \damping, 0.62,
				\earlylevel, -11, \taillevel, -13
			);
		}).add;
		~church = OSCresponderNode(nil,Ê'/outs/church', {Ê|t,r,m|
				~rev.set(
					\roomsize, 80, \revtime, 4.85, \damping, 0.41, 
					\inputbw, 0.19, \drylevel -3, 
					\earlylevel, -9, \taillevel, -11
				);
		}).add;
		~cathedral = OSCresponderNode(nil,Ê'/outs/cath', {Ê|t,r,m|
				~rev.set(
					\roomsize, 243, \revtime, 1, \damping, 0.1, 
					\inputbw, 0.34, \drylevel -3, 
					\earlylevel, -11, \taillevel, -9
				);
		}).add;
		~canyon = OSCresponderNode(nil,Ê'/outs/canyon', {Ê|t,r,m|
				~rev.set(
					\roomsize, 300, \revtime, 103, \damping, 0.43, 
					\inputbw, 0.51, \drylevel -5, 
					\earlylevel, -26, \taillevel, -20
				);
		}).add;
	
	
	
	}
	
	*unLoad{
	
	
	}
	
}