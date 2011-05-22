/*

ChRlpf.load;

*/

ChRlpf {
	classvar <action;
	*load {
	var s;
	s = Server.default;
	
		SynthDef("rlpf",{ |out = 0, amp = 0.8 in = 0, pan = 0,
			ffreq = 600, rq = 0.1, lagup=1.2, lagdown=1.5, 
			lvl = 0.9, durt = 0.01|
			var ses;
			ses =  In.ar(in, 2);
			ses = RLPF.ar( 
				ses, 
				Lag2UD.kr( // lag the frequency
					ffreq,
					lagup,
					lagdown
				), 
				rq, 
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
			
			Out.ar( out, ses)
		}).send(s);
		
		
//RLPF
		~rlpfreqF =ÊOSCresponderNode(nil,Ê'/outs/rlpfreq', {Ê|t,r,m|Ê
			varÊn1;
			n1Ê= (m[1]*5400)+ 20 ;
			~rlp.set(\ffreq, n1);
		}).add;		
		~rlprqF =ÊOSCresponderNode(nil,Ê'/outs/rlprq', {Ê|t,r,m|Ê
			varÊn1;
			n1Ê= (m[1]*10)-2 ;
			~rlp.set(\rq, n1);
		}).add;		
		~rlpampF =ÊOSCresponderNode(nil,Ê'/outs/rlpamp', {Ê|t,r,m|Ê
			varÊn1;
			n1Ê= (m[1]*4) ;
			~rlp.set(\amp, n1);
		}).add;
	
	
	
	}
	
	*unLoad{
	
	
	}
	
}