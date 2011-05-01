/*
Add UniqueObject support plus support for a number of UniqueSupport subclasses.

*/

+ Magnitude { asKey { ^this } }

+ String { 
	asKey { ^this.hash }	
	fork { | clock | this.compile.fork(clock ? AppClock); }
	evalPost { | clock | this.eval.postln; }
	eval { | clock | ^this.interpret; }
	window { | makeFunc | ^this.asSymbol.window(makeFunc); }
	close { this.asSymbol.close }
}

+ Symbol {
	asKey { ^this }	
	// access a unique synth if present, without starting it
	synth { | server | ^UniqueSynth.at(this, server) }
	
	// start a unique synth if not already running
	play { | args, target, addAction=\addToHead |
		^UniqueSynth(this, this, args, target, addAction);
	}

	// start a unique synth, use symbol as key for storing and first arg as name of SynthDef
	playDef { | def, args, target, addAction=\addToHead |
		^UniqueSynth(this, def ? this, args, target, addAction);
	}

	playFunc { | func, target, outbus = 0, fadeTime = 0.02, addAction=\addToHead, args |
		^UniquePlay(func, target, outbus, fadeTime, addAction, args, this);
	}	

	playBuf { | func, target, outbus = 0, fadeTime = 0.02, addAction=\addToHead, args |
		^UniqueBuffer(func, target, outbus, fadeTime, addAction, args, this);
	}	

	buffer { | target |
		^switch (this, 
			\default, { UniqueBuffer.default(target.asTarget.server) },
			\current, { UniqueBuffer.current(target.asTarget.server) },
			{ UniqueBuffer(this, target.asTarget.server) }
		)
	}

	free { | server | ^this.synth(server).free }
	wait { | dtime, server | ^this.synth(server).wait(dtime) }
	
	set { | ... args | this.synth.object.set(*args) }
	setn { | ... args | this.synth.object.setn(*args) }
	release { | dur = 0.02, server | this.synth(server).release(dur) }
	
	window { | makeFunc | ^UniqueWindow(this, makeFunc) }
	close { this.window.close }
}

+ Function {
	asKey { ^def.sourceCode.hash }
	uplay { | target, outbus = 0, fadeTime = 0.02, addAction=\addToHead, args |
		^UniquePlay(this, target, outbus, fadeTime, addAction, args);
	}
	// create new key to force creation of multiple UniquePlay instances
	mplay { | target, outbus = 0, fadeTime = 0.02, addAction=\addToHead, args, 
			dur, fadeOut = 0.2, message = \release |
		^("play" ++ UniqueID.next).asSymbol.playFunc(this, target, outbus, fadeTime, addAction, args)
			.dur(dur, fadeOut, message);
	}


	doOnce { | ... args | ^UniqueFunction(this, *args) }
	doOnceIn { | duration = 0.5 |
		^TimedFunction(this, duration);
	}

	// Function methods yet to test! : 
	// NOT YET TESTED
	remove {	^UniqueFunction.removeAtKey(this.asKey) }
	// NOT YET TESTED
	forkOnce { | clock ... args | ^UniqueRoutine(this, clock, *args); }
}

