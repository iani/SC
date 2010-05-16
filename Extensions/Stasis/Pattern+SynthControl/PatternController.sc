/* 

PatternController is for controlling patterns via MIDI Control Change messages

SynthController is for controlling synths via MIDI Control Change messages

Version 1.0 (IZ+OH+DT Wednesday, May 12, 2010)

See examples in PatternController.html help file. 

*/


AbstractProcessController { 
	/* abstract class for controlling processes that are either Synths or Patterns. 
		Its subclasses do the job. They are: PatternController and SynthController
		This abstract class only codes the common mechanism for creating controllers. 
	*/

	var <event;	// can store patterns for Pbind or other defaults for Synth, for example specs for midi mapping
	var <processFunc; 	// the function that creates the process: creates Pbind and plays it or Synth or ...
	var <controllers;	// all the MIDI CC controllers
	var <process;		// the process that is playing: EventStreamPlayer or Synth or Group ...
	

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
		^CCResponder({ | src, chan, num, value |
			if (value == 0) {
				this.stop;
			}{
				this.start;
			}
		}, chan: argChan, num: argNum, install: true);
	}

	makeActionController { | argChan, argNum, action1, action2 |
		^if (action2.isNil) {
			CCResponder({ | src, chan, num, value |
				event use: { action1.(value, process) };
			}, chan: argChan, num: argNum, install: true);
		}{
			CCResponder({ | src, chan, num, value |
				if (value == 0) {
					event use: { action1.(value, process); }
				}{
					event use: { action2.(value, process); }
				}
			}, chan: argChan, num: argNum, install: true);
		}
	}
	
	start {
		if (process.isNil) {
			process = event use: processFunc;
		}
	}
	
	activate {
		controllers do: _.init(true); // { | element | element.install(true) }
	}

	deactivate {
		controllers do: _.remove;
	}
}

PatternController : AbstractProcessController {
	stop {
		if (process.notNil) {
			process.stop;
			process = nil;
		}
	}
}

SynthController : AbstractProcessController {
	var <>stopFunc;

	init { | argControllers |
//		[thisMethod.name, "initializing"].postln; 
		super.init(argControllers);
//		[thisMethod.name, "initializing2"].postln;
		if (stopFunc.isNil) { this.makeStopFunc }; 
//		stopFunc.postln;
	}

	makeStopFunc {
		stopFunc = { | argProcess | argProcess.postln.free; }
	}

	makeReleaseFunc {
		stopFunc = { | argProcess | argProcess.release; }
	}

	stop {
		if (process.notNil) {
			stopFunc.(process);
			process = nil;
		}
	}
}



