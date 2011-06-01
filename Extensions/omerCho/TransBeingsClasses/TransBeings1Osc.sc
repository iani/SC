/*

TransBeings1Osc.load;

*/

TransBeings1Osc {
	classvar <action;
	*load {


			//REVERB
				~roomF =ÊOSCresponderNode(nil,Ê'/pg1/room', {Ê|t,r,m|Ê
					varÊn1;
					n1Ê= (m[1]*300);
					~rev.set(\roomsize, n1);
					}).add;
		
				~revtimeF =ÊOSCresponderNode(nil,Ê'/pg1/revtime', {Ê|t,r,m|Ê
					varÊn1;
					n1Ê= (m[1]*100);
					~rev.set(\revtime, n1);
					}).add;
		
				~dampF =ÊOSCresponderNode(nil,Ê'/pg1/damp', {Ê|t,r,m|Ê
					varÊn1;
					n1Ê= (m[1]);
					~rev.set(\damping, n1);
					}).add;			
		
				~revampF =ÊOSCresponderNode(nil,Ê'/pg1/revamp', {Ê|t,r,m|Ê
					varÊn1;
					n1Ê= (m[1]) ;
					~rev.set(\amp, n1);
				}).add;
				
				~revmain = OSCresponderNode(nil,Ê'/pg1/setmain', {Ê|t,r,m|
					~rev.set(
						\revtime, 20, \roomsize, 120, \damping, 0.9, 
						\inputbw, 0.3, \drylevel -9, 
						\earlylevel, -10, \taillevel, -10.1, \amp, 0.0005
					);
				}).add;
				~bath = OSCresponderNode(nil,Ê'/pg1/bath', {Ê|t,r,m|
					~rev.set(
						\roomsize, 5, \revtime, 0.6, \damping, 0.62,
						\earlylevel, -11, \taillevel, -13
					);
				}).add;
				~church = OSCresponderNode(nil,Ê'/pg1/church', {Ê|t,r,m|
						~rev.set(
							\roomsize, 80, \revtime, 4.85, \damping, 0.41, 
							\inputbw, 0.19, \drylevel -3, 
							\earlylevel, -9, \taillevel, -11
						);
				}).add;
				~cathedral = OSCresponderNode(nil,Ê'/pg1/cath', {Ê|t,r,m|
						~rev.set(
							\roomsize, 243, \revtime, 1, \damping, 0.1, 
							\inputbw, 0.34, \drylevel -3, 
							\earlylevel, -11, \taillevel, -9
						);
				}).add;
				~canyon = OSCresponderNode(nil,Ê'/pg1/canyon', {Ê|t,r,m|
						~rev.set(
							\roomsize, 300, \revtime, 103, \damping, 0.43, 
							\inputbw, 0.51, \drylevel -5, 
							\earlylevel, -26, \taillevel, -20
						);
				}).add;		
				
				
				//DELAY
				~delayF =ÊOSCresponderNode(nil,Ê'/pg1/delay', {Ê|t,r,m|Ê
					varÊn1;
					n1Ê= (m[1]*5);
					~dly.set(\delay, n1);
					}).add;
				~decayF =ÊOSCresponderNode(nil,Ê'/pg1/decay', {Ê|t,r,m|Ê
					varÊn1;
					n1Ê= (m[1]*15) +0.1;
					~dly.set(\decay, n1);
					}).add;
				~dlyampF =ÊOSCresponderNode(nil,Ê'/pg1/dlyamp', {Ê|t,r,m|Ê
					varÊn1;
					n1Ê= (m[1]*2);
					~dly.set(\amp, n1);
					}).add;
				~dlyMain = OSCresponderNode(nil,Ê'/pg1/dlymain', {Ê|t,r,m|
					~dly.set(\delay, 0, \decay, 3);
				}).add;
				~dly1Set = OSCresponderNode(nil,Ê'/pg1/dly1', {Ê|t,r,m|
					~dly.set(\delay, 1, \decay, 3);
				}).add;		
				~dly2Set = OSCresponderNode(nil,Ê'/pg1/dly2', {Ê|t,r,m|
					~dly.set(\delay, 2, \decay, 3);
				}).add;		
				~dly3Set = OSCresponderNode(nil,Ê'/pg1/dly3', {Ê|t,r,m|
					~dly.set(\delay, 3, \decay, 3);
				}).add;		
				~dly4Set = OSCresponderNode(nil,Ê'/pg1/dly4', {Ê|t,r,m|
					~dly.set(\delay, 4, \decay, 3);
				}).add;		
				
				//RLPF
				~rlpfreqF =ÊOSCresponderNode(nil,Ê'/pg1/rlpfreq', {Ê|t,r,m|Ê
					varÊn1;
					n1Ê= (m[1]*5400)+ 20 ;
					~rlp.set(\ffreq, n1);
				}).add;		
				~rlprqF =ÊOSCresponderNode(nil,Ê'/pg1/rlprq', {Ê|t,r,m|Ê
					varÊn1;
					n1Ê= (m[1]*10)-2 ;
					~rlp.set(\rq, n1);
				}).add;		
				~rlpampF =ÊOSCresponderNode(nil,Ê'/pg1/rlpamp', {Ê|t,r,m|Ê
					varÊn1;
					n1Ê= (m[1]*4) ;
					~rlp.set(\amp, n1);
				}).add;		
				
				~limlevF =ÊOSCresponderNode(nil,Ê'/pg1/limlev', {Ê|t,r,m|Ê
					varÊn1;
					n1Ê= (m[1]) ;
					~lim.set(\lvl, n1);
				}).add;
				~limdurtF =ÊOSCresponderNode(nil,Ê'/pg1/limdurt', {Ê|t,r,m|Ê
					varÊn1;
					n1Ê= (m[1]*2) ;
					~lim.set(\durt, n1);
				}).add;		
				
				~distortF =ÊOSCresponderNode(nil,Ê'/pg1/distort', {Ê|t,r,m|Ê
					varÊn1;
					n1Ê= (m[1]*50) ;
					~wah.set(\dist, n1);
				}).add;		
				~wahrqF =ÊOSCresponderNode(nil,Ê'/pg1/wahrq', {Ê|t,r,m|Ê
					varÊn1;
					n1Ê= (m[1]*20)-5 ;
					~wah.set(\rq, n1);
				}).add;
				~wahampF =ÊOSCresponderNode(nil,Ê'/pg1/wahamp', {Ê|t,r,m|Ê
					varÊn1;
					n1Ê= (m[1]*6) ;
					~wah.set(\amp, n1);
				}).add;
		
				//PANNING
				~panSpec = ControlSpec(-1, 1, \lin);
				~rlpPanF =ÊOSCresponderNode(nil,Ê'/pg1/pans/1', {Ê|t,r,m|Ê
					varÊn1;
					n1Ê= (m[1]);
					~rlp.set(\pan,~panSpec.map(n1));
					}).add;
		
				~dlyPanF =ÊOSCresponderNode(nil,Ê'/pg1/pans/2', {Ê|t,r,m|Ê
					varÊn1;
					n1Ê= (m[1]);
					~dly.set(\pan,~panSpec.map(n1));
					}).add;
		
				~wahPanF =ÊOSCresponderNode(nil,Ê'/pg1/pans/3', {Ê|t,r,m|Ê
					varÊn1;
					n1Ê= (m[1]);
					~wah.set(\pan,~panSpec.map(n1));
					}).add;
		
				~revPanF =ÊOSCresponderNode(nil,Ê'/pg1/pans/4', {Ê|t,r,m|Ê
					varÊn1;
					n1Ê= (m[1]);
					~rev.set(\pan,~panSpec.map(n1));
					}).add;

			
		
	}
	
	*unLoad{
	
	
	}
	
}