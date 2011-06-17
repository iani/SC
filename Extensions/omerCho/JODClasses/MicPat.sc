/*

MicPat.load;

*/

MicPat {
	classvar <action;
	*load {
	
		var s;
		
		s = Server.default;	


		SynthDef(\playBuf1, { | out=0, vol = 0.5, bufnum = 0, gate = 1.5, rate = 1, startPos = 0, amp = 1.0, 
			att = 0.1, sus = 1, rls = 0.5, lvl=0.8,
			pan = 0, loop = 0|
			var audio, env;
			env = EnvGen.ar(Env.perc(att, rls, lvl), 1.5, timeScale: sus, doneAction: 2);
		
			rate = rate * BufRateScale.kr(bufnum);
			startPos = startPos * BufFrames.kr(bufnum);
			
			audio = BufRd.ar(1, bufnum, Phasor.ar(1, rate, startPos, BufFrames.ir(bufnum)), 0, 4);
			audio = env * audio;
			audio = Pan2.ar(audio, pan, amp*6*vol);
			Out.ar(out, audio);
		}).add;

/////////////OSC/////////
~volA = PatternProxy ( Pn(1.0, inf) );
~vol1 =ÊOSCresponderNode(nil,Ê'/bufP/volA', {Ê|t,r,m|Ê
	~volA.source = (m[1]);	 
}).add;

~durationA = PatternProxy ( Pn(1.0, inf) );
~durationASpec = ControlSpec(3, 0.3, \lin);
~dur1 =ÊOSCresponderNode(nil,Ê'/bufP/durA', {Ê|t,r,m|Ê
	
	~durationA.source = (m[1]);	 
	
	}).add;


~positionA = PatternProxy ( Pn(0, inf) );
~positionASpec = ControlSpec(0, 1, \lin);

~rateA = PatternProxy ( Pn(0.5, inf) );
~rateASpec = ControlSpec(0.1, 8, \lin);
~xy1 =ÊOSCresponderNode(nil,Ê'/bufP/xyA', {Ê|t,r,m|Ê
	varÊn1, n2;
	n1Ê= (m[1]);
	n2Ê= (m[2]);
	
	~rateA.source = ~rateASpec.map(n1);
	~positionA.source = ~positionASpec.map(n2);
	~durationA.source = (m[2]);
	 
	}).add;

~randA = PatternProxy ( Pn(0.5, inf) );
~randASpec = ControlSpec(0.001, 2, \lin);
~randomA =ÊOSCresponderNode(nil,Ê'/bufP/randA', {Ê|t,r,m|Ê
	var n1;
	n1Ê= (m[1]);
	~randA.source = ~randASpec.map(n1);
	}).add;




~toggleA=ÊOSCresponderNode(nil,Ê'/bufP/togA', {Ê|t,r,m|Ê
	if (~pat1.isNil or: { ~pat1.isPlaying.not}) {
		~pat1 = Pdef(\buf1, Pbind(
			
			\instrument,	\playBuf1,
			\vol,		~volA ,
			\amp,		Pseq([~turkAksagiAmp], inf) ,
			\dur,		Prand([0.5,0.1,0.01], inf).sin** ~durationA*4,
			\startPos,	~positionA,
			\rate,		Pwhite(0.1, 4).abs/8*~rateA,
			\att,		0.1,
			\sus,		Pseq([ (4..1),2 ], inf),
			\rls,		2.5,
			\lvl,		0.9,
			\pan,  		Pseq([ (-3..3),inf ], inf).tanh,
			\bufnum,		 Pseq([~bufJer1, ~bufA, ~bufB, ~bufIn, ~bufJer2], inf),
			\group, 		~piges,
			\out,		Pseq([[~limBus, ~revBus, ~dlyBus], [~rlpBus, ~revBus, ~dlyBus], [~wahBus, ~revBus, ~dlyBus]], inf )
	
		)).play( quant: [0, 0, 0]);
	}{
		~pat1.stop;
		~pat1 = nil;
	}
}).add;


~pushA1 =ÊOSCresponderNode(nil,Ê'/bufP/push1A', {Ê|t,r,m|Ê
	[
	~pat1 = Pdef(\buf1, Pbind(
			
			\instrument,	\playBuf1,
			\vol,		~volA ,
			\amp,		Pseq([~turkAksagiAmp], inf),
			\dur,		Prand([0.5,0.1,0.01, 0.02, 0.3], inf).sin**~durationA*2,
			\startPos,	~positionA,
			\rate,		Pwhite(0.2, 4).abs/8*~rateA,
			\att,		0.1,
			\sus,		Pseq([ (4..2),3 ], inf),
			\rls,		2.5,
			\lvl,		0.9,
			\pan,  		Pseq([ (-3..3),inf ], inf),
			\bufnum,		 Pseq([~bufJer1, ~bufA, ~bufB, ~bufIn, ~bufJer2], inf),
			\group, 		~piges,
			\out,		Pseq([[~limBus, ~revBus, ~dlyBus], [~rlpBus, ~revBus, ~dlyBus], [~wahBus, ~revBus, ~dlyBus]], inf )
	
		));
	]
	}).add;


~pushA2 =ÊOSCresponderNode(nil,Ê'/bufP/push2A', {Ê|t,r,m|Ê
	[
	~pat1 = Pdef(\buf1, Pbind(
			
			\instrument,	\playBuf1,
			\vol,		~volA ,
			\amp,		Pseq([~turkAksagiAmp], inf)/2,
			\dur,		Prand([0.5,0.1,0.01, 0.02, 0.3], inf).sin**~durationA*2,
			\startPos,	~positionA,
			\rate,		Pwhite(0.2, 4).abs/8*~rateA.cos,
			\att,		Pseq([ (1..0.2),0.3 ], inf),
			\sus,		Pseq([ (4..2),3 ], inf),
			\rls,		2.0,
			\lvl,		0.8,
			\pan,  		Pseq([ (-3..3),inf ], inf),
			\bufnum,		 Pseq([~bufJer1, ~bufA, ~bufB, ~bufIn, ~bufJer2], inf),
			\group, 		~piges,
			\out,		Pseq([[~limBus, ~revBus, ~dlyBus], [~rlpBus, ~revBus, ~dlyBus], [~wahBus, ~revBus, ~dlyBus]], inf )
	
		));
	]
	}).add;

~pushA3 =ÊOSCresponderNode(nil,Ê'/bufP/push3A', {Ê|t,r,m|Ê
	[
	~pat1 = Pdef(\buf1, Pbind(
			
			\instrument,	\playBuf1,
			\vol,		~volA ,
			\amp,		Pseq([~turkAksagiAmp], inf)/2,
			\dur,		Prand([0.5,0.1,0.01, 0.02, 0.3], inf).sin**~durationA*2,
			\startPos,	~positionA,
			\rate,		Pwhite(0.2, 4).abs/8*~rateA.cos,
			\att,		Pseq([ (1..0.2),0.3.rand ], inf),
			\sus,		Pseq([ (4..2),3 ], inf),
			\rls,		Pseq([ (2..0.6),1 ], inf),
			\lvl,		0.8,
			\pan,  		Pseq([ (-2..2),inf ], inf),
			\bufnum,		 Pseq([~bufJer1, ~bufA, ~bufB, ~bufIn, ~bufJer2], inf),
			\group, 		~piges,
			\out,		Pseq([[~rlpBus, ~revBus, ~dlyBus], [~rlpBus, ~revBus, ~dlyBus], [~wahBus, ~revBus, ~dlyBus]], inf )
	
		));
	]
	}).add;


~pushA4 =ÊOSCresponderNode(nil,Ê'/bufP/push4A', {Ê|t,r,m|Ê
	[
	~pat1 = Pdef(\buf1, Pbind(
			
			\instrument,	\playBuf1,
			\vol,		~volA ,
			\amp,		Pseq([~turkAksagiAmp], inf)/2,
			\dur,		Prand([0.5,0.1,0.01, 0.02, 0.3], inf).sin**1.1,
			\startPos,	~positionA,
			\rate,		Pwhite(0.2, 4).abs/8*~rateA.cos/4,
			\att,		Pseq([ (1..0.2),0.3.rand ], inf),
			\sus,		Pseq([ (4..2),3.tanh ], inf),
			\rls,		Pseq([ (2..0.6),1 ], inf),
			\lvl,		0.8,
			\pan,  		Pseq([ (-2..2),inf ], inf),
			\bufnum,		 Pseq([~bufJer1, ~bufA, ~bufB, ~bufIn, ~bufJer2], inf),
			\group, 		~piges,
			\out,		Pseq([[~rlpBus, ~revBus, ~dlyBus], [~rlpBus, ~revBus, ~dlyBus], [~wahBus, ~revBus, ~dlyBus]], inf )
	
		));
	]
	}).add;

	
	
	}
	
	*unLoad{
	

	
	}
	
}