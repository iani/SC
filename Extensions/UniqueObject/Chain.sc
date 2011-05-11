

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
			envir.use({ current.value }).onEnd({ this.next });
		}{
			stream = nil;	
		}
	}
}

ChainLink {
	var <>func, <envir, onEnd;
	
	*new { | func, envir |
		^this.newCopyArgs(func, envir ?? { () });	
	}
	
	onEnd { | argEnd | onEnd = argEnd }
	
	sched { | dtime = 0, clock |
		(clock ? SystemClock).sched(dtime, { 
			envir use: {
				var dur;
				dur = func.(envir); // envir use: { func.(envir) };
				if (dur.isNil) { onEnd.(envir) }; // { envir use: { onEnd.(envir) } }
				dur;
			}
		});
	}
}
