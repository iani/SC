

Chain {
	var <>pattern, <envir, <stream, <current;
	
	*new { | pattern, envir |
		^this.newCopyArgs(pattern, envir ? ()).init.start;
	}
	
	init { CmdPeriod.add(this);	}

	cmdPeriod { 	// ensure stopping on Command Period
		stream = nil;
	}	

	start { if (this.isRunning) { } { this.reset.next } } 
	
	isRunning { ^stream.notNil }
	
	reset {
		stream = pattern.asStream;			
	}

	next {
		current = stream.next;
		if (current.notNil) {
			envir.use({ current.(envir) }).onEnd({ this.next });
		}{
			stream = nil;	
		}
	}
}

ChainLink { 
	var <>func, <>times, <envir, onEnd;
	
	*new { | func, times, envir |
		^this.newCopyArgs(func, times, envir ?? { () }).init;
	}
	
	// inherit environment from the Chain
	init {
		envir.parent = currentEnvironment;
	}
	
	onEnd { | argEnd | onEnd = argEnd }
	
	sched { | dtime = 0, clock |
		(clock ? SystemClock).sched(dtime, { 
			envir use: {
				var dur;
				times.postln;
				envir.postln;
				dur = times.(envir).next;
				if (dur.isNil) {
					onEnd.(envir);
				}{
					func.(envir);
				}; 
				dur;
			}
		});
	}

}
/*
Pattern and stream support for looping Functions
*/

// Help for coding chains
+ Function {
	/* transform a function into a function that makes an EventStream */
	pchain { | timePattern, envir, dtime = 0, clock, key = \dur |
		^this.chain({ key.stream(timePattern.value ?? { Pn(0, 1) }) }, envir, dtime, clock)
	}

	chain { | times, envir, dtime = 0, clock | 
		^{ this.stream(times, envir.value, dtime.value, clock.value) } 
	}

	// nicer to use this shorter word, but semantically acceptable?
	stream { | times, envir, dtime = 0, clock, onEnd | ^this.schedEnvir(times, envir, dtime, clock, onEnd) }

	once { | dur = 0, envir, dtime = 0, clock |
		^{ this.stream({ \dur.once(dur) }, envir.value, dtime.value, clock.value) } 
	}

	schedEnvir { | times, envir, dtime = 0, clock, onEnd |
		^ChainLink(this, times, envir).sched(dtime, clock).onEnd(onEnd);
	}

	// Not used by Chain but useful as shortcut for scheduling
	sched { | dtime = 0, clock | (clock ? SystemClock).sched(dtime, this); }
}

