

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

/* // previous

    next {
        current = stream.next;
        if (current.notNil) {
            envir.use({ current.value }).onEnd({ this.next });
        }{
            stream = nil;   
        }
    }
   
*/
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
	init { envir.parent = currentEnvironment }
	
	onEnd { | argEnd | onEnd = argEnd }
	
	// how to introduce method 'once'???
	sched { | dtime = 0, clock |
		(clock ? SystemClock).sched(dtime, { 
			envir use: {
				var dur;
				dur = times.(envir); // envir.use({ times.(envir) }); // !!!!!
				if (dur.isNil) {
//					envir use: { onEnd.(envir) } // onEnd.(envir); // 
					onEnd.(envir);
				}{
//					envir use: { func.(envir) }; // 
					func.(envir);
				}; 
				dur;
			}
		});
	}


/* /// Not right!!!
    sched { | dtime = 0, clock |
        var timeStream;
        timeStream = envir.use({ times.(envir) }).asStream;
        (clock ? SystemClock).sched(dtime, { 
            envir use: {
                var dur;
                dur = timeStream.next.(envir); // envir.use({ times.(envir) }); // !!!!!
                if (dur.isNil) {
//                  envir use: { onEnd.(envir) } // onEnd.(envir); // 
                    onEnd.(envir);
                }{
//                  envir use: { func.(envir) }; // 
                    func.(envir);
                }; 
                dur;
            }
        });
    }
*/
}
/*
Pattern and stream support for looping Functions
*/

// Help for coding chains
+ Function {
	/* transform a function into a function that makes an EventStream */
	chain { | times, envir, dtime = 0, clock | 
		^{ this.stream(times, envir.value, dtime.value, clock.value) } 
	}

	// nicer to use this shorter word, but semantically acceptable?
	stream { | times, envir, dtime = 0, clock, onEnd | ^this.schedEnvir(times, envir, dtime, clock, onEnd) }

	schedEnvir { | times, envir, dtime = 0, clock, onEnd |
		^ChainLink(this, times, envir).sched(dtime, clock).onEnd(onEnd);
	}

	// Not used by Chain but useful as shortcut for scheduling
	sched { | dtime = 0, clock | (clock ? SystemClock).sched(dtime, this); }
}

// Note: The following do not simplify coding and are therefore removed: 
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