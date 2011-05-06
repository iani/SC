
FunctionChain : List {
	var <>onEnd, <routine, <func;
	
	*new { | funcs, onEnd |
		^super.new.addAll(funcs).onEnd_(onEnd);	
	}
	
	start {
				postf("%: %\n", this, thisMethod.name);

		if (routine.notNil) { ^this };	// do not start if running
		postf("%: %\n NOW STARTING ROUTINE", this, thisMethod.name);
		routine = {
			while { (func = this.pop).notNil } { func.(this).postln; this.yield; };
//			onEnd.(this);				// had problems with this, disabling it
			routine = nil; 			// routine ended, can restart
		}.fork;
	}

	next {
		routine.next;	
	}
	
	isRunning { ^routine.notNil }
}

