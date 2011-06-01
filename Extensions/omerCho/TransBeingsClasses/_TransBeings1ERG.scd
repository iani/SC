
(
        			~piges = Group.head(s);
				~effe = Group.tail(s);	
				TransBeings1SynthDefs.load;
				TransBeings1Buffers.load;
				TransBeings1Busses.load;
				TransBeings1Osc.load;

)
(

		SynthDef(\buf, { | out=0, bufnum = 0, gate = 1, rate = 1, startPos = 0, amp = 1.0,att = 0.001, sust = 1, rls = 3, pan = 0, loop = 1|
			var audio;
			rate = rate * BufRateScale.kr(bufnum);
			startPos = startPos * BufFrames.kr(bufnum);
			
			audio = BufRd.ar(1, bufnum, Phasor.ar(1, rate, startPos, BufFrames.ir(bufnum)), 0, 2);
			audio = EnvGen.ar(Env.asr(att, sust, rls), gate, doneAction: 2) * audio;
			audio = Pan2.ar(audio, pan, amp);
			OffsetOut.ar(out, audio);
		}).send(s);

)



(
		
		~fok1Synth = Synth.head(~piges, \buf,
			[\amp,		0.8,
			\dur,		30,
			\startPos,	1,
			\rate,		1,
			\sust,		10,
			\rls,		20,
			\pan,  		0.3,
			\out, 		~revBus,
			\bufnum,		~tbFok2
			] 
		);

		~rev = Synth.tail(~effe,"reverb", 
			[\in,  ~revBus, \out, ~limBus, \amp, 0.5
			]
		);
		~dly = Synth.tail(~effe,"delay", 
			[\in,  ~dlyBus, \out, ~limBus, \amp, 0.5
			]
		);
		~rlp = Synth.tail(~effe,"rlpf", 
			[\in,  ~rlpBus, \out, ~limBus, 
			\ffreq, 220, \rq, 1.5, \amp, 0.1
			]
		);
		~wah = Synth.tail(~effe,"wah", 
			[\in,  ~wahBus, \out, ~limBus
			]
		);
		~lim = Synth.tail(~effe, "limiter",
			[ \in ,~limBus, \out, 0,  
			\lvl, 0.6, \durt, 0.01
			]
		);	


)

(
		
		~fok1Synth.set(
			\amp,		0.1,
			\dur,		5,
			\startPos,	0,
			\rate,		0.1,
			\sust,		1,
			\rls,		1,
			\pan,  		0.3,
			\out, 		~revBus,
			\bufnum,		~tbCon1
		);

		~rev.set(
			\roomsize, 1,
			\revtime, 1,
			\damping, 0.8,
			\in,  ~revBus, 
			\out, ~dlyBus, 
			\amp, 0.5
		);
		~dly.set(
			\delay, 0.001,
			\decay, 0.1,
			\in,  ~dlyBus, 
			\out, ~rlpBus, 
			\amp, 0.5
		);
		~rlp.set(
			\in,  ~rlpBus, \out, ~wahBus, 
			\ffreq, 520, \rq, 1.5, \amp, 0.6
		);
		~wah.set(
			\amp, 2,
			\dist, 2.1,
			\in,  ~wahBus, 
			\out, ~limBus
		);
		~lim.set(
			\in ,~limBus, \out, 0,  
			\lvl, 0.6, \durt, 0.01
		);	


)

(
		Pdef( \bufok,
			Pbind(
				\instrument, \buf,
				\amp,		0.8,
				\dur,		30,
				\startPos,	1.abs,
				\rate,		1.sin,
				\sust,		10,
				\rls,		20,
				\pan,  		0.3,
				\group,		~piges,
				\out, 		~revBus,
				\bufnum,		~tbCon1
			)
		);
		Pdef( \bufok).play;

)



(

		Pdef( \bufok,
			Pbind(
				\instrument, \buf,
				\amp,		0.8,
				\dur,		1,
				\startPos,	0,
				\rate,		0.1.sin,
				\sust,		1,
				\rls,		6,
				\pan,  		0.3,
				\group,		~piges,
				\out, 		Pseq([~revBus, ~rlpBus, ~limBus ], inf),
				\bufnum,		~tbFok1
			)
		);

)

(

		Pdef( \bufok,
			Pbind(
				\instrument, \buf,
				\amp,		0.8,
				\dur,		Pseq([0.1, 1, 4, 0.5], inf),
				\startPos,	0,
				\rate,		0.5,
				\sust,		1,
				\rls,		6,
				\pan,  		0.3,
				\group,		~piges,
				\out, 		Pseq([~revBus, ~rlpBus, ~limBus, ~wahBus, ~dlyBus ], inf),
				\bufnum,		Pseq([~tbFok2, ~tbFok3, ~tbFok4, ~tbFok5 ], inf)
			)
		);

)

(

		Pdef( \bufok,
			Pbind(
				\instrument, \buf,
				\amp,		0.8,
				\dur,		Pseq([0.1, 1, 0.4, 0.5, 3], inf),
				\startPos,	0,
				\rate,		Pseq([1, 6, 0.4, 0.5, 10], inf),
				\sust,		1,
				\rls,		6,
				\pan,  		0.3,
				\group,		~piges,
				\out, 		Pseq([~revBus, ~rlpBus, ~limBus, ~wahBus, ~dlyBus ], inf),
				\bufnum,		Pseq([~tbFok2, ~tbFok3, ~tbFok4, ~tbFok5 ], inf)
			)
		);

)


(

		Pdef( \bufok,
			Pbind(
				\instrument, \buf,
				\amp,		0.8,
				\dur,		Pseq([0.1, 1, 0.4, 0.5, 3]*4, inf),
				\startPos,	0,
				\rate,		Pseq([1, 6.round, 0.4, 0.5, 10], inf),
				\sust,		1.abs,
				\rls,		Pseq([1, 6, 0.4, 0.5.sin, 10], inf),
				\pan,  		0.3,
				\group,		~piges,
				\out, 		Pseq([~revBus, ~rlpBus, ~limBus, ~wahBus, ~dlyBus ], inf),
				\bufnum,		Pseq([~tbFok2, ~tbFok3, ~tbFok4, ~tbFok5 ], inf)
			)
		);

)