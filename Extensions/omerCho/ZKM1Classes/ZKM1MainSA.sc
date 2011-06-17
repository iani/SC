/*

ZKM1MainSA.load;
~part1 = SyncSender(Pbind.new);
~part1.start;
~part1.stop;

ZKM1MainSA.unLoad;

*/


/*


*/


ZKM1MainSA {
	classvar <action;
	*load {
		var s;
		
		s = Server.default;
		
		action =  Preceive(
	1 -> {
		Ia1Groups.load;
		Ia1SynthDefs.load;
		Ia1Buffers.load;
		Ia1Busses.load;
		Ia1Osc.load; },
	3 -> { ~xor1 = Synth.head( ~piges, \xorInt, [\out, [~revBus, ~rlpBus]]); },
	2 -> {			~rev = Synth.tail(~effe,"reverb", 
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
	4 -> { ~buf1 = Synth.head(~piges, \playBuf, [ \bufnum, ~indbuf, 
						\out, ~limBus,
						\amp, 1, \rate, 1 ]);
		}
).play;
	}
	*unLoad{
	
	action.deactivate;
	
	}
	
}