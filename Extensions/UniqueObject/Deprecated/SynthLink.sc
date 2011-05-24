/*
Deprecated. 

Not necessary.


*/

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

+ Function {
	/* 	transform a function that creates a synth into a form that will accept 
		onEnd for use in Chain */
	s { 	| envir |
		^{ SynthLink(this, envir) };	
	}
}