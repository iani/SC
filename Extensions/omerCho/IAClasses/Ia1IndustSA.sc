/*

SA.load;
~part1 = SyncSender(Pbind.new);
~part1.start;
~part1.stop;
6
Ia1MainSA.unLoad;

*/

Ia1IndustSA {
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
					Synth.head(~piges, \playBuf, [ \bufnum, ~indbuf, 
						\out, ~limBus,
						\amp, 1, \rate, 1 ]);
				},
				16, {
					Synth.head(~piges, \playBuf, [ \bufnum, ~indbuf1, 
						\out, ~limBus,
						\amp, 0.4, \rate, 1 ]);
				},
				36, {
					Synth.head(~piges, \playBuf, [ \bufnum, ~indbuf, 
						\out, ~limBus,
						\amp, 0.6, \rate, 1 ]);
				},
				46, {
					Synth.head(~piges, \playBuf, [ \bufnum, ~indbuf2, 
						\out, ~limBus,
						\amp, 0.5, \rate, 1 ]);
				},
				66, { //OK
					Synth.head(~piges, \playBuf, [ \bufnum, ~indbufRev1, 
						\out, ~limBus,
						\amp, 1, \rate, 1 ]);
				},
				96, {
					Synth.head(~piges, \playBuf, [ \bufnum, ~indbufRev2, 
						\out, ~limBus,
						\amp, 1, \rate, 0.3 ]);
				},
				100, {
					Synth.head(~piges, \playBuf, [ \bufnum, ~indbufRev3, 
						\out, ~revBus,
						\amp, 1, \rate, 1 ]);
				},
				108, {
					Synth.head(~piges, \playBuf, [ \bufnum, ~indbufRev1, 
						\out, ~revBus,
						\amp, 1, \rate, 0.3 ]);
				},
				114, {
					Synth.head(~piges, \playBuf, [ \bufnum, ~indbufRev1, 
						\out, ~limBus,
						\amp, 0.8, \rate, 1 ]);
				},
				126, {
					Synth.head(~piges, \playBuf, [ \bufnum, ~indbufRev3, 
						\out, ~limBus,
						\amp, 1, \rate, 0.1 ]);
				},
				130, {
					Synth.head(~piges, \playBuf, [ \bufnum, ~indbufRev2, 
						\out, ~limBus,
						\amp, 0.8, \rate, 0.5 ]);
				},
				140, {
					Synth.head(~piges, \playBuf, [ \bufnum, ~indbufRev1, 
						\out, ~revBus,
						\amp, 0.8, \rate, 0.6 ]);
				},
				156, {
					Synth.head(~piges, \playBuf, [ \bufnum, ~indbufRev2, 
						\out, ~limBus,
						\amp, 0.8, \rate, 1 ]);
				},
				166, {
					Synth.head(~piges, \playBuf, [ \bufnum, ~indbufRev3, 
						\out, ~revBus,
						\amp, 0.8, \rate, 0.1 ]);
				},
				172, {
					Synth.head(~piges, \playBuf, [ \bufnum, ~indbufRev2, 
						\out, ~limBus,
						\amp, 1, \rate, 0.2 ]);
				},
				196, {
					Synth.head(~piges, \playBuf, [ \bufnum, ~indbufRev2, 
						\out, ~limBus,
						\amp, 0.8, \rate, 0.5 ]);
				},
				712, {
					Synth.head(~piges, \playBuf, [ \bufnum, ~indbufRev3, 
						\out, ~limBus,
						\amp, 1, \rate, 1 ]);
				},
				716, {
					Synth.head(~piges, \playBuf, [ \bufnum, ~indbuf, 
						\out, ~limBus,
						\amp, 1, \rate, 1 ]);
				}
			)  
		})
	}
	*unLoad{
	
	action.deactivate;
	
	}
	
}