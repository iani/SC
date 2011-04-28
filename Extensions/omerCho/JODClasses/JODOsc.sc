/*

JODOsc.load;

*/

JODOsc {
	classvar <action;
	*load {

////////////////////////MainPattern

/*
~attA = PatternProxy ( Pn(0.1, inf) );
~attASpec = ControlSpec(0.05, 2, \lin);

~decA = PatternProxy ( Pn(0.5, inf) );
~decASpec = ControlSpec(0.3, 1.5, \lin);

~susA = PatternProxy ( Pn(1, inf) );
~susASpec = ControlSpec(0.5, 2, \lin);


~rlsA = PatternProxy ( Pn(1, inf) );
~rlsASpec = ControlSpec(0.4, 2, \lin);

~lvlA = PatternProxy ( Pn(0.8, inf) );
~lvlASpec = ControlSpec(0, 1, \lin);
*/



/*
~envAatt =ÊOSCresponderNode(nil,Ê'/bufP/envA/1', {Ê|t,r,m|Ê
	var n1;
	n1Ê= (m[1]);
	~attA.source = ~attASpec.map(n1);
	}).add;

~envAdec =ÊOSCresponderNode(nil,Ê'/bufP/envA/2', {Ê|t,r,m|Ê
	var n1;
	n1Ê= (m[1]);
	~decA.source = ~decASpec.map(n1);
	}).add;

~envAsus =ÊOSCresponderNode(nil,Ê'/bufP/envA/3', {Ê|t,r,m|Ê
	var n1;
	n1Ê= (m[1]);
	~susA.source = ~susASpec.map(n1);
	}).add;

~envArls =ÊOSCresponderNode(nil,Ê'/bufP/envA/4', {Ê|t,r,m|Ê
	var n1;
	n1Ê= (m[1]);
	~rlsA.source = ~rlsASpec.map(n1);
	}).add;

~envAlvl =ÊOSCresponderNode(nil,Ê'/bufP/envA/5', {Ê|t,r,m|Ê
	var n1;
	n1Ê= (m[1]);
	~lvlA.source = ~lvlASpec.map(n1);
	}).add;

*/



//baxx

~bax=ÊOSCresponderNode(nil,Ê'/bufP/basset', {Ê|t,r,m|Ê
	if (~baxx.isNil) {
		~baxx = Synth("baxx", 
				[
				\freq, [42.0 rrand: 49.9],
				\pan, [-1.0 rrand: 1.0],
				\out, 6, \amp, 0.2
				]
			);
	}{
		~baxx.release(4);
		~baxx = nil;
	}
}).add;

~mNois=ÊOSCresponderNode(nil,Ê'/bufP/kaos', {Ê|t,r,m|Ê
	if (~mNoise.isNil) {
		~mNoise = Synth(\abs);
	}{
		~mNoise.release(5);
		~mNoise = nil;
	}
}).add;






~mx1Spec = ControlSpec(0.1, 3.0, \exp);
~mx2Spec = ControlSpec(3.0, 0.1, \exp);

~my1Spec = ControlSpec(0.1, 3.0, \exp);
~my2Spec = ControlSpec(3.0, 0.1, \exp);

~xyK =ÊOSCresponderNode(nil,Ê'/bufP/xyKaos', {Ê|t,r,m|Ê
	varÊn1, n2;
	n1Ê= (m[1]);
	n2Ê= (m[2]);
	
	~mNoise.set(\mx1, ~mx1Spec.map(n1));
	~mNoise.set(\mx2, ~mx2Spec.map(n1));

	~mNoise.set(\my1, ~mx1Spec.map(n2));
	~mNoise.set(\my2, ~mx2Spec.map(n2));
	 
	}).add;


~kaosVolSpec = ControlSpec(0.0, 1.1, \lin);
~kaosVol =ÊOSCresponderNode(nil,Ê'/bufP/kaosvol', {Ê|t,r,m|Ê
	varÊn1;
	n1Ê= (m[1]);
	~mNoise.set(\vol, ~kaosVolSpec.map(n1));
}).add;

//////////////////////////TAP		
		~togTap1=ÊOSCresponderNode(nil,Ê'/bufP/togTapA', {Ê|t,r,m|Ê
			if (~tapBuf1.isNil) {
				~tapBuf1 = Synth.tail(~piges,\tapBuf, 
					[
					\bufnum, ~bufTap, 
					\out, [~revBus, ~dlyBus, ~rlpBus, ~wahBus]
					]
				);
			}{
				~tapBuf1.free;
				~tapBuf1 = nil;
			}
		}).add;
		
		
		~delayA1Spec = ControlSpec(0, 3, \lin);
		~delayA2Spec = ControlSpec(0, 8, \lin);
		
		~dly1Tap =ÊOSCresponderNode(nil,Ê'/bufP/dly1Tap', {Ê|t,r,m|Ê
			varÊn1;
			n1Ê= (m[1]);
			
			~tapBuf1.set(\delay1, ~delayA1Spec.map(n1));
		
		}).add;
		
		~dlyT2ap =ÊOSCresponderNode(nil,Ê'/bufP/dly1Tap', {Ê|t,r,m|Ê
			varÊn1;
			n1Ê= (m[1]);
			
			~tapBuf1.set(\delay2, ~delayA2Spec.map(n1));
		
		}).add;
		
		~set1Tap =ÊOSCresponderNode(nil,Ê'/bufP/push4', {Ê|t,r,m|Ê
			varÊn1;
			n1Ê= (m[1]);
			
			~tapBuf1.set(\delay1, 0.01);
		
		}).add;
		
		~set2Tap =ÊOSCresponderNode(nil,Ê'/bufP/push5', {Ê|t,r,m|Ê
			varÊn1;
			n1Ê= (m[1]);
			
			~tapBuf1.set(\delay2, 0.02);
		
		}).add;
		
		
		~volTap =ÊOSCresponderNode(nil,Ê'/bufP/volTapA', {Ê|t,r,m|Ê
			varÊn1;
			n1Ê= (m[1]);
			
			~tapBuf1.set(\vol, n1);
			 
		}).add;
		


////////////////////////////////////////Effects///////////////////////////////////////////////
			//REVERB
				~roomF =ÊOSCresponderNode(nil,Ê'/bufP/room', {Ê|t,r,m|Ê
					varÊn1;
					n1Ê= (m[1]*300);
					~rev.set(\roomsize, n1);
					}).add;
		
				~room2 =ÊOSCresponderNode(nil,Ê'/bufP/room2', {Ê|t,r,m|Ê
					varÊn1;
					n1Ê= (m[1]*3);
					~rev.set(\roomsize, n1);
					}).add;

				~revtimeF =ÊOSCresponderNode(nil,Ê'/bufP/revtime', {Ê|t,r,m|Ê
					varÊn1;
					n1Ê= (m[1]*100);
					~rev.set(\revtime, n1);
					}).add;
		
				~dampF =ÊOSCresponderNode(nil,Ê'/bufP/damp', {Ê|t,r,m|Ê
					varÊn1;
					n1Ê= (m[1]);
					~rev.set(\damping, n1);
					}).add;			
		
				~revampF =ÊOSCresponderNode(nil,Ê'/bufP/revamp', {Ê|t,r,m|Ê
					varÊn1;
					n1Ê= (m[1]) ;
					~rev.set(\amp, n1);
				}).add;
				
				~revmain = OSCresponderNode(nil,Ê'/bufP/revmain', {Ê|t,r,m|
					~rev.set(
						\revtime, 20, \roomsize, 120, \damping, 0.9, 
						\inputbw, 0.3, \drylevel -9, 
						\earlylevel, -10, \taillevel, -10.1, \amp, 0.0005
					);
				}).add;
				~bath = OSCresponderNode(nil,Ê'/bufP/bath', {Ê|t,r,m|
					~rev.set(
						\roomsize, 5, \revtime, 0.6, \damping, 0.62,
						\earlylevel, -11, \taillevel, -13
					);
				}).add;
				~church = OSCresponderNode(nil,Ê'/bufP/church', {Ê|t,r,m|
						~rev.set(
							\roomsize, 80, \revtime, 4.85, \damping, 0.41, 
							\inputbw, 0.19, \drylevel -3, 
							\earlylevel, -9, \taillevel, -11
						);
				}).add;
				~cathedral = OSCresponderNode(nil,Ê'/bufP/cath', {Ê|t,r,m|
						~rev.set(
							\roomsize, 243, \revtime, 1, \damping, 0.1, 
							\inputbw, 0.34, \drylevel -3, 
							\earlylevel, -11, \taillevel, -9
						);
				}).add;
				~canyon = OSCresponderNode(nil,Ê'/bufP/canyon', {Ê|t,r,m|
						~rev.set(
							\roomsize, 300, \revtime, 103, \damping, 0.43, 
							\inputbw, 0.51, \drylevel -5, 
							\earlylevel, -26, \taillevel, -20
						);
				}).add;		
				
				
				//DELAY
				~delayF =ÊOSCresponderNode(nil,Ê'/bufP/delay', {Ê|t,r,m|Ê
					varÊn1;
					n1Ê= (m[1]*5);
					~dly.set(\delay, n1);
					}).add;
				~decayF =ÊOSCresponderNode(nil,Ê'/bufP/decay', {Ê|t,r,m|Ê
					varÊn1;
					n1Ê= (m[1]*15) +0.1;
					~dly.set(\decay, n1);
					}).add;
				~dlyampF =ÊOSCresponderNode(nil,Ê'/bufP/dlyamp', {Ê|t,r,m|Ê
					varÊn1;
					n1Ê= (m[1]*2);
					~dly.set(\amp, n1);
					}).add;
				~dlyMain = OSCresponderNode(nil,Ê'/bufP/dlymain', {Ê|t,r,m|
					~dly.set(\delay, 0, \decay, 3);
				}).add;
				~dly1Set = OSCresponderNode(nil,Ê'/bufP/dly1', {Ê|t,r,m|
					~dly.set(\delay, 1, \decay, 3);
				}).add;		
				~dly2Set = OSCresponderNode(nil,Ê'/bufP/dly2', {Ê|t,r,m|
					~dly.set(\delay, 2, \decay, 3);
				}).add;		
				~dly3Set = OSCresponderNode(nil,Ê'/bufP/dly3', {Ê|t,r,m|
					~dly.set(\delay, 3, \decay, 3);
				}).add;		
				~dly4Set = OSCresponderNode(nil,Ê'/bufP/dly4', {Ê|t,r,m|
					~dly.set(\delay, 4, \decay, 3);
				}).add;		
				
				//RLPF
				~rlpfreqF =ÊOSCresponderNode(nil,Ê'/bufP/rlpfreq', {Ê|t,r,m|Ê
					varÊn1;
					n1Ê= (m[1]*5400)+ 20 ;
					~rlp.set(\ffreq, n1);
				}).add;		
				~rlprqF =ÊOSCresponderNode(nil,Ê'/bufP/rlprq', {Ê|t,r,m|Ê
					varÊn1;
					n1Ê= (m[1]*10)-2 ;
					~rlp.set(\rq, n1);
				}).add;		
				~rlpampF =ÊOSCresponderNode(nil,Ê'/bufP/rlpamp', {Ê|t,r,m|Ê
					varÊn1;
					n1Ê= (m[1]*4) ;
					~rlp.set(\amp, n1);
				}).add;		
				
				~limlevF =ÊOSCresponderNode(nil,Ê'/bufP/limlev', {Ê|t,r,m|Ê
					varÊn1;
					n1Ê= (m[1]) ;
					~lim.set(\lvl, n1);
				}).add;
				~limdurtF =ÊOSCresponderNode(nil,Ê'/bufP/limdurt', {Ê|t,r,m|Ê
					varÊn1;
					n1Ê= (m[1]*2) ;
					~lim.set(\durt, n1);
				}).add;		
				
				~distortF =ÊOSCresponderNode(nil,Ê'/bufP/distort', {Ê|t,r,m|Ê
					varÊn1;
					n1Ê= (m[1]*50) ;
					~wah.set(\dist, n1);
				}).add;		
				~wahrqF =ÊOSCresponderNode(nil,Ê'/bufP/wahrq', {Ê|t,r,m|Ê
					varÊn1;
					n1Ê= (m[1]*20)-5 ;
					~wah.set(\rq, n1);
				}).add;
				~wahampF =ÊOSCresponderNode(nil,Ê'/bufP/wahamp', {Ê|t,r,m|Ê
					varÊn1;
					n1Ê= (m[1]*6) ;
					~wah.set(\amp, n1);
				}).add;
		
				//PANNING
				~panSpec = ControlSpec(-1, 1, \lin);
				~rlpPanF =ÊOSCresponderNode(nil,Ê'/bufP/pans/1', {Ê|t,r,m|Ê
					varÊn1;
					n1Ê= (m[1]);
					~rlp.set(\pan,~panSpec.map(n1));
					}).add;
		
				~dlyPanF =ÊOSCresponderNode(nil,Ê'/bufP/pans/2', {Ê|t,r,m|Ê
					varÊn1;
					n1Ê= (m[1]);
					~dly.set(\pan,~panSpec.map(n1));
					}).add;
		
				~wahPanF =ÊOSCresponderNode(nil,Ê'/bufP/pans/3', {Ê|t,r,m|Ê
					varÊn1;
					n1Ê= (m[1]);
					~wah.set(\pan,~panSpec.map(n1));
					}).add;
		
				~revPanF =ÊOSCresponderNode(nil,Ê'/bufP/pans/4', {Ê|t,r,m|Ê
					varÊn1;
					n1Ê= (m[1]);
					~rev.set(\pan,~panSpec.map(n1));
					}).add;

			
		
	}
	
	*unLoad{
	
	
	}
	
}