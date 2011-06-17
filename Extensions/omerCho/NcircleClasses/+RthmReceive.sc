
/*
RthmReceive.load;
*/


RthmReceive {
	*load {
///////////////////////////////////////
var s;
s = Server.default;



~ats1 = Buffer.read(s, "sounds/_Evfer/ates01.aif");
~ats2 = Buffer.read(s, "sounds/_Evfer/ates02.aif");
~ats3 = Buffer.read(s, "sounds/_Evfer/ates03.aif");
~ats4 = Buffer.read(s, "sounds/_Evfer/ates04.aif");

~kick1 = Buffer.read(s, "sounds/_Evfer/kick1.aif");
~bass1 = Buffer.read(s, "sounds/_Evfer/bass01.aif");
~bass2 = Buffer.read(s, "sounds/_Evfer/bassGen01.aif");
~bass3 = Buffer.read(s, "sounds/_Evfer/basStr01.aif");

~citMin = Buffer.read(s, "sounds/_Evfer/citMin.aif");
~cirMin = Buffer.read(s, "sounds/_Evfer/cirMin.aif");
~circir1 = Buffer.read(s, "sounds/_Evfer/circir1.aif");

~dlStr = Buffer.read(s, "sounds/_Evfer/dlStr.aif");
~dlyStr1 = Buffer.read(s, "sounds/_Evfer/dlyStrA1.aif");
~dlyStr2 = Buffer.read(s, "sounds/_Evfer/dlyStrA2.aif");
~dlyStr3 = Buffer.read(s, "sounds/_Evfer/dlyStrA3.aif");

~fub1 = Buffer.read(s, "sounds/_Evfer/fub1.aif");

~dran1 = Buffer.read(s, "sounds/_Evfer/dran01.aif");
~dran2 = Buffer.read(s, "sounds/_Evfer/dran02.aif");

~brdk1 = Buffer.read(s, "sounds/_Evfer/bardak1.aif");

~gtr1 = Buffer.read(s, "sounds/_Evfer/gtr1.aif");
~git1 = Buffer.read(s, "sounds/_Evfer/git1.aif");

~gir1 = Buffer.read(s, "sounds/_Evfer/gir01.aif");
~gir2 = Buffer.read(s, "sounds/_Evfer/gir02.aif");
~gir3 = Buffer.read(s, "sounds/_Evfer/gir03.aif");

~int1 = Buffer.read(s, "sounds/_Evfer/int01.aif");
~int2 = Buffer.read(s, "sounds/_Evfer/int02.aif");
~int3 = Buffer.read(s, "sounds/_Evfer/int03.aif");
~int4 = Buffer.read(s, "sounds/_Evfer/int04.aif");
~int5 = Buffer.read(s, "sounds/_Evfer/int05.aif");
~int6 = Buffer.read(s, "sounds/_Evfer/int06.aif");

~zil01 = Buffer.read(s, "sounds/~zkm1/zilA01.aif");
~zil02 = Buffer.read(s, "sounds/~zkm1/zilA02.aif");
~zil03 = Buffer.read(s, "sounds/~zkm1/zilA03.aif");
~zil04 = Buffer.read(s, "sounds/~zkm1/zilA04.aif");


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
)
(
NcMainVol.play;
ChClean.play;
)
RthmReceive.load;
(
Synth.head(~piges, \buf1, 
	[ 
		\att, 0.01, \dec, 0.9, \rls, 0.8, 
		 
		
		\amp, 0.8, 
		\startPos, 0.1,
		\rate, -0.90 rrand: 0.90,
		\bufnum, ~int1,
		\out, 0 
	]
);
)

*/


~kick1Vars = Preceive(
	'dum1' -> {

		~rit=Synth.head(~piges, \buf1, 
			[ 
				\att, 0.1, \dec, 0.8, \rls, 3, 
				\bufnum, ~kick1, 
				\out, ~mainBus,
				\amp, 0.9, 
				\rate, 0.999 
			]
		);
			
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

			~rit=Synth.head(~piges, \buf1, 
				[ 
					\att, 0.1, \dec, 0.8, \rls, 1.25, 
					\bufnum, ~circir1, 
					\out, ~mainBus,
					\amp, 0.8, 
					\rate, 1 
				]
			);
						
		},
	'cir2' -> {
		
			~rit=Synth.head(~piges, \buf1, 
				[ 
					\att, 0.1, \dec, 0.8, \rls, 1.25, 
					\bufnum, ~circir1, 
					\out, ~mainBus,
					\amp, 0.8, 
					\rate, -1 
				]
			);
	
		},
	'cir3' -> {
		
			~rit=Synth.head(~piges, \buf1, 
				[ 
					\att, 0.1, \dec, 0.8, \rls, 1.05, 
					\bufnum, ~circir1, 
					\out, ~mainBus,
					\amp, 0.8, 
					\rate, -10 
				]
			);

		},
	'cir4' -> {

			~rit=Synth.head(~piges, \buf1, 
				[ 
					\att, 0.1, \dec, 0.2, \rls, 1.05, 
					\bufnum, ~circir1, 
					\out, ~mainBus,
					\amp, 0.8, 
					\rate, -20 
				]
			);

		}
).play;

//////////////////////////////////////
	}
	*unLoad { 
	}
}

/*


RthmReceive.load;

Pdef(\kick1def).play;
Pdef(\bassdef).play;
Pdef(\cir1def).play;

(
Pdef(\bassdef, Posc(
	\msg, Pseq([\bass1, \tek1, \nil, 	\bass1, \nil, \bass2, \nil, \nil, \bass2, \nil], inf),
	\dur, Pseq([1, 0.5,2], inf)/4
	)
);

Pdef(\kick1def, Posc(
	\msg, Pseq([\dum1, \tek1, \nil, 	\dum1, \nil, \dum1, \nil, \nil, \tek2, \nil], inf),
	\dur, Pseq([1, 0.5,2], inf)/4
	)
);

Pdef(\cir1def, Posc(
	\msg, Pseq([\cir2, \nil, \cir4, 	\nil, \nil, \nil, \nil, \nil, \cir1, \nil], inf),
	\dur, Pseq([1.5], inf)/4
	)
);
)

Pdef(\kick1def).stop;
Pdef(\cir1def).stop;


s.queryAllNodes;

*/