/*

NcircFXSynths.load;

*/

NcircFXSynths {
	classvar <action;
	*load {

/*
		~lim = Synth.after(~piges, "limiter",
			[ \in ,~limBus, \out, 0,  
			\lvl, 0.9, \durt, 0.01
			]
		);

		~main = Synth.tail(~piges, "mainVolCtrl",
			[ 
			\in ,~mainBus, 
			[\out1, \out2, \out3, \out4, \out5, \out6],  [~limBus, ~revBus, ~dlyBus, ~rlpBus, ~wahBus, ~flowBus]
			]
		);


		~rev = Synth.after(~piges,"reverb", 
			[
			\in,  ~revBus, \out, 0, 
			\amp, 0.5
			]
		);
		~dly = Synth.after(~piges,"delay", 
			[\in,  ~dlyBus, \out, 0, 
			\amp, 0.8
			]
		);
		~rlp = Synth.after(~piges,"rlpf", 
			[\in,  ~rlpBus, \out, 0, 
			\ffreq, 220, \rq, 1.5, \amp, 0.1
			]
		);
		~wah = Synth.after(~piges,"wah", 
			[\in,  ~wahBus, \out, 0]
		);


		~flow = Synth.after(~piges,"flower", 
			[ \in, ~flowBus, \out, 0 ]
		);

*/

	
	}
	
	*unLoad{
	
		~rev.free;
		~dly.free;
		~rlp.free;
		~wah.free;
		~lim.free;

		~lvlASynth.free;

	
	}
	
}