/* 
Better: The Stream stores itself in the current environment, that way each environment can have its own namespace, and name reuse becomes possible 
*/

/*
Pattern and stream support for looping Functions

UniqueStream to be phased out in favor of EnvirStream. 

*/

EventStream {
	var <key, <pattern, <stream;
	
	*new { | key, pattern |
		^currentEnvironment[key] ?? { this.newCopyArgs(key, pattern).init };
	}

	init {
		currentEnvironment[key] = this;
		this.reset;
	}
	
	next { 
		var next;
		next = stream.next;
		if (next.isNil) { this.remove };
		^next;
	}

	remove { currentEnvironment[key] = nil }

	reset { stream = pattern.asStream }
		
}

+ Function {
	sched { | dtime = 0, clock | (clock ? SystemClock).sched(dtime, this); }

	// nicer to use this shorter word, but semantically acceptable?
	stream { | envir, dtime = 0, clock | this.schedEnvir(envir, dtime, clock) }

	schedEnvir { | envir, dtime = 0, clock | 
		envir = envir ?? { Event.new };
		(clock ? SystemClock).sched(dtime, { envir use: this });
	}
	

}
