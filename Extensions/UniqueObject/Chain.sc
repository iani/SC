

Chain {
	var <>pattern, <envir, <stream, <currentFunc, <link, onEnd;
	
	*new { | pattern, envir |
		^this.newCopyArgs(pattern, envir ? ()).init.start;
	}
	
	init { CmdPeriod.doOnceFirst(this); }

	cmdPeriod { this.stop; }	

	start { if (this.isRunning) { } { this.reset.next } } 
	
	isRunning { ^stream.notNil }
	
	reset {
		stream = pattern.asStream;			
	}

	next {
		currentFunc = stream.next;
		if (currentFunc.notNil) {
			link = envir.use({ currentFunc.(envir) }).onEnd({ this.next });
		}{
			stream = nil;
			envir use: { onEnd.(envir) };
		}
	}
	stop {	// does not release any current synth
		if (this.isRunning) { stream = nil; link.stopLink; }	}
	
	free {	// stop and free synth if appropriate
		link.postln.free;	// currently works only with UniqueSynth as link
		this.stop;
	}

	release { | fadeout = 0.02 |
		this.stop;
		link.releaseSynth(fadeout);	// currently works only with UniqueSynth as link
	}
	
	// Chaining chains
	onEnd { | argOnEnd | onEnd = argOnEnd }
	stopLink { this.stop }
}

// TODO: IMPORTANT: Test combining SynthLinks with ChainLinks in the same chain
SynthLink {
	var <>func, <envir, <synth, onEnd;

	*new { | func, envir |
		^this.newCopyArgs(func, envir ?? { () }).init;
	}
	
	// inherit environment from the Chain
	init {
		envir.parent = currentEnvironment;
		synth = envir use: func;
		synth.onStart({ synth.onEnd(onEnd) });
	}

	onEnd { | argEnd | 
		onEnd = argEnd;
	}

	stopLink {
		onEnd = nil;
		synth.removeAllNotifications;
		if (synth.isPlaying) { synth.free };
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
	
	stopLink {
		onEnd = nil;
		times = nil;
		func = nil;	
	}
}
/*
Pattern and stream support for looping Functions
*/

// Help for coding chains
+ Function {
	/* 	transform a function that creates a synth into a form that will accept 
		onEnd for use in Chain */
	s { 	| envir |
		^{ SynthLink(this, envir) };	
	}	
	
	/* transform a function into a function that makes an EventStream */
	chain { | timePat, envir, dtime = 0, clock, key = \dur | 
		^{ this.stream(
			{ key.stream(timePat.value ?? { Pn(0, 1) }) }, // still to be debugged
			envir.value, dtime.value, clock.value) 
		}
	}

	/* Schedule functions for repeated evaluation in time, within Chain or otherwise */
	// nicer to use this shorter word, but semantically acceptable?
	stream { | times, envir, dtime = 0, clock, onEnd | ^this.schedEnvir(times, envir, dtime, clock, onEnd) }

	schedEnvir { | times, envir, dtime = 0, clock, onEnd |
		^ChainLink(this, times, envir).sched(dtime, clock).onEnd(onEnd);
	}

	// Not used by Chain but useful as shortcut for scheduling
	sched { | dtime = 0, clock | (clock ? SystemClock).sched(dtime, this); }
}

+ Symbol {
	chain { | pattern, envir |
		^UniqueObject(this, { Chain(pattern, envir).onEnd({ UniqueObject(this).remove }) })
	}
	chainSeq { | ... links |
		^UniqueObject(this, { Chain(Pseq(links, 1)).onEnd({ UniqueObject(this).remove }) })
	}
}

+ CmdPeriod {
	*doOnceFirst { arg object;
		var f = { this.remove(f); object.doOnCmdPeriod  };
		objects.addFirst(f);
	}
}

