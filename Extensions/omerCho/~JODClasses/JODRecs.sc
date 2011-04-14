/*

JODRecs.load;

*/

JODRecs {
	classvar <action;
	*load {

			var s;
			
			s = Server.default;	
	
///////////////SynthDefs/////////////

		SynthDef(\recToBufA,{ arg out=0,bufnum=0;
			var source;
			source = SoundIn.ar(6, 2);		
			RecordBuf.ar(source, ~bufA); 
		}).add;
		
		SynthDef(\recToBufB,{ arg out=0,bufnum=0;
			var source;
			source = SoundIn.ar(6, 2);		
			RecordBuf.ar(source, ~bufB); 
		}).add;
		
		SynthDef(\recToBufJer1,{ arg out=0,bufnum=0;
			var source;
			source = SoundIn.ar(0, 2);		
			RecordBuf.ar(source, ~bufJer1); 
		}).add;
		
		SynthDef(\recToBufJer2,{ arg out=0,bufnum=0;
			var source;
			source = SoundIn.ar(0, 2);		
			RecordBuf.ar(source, ~bufJer2); 
		}).add;
		
		SynthDef(\recToBufIn,{ arg out=0, in=0, bufnum=0;
			var source;
			source = In.ar(in, 1);		
			RecordBuf.ar(source, ~bufIn); 
		}).add;

///////////////OSC///////////////

		~recA=ÊOSCresponderNode(nil,Ê'/bufP/recA', {Ê|t,r,m|Ê
			if (~recBufA.isNil) {
				~recBufA = Synth(\recToBufA, [\bufnum, ~bufA]);
			}{
				~recBufA.free;
				~recBufA = nil;
			}
		}).add;

		~recB=ÊOSCresponderNode(nil,Ê'/bufP/recB', {Ê|t,r,m|Ê
			if (~recBufB.isNil) {
				~recBufB = Synth(\recToBufB, [\bufnum, ~bufB]);
			}{
				~recBufB.free;
				~recBufB = nil;
			}
		}).add;
		
		~recJer1=ÊOSCresponderNode(nil,Ê'/bufP/jerA', {Ê|t,r,m|Ê
			if (~recBufJer1.isNil) {
				~recBufJer1 = Synth(\recToBufJer1, [\bufnum, ~bufJer1]);
			}{
				~recBufJer1.free;
				~recBufJer1 = nil;
			}
		}).add;		
		
		~recJer2=ÊOSCresponderNode(nil,Ê'/bufP/jerB', {Ê|t,r,m|Ê
			if (~recBufJer2.isNil) {
				~recBufJer2 = Synth(\recToBufJer1, [\bufnum, ~bufJer2]);
			}{
				~recBufJer2.free;
				~recBufJer2 = nil;
			}
		}).add;
		
		~recIn1=ÊOSCresponderNode(nil,Ê'/bufP/recIn', {Ê|t,r,m|Ê
			if (~recBufIn.isNil) {
				~recBufIn = Synth.after(~rev, \recToBufIn, [\bufnum, ~bufIn, \in, 0, \out, ~limBus]);
			}{
				~recBufIn.free;
				~recBufIn = nil;
			}
		}).add;	
		
	}
	
	*unLoad{
	
	
	}
	
}