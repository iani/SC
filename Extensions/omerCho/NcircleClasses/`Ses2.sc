

/*
Ses2.load;
*/

Ses2 {

	*load{

	var s;
	s = Server.default;

SynthDef( \aut2, { | out = 0, vol = 0.5, amp = 1, sustain = 1.1, freq = 160, freqlp, 
	sina = 0, sinb = 0.1, brown = 0.1, saw = 400, 
	attime = 0.1, rlstime = 0.8 |
	var in, osc, env, ses;
	env =  EnvGen.ar(Env.perc(attime, rlstime), doneAction: 2, levelScale: 0.8, timeScale: sustain);
	in = SinOsc.ar(FSinOsc.ar(freq, 0, brown *2 )/8, 1.1);
	ses = SinOsc.ar(100, in, 0.01) ;
	ses = RLPF.ar(ses, freqlp, 10.4, 16.6, 0.4 );
	ses = ses.sin + SinOsc.ar(freqlp, Decay.ar(SinOsc.ar(sina, sinb), 0.2.abs, Saw.ar(brown)));
	
	Out.ar(out, ses   *amp *env *vol !2);
}).add;





~amp2 = PatternProxy( 
			Prand([ 
				Pseq([0, 0, 0.1, 0, 0.0, 0.4, 0, 0.6, 0, 0, 0] , 3),
				Pshuf([0, 0, 0.5, 0, 0.0, 0.4, 0, 0.6, 0, 0, 0] , 1)
				] , inf) );
~dur2 = PatternProxy( 1/3 );

~freqlp2 = PatternProxy( Pseries( [ Pbrown(100, 110, 1, 7), Pbrown(10, 20, 1, 1) ], inf) );
~brown2 = PatternProxy( Pseq([10000.cos, 1000.cos],inf) );
							
~sina2 = PatternProxy ( 5.abs );
~sinb2 = PatternProxy ( 0.9.abs );
~vol2 = PatternProxy ( 0.5 );
~vol2Spec = ControlSpec(0, 1.6, \lin);

~tog2= OSCresponderNode(nil, '/harmP/toggle2', { |t,r,m| 
	if (~ses2.isNil or: { ~ses2.isPlaying.not}) {
		~ses2 = Pbind(
			\instrument, \aut2,
			\dur, ~dur2,
			\amp, ~amp2,
			\freqlp, ~freqlp2,
			\brown, ~brown2,
			\sina, ~sina2,
			\sinb, ~sinb2,
			\vol, ~vol2,
			\out, ~mainBus			
		).play(~tempo, quant: [8, 0, 0]);
	}{
		~ses2.stop;
		~ses2 = nil;
	}
}).add;



~fad2 = OSCresponderNode(nil, '/harmP/fader2', { |t,r,m| 
	var n1;
	n1 = (m[1]*1) +0.1;
	~vol2.source = ~vol2Spec.map(m[1]*1.1) 
	}).add;

	
	}

}