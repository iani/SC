/*

ChDelay.load;

*/

ChDelay {
	classvar <action;
	*load {
		var s;
		s = Server.default;
		
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
						4, 						// numChans
						ses, 					// in
						SinOsc.kr(0.01, -0.1,0.1), 	// pos
						0.5,						// level
						2.5						// width
					);
				Out.ar(out,  ses);
			}).send(s);
			
			
	//DELAY
	
	//tog
			/*~togFlow=ÊOSCresponderNode(nil,Ê'/outs/togDly', {Ê|t,r,m|Ê
				if (~dly.isNil) {
				~dly = Synth.after(~piges,"delay", 
					[\in,  ~dlyBus, \out, 0, 
					\amp, 0.8
					]
				);
				}{
					~dly.free;
					~dly = nil;
				}
			}).add;*/
	
			~delayF =ÊOSCresponderNode(nil,Ê'/outs/delay', {Ê|t,r,m|Ê
				varÊn1;
				n1Ê= (m[1]*5);
				~dly.set(\delay, n1);
				}).add;
			~decayF =ÊOSCresponderNode(nil,Ê'/outs/decay', {Ê|t,r,m|Ê
				varÊn1;
				n1Ê= (m[1]*15) +0.1;
				~dly.set(\decay, n1);
				}).add;
			~dlyampF =ÊOSCresponderNode(nil,Ê'/outs/dlyamp', {Ê|t,r,m|Ê
				varÊn1;
				n1Ê= (m[1]*2);
				~dly.set(\amp, n1);
				}).add;
			~dlyMain = OSCresponderNode(nil,Ê'/outs/dlyMain', {Ê|t,r,m|
				~dly.set(\delay, 0, \decay, 3);
			}).add;
			~dly1Set = OSCresponderNode(nil,Ê'/outs/dly1', {Ê|t,r,m|
				~dly.set(\delay, 1, \decay, 3);
			}).add;		
			~dly2Set = OSCresponderNode(nil,Ê'/outs/dly2', {Ê|t,r,m|
				~dly.set(\delay, 2, \decay, 3);
			}).add;		
			~dly3Set = OSCresponderNode(nil,Ê'/outs/dly3', {Ê|t,r,m|
				~dly.set(\delay, 3, \decay, 3);
			}).add;		
			~dly4Set = OSCresponderNode(nil,Ê'/outs/dly4', {Ê|t,r,m|
				~dly.set(\delay, 4, \decay, 3);
			}).add;
	
	
	
	}
	
	*play{
		
		~dly = Synth.after(~piges,"delay", 
					[\in,  ~dlyBus, \out, 0, 
					\amp, 0.0
					]
				);
	
	}
	
}