/*

Ia1FxSA.load;
~part1 = SyncSender(Pbind.new);
~part1.start;
~part1.stop;
6
Ia1MainSA.unLoad;

*/

Ia1FxSA {
	classvar <action;
	*load {
		var s;
		
		s = Server.default;
		
		action =  SyncAction(\beats, { | beat ... otherStuff |
		//beat.postln;
		
			switch (beat,
				0, {
				},
				1, {
					~rev = Synth.tail(~effe,"reverb", 
						[\in,  ~revBus, \out, ~limBus, \amp, 0.5
						]
					);
					~dly = Synth.tail(~effe,"delay", 
						[\in,  ~dlyBus, \out, ~limBus, \amp, 0.8
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
				},
				176, {
					~wah.set(\rq, 0.45, \dist, 2.95, \mfreq, 100,  \cfreq, ~c3, \amp, 1.5);
					~rlp.set(\ffreq, ~d2, \rq, 2.165, \amp, 1.9);
				},
				254, {
				},
				588, {
				},
				608, {
				},
				716, {
				}
			)  
		})
	}
	*unLoad{
	
	action.deactivate;
	
	}
	
}