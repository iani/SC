
FunctionChain : List {
	var <>onEnd, <routine, <func;
	
	*new { | funcs, onEnd |
		^super.new.addAll(funcs).onEnd_(onEnd);	
	}
	
	start {
		if (routine.notNil) { ^this };	// do not start if running
		routine = {
			while { (func = this.pop).notNil } { func.(this); this.yield; };
			onEnd.(this);
			routine = nil; // prevent restarting
		}.fork;
	}

	next {
		routine.next;	
	}
}

