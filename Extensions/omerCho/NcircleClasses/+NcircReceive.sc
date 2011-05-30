
/*
(
RastMakam.load;
Globals.tempo;
Globals.scales;
Globals.groups;
Globals.buses;
)
(
NcMainVol.load;
ChClean.load;
ChReverb.load;
ChDelay.load;
ChRlpf.load;
ChWah.load;
ChFlow.load;

Avos.load;

Gendai.load;
AutSynth.load;
Baxx.load;
Kaos.load;
Ses1.load;
Ses2.load;
)
Ncircle.loadBuffers;

Ncircle.loadOsc;
Ncircle.receive;

(
NcMainVol.play;
ChClean.play;
ChReverb.play;
ChDelay.play;
ChRlpf.play;
ChWah.play;
)



~ats1.att_(0.1).sus_(2).rls_(5.0).rate_([~rastRateA1 , ~rastRateB1, ~rastRateC1, ~rastRateD1].mirror2).mul_(0.9).pan_(-4.0).out_(~mainBus).play0;
~ats1.att_(0.1).sus_(2).rls_(5.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(~mainBus).playDown;
~ats2.att_(0.1).sus_(2).rls_(5.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(~mainBus).play0;
~ats2.att_(0.1).sus_(2).rls_(5.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(~mainBus).play0;
~ats3.att_(0.1).sus_(2).rls_(5.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(~mainBus).play0;
~ats4.att_(0.1).sus_(2).rls_(5.0).rate_(0.8 rrand: 1.5).mul_(1.2).pan_(0.0).out_(~mainBus).play0;

~kick1.att_(0.1).sus_(2).rls_(1.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(~mainBus).play0;
~kick1.att_(0.1).sus_(2).rls_(2.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(~mainBus).play0;
~bass1.att_(0.1).sus_(1).rls_(2.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(~mainBus).play0;
~bass2.att_(0.1).sus_(2).rls_(5.0).rate_(1.8 rrand: 1.5).mul_(1.9).pan_(0.1).out_(~mainBus).play0; 
~bass3.att_(0.1).sus_(2).rls_(5.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(-0.2).out_(~mainBus).play0;

~citMin.att_(0.1).sus_(2).rls_(5.0).rate_(-00.8 rrand: 00.5).mul_(0.6).pan_(0.0).out_(~mainBus).play0;
~cirMin.att_(0.1).sus_(2).rls_(5.0).rate_(-1.8 rrand: 1.5).mul_(0.9).pan_(-0.2 rrand: 0.2 ).out_(~mainBus).play0;


~circir1.att_(0.1).sus_(2).rls_(5.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(~mainBus).play0;

~dlStr.att_(0.1).sus_(2).rls_(5.0).rate_(-0.8 rrand: -1.5).mul_(0.9).pan_(-0.7).out_(~mainBus).play0;
~dlyStr1.att_(0.1).sus_(2).rls_(5.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(~mainBus).play0;
~dlyStr2.att_(0.1).sus_(2).rls_(5.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(~mainBus).play0;
~dlyStr3.att_(0.1).sus_(2).rls_(5.0).rate_(-0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(~mainBus).play0;

~fub1.att_(0.1).sus_(0.2).rls_(1.0).rate_(0.99).mul_(0.9).pan_(0.0).out_(~mainBus).play0;

~dran1.att_(0.1).sus_(2).rls_(5.0).rate_(0.8).mul_(0.9).pan_(0.0).out_(~mainBus).play0;
~dran2.att_(0.1).sus_(2).rls_(5.0).rate_(0.8).mul_(0.9).pan_(0.0).out_(~mainBus).play0;

~brdk1.att_(0.1).sus_(2).rls_(5.0).rate_(-0.8).mul_(0.9).pan_(0.0).out_(~mainBus).play0;
~brdk1.att_(0.1).sus_(2).rls_(5.0).rate_(0.8).mul_(0.9).pan_(0.0).out_(~mainBus).play0;
~brdk1.att_(0.1).sus_(2).rls_(5.0).rate_(0.8).mul_(0.9).pan_(0.0).out_(~mainBus).play0;

~gtr1.att_(0.1).sus_(2).rls_(5.0).rate_(~rastRateD1).mul_(0.9).pan_(-4.0).out_(3).play0;
~gtr1.att_(0.1).sus_(2).rls_(5.0).rate_(~rastRateD1).mul_(0.9).pan_(4.0).out_(0).play0;
~gtr1.att_(0.1).sus_(2).rls_(5.0).rate_(0.8).mul_(0.9).pan_(0.0).out_(~mainBus).play0;

~git1.att_(0.1).sus_(2).rls_(5.0).rate_(0.8).mul_(0.9).pan_(0.0).out_(~mainBus).play0;
~git1.att_(0.1).sus_(2).rls_(5.0).rate_(0.8).mul_(0.9).pan_(0.0).out_(~mainBus).play0;
~git1.att_(0.1).sus_(2).rls_(5.0).rate_(0.8).mul_(0.9).pan_(0.0).out_(~mainBus).play0;

~gir1.att_(0.00001).sus_(2).rls_(5.0).rate_([~rastRateA1 , ~rastRateB1, ~rastRateC1, ~rastRateD1].choose).mul_(0.6).pan_(-0.1.rand).out_(~mainBus).play0;
~gir1.rate_(~rastRateA1 rrand: 2).play0;
~gir2.att_(0.00001).sus_(1.2).rls_(3.0).rate_([~rastRateA1 , ~rastRateB1, ~rastRateC1, ~rastRateD1].choose).mul_(0.6).pan_(-0.1.rand).out_(~mainBus).play0;
~gir2.rate_(0.5 rrand: 2).play0;
~gir3.rate_(0.5 rrand: 2).play0;
~gir3.rate_(0.5 rrand: 2).play0;

~int1.att_(0.001).sus_(2).rls_(3.0).rate_(-1.8).mul_(0.9).pan_(0.0).out_(~mainBus).play0;
~int2.att_(0.001).sus_(2).rls_(5.0).rate_(-0.8).mul_(0.9).pan_(0.0).out_(~mainBus).play0;
~int3.att_(0.001).sus_(2).rls_(5.0).rate_(-0.8).mul_(0.9).pan_(0.0).out_(~mainBus).play0;
~int4.att_(0.1).sus_(2).rls_(0.2).rate_(-0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(~mainBus).play0;
~int5.att_(0.1).sus_(2).rls_(1.0).rate_(-0.8).mul_(0.9).pan_(0.0).out_(~mainBus).play0;
~int6.att_(0.1).sus_(2).rls_(0.1).rate_(0.8).mul_(-0.9).pan_(0.0).out_(~mainBus).playVib;

~zil01.att_(0.001).sus_(2).rls_(5.0).rate_(-0.5 rrand: 0.8).mul_(0.9).pan_(0.0).out_(~mainBus).play0;
~zil02.att_(0.1).sus_(2).rls_(5.0).rate_(0.8).mul_(0.9).pan_(-0.3).out_(~mainBus).play0;
~zil03.att_(0.1).sus_(2).rls_(5.0).rate_(-0.8).mul_(0.9).pan_(0.0).out_(~mainBus).play0;
~zil04.att_(0.1).sus_(2).rls_(5.0).rate_(0.8).mul_(0.9).pan_(0.0).out_(~mainBus).play0;


~ats1.att_(0.1).sus_(2).rls_(5.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(0).play0;
~ats1.att_(0.1).sus_(2).rls_(5.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(0).play0;
~ats1.att_(0.1).sus_(2).rls_(5.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(0).playUp;

~ats2.att_(0.1).sus_(2).rls_(5.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(0).play0;
~ats2.att_(0.1).sus_(2).rls_(5.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(0).playVib;

~ats3.att_(0.1).sus_(2).rls_(5.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(0).play0;
~ats3.att_(0.1).sus_(2).rls_(5.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(0).playUp;
~ats3.att_(0.1).sus_(2).rls_(5.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(0).playDown;
~ats3.att_(0.1).sus_(2).rls_(5.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(0).playVib;

~ats4.att_(0.1).sus_(2).rls_(5.0).rate_(0.8 rrand: 1.5).mul_(1.2).pan_(0.0).out_(0).play0;
~ats4.att_(1.1).sus_(2.5).rls_(5.0).rate_(0.8 rrand: 1.5).mul_(1.2).pan_(0.0).out_(0).playUp;


~kick1.att_(0.1).sus_(2).rls_(1.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(1).out_(0).play0;
~kick1.att_(0.1).sus_(2).rls_(2.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(0).playUp;
~kick1.att_(0.1).sus_(2).rls_(2.0).rate_(10.8 rrand: 11.5).mul_(0.9).pan_(0.0).out_(0).playDown;
~kick1.att_(0.1).sus_(2).rls_(2.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(0).playVib;

~bass1.att_(0.1).sus_(1).rls_(2.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(0).play0;
~bass1.att_(1.1).sus_(1).rls_(2.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(0).playUp;

~bass2.att_(0.1).sus_(2).rls_(5.0).rate_(1.8 rrand: 1.5).mul_(1.9).pan_(0.1).out_(0).play0; 
~bass3.att_(0.1).sus_(2).rls_(5.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(-0.2).out_(0).play0;

~citMin.att_(0.1).sus_(2).rls_(5.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(0).play0;
~cirMin.att_(0.1).sus_(2).rls_(5.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(0).play0;
~circir1.att_(0.1).sus_(0.2).rls_(1.0).rate_(0.8 rrand: -0.5).mul_(0.1).pan_(-0.4.rand).out_(0).play0;

~dlStr.att_(0.1).sus_(0.2).rls_(1.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(1.0).out_(~mainBus).playVib;
~dlyStr1.att_(0.1).sus_(2).rls_(5.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(0).play0;
~dlyStr2.att_(0.1).sus_(2).rls_(5.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(0).play0;
~dlyStr3.att_(0.1).sus_(2).rls_(5.0).rate_(-0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(0).play0;

*/





Ncircle { 

	
	*receive {
		var s;
		s = Server.default;
	
	SynthDef(\buf1, { | out=0, vol = 0.5, bufnum = 0, gate = 1.0, rate = 1, startPos = 0.01, amp = 1.0, 
		att = 0.1, dec = 0.5, sus = 1, rls = 0.5, lvl=0.8,
		pan = 0, wid = 2, loop = 0|
		var audio, env;
		env =  EnvGen.kr(Env.perc(att, rls, dec), gate, doneAction:2);
	
		rate = rate * BufRateScale.kr(bufnum);
		startPos = startPos * BufFrames.kr(bufnum);
		
		
		audio = BufRd.ar(1, bufnum, Phasor.ar(1, rate, startPos, BufFrames.ir(bufnum)), loop, 4);
		audio =  audio;
		audio = Pan2.ar(audio, pan, amp);
		//audio = PanAz.ar( 8, audio, pan, amp*4, width: wid);
		Out.ar(out, env *audio);
	}).send(s);

///////////////////////////////////////
		Preceive(

			
			\1 -> {
//~kick1.att_(0.1).sus_(2).rls_(1.0).rate_(-0.8 rrand: -1.5).mul_(0.9).pan_(2).out_(~mainBus).play0;

~ats3.att_(0.1).sus_(2).rls_(5.0).rate_(~rastRateA1 rrand: 1.5).mul_(0.9).pan_(1.2).out_(0 rrand: 3).play0;
~gir1.att_(0.00001).sus_(2).rls_(5.0).rate_([~rastRateA1 , ~rastRateB1, ~rastRateC1, ~rastRateD1].choose).mul_(0.6).pan_(2.1.rand).out_(0 rrand: 3).play0;
				},
			\2 -> {
~gir1.att_(0.00001).sus_(2).rls_(5.0).rate_([~rastRateA1 , ~rastRateB1, ~rastRateC1, ~rastRateD1].choose).mul_(0.6).pan_(-0.1.rand).out_(~mainBus).play0;
~int3.att_(0.001).sus_(2).rls_(5.0).rate_(-0.8).mul_(0.9).pan_(0.0).out_(~mainBus).play0;

				},
			\3 -> {
~gir1.att_(0.00001).sus_(2).rls_(5.0).rate_([~rastRateA1 , ~rastRateB1, ~rastRateC1, ~rastRateD1].choose).mul_(0.6).pan_(-0.1.rand).out_(~mainBus).play0;
~int3.att_(0.001).sus_(2).rls_(5.0).rate_(-0.8).mul_(0.9).pan_(0.0).out_(~mainBus).play0;
				
				},
			\4 -> {

~gir1.att_(0.00001).sus_(2).rls_(5.0).rate_([~rastRateA1 , ~rastRateB1, ~rastRateC1, ~rastRateD1].choose).mul_(0.6).pan_(-0.1.rand).out_(~mainBus).play0;				
				
				},
			\5 -> {
~gir1.att_(0.00001).sus_(2).rls_(5.0).rate_([~rastRateA1 , ~rastRateB1, ~rastRateC1, ~rastRateD1].choose).mul_(0.6).pan_(-0.1.rand).out_(~mainBus).play0;
~int3.att_(0.001).sus_(2).rls_(5.0).rate_(-0.8).mul_(0.9).pan_(0.0).out_(~mainBus).play0;

				
				},
			\6 -> {},
			\7 -> {},
			\8 -> {
~gir1.att_(0.00001).sus_(2).rls_(5.0).rate_([~rastRateA1 , ~rastRateB1, ~rastRateC1, ~rastRateD1].choose).mul_(0.6).pan_(-0.1.rand).out_(~mainBus).play0;
				
				},
			\9 -> {
~int1.att_(0.001).sus_(2).rls_(3.0).rate_(-0.8).mul_(0.9).pan_(0.0).out_(~mainBus).play0;
				},
			\10 -> {
				~kick1.att_(0.1).sus_(2).rls_(2.0).rate_(5.0 rrand: 5.5).mul_(0.9).pan_(0.0).out_(0).playUp;
				},
			
			\11 -> {
				
				},
			\12 -> {
				~dlStr.att_(0.1).sus_(0.2).rls_(1.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(0).playVib;
				
				},
			\13 -> {},
			\14 -> {},
			\15 -> {
				~circir1.att_(0.1).sus_(0.2).rls_(1.0).rate_(0.8 rrand: -0.5).mul_(0.1).pan_(4.rand).out_(0).play0;
				},
			\16 -> {},
			\17 -> {},
			\18 -> {
				~circir1.att_(0.1).sus_(0.2).rls_(1.0).rate_(0.8 rrand: -0.5).mul_(0.1).pan_(0.4.rand).out_(0).play0;
				},
			\19 -> {},
			\20 -> {},
			
			\21 -> {},
			\22 -> {},
			\23 -> {
				~circir1.att_(0.1).sus_(0.2).rls_(1.0).rate_(0.8 rrand: -0.5).mul_(0.1).pan_(0.4.rand).out_(0).play0;
				},
			\24 -> {},
			\25 -> {},
			\26 -> {
	~gir2.att_(0.00001).sus_(1.2).rls_(3.0).rate_([~rastRateA1 , ~rastRateB1, ~rastRateC1, ~rastRateD1].choose).mul_(0.6).pan_(-0.1.rand).out_(~mainBus).play0;
				},
			\27 -> {},
			\28 -> {},
			\29 -> {},
			\30 -> {
				~circir1.att_(0.1).sus_(0.2).rls_(1.0).rate_(0.8 rrand: -0.5).mul_(0.1).pan_(2.4.rand).out_(0).play0;
				},
			
			\31 -> {},
			\32 -> {},
			\33 -> {},
			\34 -> {
				~wah.set(
					\amp, 0.9,
					\rate, 0.005,
					\cfreq, ~rastD1*92.0,
					\mfreq, ~rastD1*92.0
				);
				~autS.set(
					\freqlp, ~rastA1 rrand: ~rastA1+1
				);
			},
			\35 -> {},
			\36 -> {
				
				~autS.set(
					\freqlp, ~rastB7 rrand: ~rastB7+1
				);
				~wah.set(
					\amp, 0.6,
					\rate, 0.005,
					\cfreq, ~rastA7*92.0,
					\mfreq, ~rastA7*92.02
				);
				~rev.set(
					\roomsize, 2, \revtime, 0.08, \damping, 0.62,
					\earlylevel, -11, \taillevel, -13
				);
			},
			\37 -> {},
			\38 -> {
				~circir1.att_(0.1).sus_(0.2).rls_(1.0).rate_(0.8 rrand: -0.5).mul_(0.1).pan_(1.4.rand).out_(~mainBus).play0;
				},
			\39 -> {},
			\40 -> {},
			
			\41 -> {},
			\42 -> {
~gir1.att_(0.00001).sus_(2).rls_(5.0).rate_([~rastRateA1 , ~rastRateB1, ~rastRateC1, ~rastRateD1].choose).mul_(0.6).pan_(0.1.rand).out_(~mainBus).play0;
				},
			\43 -> {},
			\44 -> {
~gir2.att_(0.00001).sus_(1.2).rls_(3.0).rate_([~rastRateA1 , ~rastRateB1, ~rastRateC1, ~rastRateD1].choose).mul_(0.6).pan_(-0.1.rand).out_(~mainBus).play0;
				},
			\45 -> {},
			\46 -> {
				
				~autS.set(
					\freqlp, ~rastB7 rrand: ~rastB7+1
				);
				~wah.set(
					\amp, 0.6,
					\rate, 0.005,
					\cfreq, ~rastA7*92.0,
					\mfreq, ~rastA7*92.02
				);
				~rev.set(
					\roomsize, 2, \revtime, 0.08, \damping, 0.62,
					\earlylevel, -11, \taillevel, -13
				);
			},
			\47 -> {},
			\48 -> {
				~circir1.att_(0.1).sus_(0.2).rls_(1.0).rate_(0.8 rrand: -0.5).mul_(0.1).pan_(0.4.rand).out_(~mainBus).play0;
				},
			\49 -> {},
			\50 -> {},
			
			\61 -> {},
			\62 -> {
				~wah.set(
					\amp, 0.9,
					\rate, 0.005,
					\cfreq, ~rastD1*92.0,
					\mfreq, ~rastD1*92.0
				);
				~autS.set(
					\freqlp, ~rastA1 rrand: ~rastA1+1
				);
			},
			\63 -> {
~gir1.att_(0.00001).sus_(1.2).rls_(3.0).rate_([~rastRateA1 , ~rastRateB1, ~rastRateC1, ~rastRateD1].choose).mul_(0.6).pan_(-0.1.rand).out_(~mainBus).play0;
				},
			\64 -> {
~gir3.att_(0.00001).sus_(1.2).rls_(3.0).rate_([~rastRateA1 , ~rastRateB1, ~rastRateC1, ~rastRateD1].choose).mul_(0.6).pan_(-0.1.rand).out_(~mainBus).play0;
				},
			\65 -> {},
			\66 -> {},
			\67 -> {
				~circir1.att_(0.1).sus_(0.2.rand).rls_(1.0).rate_(0.8 rrand: -0.5).mul_(0.4.rand).pan_(-0.4.rand).out_(~mainBus).play0;
				
				},
			\68 -> {},
			\69 -> {},
			\70 -> {
				
				~autS.set(
					\freqlp, ~rastB7 rrand: ~rastB7+1
				);
				~wah.set(
					\amp, 0.6,
					\rate, 0.005,
					\cfreq, ~rastA7*92.0,
					\mfreq, ~rastA7*92.02
				);
				~rev.set(
					\roomsize, 2, \revtime, 0.08, \damping, 0.62,
					\earlylevel, -11, \taillevel, -13
				);
			},
			
			\71 -> {},
			\72 -> {},
			\73 -> {
	~gir2.att_(0.00001).sus_(1.2).rls_(3.0).rate_([~rastRateA1 , ~rastRateB1, ~rastRateC1, ~rastRateD1].choose).mul_(0.6).pan_(-0.1.rand).out_(~mainBus).play0;
				},
			\74 -> {},
			\75 -> {},
			\76 -> {},
			\77 -> {
~gir1.att_(0.00001).sus_(2).rls_(5.0.rand).rate_([~rastRateA1 , ~rastRateB1, ~rastRateC1, ~rastRateD1].choose).mul_(0.6).pan_(-0.1.rand).out_(~mainBus).play0;

				
				},
			\78 -> {},
			\79 -> {},
			\80 -> {},
			
			\81 -> {
				


				~wah.set(
					\amp, 0.9,
					\rate, 0.005,
					\cfreq, ~rastA1*92.0,
					\mfreq, ~rastA1*92.0
				);
				~rev.set(
					\roomsize, 5, \revtime, 0.06, \damping, 0.82,
					\earlylevel, -21, \taillevel, -13
				);
			
				
				
				},
			\82 -> {
				~wah.set(
					\amp, 0.9,
					\rate, 0.005,
					\cfreq, ~rastD1*92.0,
					\mfreq, ~rastD1*92.0
				);
				~autS.set(
					\freqlp, ~rastA1 rrand: ~rastA1+1
				);
			},
			\83 -> {},
			\84 -> {},
			\85 -> {
				~wah.set(
					\amp, 0.9,
					\rate, 0.005,
					\cfreq, ~rastD1*92.0,
					\mfreq, ~rastD1*92.0
				);
				~autS.set(
					\freqlp, ~rastA1 rrand: ~rastA1+1
				);
			},
			\86 -> {},
			\87 -> {},
			\88 -> {
				
				~autS.set(
					\freqlp, ~rastB7 rrand: ~rastB7+1
				);
				~wah.set(
					\amp, 0.6,
					\rate, 0.005,
					\cfreq, ~rastA7*92.0,
					\mfreq, ~rastA7*92.02
				);
				~rev.set(
					\roomsize, 2, \revtime, 0.08, \damping, 0.62,
					\earlylevel, -11, \taillevel, -13
				);
			},
			\89 -> {},
			\90 -> {},
			
			\91 -> {},
			\92 -> {},
			\93 -> {},
			\94 -> {},
			\95 -> {
~gir1.att_(0.00001).sus_(2).rls_(5.0).rate_([~rastRateA1 , ~rastRateB1, ~rastRateC1, ~rastRateD1].choose).mul_(0.6).pan_(-0.1.rand).out_(~mainBus).play0;
				
				},
			\96 -> {},
			\97 -> {},
			\98 -> {},
			\99 -> {},
			\100 -> {},
			
			\101 -> {},
			\102 -> {},
			\103 -> {},
			\104 -> {},
			\105 -> {},
			\106 -> {
				~wah.set(
					\amp, 0.9,
					\rate, 0.005,
					\cfreq, ~rastD1*92.0,
					\mfreq, ~rastD1*92.0
				);
				~autS.set(
					\freqlp, ~rastA1 rrand: ~rastA1+1
				);
			},
			\107 -> {},
			\108 -> {
				
				~autS.set(
					\freqlp, ~rastB7 rrand: ~rastB7+1
				);
				~wah.set(
					\amp, 0.6,
					\rate, 0.005,
					\cfreq, ~rastA7*92.0,
					\mfreq, ~rastA7*92.02
				);
				~rev.set(
					\roomsize, 2, \revtime, 0.08, \damping, 0.62,
					\earlylevel, -11, \taillevel, -13
				);
			},
			\109 -> {},
			\110 -> {},
			
			\111 -> {},
			\112 -> {},
			\113 -> {},
			\114 -> {
				~kick1.att_(0.1).sus_(2).rls_(2.0).rate_(10.8 rrand: 11.5).mul_(0.3).pan_(0.0).out_(2).playDown;
				},
			\115 -> {},
			\116 -> {
				


				~wah.set(
					\amp, 0.9,
					\rate, 0.005,
					\cfreq, ~rastA1*92.0,
					\mfreq, ~rastA1*92.0
				);
				~rev.set(
					\roomsize, 5, \revtime, 0.06, \damping, 0.82,
					\earlylevel, -21, \taillevel, -13
				);
			
				
				},
			\117 -> {},
			\118 -> {},
			\119 -> {
				
				~autS.set(
					\freqlp, ~rastB7 rrand: ~rastB7+1
				);
				~wah.set(
					\amp, 0.6,
					\rate, 0.005,
					\cfreq, ~rastA7*92.0,
					\mfreq, ~rastA7*92.02
				);
				~rev.set(
					\roomsize, 2, \revtime, 0.08, \damping, 0.62,
					\earlylevel, -11, \taillevel, -13
				);
			},
			\120 -> {},
			
			\121 -> {
	~gir2.att_(0.00001).sus_(1.2).rls_(3.0).rate_([~rastRateA1 , ~rastRateB1, ~rastRateC1, ~rastRateD1].choose).mul_(0.6).pan_(-0.1.rand).out_(~mainBus).play0;
				},
			\122 -> {},
			\123 -> {},
			\124 -> {},
			\125 -> {
				~circir1.att_(0.1).sus_(0.2).rls_(1.0.rand).rate_(0.8 rrand: -0.5).mul_(0.1).pan_(1.4.rand).out_(3).play0;
				},
			\126 -> {},
			\127 -> {},
			\128 -> {},
			\129 -> {},
			\130 -> {},
			
			\131 -> {},
			\132 -> {
				
				~autS.set(
					\freqlp, ~rastB7 rrand: ~rastB7+1
				);
				~wah.set(
					\amp, 0.6,
					\rate, 0.005,
					\cfreq, ~rastA7*92.0,
					\mfreq, ~rastA7*92.02
				);
				~rev.set(
					\roomsize, 2, \revtime, 0.08, \damping, 0.62,
					\earlylevel, -11, \taillevel, -13
				);
			},
			\133 -> {
~gir2.att_(0.00001).sus_(1.2).rls_(3.0).rate_([~rastRateA1 , ~rastRateB1, ~rastRateC1, ~rastRateD1].choose).mul_(0.6).pan_(-0.1.rand).out_(~mainBus).play0;
				},
			\134 -> {},
			\135 -> {},
			\136 -> {},
			\137 -> {
				~circir1.att_(0.1).sus_(0.2).rls_(1.0).rate_(0.8 rrand: -0.5).mul_(0.1).pan_(-0.4.rand).out_(2).play0;
				},
			\138 -> {},
			\139 -> {},
			\140 -> {},
			
			\141 -> {
				
				~autS.set(
					\freqlp, ~rastB7 rrand: ~rastB7+1
				);
				~wah.set(
					\amp, 0.6,
					\rate, 0.005,
					\cfreq, ~rastA7*92.0,
					\mfreq, ~rastA7*92.02
				);
				~rev.set(
					\roomsize, 2, \revtime, 0.08, \damping, 0.62,
					\earlylevel, -11, \taillevel, -13
				);
			},
			\142 -> {
~gir2.att_(0.00001).sus_(1.2).rls_(3.0).rate_([~rastRateA1 , ~rastRateB1, ~rastRateC1, ~rastRateD1].choose).mul_(0.6).pan_(-0.1.rand).out_(~mainBus).play0;
				},
			\143 -> {},
			\144 -> {},
			\145 -> {
				~circir1.att_(0.1).sus_(0.2).rls_(1.0).rate_(0.8 rrand: -0.5).mul_(0.1).pan_(-0.4.rand).out_(1).play0;
				},
			\146 -> {},
			\147 -> {},
			\148 -> {},
			\149 -> {},
			\150 -> {},
			
			\161 -> {},
			\162 -> {
~gir2.att_(0.00001).sus_(1.2).rls_(3.0).rate_([~rastRateA1 , ~rastRateB1, ~rastRateC1, ~rastRateD1].choose).mul_(0.6).pan_(-0.1.rand).out_(~mainBus).play0;
				
				},
			\163 -> {},
			\164 -> {},
			\165 -> {
				~circir1.att_(0.1).sus_(0.2).rls_(1.0).rate_(0.8 rrand: -0.5).mul_(0.1).pan_(1.4.rand).out_(0).play0;
				},
			\166 -> {},
			\167 -> {},
			\168 -> {
				~kick1.att_(0.1).sus_(2).rls_(2.0).rate_(10.8 rrand: 11.5).mul_(0.5).pan_(2.0).out_(2).playDown;
				},
			\169 -> {
				~wah.set(
					\amp, 0.9,
					\rate, 0.005,
					\cfreq, ~rastD1*92.0,
					\mfreq, ~rastD1*92.0
				);
				~autS.set(
					\freqlp, ~rastA1 rrand: ~rastA1+1
				);
			},
			\170 -> {},
			
			\171 -> {},
			\172 -> {},
			\173 -> {
				
				~autS.set(
					\freqlp, ~rastB7 rrand: ~rastB7+1
				);
				~wah.set(
					\amp, 0.6,
					\rate, 0.005,
					\cfreq, ~rastA7*92.0,
					\mfreq, ~rastA7*92.02
				);
				~rev.set(
					\roomsize, 2, \revtime, 0.08, \damping, 0.62,
					\earlylevel, -11, \taillevel, -13
				);
			},
			\174 -> {},
			\175 -> {},
			\176 -> {
				~circir1.att_(0.1).sus_(0.2).rls_(1.0).rate_(0.8 rrand: -0.5).mul_(0.1).pan_(3.rand).out_(0).play0;
				},
			\177 -> {},
			\178 -> {},
			\179 -> {},
			\180 -> {},
			
			\181 -> {},
			\182 -> {},
			\183 -> {},
			\184 -> {
				~circir1.att_(0.1).sus_(0.2).rls_(1.0).rate_(0.8 rrand: -0.5).mul_(0.1).pan_(0.4.rand).out_(0).play0;
				},
			\185 -> {
				
				~autS.set(
					\freqlp, ~rastB7 rrand: ~rastB7+1
				);
				~wah.set(
					\amp, 0.6,
					\rate, 0.005,
					\cfreq, ~rastA7*92.0,
					\mfreq, ~rastA7*92.02
				);
				~rev.set(
					\roomsize, 2, \revtime, 0.08, \damping, 0.62,
					\earlylevel, -11, \taillevel, -13
				);
			},
			\186 -> {},
			\187 -> {
				
				~autS.set(
					\freqlp, ~rastB7 rrand: ~rastB7+1
				);
				~wah.set(
					\amp, 0.6,
					\rate, 0.005,
					\cfreq, ~rastA7*92.0,
					\mfreq, ~rastA7*92.02
				);
				~rev.set(
					\roomsize, 2, \revtime, 0.08, \damping, 0.62,
					\earlylevel, -11, \taillevel, -13
				);
			},
			\188 -> {},
			\189 -> {},
			\190 -> {},
			
			\191 -> {},
			\192 -> {},
			\193 -> {},
			\194 -> {
				~circir1.att_(0.1).sus_(0.2).rls_(1.0).rate_(0.8 rrand: -0.5).mul_(0.1).pan_(-0.4.rand).out_(1).play0;
				},
			\195 -> {},
			\196 -> {},
			\197 -> {},
			\198 -> {},
			\199 -> {},
			\200 -> {},
			
			'dum1' -> {
		
~kick1.att_(0.1).sus_(2).rls_(2.0).rate_(5.0 rrand: 5.5).mul_(0.9).pan_(2.0).out_(0).playUp;
					
				},
		
			'bass1' -> {
		
				~rit=Synth.head(~piges, \buf1, 
					[ 
						\att, 0.1, \dec, 0.8, \rls, 3, 
						\bufnum, ~bass1, 
						\out, ~mainBus,
						\amp, 0.9, 
						\rate, 0.999 rrand: 0.80 
					]
				);
					
				},
			'bass2' -> {
		
				~rit=Synth.head(~piges, \buf1, 
					[ 
						\att, 0.1, \dec, 0.8, \rls, 3, 
						\bufnum, ~bass2, 
						\out, ~mainBus,
						\amp, 0.9, 
						\rate, 0.99 rrand: -0.90 
					]
				);
					
				},
			'bass3' -> {
		
				~rit=Synth.head(~piges, \buf1, 
					[ 
						\att, 0.1, \dec, 0.8, \rls, 3, 
						\bufnum, ~bass3, 
						\out, ~mainBus,
						\amp, 0.9, 
						\rate, 0.999 rrand: 0.50
					]
				);
					
				},
			'tek1' -> {
				
				~rit=Synth.head(~piges, \buf1, 
					[ 
						\att, 0.01, \dec, 0.8, \rls, 0.2, 
						\bufnum, ~kick1, 
						\out, ~mainBus,
						\amp, 0.9, 
						\rate, 8.999 
					]
				);
			
				},
			'tek2' -> {
				
				~rit=Synth.head(~piges, \buf1, 
					[ 
						\att, 0.01, \dec, 0.8, \rls, 0.1, 
						\bufnum, ~kick1, 
						\out, ~mainBus,
						\amp, 0.9, 
						\rate, 18.999 
					]
				);
		
				},
			'teke1' -> {
		
				~rit=Synth.head(~piges, \buf1, 
					[ 
						\att, 0.01, \dec, 0.8, \rls, 0.4, 
						\bufnum, ~kick1, 
						\out, ~mainBus,
						\amp, 0.9, 
						\rate, 15 
					]
				);
		
				},
			'trr1' -> {
		
				~rit=Synth.head(~piges, \buf1, 
					[ 
						\att, 0.01, \dec, 0.8, \rls, 0.8, 
						\bufnum, ~kick1, 
						\out, ~mainBus,
						\amp, 0.8, 
						\rate, 125 
					]
				);
		
				},
			'trr2' -> {
				
				~rit=Synth.head(~piges, \buf1, 
					[ 
						\att, 0.01, \dec, 0.8, \rls, 0.15, 
						\bufnum, ~kick1, 
						\out, ~mainBus,
						\amp, 0.8, 
						\rate, 225 
					]
				);
		
				},
			
		//int	
			'int2a' -> {
				
				~rit=Synth.head(~piges, \buf1, 
					[ 
						\att, 0.01, \dec, 0.9, \rls, 0.8, 
						 
						
						\amp, 0.8, 
						\startPos, 0.1,
						\rate, -0.80,
						\bufnum, ~int2,
						\out, ~mainBus
					]
				);
				},
			'int2b' -> {
				
				~rit=Synth.head(~piges, \buf1, 
					[ 
						\att, 0.01, \dec, 0.9, \rls, 0.8, 
						 
						
						\amp, 0.8, 
						\startPos, 0.1,
						\rate, -0.90,
						\bufnum, ~int2,
						\out, ~mainBus 
					]
				);
		
		
				},
			
			'fit1' -> {
				
				~rit=Synth.head(~piges, \buf1, 
					[ 
						\att, 0.01, \dec, 0.8, \rls, 0.45, 
						\bufnum, ~kick1, 
						\out, ~mainBus,
						\amp, 0.8, 
						\rate, -25 
					]
				);
		
				},
			'fit2' -> {
		
				~rit=Synth.head(~piges, \buf1, 
					[ 
						\att, 0.01, \dec, 0.8, \rls, 0.25, 
						\bufnum, ~kick1, 
						\out, ~mainBus,
						\amp, 0.8, 
						\rate, -35 
					]
				);
		
				},
			'cir1' -> {
		
~circir1.att_(0.1).sus_(0.2).rls_(1.0).rate_(0.8 rrand: -0.5).mul_(0.1).pan_(-0.4.rand).out_(0).play0;
								
				},
			'cir2' -> {
				
~circir1.att_(0.1).sus_(0.2).rls_(1.0).rate_(0.8 rrand: -0.5).mul_(0.1).pan_(-0.4.rand).out_(0).play0;
			
				},
			'cir3' -> {
				
~circir1.att_(0.1).sus_(0.2).rls_(1.0).rate_(0.8 rrand: -0.5).mul_(0.1).pan_(-0.4.rand).out_(0).play0;
		
				},
			'cir4' -> {
		
~circir1.att_(0.1).sus_(0.2).rls_(1.0).rate_(0.8 rrand: -0.5).mul_(0.1).pan_(-0.4.rand).out_(0).play0;
		
				},

	//Main1	
			\a1 -> {


				~wah.set(
					\amp, 0.9,
					\rate, 0.005,
					\cfreq, ~rastA1*92.0,
					\mfreq, ~rastA1*92.0
				);
				~rev.set(
					\roomsize, 5, \revtime, 0.06, \damping, 0.82,
					\earlylevel, -21, \taillevel, -13
				);
			},
			\a3 -> {
				~wah.set(
					\amp, 0.8,
					\rate, 0.05,
					\cfreq, ~rastA5*92.0,
					\mfreq, ~rastA5*92.01
				);
				~rev.set(
					\roomsize, 5, \revtime, 0.1, \damping, 0.92,
					\earlylevel, -11, \taillevel, -13
				);
				
				~autS.set(
					\freqlp, ~rastB1 rrand: ~rastB1+1
				);
			},
			\a4 -> {
				
				~autS.set(
					\freqlp, ~rastB7 rrand: ~rastB7+1
				);
				~wah.set(
					\amp, 0.6,
					\rate, 0.005,
					\cfreq, ~rastA7*92.0,
					\mfreq, ~rastA7*92.02
				);
				~rev.set(
					\roomsize, 2, \revtime, 0.08, \damping, 0.62,
					\earlylevel, -11, \taillevel, -13
				);
			},
			\a5 -> {
				~wah.set(
					\amp, 0.5,
					\rate, 0.5,
					\cfreq, ~rastB1*92.0,
					\mfreq, ~rastB1*92.01
				);
				~rev.set(
					\roomsize, 0.1, \revtime, 0.062, \damping, 0.9,
					\earlylevel, -40, \taillevel, -28
				);
			},
			\a6 -> {
				~wah.set(
					\amp, 0.4,
					\rate, 1.005,
					\cfreq, ~rastB7*92.0,
					\mfreq, ~rastB7*92.0
				);
				~rev.set(
					\roomsize, 0.5, \revtime, 0.06, \damping, 0.72,
					\earlylevel, -31, \taillevel, -33
				);
			},
			\a7 -> {
				~wah.set(
					\amp, 0.6,
					\rate, 0.5,
					\cfreq, ~rastC2*92.0,
					\mfreq, ~rastC2*92.01
				);
				~rev.set(
					\roomsize, 45, \revtime, 0.2, \damping, 0.92,
					\earlylevel, -101, \taillevel, -13
				);

				~autS.set(
					\freqlp, ~rastC1 rrand: ~rastC1+1
				);
			
			},
			\a8 -> {
				~wah.set(
					\amp, 0.7,
					\rate, 0.05,
					\cfreq, ~rastA1*92.0,
					\mfreq, ~rastA1*92.02
				);

				~rev.set(
					\roomsize, 1, \revtime, 0.09, \damping, 0.92,
					\earlylevel, -11, \taillevel, -13
				);
			},
			\a9 -> {
				~wah.set(
					\amp, 0.8,
					\rate, 0.005,
					\cfreq, ~rastC4*92.0,
					\mfreq, ~rastC4*92.01
				);
			},
			\a10 -> {
				~wah.set(
					\amp, 0.9,
					\rate, 0.005,
					\cfreq, ~rastD1*92.0,
					\mfreq, ~rastD1*92.0
				);
				~autS.set(
					\freqlp, ~rastA1 rrand: ~rastA1+1
				);
			},
			\b1 -> {


				~wah.set(
					\amp, 0.9,
					\rate, 0.005,
					\cfreq, ~rastA1*92.0,
					\mfreq, ~rastA1*92.0
				);
				~rev.set(
					\roomsize, 5, \revtime, 0.06, \damping, 0.82,
					\earlylevel, -21, \taillevel, -13
				);
			},
			\b3 -> {
				~wah.set(
					\amp, 0.8,
					\rate, 0.05,
					\cfreq, ~rastA5*92.0,
					\mfreq, ~rastA5*92.01
				);
				~rev.set(
					\roomsize, 5, \revtime, 0.1, \damping, 0.92,
					\earlylevel, -11, \taillevel, -13
				);
				
				~autS.set(
					\freqlp, ~rastB1 rrand: ~rastB1+1
				);
			},
			\b4 -> {
~gir1.att_(0.00001).sus_(1.2).rls_(3.0).rate_([~rastRateA1 , ~rastRateB1, ~rastRateC1, ~rastRateD1].choose).mul_(0.6).pan_(-0.1.rand).out_(~mainBus).play0;				
				~autS.set(
					\freqlp, ~rastB7 rrand: ~rastB7+1
				);
				~wah.set(
					\amp, 0.6,
					\rate, 0.005,
					\cfreq, ~rastA7*92.0,
					\mfreq, ~rastA7*92.02
				);
				~rev.set(
					\roomsize, 2, \revtime, 0.08, \damping, 0.62,
					\earlylevel, -11, \taillevel, -13
				);
			},
			\b5 -> {
				~wah.set(
					\amp, 0.5,
					\rate, 0.5,
					\cfreq, ~rastB1*92.0,
					\mfreq, ~rastB1*92.01
				);
				~rev.set(
					\roomsize, 0.1, \revtime, 0.062, \damping, 0.9,
					\earlylevel, -40, \taillevel, -28
				);
			},
			\b6 -> {
				~wah.set(
					\amp, 0.4,
					\rate, 1.005,
					\cfreq, ~rastB7*92.0,
					\mfreq, ~rastB7*92.0
				);
				~rev.set(
					\roomsize, 0.5, \revtime, 0.06, \damping, 0.72,
					\earlylevel, -31, \taillevel, -33
				);
			},
			\b7 -> {
				~wah.set(
					\amp, 0.6,
					\rate, 0.5,
					\cfreq, ~rastC2*92.0,
					\mfreq, ~rastC2*92.01
				);
				~rev.set(
					\roomsize, 45, \revtime, 0.2, \damping, 0.92,
					\earlylevel, -101, \taillevel, -13
				);
~gir2.att_(0.00001).sus_(1.2).rls_(3.0).rate_([~rastRateA1 , ~rastRateB1, ~rastRateC1, ~rastRateD1].choose).mul_(0.6).pan_(-0.1.rand).out_(~mainBus).play0;
				~autS.set(
					\freqlp, ~rastC1 rrand: ~rastC1+1
				);
			
			},
~gir2.att_(0.00001).sus_(1.2).rls_(3.0).rate_([~rastRateA1 , ~rastRateB1, ~rastRateC1, ~rastRateD1].choose).mul_(0.6).pan_(-0.1.rand).out_(~mainBus).play0;
			\b8 -> {
				~wah.set(
					\amp, 0.7,
					\rate, 0.05,
					\cfreq, ~rastA1*92.0,
					\mfreq, ~rastA1*92.02
				);
~gir2.att_(0.00001).sus_(1.2).rls_(3.0).rate_([~rastRateA1 , ~rastRateB1, ~rastRateC1, ~rastRateD1].choose).mul_(0.6).pan_(-0.1.rand).out_(~mainBus).play0;
				~rev.set(
					\roomsize, 1, \revtime, 0.09, \damping, 0.92,
					\earlylevel, -11, \taillevel, -13
				);
			},
			\b9 -> {
				~wah.set(
					\amp, 0.8,
					\rate, 0.005,
					\cfreq, ~rastC4*92.0,
					\mfreq, ~rastC4*92.01
				);
			},
			\b10 -> {
				~wah.set(
					\amp, 0.9,
					\rate, 0.005,
					\cfreq, ~rastD1*92.0,
					\mfreq, ~rastD1*92.0
				);
				~autS.set(
					\freqlp, ~rastA1 rrand: ~rastA1+1
				);
			}
			
		).play;
//////////////////////////////////////
	}


	*loadBuffers{
		var s;
		s = Server.default;
		
			~ats1 = Bummer.read(s, "sounds/_Evfer/ates01.aif");
			~ats2 = Bummer.read(s, "sounds/_Evfer/ates02.aif");
			~ats3 = Bummer.read(s, "sounds/_Evfer/ates03.aif");
			~ats4 = Bummer.read(s, "sounds/_Evfer/ates04.aif");
			
			~kick1 = Bummer.read(s, "sounds/_Evfer/kick1.aif");
			~bass1 = Bummer.read(s, "sounds/_Evfer/bass01.aif");
			~bass2 = Bummer.read(s, "sounds/_Evfer/bassGen01.aif");
			~bass3 = Bummer.read(s, "sounds/_Evfer/bassStr01.aif");
			
			~citMin = Bummer.read(s, "sounds/_Evfer/citMin.aif");
			~cirMin = Bummer.read(s, "sounds/_Evfer/cirMin.aif");
			~circir1 = Bummer.read(s, "sounds/_Evfer/circir1.aif");
			
			~dlStr = Bummer.read(s, "sounds/_Evfer/dlStr.aif");
			~dlyStr1 = Bummer.read(s, "sounds/_Evfer/dlyStrA1.aif");
			~dlyStr2 = Bummer.read(s, "sounds/_Evfer/dlyStrA2.aif");
			~dlyStr3 = Bummer.read(s, "sounds/_Evfer/dlyStrA3.aif");
			
			~fub1 = Bummer.read(s, "sounds/_Evfer/fub1.aif");
			
			~dran1 = Bummer.read(s, "sounds/_Evfer/dran01.aif");
			~dran2 = Bummer.read(s, "sounds/_Evfer/dran02.aif");
			
			~brdk1 = Bummer.read(s, "sounds/_Evfer/bardak1.aif");
			
			~gtr1 = Bummer.read(s, "sounds/_Evfer/gtr1.aif");
			~git1 = Bummer.read(s, "sounds/_Evfer/git1.aif");
			
			~gir1 = Bummer.read(s, "sounds/_Evfer/gir01.aif");
			~gir2 = Bummer.read(s, "sounds/_Evfer/gir02.aif");
			~gir3 = Bummer.read(s, "sounds/_Evfer/gir03.aif");
			
			~int1 = Bummer.read(s, "sounds/_Evfer/int01.aif");
			~int2 = Bummer.read(s, "sounds/_Evfer/int02.aif");
			~int3 = Bummer.read(s, "sounds/_Evfer/int03.aif");
			~int4 = Bummer.read(s, "sounds/_Evfer/int04.aif");
			~int5 = Bummer.read(s, "sounds/_Evfer/int05.aif");
			~int6 = Bummer.read(s, "sounds/_Evfer/int06.aif");
			
			~zil01 = Bummer.read(s, "sounds/~zkm1/zilA01.aif");
			~zil02 = Bummer.read(s, "sounds/~zkm1/zilA02.aif");
			~zil03 = Bummer.read(s, "sounds/~zkm1/zilA03.aif");
			~zil04 = Bummer.read(s, "sounds/~zkm1/zilA04.aif");
	}

	*loadOsc{
	
		~nCirc=ÊOSCresponderNode(nil,Ê'/outs/togNcirc', {Ê|t,r,m|Ê
			if (m[1] == 1) {
				Ncircle.play;

			}{
				Ncircle.stop;			
			}
			
		}).add;

		~cir1=ÊOSCresponderNode(nil,Ê'/harmP/cir1', {Ê|t,r,m|Ê
			if (m[1] == 1) {
				Ncircle.playCirdef;

			}{
				Ncircle.stopCirdef;			
			}
			
		}).add;

		~bass1=ÊOSCresponderNode(nil,Ê'/harmP/bass1', {Ê|t,r,m|Ê
			if (m[1] == 1) {
				Ncircle.playBassdef;

			}{
				Ncircle.stopBassdef;			
			}
			
		}).add;

		~kick1=ÊOSCresponderNode(nil,Ê'/harmP/kick1', {Ê|t,r,m|Ê
			if (m[1] == 1) {
				Ncircle.playKickdef;

			}{
				Ncircle.stopKickdef;			
			}
			
		}).add;
		
		~mn1a=ÊOSCresponderNode(nil,Ê'/outs/main1', {Ê|t,r,m|Ê
			Ncircle.playMain1;
		}).add;	
		~mn1b=ÊOSCresponderNode(nil,Ê'/harmP/main1', {Ê|t,r,m|Ê
			Ncircle.playMain1;
		}).add;

		~mn2a=ÊOSCresponderNode(nil,Ê'/outs/main2', {Ê|t,r,m|Ê
			Ncircle.playMain2;
		}).add;	
		~mn2b=ÊOSCresponderNode(nil,Ê'/harmP/main2', {Ê|t,r,m|Ê
			Ncircle.playMain2;
		}).add;


		~nCircRan1=ÊOSCresponderNode(nil,Ê'/outs/push19', {Ê|t,r,m|Ê
		
				Pdef(\Ncircle, Posc(
					\msg, Prand(
						[
						\1, \2, \3, \4, \5, \6, \7, \8, \9, \10,
						\11, \12, \13, \14, \15, \16, \17, \18, \19, \20,
						\21, \22, \23, \24, \25, \26, \27, \28, \29, \30,
						\31, \32, \33, \34, \35, \36, \37, \38, \39, \40,
						\41, \42, \43, \44, \45, \46, \47, \48, \49, \50,
						\51, \52, \53, \54, \55, \56, \57, \58, \59, \60,
						\61, \62, \63, \64, \65, \66, \67, \68, \69, \70,
						\71, \72, \73, \74, \75, \76, \77, \78, \79, \80,
						\81, \82, \83, \84, \85, \86, \87, \88, \89, \90,
						\91, \92, \93, \94, \95, \96, \97, \98, \99, \100,
						
						\101, \102, \103, \104, \105, \106, \107, \108, \109, \110,
						\111, \112, \113, \114, \115, \116, \117, \118, \119, \120,
						\121, \122, \123, \124, \125, \126, \127, \128, \129, \130,
						\131, \132, \133, \134, \135, \136, \137, \138, \139, \140,
						\141, \142, \143, \144, \145, \146, \147, \148, \149, \150,
						\151, \152, \153, \154, \155, \156, \157, \158, \159, \160,
						\161, \162, \163, \164, \165, \166, \167, \168, \169, \170,
						\171, \172, \173, \174, \175, \176, \177, \178, \179, \180,
						\181, \182, \183, \184, \185, \186, \187, \188, \189, \190,
						\191, \192, \193, \194, \195, \196, \197, \198, \199, \200
						], inf
					),
					\dur, Pseq([~duyekKudDur], inf)
					)
				);
	
		}).add;

		~nCircRan2=ÊOSCresponderNode(nil,Ê'/outs/push20', {Ê|t,r,m|Ê
		
				Pdef(\Ncircle, Posc(
					\msg, Prand(
						[
						\1, \2, \3, \4, \5, \6, \7, \8, \9, \10,
						\11, \12, \13, \14, \15, \16, \17, \18, \19, \20,
						\21, \22, \23, \24, \25, \26, \27, \28, \29, \30,
						\31, \32, \33, \34, \35, \36, \37, \38, \39, \40,
						\41, \42, \43, \44, \45, \46, \47, \48, \49, \50,
						\51, \52, \53, \54, \55, \56, \57, \58, \59, \60,
						\61, \62, \63, \64, \65, \66, \67, \68, \69, \70,
						\71, \72, \73, \74, \75, \76, \77, \78, \79, \80,
						\81, \82, \83, \84, \85, \86, \87, \88, \89, \90,
						\91, \92, \93, \94, \95, \96, \97, \98, \99, \100,
						
						\101, \102, \103, \104, \105, \106, \107, \108, \109, \110,
						\111, \112, \113, \114, \115, \116, \117, \118, \119, \120,
						\121, \122, \123, \124, \125, \126, \127, \128, \129, \130,
						\131, \132, \133, \134, \135, \136, \137, \138, \139, \140,
						\141, \142, \143, \144, \145, \146, \147, \148, \149, \150,
						\151, \152, \153, \154, \155, \156, \157, \158, \159, \160,
						\161, \162, \163, \164, \165, \166, \167, \168, \169, \170,
						\171, \172, \173, \174, \175, \176, \177, \178, \179, \180,
						\181, \182, \183, \184, \185, \186, \187, \188, \189, \190,
						\191, \192, \193, \194, \195, \196, \197, \198, \199, \200
						], inf
					),
					\dur, Pseq([~duyekKudDur]/2, inf)
					)
				);
	
		}).add;

		~nCircRan3=ÊOSCresponderNode(nil,Ê'/outs/push21', {Ê|t,r,m|Ê
		
				Pdef(\Ncircle, Posc(
					\msg, Prand(
						[
						\1, \2, \3, \4, \5, \6, \7, \8, \9, \10,
						\11, \12, \13, \14, \15, \16, \17, \18, \19, \20,
						\21, \22, \23, \24, \25, \26, \27, \28, \29, \30,
						\31, \32, \33, \34, \35, \36, \37, \38, \39, \40,
						\41, \42, \43, \44, \45, \46, \47, \48, \49, \50,
						\51, \52, \53, \54, \55, \56, \57, \58, \59, \60,
						\61, \62, \63, \64, \65, \66, \67, \68, \69, \70,
						\71, \72, \73, \74, \75, \76, \77, \78, \79, \80,
						\81, \82, \83, \84, \85, \86, \87, \88, \89, \90,
						\91, \92, \93, \94, \95, \96, \97, \98, \99, \100,
						
						\101, \102, \103, \104, \105, \106, \107, \108, \109, \110,
						\111, \112, \113, \114, \115, \116, \117, \118, \119, \120,
						\121, \122, \123, \124, \125, \126, \127, \128, \129, \130,
						\131, \132, \133, \134, \135, \136, \137, \138, \139, \140,
						\141, \142, \143, \144, \145, \146, \147, \148, \149, \150,
						\151, \152, \153, \154, \155, \156, \157, \158, \159, \160,
						\161, \162, \163, \164, \165, \166, \167, \168, \169, \170,
						\171, \172, \173, \174, \175, \176, \177, \178, \179, \180,
						\181, \182, \183, \184, \185, \186, \187, \188, \189, \190,
						\191, \192, \193, \194, \195, \196, \197, \198, \199, \200
						], inf
					),
					\dur, Pseq([~duyekKudDur]/4, inf)
					)
				);
	
		}).add;

		~nCircRan4=ÊOSCresponderNode(nil,Ê'/outs/push22', {Ê|t,r,m|Ê
		
			Pdef(\bassdef, Posc(
						\msg, Prand([\dum1, \nil, \fit1, \dum1, \nil, \nil, \nil, \nil, \tek1, \nil], inf),
						\dur, Pseq([~duyekDur], inf)
						)
					);
	
		}).add;

		~nCircRan5=ÊOSCresponderNode(nil,Ê'/outs/push23', {Ê|t,r,m|Ê
		
			Pdef(\bassdef, Posc(
						\msg, Prand([\dum1, \nil, \fit1, \dum1, \nil, \nil, \nil, \nil, \tek1, \nil], inf),
						\dur, Pseq([~duyekDur]/2, inf)
						)
					);
	
		}).add;

		~nCircRan6=ÊOSCresponderNode(nil,Ê'/outs/push24', {Ê|t,r,m|Ê
		
			Pdef(\bassdef, Posc(
						\msg, Prand([\dum1, \nil, \fit1, \dum1, \nil, \nil, \nil, \nil, \tek1, \nil], inf),
						\dur, Pseq([~duyekDur]/4, inf)
						)
					);
	
		}).add;

		~nCircRan7=ÊOSCresponderNode(nil,Ê'/outs/push25', {Ê|t,r,m|Ê
		
			Pdef(\kickdef, Posc(
				\msg, Prand([\dum1, \nil, \nil, 	\dum1, \nil, \nil, \nil, \nil, \tek2, \nil], inf),
				\dur, Pseq([~duyekDur], inf)
				)
			);
	
		}).add;

		~nCircRan8=ÊOSCresponderNode(nil,Ê'/outs/push26', {Ê|t,r,m|Ê
		
			Pdef(\kickdef, Posc(
				\msg, Prand([\dum1, \nil, \nil, 	\dum1, \nil, \nil, \nil, \nil, \tek2, \nil], inf),
				\dur, Pseq([~duyekDur]/2, inf)
				)
			);
	
		}).add;

		~nCircRan9=ÊOSCresponderNode(nil,Ê'/outs/push27', {Ê|t,r,m|Ê
		
			Pdef(\kickdef, Posc(
				\msg, Prand([\dum1, \nil, \nil, 	\dum1, \nil, \nil, \nil, \nil, \tek2, \nil], inf),
				\dur, Pseq([~duyekDur]/4, inf)
				)
			);
	
		}).add;

		~nCircRan10=ÊOSCresponderNode(nil,Ê'/outs/push28', {Ê|t,r,m|Ê
		
			Pdef(\cirdef, Posc(
				\msg, Prand([\dum1, \nil, \nil, 	\dum1, \nil, \nil, \nil, \nil, \teke1, \nil], inf),
				\dur, Pseq([~duyekDur], inf)
				)
			);
		}).add;

		~nCircRan11=ÊOSCresponderNode(nil,Ê'/outs/push29', {Ê|t,r,m|Ê
		
			Pdef(\cirdef, Posc(
				\msg, Prand([\dum1, \nil, \nil, 	\dum1, \nil, \nil, \nil, \nil, \teke1, \nil], inf),
				\dur, Pseq([~duyekDur]/2, inf)
				)
			);
		}).add;

		~nCircRan12=ÊOSCresponderNode(nil,Ê'/outs/push30', {Ê|t,r,m|Ê
		
			Pdef(\cirdef, Posc(
				\msg, Prand([\dum1, \nil, \nil, 	\dum1, \nil, \nil, \nil, \nil, \teke1, \nil], inf),
				\dur, Pseq([~duyekDur]/4, inf)
				)
			);
		}).add;


		~recmain=ÊOSCresponderNode(nil,Ê'/outs/recMain', {Ê|t,r,m|Ê
		Preceive(

			
			\1 -> {
~kick1.att_(0.1).sus_(2).rls_(1.0).rate_(-0.8 rrand: -1.5).mul_(0.9).pan_(2).out_(~mainBus).play0;

~ats3.att_(0.1).sus_(2).rls_(5.0).rate_(~rastRateA1 rrand: 1.5).mul_(0.9).pan_(1.2).out_(0 rrand: 3).play0;
~gir1.att_(0.00001).sus_(2).rls_(5.0).rate_([~rastRateA1 , ~rastRateB1, ~rastRateC1, ~rastRateD1].choose).mul_(0.6).pan_(2.1.rand).out_(0 rrand: 3).play0;
				},
			\2 -> {
~gir1.att_(0.00001).sus_(2).rls_(5.0).rate_([~rastRateA1 , ~rastRateB1, ~rastRateC1, ~rastRateD1].choose).mul_(0.6).pan_(-0.1.rand).out_(~mainBus).play0;
~int3.att_(0.001).sus_(2).rls_(5.0).rate_(-0.8).mul_(0.9).pan_(0.0).out_(~mainBus).play0;

				},
			\3 -> {
~gir1.att_(0.00001).sus_(2).rls_(5.0).rate_([~rastRateA1 , ~rastRateB1, ~rastRateC1, ~rastRateD1].choose).mul_(0.6).pan_(-0.1.rand).out_(~mainBus).play0;
~int3.att_(0.001).sus_(2).rls_(5.0).rate_(-0.8).mul_(0.9).pan_(0.0).out_(~mainBus).play0;
				
				},
			\4 -> {

~gir1.att_(0.00001).sus_(2).rls_(5.0).rate_([~rastRateA1 , ~rastRateB1, ~rastRateC1, ~rastRateD1].choose).mul_(0.6).pan_(-0.1.rand).out_(~mainBus).play0;				
				
				},
			\5 -> {
~gir1.att_(0.00001).sus_(2).rls_(5.0).rate_([~rastRateA1 , ~rastRateB1, ~rastRateC1, ~rastRateD1].choose).mul_(0.6).pan_(-0.1.rand).out_(~mainBus).play0;
~int3.att_(0.001).sus_(2).rls_(5.0).rate_(-0.8).mul_(0.9).pan_(0.0).out_(~mainBus).play0;

				
				},
			\6 -> {},
			\7 -> {},
			\8 -> {
~gir1.att_(0.00001).sus_(2).rls_(5.0).rate_([~rastRateA1 , ~rastRateB1, ~rastRateC1, ~rastRateD1].choose).mul_(0.6).pan_(-0.1.rand).out_(~mainBus).play0;
				
				},
			\9 -> {
~int1.att_(0.001).sus_(2).rls_(3.0).rate_(-0.8).mul_(0.9).pan_(0.0).out_(~mainBus).play0;
				},
			\10 -> {
				~kick1.att_(0.1).sus_(2).rls_(2.0).rate_(5.0 rrand: 5.5).mul_(0.9).pan_(0.0).out_(0).playUp;
				},
			
			\11 -> {
				
				},
			\12 -> {
				~dlStr.att_(0.1).sus_(0.2).rls_(1.0).rate_(0.8 rrand: 1.5).mul_(0.9).pan_(0.0).out_(0).playVib;
				
				},
			\13 -> {},
			\14 -> {},
			\15 -> {
				~circir1.att_(0.1).sus_(0.2).rls_(1.0).rate_(0.8 rrand: -0.5).mul_(0.1).pan_(4.rand).out_(0).play0;
				},
			\16 -> {},
			\17 -> {},
			\18 -> {
				~circir1.att_(0.1).sus_(0.2).rls_(1.0).rate_(0.8 rrand: -0.5).mul_(0.1).pan_(0.4.rand).out_(0).play0;
				},
			\19 -> {},
			\20 -> {},
			
			\21 -> {},
			\22 -> {},
			\23 -> {
				~circir1.att_(0.1).sus_(0.2).rls_(1.0).rate_(0.8 rrand: -0.5).mul_(0.1).pan_(0.4.rand).out_(0).play0;
				},
			\24 -> {},
			\25 -> {},
			\26 -> {
	~gir2.att_(0.00001).sus_(1.2).rls_(3.0).rate_([~rastRateA1 , ~rastRateB1, ~rastRateC1, ~rastRateD1].choose).mul_(0.6).pan_(-0.1.rand).out_(~mainBus).play0;
				},
			\27 -> {},
			\28 -> {},
			\29 -> {},
			\30 -> {
				~circir1.att_(0.1).sus_(0.2).rls_(1.0).rate_(0.8 rrand: -0.5).mul_(0.1).pan_(2.4.rand).out_(0).play0;
				},
			
			\31 -> {},
			\32 -> {},
			\33 -> {},
			\34 -> {
				~wah.set(
					\amp, 0.9,
					\rate, 0.005,
					\cfreq, ~rastD1*92.0,
					\mfreq, ~rastD1*92.0
				);
				~autS.set(
					\freqlp, ~rastA1 rrand: ~rastA1+1
				);
			},
			\35 -> {},
			\36 -> {
				
				~autS.set(
					\freqlp, ~rastB7 rrand: ~rastB7+1
				);
				~wah.set(
					\amp, 0.6,
					\rate, 0.005,
					\cfreq, ~rastA7*92.0,
					\mfreq, ~rastA7*92.02
				);
				~rev.set(
					\roomsize, 2, \revtime, 0.08, \damping, 0.62,
					\earlylevel, -11, \taillevel, -13
				);
			},
			\37 -> {},
			\38 -> {
				~circir1.att_(0.1).sus_(0.2).rls_(1.0).rate_(0.8 rrand: -0.5).mul_(0.1).pan_(1.4.rand).out_(~mainBus).play0;
				},
			\39 -> {},
			\40 -> {},
			
			\41 -> {},
			\42 -> {
~gir1.att_(0.00001).sus_(2).rls_(5.0).rate_([~rastRateA1 , ~rastRateB1, ~rastRateC1, ~rastRateD1].choose).mul_(0.6).pan_(0.1.rand).out_(~mainBus).play0;
				},
			\43 -> {},
			\44 -> {
~gir2.att_(0.00001).sus_(1.2).rls_(3.0).rate_([~rastRateA1 , ~rastRateB1, ~rastRateC1, ~rastRateD1].choose).mul_(0.6).pan_(-0.1.rand).out_(~mainBus).play0;
				},
			\45 -> {},
			\46 -> {
				
				~autS.set(
					\freqlp, ~rastB7 rrand: ~rastB7+1
				);
				~wah.set(
					\amp, 0.6,
					\rate, 0.005,
					\cfreq, ~rastA7*92.0,
					\mfreq, ~rastA7*92.02
				);
				~rev.set(
					\roomsize, 2, \revtime, 0.08, \damping, 0.62,
					\earlylevel, -11, \taillevel, -13
				);
			},
			\47 -> {},
			\48 -> {
				~circir1.att_(0.1).sus_(0.2).rls_(1.0).rate_(0.8 rrand: -0.5).mul_(0.1).pan_(0.4.rand).out_(~mainBus).play0;
				},
			\49 -> {},
			\50 -> {},
			
			\61 -> {},
			\62 -> {
				~wah.set(
					\amp, 0.9,
					\rate, 0.005,
					\cfreq, ~rastD1*92.0,
					\mfreq, ~rastD1*92.0
				);
				~autS.set(
					\freqlp, ~rastA1 rrand: ~rastA1+1
				);
			},
			\63 -> {
~gir1.att_(0.00001).sus_(1.2).rls_(3.0).rate_([~rastRateA1 , ~rastRateB1, ~rastRateC1, ~rastRateD1].choose).mul_(0.6).pan_(-0.1.rand).out_(~mainBus).play0;
				},
			\64 -> {
~gir3.att_(0.00001).sus_(1.2).rls_(3.0).rate_([~rastRateA1 , ~rastRateB1, ~rastRateC1, ~rastRateD1].choose).mul_(0.6).pan_(-0.1.rand).out_(~mainBus).play0;
				},
			\65 -> {},
			\66 -> {},
			\67 -> {
				~circir1.att_(0.1).sus_(0.2.rand).rls_(1.0).rate_(0.8 rrand: -0.5).mul_(0.4.rand).pan_(-0.4.rand).out_(~mainBus).play0;
				
				},
			\68 -> {},
			\69 -> {},
			\70 -> {
				
				~autS.set(
					\freqlp, ~rastB7 rrand: ~rastB7+1
				);
				~wah.set(
					\amp, 0.6,
					\rate, 0.005,
					\cfreq, ~rastA7*92.0,
					\mfreq, ~rastA7*92.02
				);
				~rev.set(
					\roomsize, 2, \revtime, 0.08, \damping, 0.62,
					\earlylevel, -11, \taillevel, -13
				);
			},
			
			\71 -> {},
			\72 -> {},
			\73 -> {
	~gir2.att_(0.00001).sus_(1.2).rls_(3.0).rate_([~rastRateA1 , ~rastRateB1, ~rastRateC1, ~rastRateD1].choose).mul_(0.6).pan_(-0.1.rand).out_(~mainBus).play0;
				},
			\74 -> {},
			\75 -> {},
			\76 -> {},
			\77 -> {
~gir1.att_(0.00001).sus_(2).rls_(5.0.rand).rate_([~rastRateA1 , ~rastRateB1, ~rastRateC1, ~rastRateD1].choose).mul_(0.6).pan_(-0.1.rand).out_(~mainBus).play0;

				
				},
			\78 -> {},
			\79 -> {},
			\80 -> {},
			
			\81 -> {
				


				~wah.set(
					\amp, 0.9,
					\rate, 0.005,
					\cfreq, ~rastA1*92.0,
					\mfreq, ~rastA1*92.0
				);
				~rev.set(
					\roomsize, 5, \revtime, 0.06, \damping, 0.82,
					\earlylevel, -21, \taillevel, -13
				);
			
				
				
				},
			\82 -> {
				~wah.set(
					\amp, 0.9,
					\rate, 0.005,
					\cfreq, ~rastD1*92.0,
					\mfreq, ~rastD1*92.0
				);
				~autS.set(
					\freqlp, ~rastA1 rrand: ~rastA1+1
				);
			},
			\83 -> {},
			\84 -> {},
			\85 -> {
				~wah.set(
					\amp, 0.9,
					\rate, 0.005,
					\cfreq, ~rastD1*92.0,
					\mfreq, ~rastD1*92.0
				);
				~autS.set(
					\freqlp, ~rastA1 rrand: ~rastA1+1
				);
			},
			\86 -> {},
			\87 -> {},
			\88 -> {
				
				~autS.set(
					\freqlp, ~rastB7 rrand: ~rastB7+1
				);
				~wah.set(
					\amp, 0.6,
					\rate, 0.005,
					\cfreq, ~rastA7*92.0,
					\mfreq, ~rastA7*92.02
				);
				~rev.set(
					\roomsize, 2, \revtime, 0.08, \damping, 0.62,
					\earlylevel, -11, \taillevel, -13
				);
			},
			\89 -> {},
			\90 -> {},
			
			\91 -> {},
			\92 -> {},
			\93 -> {},
			\94 -> {},
			\95 -> {
~gir1.att_(0.00001).sus_(2).rls_(5.0).rate_([~rastRateA1 , ~rastRateB1, ~rastRateC1, ~rastRateD1].choose).mul_(0.6).pan_(-0.1.rand).out_(~mainBus).play0;
				
				},
			\96 -> {},
			\97 -> {},
			\98 -> {},
			\99 -> {},
			\100 -> {},
			
			\101 -> {},
			\102 -> {},
			\103 -> {},
			\104 -> {},
			\105 -> {},
			\106 -> {
				~wah.set(
					\amp, 0.9,
					\rate, 0.005,
					\cfreq, ~rastD1*92.0,
					\mfreq, ~rastD1*92.0
				);
				~autS.set(
					\freqlp, ~rastA1 rrand: ~rastA1+1
				);
			},
			\107 -> {},
			\108 -> {
				
				~autS.set(
					\freqlp, ~rastB7 rrand: ~rastB7+1
				);
				~wah.set(
					\amp, 0.6,
					\rate, 0.005,
					\cfreq, ~rastA7*92.0,
					\mfreq, ~rastA7*92.02
				);
				~rev.set(
					\roomsize, 2, \revtime, 0.08, \damping, 0.62,
					\earlylevel, -11, \taillevel, -13
				);
			},
			\109 -> {},
			\110 -> {},
			
			\111 -> {},
			\112 -> {},
			\113 -> {},
			\114 -> {
				~kick1.att_(0.1).sus_(2).rls_(2.0).rate_(10.8 rrand: 11.5).mul_(0.3).pan_(0.0).out_(2).playDown;
				},
			\115 -> {},
			\116 -> {
				


				~wah.set(
					\amp, 0.9,
					\rate, 0.005,
					\cfreq, ~rastA1*92.0,
					\mfreq, ~rastA1*92.0
				);
				~rev.set(
					\roomsize, 5, \revtime, 0.06, \damping, 0.82,
					\earlylevel, -21, \taillevel, -13
				);
			
				
				},
			\117 -> {},
			\118 -> {},
			\119 -> {
				
				~autS.set(
					\freqlp, ~rastB7 rrand: ~rastB7+1
				);
				~wah.set(
					\amp, 0.6,
					\rate, 0.005,
					\cfreq, ~rastA7*92.0,
					\mfreq, ~rastA7*92.02
				);
				~rev.set(
					\roomsize, 2, \revtime, 0.08, \damping, 0.62,
					\earlylevel, -11, \taillevel, -13
				);
			},
			\120 -> {},
			
			\121 -> {
	~gir2.att_(0.00001).sus_(1.2).rls_(3.0).rate_([~rastRateA1 , ~rastRateB1, ~rastRateC1, ~rastRateD1].choose).mul_(0.6).pan_(-0.1.rand).out_(~mainBus).play0;
				},
			\122 -> {},
			\123 -> {},
			\124 -> {},
			\125 -> {
				~circir1.att_(0.1).sus_(0.2).rls_(1.0.rand).rate_(0.8 rrand: -0.5).mul_(0.1).pan_(1.4.rand).out_(3).play0;
				},
			\126 -> {},
			\127 -> {},
			\128 -> {},
			\129 -> {},
			\130 -> {},
			
			\131 -> {},
			\132 -> {
				
				~autS.set(
					\freqlp, ~rastB7 rrand: ~rastB7+1
				);
				~wah.set(
					\amp, 0.6,
					\rate, 0.005,
					\cfreq, ~rastA7*92.0,
					\mfreq, ~rastA7*92.02
				);
				~rev.set(
					\roomsize, 2, \revtime, 0.08, \damping, 0.62,
					\earlylevel, -11, \taillevel, -13
				);
			},
			\133 -> {
~gir2.att_(0.00001).sus_(1.2).rls_(3.0).rate_([~rastRateA1 , ~rastRateB1, ~rastRateC1, ~rastRateD1].choose).mul_(0.6).pan_(-0.1.rand).out_(~mainBus).play0;
				},
			\134 -> {},
			\135 -> {},
			\136 -> {},
			\137 -> {
				~circir1.att_(0.1).sus_(0.2).rls_(1.0).rate_(0.8 rrand: -0.5).mul_(0.1).pan_(-0.4.rand).out_(2).play0;
				},
			\138 -> {},
			\139 -> {},
			\140 -> {},
			
			\141 -> {
				
				~autS.set(
					\freqlp, ~rastB7 rrand: ~rastB7+1
				);
				~wah.set(
					\amp, 0.6,
					\rate, 0.005,
					\cfreq, ~rastA7*92.0,
					\mfreq, ~rastA7*92.02
				);
				~rev.set(
					\roomsize, 2, \revtime, 0.08, \damping, 0.62,
					\earlylevel, -11, \taillevel, -13
				);
			},
			\142 -> {
~gir2.att_(0.00001).sus_(1.2).rls_(3.0).rate_([~rastRateA1 , ~rastRateB1, ~rastRateC1, ~rastRateD1].choose).mul_(0.6).pan_(-0.1.rand).out_(~mainBus).play0;
				},
			\143 -> {},
			\144 -> {},
			\145 -> {
				~circir1.att_(0.1).sus_(0.2).rls_(1.0).rate_(0.8 rrand: -0.5).mul_(0.1).pan_(-0.4.rand).out_(1).play0;
				},
			\146 -> {},
			\147 -> {},
			\148 -> {},
			\149 -> {},
			\150 -> {},
			
			\161 -> {},
			\162 -> {
~gir2.att_(0.00001).sus_(1.2).rls_(3.0).rate_([~rastRateA1 , ~rastRateB1, ~rastRateC1, ~rastRateD1].choose).mul_(0.6).pan_(-0.1.rand).out_(~mainBus).play0;
				
				},
			\163 -> {},
			\164 -> {},
			\165 -> {
				~circir1.att_(0.1).sus_(0.2).rls_(1.0).rate_(0.8 rrand: -0.5).mul_(0.1).pan_(1.4.rand).out_(0).play0;
				},
			\166 -> {},
			\167 -> {},
			\168 -> {
				~kick1.att_(0.1).sus_(2).rls_(2.0).rate_(10.8 rrand: 11.5).mul_(0.5).pan_(2.0).out_(2).playDown;
				},
			\169 -> {
				~wah.set(
					\amp, 0.9,
					\rate, 0.005,
					\cfreq, ~rastD1*92.0,
					\mfreq, ~rastD1*92.0
				);
				~autS.set(
					\freqlp, ~rastA1 rrand: ~rastA1+1
				);
			},
			\170 -> {},
			
			\171 -> {},
			\172 -> {},
			\173 -> {
				
				~autS.set(
					\freqlp, ~rastB7 rrand: ~rastB7+1
				);
				~wah.set(
					\amp, 0.6,
					\rate, 0.005,
					\cfreq, ~rastA7*92.0,
					\mfreq, ~rastA7*92.02
				);
				~rev.set(
					\roomsize, 2, \revtime, 0.08, \damping, 0.62,
					\earlylevel, -11, \taillevel, -13
				);
			},
			\174 -> {},
			\175 -> {},
			\176 -> {
				~circir1.att_(0.1).sus_(0.2).rls_(1.0).rate_(0.8 rrand: -0.5).mul_(0.1).pan_(3.rand).out_(0).play0;
				},
			\177 -> {},
			\178 -> {},
			\179 -> {},
			\180 -> {},
			
			\181 -> {},
			\182 -> {},
			\183 -> {},
			\184 -> {
				~circir1.att_(0.1).sus_(0.2).rls_(1.0).rate_(0.8 rrand: -0.5).mul_(0.1).pan_(0.4.rand).out_(0).play0;
				},
			\185 -> {
				
				~autS.set(
					\freqlp, ~rastB7 rrand: ~rastB7+1
				);
				~wah.set(
					\amp, 0.6,
					\rate, 0.005,
					\cfreq, ~rastA7*92.0,
					\mfreq, ~rastA7*92.02
				);
				~rev.set(
					\roomsize, 2, \revtime, 0.08, \damping, 0.62,
					\earlylevel, -11, \taillevel, -13
				);
			},
			\186 -> {},
			\187 -> {
				
				~autS.set(
					\freqlp, ~rastB7 rrand: ~rastB7+1
				);
				~wah.set(
					\amp, 0.6,
					\rate, 0.005,
					\cfreq, ~rastA7*92.0,
					\mfreq, ~rastA7*92.02
				);
				~rev.set(
					\roomsize, 2, \revtime, 0.08, \damping, 0.62,
					\earlylevel, -11, \taillevel, -13
				);
			},
			\188 -> {},
			\189 -> {},
			\190 -> {},
			
			\191 -> {},
			\192 -> {},
			\193 -> {},
			\194 -> {
				~circir1.att_(0.1).sus_(0.2).rls_(1.0).rate_(0.8 rrand: -0.5).mul_(0.1).pan_(-0.4.rand).out_(1).play0;
				},
			\195 -> {},
			\196 -> {},
			\197 -> {},
			\198 -> {},
			\199 -> {},
			\200 -> {},
			
			'dum1' -> {
		
~kick1.att_(0.1).sus_(2).rls_(2.0).rate_(5.0 rrand: 5.5).mul_(0.9).pan_(2.0).out_(0).playUp;
					
				},
		
			'bass1' -> {
		
				~rit=Synth.head(~piges, \buf1, 
					[ 
						\att, 0.1, \dec, 0.8, \rls, 3, 
						\bufnum, ~bass1, 
						\out, ~mainBus,
						\amp, 0.9, 
						\rate, 0.999 rrand: 0.80 
					]
				);
					
				},
			'bass2' -> {
		
				~rit=Synth.head(~piges, \buf1, 
					[ 
						\att, 0.1, \dec, 0.8, \rls, 3, 
						\bufnum, ~bass2, 
						\out, ~mainBus,
						\amp, 0.9, 
						\rate, 0.99 rrand: -0.90 
					]
				);
					
				},
			'bass3' -> {
		
				~rit=Synth.head(~piges, \buf1, 
					[ 
						\att, 0.1, \dec, 0.8, \rls, 3, 
						\bufnum, ~bass3, 
						\out, ~mainBus,
						\amp, 0.9, 
						\rate, 0.999 rrand: 0.50
					]
				);
					
				},
			'tek1' -> {
				
				~rit=Synth.head(~piges, \buf1, 
					[ 
						\att, 0.01, \dec, 0.8, \rls, 0.2, 
						\bufnum, ~kick1, 
						\out, ~mainBus,
						\amp, 0.9, 
						\rate, 8.999 
					]
				);
			
				},
			'tek2' -> {
				
				~rit=Synth.head(~piges, \buf1, 
					[ 
						\att, 0.01, \dec, 0.8, \rls, 0.1, 
						\bufnum, ~kick1, 
						\out, ~mainBus,
						\amp, 0.9, 
						\rate, 18.999 
					]
				);
		
				},
			'teke1' -> {
		
				~rit=Synth.head(~piges, \buf1, 
					[ 
						\att, 0.01, \dec, 0.8, \rls, 0.4, 
						\bufnum, ~kick1, 
						\out, ~mainBus,
						\amp, 0.9, 
						\rate, 15 
					]
				);
		
				},
			'trr1' -> {
		
				~rit=Synth.head(~piges, \buf1, 
					[ 
						\att, 0.01, \dec, 0.8, \rls, 0.8, 
						\bufnum, ~kick1, 
						\out, ~mainBus,
						\amp, 0.8, 
						\rate, 125 
					]
				);
		
				},
			'trr2' -> {
				
				~rit=Synth.head(~piges, \buf1, 
					[ 
						\att, 0.01, \dec, 0.8, \rls, 0.15, 
						\bufnum, ~kick1, 
						\out, ~mainBus,
						\amp, 0.8, 
						\rate, 225 
					]
				);
		
				},
			
		//int	
			'int2a' -> {
				
				~rit=Synth.head(~piges, \buf1, 
					[ 
						\att, 0.01, \dec, 0.9, \rls, 0.8, 
						 
						
						\amp, 0.8, 
						\startPos, 0.1,
						\rate, -0.80,
						\bufnum, ~int2,
						\out, ~mainBus
					]
				);
				},
			'int2b' -> {
				
				~rit=Synth.head(~piges, \buf1, 
					[ 
						\att, 0.01, \dec, 0.9, \rls, 0.8, 
						 
						
						\amp, 0.8, 
						\startPos, 0.1,
						\rate, -0.90,
						\bufnum, ~int2,
						\out, ~mainBus 
					]
				);
		
		
				},
			
			'fit1' -> {
				
				~rit=Synth.head(~piges, \buf1, 
					[ 
						\att, 0.01, \dec, 0.8, \rls, 0.45, 
						\bufnum, ~kick1, 
						\out, ~mainBus,
						\amp, 0.8, 
						\rate, -25 
					]
				);
		
				},
			'fit2' -> {
		
				~rit=Synth.head(~piges, \buf1, 
					[ 
						\att, 0.01, \dec, 0.8, \rls, 0.25, 
						\bufnum, ~kick1, 
						\out, ~mainBus,
						\amp, 0.8, 
						\rate, -35 
					]
				);
		
				},
			'cir1' -> {
		
~circir1.att_(0.1).sus_(0.2).rls_(1.0).rate_(0.8 rrand: -0.5).mul_(0.1).pan_(-0.4.rand).out_(0).play0;
								
				},
			'cir2' -> {
				
~circir1.att_(0.1).sus_(0.2).rls_(1.0).rate_(0.8 rrand: -0.5).mul_(0.1).pan_(-0.4.rand).out_(0).play0;
			
				},
			'cir3' -> {
				
~circir1.att_(0.1).sus_(0.2).rls_(1.0).rate_(0.8 rrand: -0.5).mul_(0.1).pan_(-0.4.rand).out_(0).play0;
		
				},
			'cir4' -> {
		
~circir1.att_(0.1).sus_(0.2).rls_(1.0).rate_(0.8 rrand: -0.5).mul_(0.1).pan_(-0.4.rand).out_(0).play0;
		
				},

	//Main1	
			\a1 -> {


				~wah.set(
					\amp, 0.9,
					\rate, 0.005,
					\cfreq, ~rastA1*92.0,
					\mfreq, ~rastA1*92.0
				);
				~rev.set(
					\roomsize, 5, \revtime, 0.06, \damping, 0.82,
					\earlylevel, -21, \taillevel, -13
				);
			},
			\a3 -> {
				~wah.set(
					\amp, 0.8,
					\rate, 0.05,
					\cfreq, ~rastA5*92.0,
					\mfreq, ~rastA5*92.01
				);
				~rev.set(
					\roomsize, 5, \revtime, 0.1, \damping, 0.92,
					\earlylevel, -11, \taillevel, -13
				);
				
				~autS.set(
					\freqlp, ~rastB1 rrand: ~rastB1+1
				);
			},
			\a4 -> {
				
				~autS.set(
					\freqlp, ~rastB7 rrand: ~rastB7+1
				);
				~wah.set(
					\amp, 0.6,
					\rate, 0.005,
					\cfreq, ~rastA7*92.0,
					\mfreq, ~rastA7*92.02
				);
				~rev.set(
					\roomsize, 2, \revtime, 0.08, \damping, 0.62,
					\earlylevel, -11, \taillevel, -13
				);
			},
			\a5 -> {
				~wah.set(
					\amp, 0.5,
					\rate, 0.5,
					\cfreq, ~rastB1*92.0,
					\mfreq, ~rastB1*92.01
				);
				~rev.set(
					\roomsize, 0.1, \revtime, 0.062, \damping, 0.9,
					\earlylevel, -40, \taillevel, -28
				);
			},
			\a6 -> {
				~wah.set(
					\amp, 0.4,
					\rate, 1.005,
					\cfreq, ~rastB7*92.0,
					\mfreq, ~rastB7*92.0
				);
				~rev.set(
					\roomsize, 0.5, \revtime, 0.06, \damping, 0.72,
					\earlylevel, -31, \taillevel, -33
				);
			},
			\a7 -> {
				~wah.set(
					\amp, 0.6,
					\rate, 0.5,
					\cfreq, ~rastC2*92.0,
					\mfreq, ~rastC2*92.01
				);
				~rev.set(
					\roomsize, 45, \revtime, 0.2, \damping, 0.92,
					\earlylevel, -101, \taillevel, -13
				);

				~autS.set(
					\freqlp, ~rastC1 rrand: ~rastC1+1
				);
			
			},
			\a8 -> {
				~wah.set(
					\amp, 0.7,
					\rate, 0.05,
					\cfreq, ~rastA1*92.0,
					\mfreq, ~rastA1*92.02
				);

				~rev.set(
					\roomsize, 1, \revtime, 0.09, \damping, 0.92,
					\earlylevel, -11, \taillevel, -13
				);
			},
			\a9 -> {
				~wah.set(
					\amp, 0.8,
					\rate, 0.005,
					\cfreq, ~rastC4*92.0,
					\mfreq, ~rastC4*92.01
				);
			},
			\a10 -> {
				~wah.set(
					\amp, 0.9,
					\rate, 0.005,
					\cfreq, ~rastD1*92.0,
					\mfreq, ~rastD1*92.0
				);
				~autS.set(
					\freqlp, ~rastA1 rrand: ~rastA1+1
				);
			},
			\b1 -> {


				~wah.set(
					\amp, 0.9,
					\rate, 0.005,
					\cfreq, ~rastA1*92.0,
					\mfreq, ~rastA1*92.0
				);
				~rev.set(
					\roomsize, 5, \revtime, 0.06, \damping, 0.82,
					\earlylevel, -21, \taillevel, -13
				);
			},
			\b3 -> {
				~wah.set(
					\amp, 0.8,
					\rate, 0.05,
					\cfreq, ~rastA5*92.0,
					\mfreq, ~rastA5*92.01
				);
				~rev.set(
					\roomsize, 5, \revtime, 0.1, \damping, 0.92,
					\earlylevel, -11, \taillevel, -13
				);
				
				~autS.set(
					\freqlp, ~rastB1 rrand: ~rastB1+1
				);
			},
			\b4 -> {
~gir1.att_(0.00001).sus_(1.2).rls_(3.0).rate_([~rastRateA1 , ~rastRateB1, ~rastRateC1, ~rastRateD1].choose).mul_(0.6).pan_(-0.1.rand).out_(~mainBus).play0;				
				~autS.set(
					\freqlp, ~rastB7 rrand: ~rastB7+1
				);
				~wah.set(
					\amp, 0.6,
					\rate, 0.005,
					\cfreq, ~rastA7*92.0,
					\mfreq, ~rastA7*92.02
				);
				~rev.set(
					\roomsize, 2, \revtime, 0.08, \damping, 0.62,
					\earlylevel, -11, \taillevel, -13
				);
			},
			\b5 -> {
				~wah.set(
					\amp, 0.5,
					\rate, 0.5,
					\cfreq, ~rastB1*92.0,
					\mfreq, ~rastB1*92.01
				);
				~rev.set(
					\roomsize, 0.1, \revtime, 0.062, \damping, 0.9,
					\earlylevel, -40, \taillevel, -28
				);
			},
			\b6 -> {
				~wah.set(
					\amp, 0.4,
					\rate, 1.005,
					\cfreq, ~rastB7*92.0,
					\mfreq, ~rastB7*92.0
				);
				~rev.set(
					\roomsize, 0.5, \revtime, 0.06, \damping, 0.72,
					\earlylevel, -31, \taillevel, -33
				);
			},
			\b7 -> {
				~wah.set(
					\amp, 0.6,
					\rate, 0.5,
					\cfreq, ~rastC2*92.0,
					\mfreq, ~rastC2*92.01
				);
				~rev.set(
					\roomsize, 45, \revtime, 0.2, \damping, 0.92,
					\earlylevel, -101, \taillevel, -13
				);
~gir2.att_(0.00001).sus_(1.2).rls_(3.0).rate_([~rastRateA1 , ~rastRateB1, ~rastRateC1, ~rastRateD1].choose).mul_(0.6).pan_(-0.1.rand).out_(~mainBus).play0;
				~autS.set(
					\freqlp, ~rastC1 rrand: ~rastC1+1
				);
			
			},
~gir2.att_(0.00001).sus_(1.2).rls_(3.0).rate_([~rastRateA1 , ~rastRateB1, ~rastRateC1, ~rastRateD1].choose).mul_(0.6).pan_(-0.1.rand).out_(~mainBus).play0;
			\b8 -> {
				~wah.set(
					\amp, 0.7,
					\rate, 0.05,
					\cfreq, ~rastA1*92.0,
					\mfreq, ~rastA1*92.02
				);
~gir2.att_(0.00001).sus_(1.2).rls_(3.0).rate_([~rastRateA1 , ~rastRateB1, ~rastRateC1, ~rastRateD1].choose).mul_(0.6).pan_(-0.1.rand).out_(~mainBus).play0;
				~rev.set(
					\roomsize, 1, \revtime, 0.09, \damping, 0.92,
					\earlylevel, -11, \taillevel, -13
				);
			},
			\b9 -> {
				~wah.set(
					\amp, 0.8,
					\rate, 0.005,
					\cfreq, ~rastC4*92.0,
					\mfreq, ~rastC4*92.01
				);
			},
			\b10 -> {
				~wah.set(
					\amp, 0.9,
					\rate, 0.005,
					\cfreq, ~rastD1*92.0,
					\mfreq, ~rastD1*92.0
				);
				~autS.set(
					\freqlp, ~rastA1 rrand: ~rastA1+1
				);
			}
			
		).play;
		}).add;



	}


	
	*play { 
	
		Pdef(\Ncircle, Posc(
			\msg, Pseq(
				[
				\1, \2, \3, \4, \5, \6, \7, \8, \9, \10,
				\11, \12, \13, \14, \15, \16, \17, \18, \19, \20,
				\21, \22, \23, \24, \25, \26, \27, \28, \29, \30,
				\31, \32, \33, \34, \35, \36, \37, \38, \39, \40,
				\41, \42, \43, \44, \45, \46, \47, \48, \49, \50,
				\51, \52, \53, \54, \55, \56, \57, \58, \59, \60,
				\61, \62, \63, \64, \65, \66, \67, \68, \69, \70,
				\71, \72, \73, \74, \75, \76, \77, \78, \79, \80,
				\81, \82, \83, \84, \85, \86, \87, \88, \89, \90,
				\91, \92, \93, \94, \95, \96, \97, \98, \99, \100,
				
				\101, \102, \103, \104, \105, \106, \107, \108, \109, \110,
				\111, \112, \113, \114, \115, \116, \117, \118, \119, \120,
				\121, \122, \123, \124, \125, \126, \127, \128, \129, \130,
				\131, \132, \133, \134, \135, \136, \137, \138, \139, \140,
				\141, \142, \143, \144, \145, \146, \147, \148, \149, \150,
				\151, \152, \153, \154, \155, \156, \157, \158, \159, \160,
				\161, \162, \163, \164, \165, \166, \167, \168, \169, \170,
				\171, \172, \173, \174, \175, \176, \177, \178, \179, \180,
				\181, \182, \183, \184, \185, \186, \187, \188, \189, \190,
				\191, \192, \193, \194, \195, \196, \197, \198, \199, \200
				], 1
			),
			\dur, Pseq([~duyekKudDur]*2, inf)
			)
		).play;
		 
	}


	*stop { 
	
		Pdef(\Ncircle).stop;
		 
	}

	*playCirdef{
		Pdef(\cirdef, Posc(
			\msg, Pseq([\nil, \nil, \cir4, 	\nil, \nil, \nil, \nil, \nil, \cir1, \nil], inf),
			\dur, Pseq([~duyekDur], inf)
			)
		).play;
	}




	*stopCirdef { 
	
		Pdef(\cirdef).stop;
		 
	}

	*playBassdef{
		Pdef(\bassdef, Posc(
			\msg, Pseq([\bass1, \tek1, \nil, 	\bass1, \nil, \bass2, \nil, \nil, \bass2, \nil], inf),
			\dur, Pseq([~duyekDur], inf)
			)
		).play;
	}

	*stopBassdef { 
	
		Pdef(\bassdef).stop;
		 
	}


	*playKickdef{
		Pdef(\kickdef, Posc(
			\msg, Pseq([\dum1, \nil, \nil, 	\dum1, \nil, \nil, \nil, \nil, \tek2, \nil], inf),
			\dur, Pseq([~duyekDur], inf)
			)
		).play;
	}

	*stopKickdef { 
	
		Pdef(\kickdef).stop;
		 
	}


	*playMain1{
		Pdef(\main1, Posc(
			\msg, Pseq([
				Pseq([\a2], 1),
				Pshuf(
					[
					\a1, \a3, \a4, \a5, \a6, \a7, \a8, \a9, \a10
					], 1
				),
				Pseq([\a1], 1)
			],1),
			\dur, Pseq([~duyekKudDur]/2, inf),
			\dest, [NetAddr.localAddr]
			)
		).play;
	}

	*playMain2{
		Pdef(\main1, Posc(
			\msg, Pseq([
				Pseq([\b2], 1),
				Pshuf(
					[
					\b1, \b3, \b4, \b5, \b6, \b7, \b8, \b9, \b10
					], 1
				),
				Pseq([\b1], 1)
			],1),
			\dur, Pseq([~duyekKudDur]/2, inf)
			)
		).play;
	}

}

/*
Ncircle.receive;
Ncircle.play;
Ncircle.stop;

Pdef(\Ncircle).play;

(

		Pdef(\main1, Posc(
			\msg, Pseq([
				Pseq([\a2], 1),
				Pshuf(
					[
					\a1, \a3, \a4, \a5, \a6, \a7, \a8, \a9, \a10
					], 1
				),
				Pseq([\a1], 1)
			],1),
			\dur, Pseq([~duyekKudDur]/6, inf),
			\dest, [NetAddr.localAddr]
			)
		);

Pdef(\bassdef, Posc(
			\msg, Prand([\dum1, \nil, \fit1, 	\dum1, \nil, \nil, \nil, \nil, \tek1, \nil], inf),
			\dur, Pseq([~duyekDur]/2, inf)
			)
		);

Pdef(\kickdef, Posc(
			\msg, Prand([\dum1, \nil, \nil, 	\dum1, \nil, \nil, \nil, \nil, \tek2, \nil], inf),
			\dur, Pseq([~duyekDur]/2, inf)
			)
		);
Pdef(\cirdef, Posc(
			\msg, Prand([\dum1, \nil, \nil, 	\dum1, \nil, \nil, \nil, \nil, \teke1, \nil], inf),
			\dur, Pseq([~duyekDur]/8, inf)
			)
		);


Pdef(\Ncircle, Posc(
	\msg, Prand(
		[
		\1, \2, \3, \4, \5, \6, \7, \8, \9, \10,
		\11, \12, \13, \14, \15, \16, \17, \18, \19, \20,
		\21, \22, \23, \24, \25, \26, \27, \28, \29, \30,
		\31, \32, \33, \34, \35, \36, \37, \38, \39, \40,
		\41, \42, \43, \44, \45, \46, \47, \48, \49, \50,
		\51, \52, \53, \54, \55, \56, \57, \58, \59, \60,
		\61, \62, \63, \64, \65, \66, \67, \68, \69, \70,
		\71, \72, \73, \74, \75, \76, \77, \78, \79, \80,
		\81, \82, \83, \84, \85, \86, \87, \88, \89, \90,
		\91, \92, \93, \94, \95, \96, \97, \98, \99, \100,
		
		\101, \102, \103, \104, \105, \106, \107, \108, \109, \110,
		\111, \112, \113, \114, \115, \116, \117, \118, \119, \120,
		\121, \122, \123, \124, \125, \126, \127, \128, \129, \130,
		\131, \132, \133, \134, \135, \136, \137, \138, \139, \140,
		\141, \142, \143, \144, \145, \146, \147, \148, \149, \150,
		\151, \152, \153, \154, \155, \156, \157, \158, \159, \160,
		\161, \162, \163, \164, \165, \166, \167, \168, \169, \170,
		\171, \172, \173, \174, \175, \176, \177, \178, \179, \180,
		\181, \182, \183, \184, \185, \186, \187, \188, \189, \190,
		\191, \192, \193, \194, \195, \196, \197, \198, \199, \200
		], inf
	),
	\dur, Pseq([~duyekKudDur]/4, inf)
	)
);
)

Pdef(\Ncircle).stop;


*/












/*
oldys

(
~ats1 = Bummer.read(s, "sounds/_Evfer/ates01.aif");
~ats2 = Bummer.read(s, "sounds/_Evfer/ates02.aif");
~ats3 = Bummer.read(s, "sounds/_Evfer/ates03.aif");
~ats4 = Bummer.read(s, "sounds/_Evfer/ates04.aif");

~kick1 = Bummer.read(s, "sounds/_Evfer/kick1.aif");
~bass1 = Bummer.read(s, "sounds/_Evfer/bass01.aif");
~bass2 = Bummer.read(s, "sounds/_Evfer/bassGen01.aif");
~bass3 = Bummer.read(s, "sounds/_Evfer/bassStr01.aif");

~citMin = Bummer.read(s, "sounds/_Evfer/citMin.aif");
~cirMin = Bummer.read(s, "sounds/_Evfer/cirMin.aif");
~circir1 = Bummer.read(s, "sounds/_Evfer/circir1.aif");

~dlStr = Bummer.read(s, "sounds/_Evfer/dlStr.aif");
~dlyStr1 = Bummer.read(s, "sounds/_Evfer/dlyStrA1.aif");
~dlyStr2 = Bummer.read(s, "sounds/_Evfer/dlyStrA2.aif");
~dlyStr3 = Bummer.read(s, "sounds/_Evfer/dlyStrA3.aif");

~fub1 = Bummer.read(s, "sounds/_Evfer/fub1.aif");

~dran1 = Bummer.read(s, "sounds/_Evfer/dran01.aif");
~dran2 = Bummer.read(s, "sounds/_Evfer/dran02.aif");

~brdk1 = Bummer.read(s, "sounds/_Evfer/bardak1.aif");

~gtr1 = Bummer.read(s, "sounds/_Evfer/gtr1.aif");
~git1 = Bummer.read(s, "sounds/_Evfer/git1.aif");

~gir1 = Bummer.read(s, "sounds/_Evfer/gir01.aif");
~gir2 = Bummer.read(s, "sounds/_Evfer/gir02.aif");
~gir3 = Bummer.read(s, "sounds/_Evfer/gir03.aif");

~int1 = Bummer.read(s, "sounds/_Evfer/int01.aif");
~int2 = Bummer.read(s, "sounds/_Evfer/int02.aif");
~int3 = Bummer.read(s, "sounds/_Evfer/int03.aif");
~int4 = Bummer.read(s, "sounds/_Evfer/int04.aif");
~int5 = Bummer.read(s, "sounds/_Evfer/int05.aif");
~int6 = Bummer.read(s, "sounds/_Evfer/int06.aif");

~zil01 = Bummer.read(s, "sounds/~zkm1/zilA01.aif");
~zil02 = Bummer.read(s, "sounds/~zkm1/zilA02.aif");
~zil03 = Bummer.read(s, "sounds/~zkm1/zilA03.aif");
~zil04 = Bummer.read(s, "sounds/~zkm1/zilA04.aif");
)








*/