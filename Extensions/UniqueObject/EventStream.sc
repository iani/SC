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

	reset { stream = pattern.asStream }

	remove { currentEnvironment[key] = nil }		
}

/*
Pattern and stream support for looping Functions
*/

+ Function {
	sched { | dtime = 0, clock | (clock ? SystemClock).sched(dtime, this); }

	// nicer to use this shorter word, but semantically acceptable?
	stream { | envir, dtime = 0, clock | this.schedEnvir(envir, dtime, clock) }

	schedEnvir { | envir, dtime = 0, clock | 
		envir = envir ?? { Event.new };
		(clock ? SystemClock).sched(dtime, { envir use: this });
	}
}

+ Symbol {
	stream { | pattern | ^EventStream(this, pattern).next }
	replaceStream { | pattern | ^EventStream(this).init(pattern) } // next not called!
	resetStream { ^EventStream(this).reset } 	// next not called: design choice.
	removeStream { ^EventStream(this).remove }

	// Shortcuts for creating all kinds of patterns can be added.
	pseq { | array, repeats = 1 | ^this.stream(Pseq(array, repeats)); }
	pseq1 { | ... elements | ^this.pseq(elements, 1); }
	pser { | array, repeats = 1 | ^this.stream(Pser(array, repeats)); }
	pser1 { | ... elements | ^this.stream(Pser(elements, inf)); }
	pwhite { | lo, hi, length = inf | ^this.stream(Pwhite(lo, hi, length)) }
	prand { | array, repeats = inf | ^this.stream(Prand(array, repeats)) }
	prand1 { | ... elements | ^this.stream(Prand(elements, inf)) }
	pn { | pattern, repeats = 1 | ^this.stream(Pn(pattern, repeats)) }
	pfuncn { | func, repeats = 1 | ^this.stream(Pfuncn(func, repeats)) }
}

