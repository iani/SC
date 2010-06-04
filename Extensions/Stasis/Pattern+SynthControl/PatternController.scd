/*

/// version without variable assignment, including the controllers as arguments. 

PatternController(
	// here the parameters of the pbind that you want to use initially!
	(
		melody: PatternProxy(Pseq((0..12), inf)),
		legato: PatternProxy(Pn(0.1, inf))
	),
	
	// Here the pattern that uses the above parameters
	{
		Pbind(\dur, 0.2, 
			\degree, ~melody,
			\legato, ~legato
		).play 
	},

	// here the controllers: 
	[13, 0, \toggle],
	[13, 1, { ~melody.source = Pwhite(0, 20, inf); }],
	[13, 2, { ~melody.source = Pseq((12..0), inf) }, { ~melody.source = Pseq((0..12), inf) }],
	[13, 3, { ~legato.source = Pwhite(0.1, 2, inf) }]	
);




p = PatternController(
	// here the parameters of the pbind that you want to use initially!
	(
		melody: PatternProxy(Pseq((0..12), inf)),
		legato: PatternProxy(Pn(0.1, inf))
	),
	
	// Here the pattern that uses the above parameters
	{
		Pbind(\dur, 0.2, 
			\degree, ~melody,
			\legato, ~legato
		).play 
	}
);

p.controllers = [
	[13, 0, \toggle],
	[13, 1, { ~melody.source = Pwhite(0, 20, inf); }],
	[13, 2, { ~melody.source = Pseq((12..0), inf) }, { ~melody.source = Pseq((0..12), inf) }],
	[13, 3, { ~legato.source = Pwhite(0.1, 2, inf) }]
];





*/


PatternController {
	var <event;
	var <patternFunc;
	var <controllers;
	var <streamPlayer;

	*new { | event, pattern ... controllers |
		^this.newCopyArgs(event, pattern).init(controllers);
	
	}
	
	init { | argControllers |
		this.controllers = argControllers;
	}
	
	controllers_ { | argControllers |
		controllers = argControllers collect: { | specs |
			if (specs[2] == \toggle) {
				this.makeToggleController(*specs);
			}{
				this.makeActionController(*specs);
			}
		};
	}

	makeToggleController { | argChan, argNum |
		CCResponder({ | src, chan, num, value |
			if (value == 0) {
				this.stop;
			}{
				this.start;
			}
		}, chan: argChan, num: argNum, install: true);
	}
	
	makeActionController { | argChan, argNum, action1, action2 |
		if (action2.isNil) {
			CCResponder({ | src, chan, num, value |
				event use: { action1.(value) };
			}, chan: argChan, num: argNum, install: true);
		}{
			CCResponder({ | src, chan, num, value |
				if (value == 0) {
					event use: { action1.(value); }
				}{
					event use: { action2.(value); }
				}
			}, chan: argChan, num: argNum, install: true);
		}
	}

	start {
		if (streamPlayer.isNil) {
			streamPlayer = event use: patternFunc;
		}
	}
	
	stop {
		if (streamPlayer.notNil) {
			streamPlayer.stop;
			streamPlayer = nil;
		}
	}
}