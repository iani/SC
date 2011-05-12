

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
	var <>func, <>times, <envir, onEnd;
	
	*new { | func, times, envir |
		^this.newCopyArgs(func, times, envir ?? { () });
	}
	
	onEnd { | argEnd | onEnd = argEnd }
	
	sched { | dtime = 0, clock |
		(clock ? SystemClock).sched(dtime, { 
			envir use: {
				var dur;
				dur = times.(envir);
				if (dur.isNil) {
					onEnd.(envir); // { envir use: { onEnd.(envir) } }
				}{
					func.(envir);	// envir use: { func.(envir) };
				}; 
				dur;
			}
		});
	}
}

/*
ChainLink { // has bug, plays one more time
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
*/
ChainLink2 { 
	var <>func, <>times, <envir, onEnd;
	
	*new { | func, times, envir |
		^this.newCopyArgs(func, times, envir ?? { () });
	}
	
	onEnd { | argEnd | onEnd = argEnd }
	
	sched { | dtime = 0, clock |
		(clock ? SystemClock).sched(dtime, { 
			envir use: {
				var dur;
				dur = times.(envir);
				if (dur.isNil) {
					onEnd.(envir); // { envir use: { onEnd.(envir) } }
				}{
					func.(envir);	// envir use: { func.(envir) };
				}; 
				dur;
			}
		});
	}
}



// Help for coding chains
+ Function {
	/* transform a function into a function that makes an EventStream */
//	chain { | envir, dtime = 0, clock | 
//		^{ this.stream(envir.value, dtime.value, clock.value) } 
//	}
	chain { | times, envir, dtime = 0, clock | 
		^{ this.stream2(times, envir.value, dtime.value, clock.value) } 
	}
	chain2 { | times, envir, dtime = 0, clock | 
		^{ this.stream2(times, envir.value, dtime.value, clock.value) } 
	}
}

// Note: The following actually do enable simpler coding and are therefore removed: 
/*
+ Function {
	/* transform a function into a function that makes a UniqueSynth  */
	chainSynth { | target, outbus = 0, fadeTime = 0.02, addAction=\addToHead, args, name, dur |
		var releaseTime;
		#dur, releaseTime = dur.asArray;
		dur = dur ? 1;
		releaseTime = releaseTime ? 0.02;
		^{ this.play(target.value, outbus.value, fadeTime.value, addAction.value, args.value, name.value)
			.dur(dur, releaseTime)
		}
	}
}

+ Symbol {
	/* transform a symbol into a a function that makes a UniqueSynth */
	chain { | args, target, addAction, dur | 
		var releaseTime;
		#dur, releaseTime = dur.asArray;
		dur = dur ? 1;
		^{ this.mplay(args.value, target.value, addAction.value).dur(dur, releaseTime) } 
	}
}
*/