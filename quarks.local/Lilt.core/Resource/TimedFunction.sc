/*

Prevent a functinn from being evaluated twice within a certain time interval. 

Useful for preventing crashes when double clicking a window that is supposed to close on clid. 

(
fork {
	3 do: { 
		TimedFunction({ "ONCE ONLY".postln; }, 3);
		0.1.wait;
	}
};
)

vs: 

(
fork {
	3 do: { 
		TimedFunction({ "ONCE ONLY".postln; }, 0.001);
		0.1.wait;
	}
};
)


*/

TimedFunction : FunctionResource {
	
	*new { | func, duration = 0.5 | // just renaming the arguments for clarity
		^super.new(func, duration);	
	}

	init { | func, duration |	// changing init: 
		object = func;
		value = func.value;	// no extra arguments used for func evaluation
		{ this.remove }.defer(duration)	// remove after duration seconds
	}
}