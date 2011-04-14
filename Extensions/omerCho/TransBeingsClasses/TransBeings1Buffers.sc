

/*

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

			~fok1Synth = Synth.head(~piges, \buf,
				[\amp,		0.8,
				\dur,		30,
				\startPos,	0,
				\rate,		1,
				\sust,		10,
				\rls,		20,
				\pan,  		0.3,
				\out, 		~revBus,
				\bufnum,		~tbFok1
				] 
			);
)

(

		~rev = Synth.tail(~effe,"reverb", 
			[\in,  ~revBus, \out, ~dlyBus, \amp, 0.5
			]
		);
		~dly = Synth.tail(~effe,"delay", 
			[\in,  ~dlyBus, \out, ~rlpBus, \amp, 0.5
			]
		);
		~rlp = Synth.tail(~effe,"rlpf", 
			[\in,  ~rlpBus, \out, ~wahBus, 
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

Pdef(\buf).play
Pdef(\buf).stop


*/



TransBeings1Buffers {
	*load {
		var s;


		s = Server.default;
		
		~tbFok1 = Buffer.read(s, "sounds/_trans-beings/tb01.aif");
		~tbFok2 = Buffer.read(s, "sounds/_trans-beings/tb02.aif");
		~tbFok3 = Buffer.read(s, "sounds/_trans-beings/tb03.aif");
		~tbFok4 = Buffer.read(s, "sounds/_trans-beings/tb04.aif");
		~tbFok5 = Buffer.read(s, "sounds/_trans-beings/tb05.aif");
		~tbFok6 = Buffer.read(s, "sounds/_trans-beings/tb06.aif");
		
		~tbCon1 = Buffer.read(s, "sounds/_trans-beings/conetDot01.aif");
		~tbCon2 = Buffer.read(s, "sounds/_trans-beings/conetDot02.aif");
		~tbCon3 = Buffer.read(s, "sounds/_trans-beings/conetDot03.aif");
			
	}
	
	*unLoad { 
		~tbFok1.free;
		~tbFok2.free;
		~tbFok3.free;
		~tbFok4.free;
		~tbFok5.free;
		~tbFok6.free;
	}
	

}