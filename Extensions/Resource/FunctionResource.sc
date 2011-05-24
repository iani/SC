/*
	Interpret a string only once (avoid doing the same initialization code marked by //:! 
	see DocListWindow ...

*/

// Somewhat similar to Thunk, but not the same

FunctionResource : Resource {
	var <value;

//	*mainKey { ^[\functions] }
//	*removedMessage { ^\funcReset }

	*new { | func ... args |
		^super.new(func.asKey, func, *args);	
	}

	init { | func ... args |
		object = func;
		value = func.(*args);
	}

	function { ^object }
}

RoutineResource : Resource {

//	*mainKey { ^[\routines] }
//	*removedMessage { ^\routineReset }

	*new { | func, clock ... args |
		^super.new(func.asKey, func, clock);	
	}

	init { | func, clock ... args |
		object = { 
			func.(*args);
			this.remove;	
		}.fork(clock);
		CmdPeriod.add(this);
	}

	doOnCmdPeriod { "cmd period received by ".post; this.postln; }

	routine { ^object }

}



// DRAFT!!!!!
CodeStringResource : Resource {
	classvar <>uniqueCodeStringKey = \uniqueCodeStrings;
	*new { | string |
		var hash;
		hash = string.hash;
		if (Library.at(uniqueCodeStringKey, hash).isNil) {
			string.fork;
			Library.put(uniqueCodeStringKey, hash, string);
		}
	}
}
