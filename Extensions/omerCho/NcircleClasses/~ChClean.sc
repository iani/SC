/*

ChClean.load;

*/

ChClean {
	classvar <action;
	*load {
	var s;
	s = Server.default;
	
		SynthDef("limiter",{ arg out=0, in = 0, lvl = 0.0, durt = 0.01;
			var src;
			src = Limiter.ar( In.ar(in, 2), lvl, durt);
			src = PanAz.ar(
					4, 						// numChans
					src, 					// in
					SinOsc.kr(MouseX.kr(0.02, 8, 'exponential')), // pos
					0.5,						// level
					2.5						// width
				);
			Out.ar( out, src )
		}).send(s);
		
			
		
		~limlevF =ÊOSCresponderNode(nil,Ê'/outs/limlev', {Ê|t,r,m|Ê
			varÊn1;
			n1Ê= (m[1]*2) ;
			~lim.set(\lvl, n1);
		}).add;
		~limdurtF =ÊOSCresponderNode(nil,Ê'/outs/limdurt', {Ê|t,r,m|Ê
			varÊn1;
			n1Ê= (m[1]*2) ;
			~lim.set(\durt, n1);
		}).add;
	
	
	
	}
	
	*play{
		~lim = Synth.after(~piges, "limiter",
			[ \in ,~limBus, \out, 0,  
			\lvl, 0.9, \durt, 0.01
			]
		);
	
	}
	
}