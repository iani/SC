/*
Ses1.load;
*/

Ses1 {

	*load{

	var s;
	s = Server.default;

SynthDef( \aut1, { | out = 0, vol = 0.5, amp = 1, gate = 1, pan = 0,
	freq = 90, freqlp = 29, 
	sinA = 0.01, sinB = 0.1, brown = 0.01, saw = 0.1, 
	att = 0.1, sus = 0.1, rls = 0.9 |
	
	var in, osc, env, ses;
	
	env =  EnvGen.ar(Env.new([0, 1, 0.8,  0], [att, sus, rls], 'linear', nil), gate, doneAction: 2);
	
	in = SinOsc.ar(FSinOsc.ar(freq/saw, 10, brown *2 ), 1.1, SinOsc.ar( 0.1));
	
	ses = SinOsc.ar(0, in, 0.1) ;
	
	ses = RLPF.ar(ses, freqlp+brown/4, 10, 16.6, 1.4 )*freq/0.2*2pi;
	
	ses = ses.sin/2 + SinOsc.ar(freqlp, Decay.ar(SinOsc.ar(sinA.abs, sinB), brown*8.tanh.cos, Saw.ar(saw)*freq));
	
	ses = Pan2.ar( ses, pan, amp);
	
	ses = ses *env;
	
	Out.ar(out, ses *vol);
}).add;




~amp1 = PatternProxy( Pshuf([0, 0, 0, 0, 0, 0, 0.2, 0, 0, 0, 0], inf) );

~dur1 = PatternProxy( Pseq([~duyekDur], inf) );

~freq1 = PatternProxy( Pseq([~rast/8], inf) );
~freqlp1 = PatternProxy( Pseq([ ~rast/2], inf) );

~brown1 = PatternProxy( Pseq( [100.0],inf) );

~saw1 = PatternProxy ( 0.05 );
~saw1Spec = ControlSpec(-1.0, 1.0, \lin);
							
~sin1a = PatternProxy ( Pseq([12.0,0.1], inf) );
~sin2a = PatternProxy ( Pseq( [0.01],inf) );

~att1 = PatternProxy ( 0.05 );
~att1Spec = ControlSpec(0, 1.6, \lin);

~sus1 = PatternProxy ( 0.05 );
~sus1Spec = ControlSpec(0, 1.6, \lin);

~rls1 = PatternProxy ( 0.5 );
~rls1Spec = ControlSpec(0, 1.6, \lin);

~vol1 = PatternProxy ( 0.5 );
~vol1Spec = ControlSpec(0, 1.6, \lin);
~fad1 = OSCresponderNode(nil, '/harmP/fader1', { |t,r,m| 
	var n1;
	n1 = (m[1]*1);
	~vol1.source = ~vol1Spec.map(n1) 
	}).add;

~tog1= OSCresponderNode(nil, '/harmP/toggle1', { |t,r,m| 
	if (~ses1.isNil or: { ~ses1.isPlaying.not}) {
		~ses1 = Pdef(\ss1,Pbind(
			\instrument, \aut1,
			\dur, ~dur1,
			\amp, ~amp1,
			\freq, ~freq1,
			\freqlp, ~freqlp1,
			\brown, ~brown1,
			\saw, ~saw1,
			\sinA, ~sin1a,
			\sinB, ~sin2a,
			\att, ~att1,
			\sus, ~sus1,
			\rls, ~rls1,
			\vol, ~vol1,
			\out, ~mainBus			
		
		)).play;
	}{
		~ses1.stop;
		~ses1 = nil;
	}
}).add;


/*
(
~dur1.source = Pseq([~duyekKudDur]/2, inf) ;
~amp1.source = Pseq([0.7], inf) ;
~freq1.source = Pseq([ ~rast/8], inf) ;
~freqlp1.source = Pseq([ ~rast/2], inf) ;
~brown1.source = Pseq([120.0], inf) ;
~sin1a.source = Pseq([12.0,0.1], inf) ;
~sin2a.source =112;
~att1.source = 0.1;
~sus1.source =0.1;
~rls1.source =0.5;

)
*/

~autsaw = OSCresponderNode(nil, '/harmP/autsaw', { |t,r,m| 
	var n1;
	n1 = (m[1]*1) +0.1;
	~saw1.source = ~saw1Spec.map(m[1]+0.01) 
	}).add;

~autatt = OSCresponderNode(nil, '/harmP/autatt', { |t,r,m| 
	var n1;
	n1 = (m[1]*1) +0.1;
	~att1.source = ~att1Spec.map(m[1]*1.8) 
	}).add;

~autsus = OSCresponderNode(nil, '/harmP/autsus', { |t,r,m| 
	var n1;
	n1 = (m[1]*1) +0.2;
	~rls1.source = ~rls1Spec.map(m[1]) 
	}).add;

~autrls = OSCresponderNode(nil, '/harmP/autrls', { |t,r,m| 
	var n1;
	n1 = (m[1]*1) +0.2;
	~rls1.source = ~rls1Spec.map(m[1]) 
	}).add;


~s1p1 = OSCresponderNode(nil, '/harmP/stpA1', { |t,r,m| 
	[
	~dur1.source = Prand( [ Pseq([1/3], 8), Pseq([1/6], 8), Pseq([1/3], 8), Pseq([1/6], 8)] , inf),
	~amp1.source =  Pseq([ 
				Pseq([0, 0.0, 0.8, 0, 0.0, 0.4, 1, 0.6] , 3),
				Prand([0.0, 0, 1, 0, 0.4, 0.8, 0, 0.6] , 4)
				] , inf) 
	]
	}).add;

~s1p2 = OSCresponderNode(nil, '/harmP/stpB1', { |t,r,m| 
	[
	~dur1.source = Pseq( [1, 1/4, 1/4, 1/2, 1/2, 1, 1/2, 1/2, 1, 1/4, 1/4, 1/2, 1, 1/4, 1/4, 1/2, 1, 1/2]/2 , inf),
	~amp1.source = Pseq([1, Pseq([0, 0.2, 0.25, 0.0, 0.1, 0.7, 0.5, 0.4] , 4)]/2 , inf)
	]
	}).add;


	
	
	}

}