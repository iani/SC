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
	synth { | server | ^UniqueSynth.at(UniqueSynth.makeKey(this, target: server ? Server.default)) }
	
	// start a unique synth if not already running
	play { | args, target, addAction=\addToHead |
		^UniqueSynth(this, this, args, target, addAction);
	}
	
	free { | server | ^this.synth(server).free }
	wait { | dtime, server | ^this.synth(server).wait(dtime) }
	
	set { | ... args | this.synth.object.set(*args) }
	setn { | ... args | this.synth.object.setn(*args) }
	release { | server | this.synth(server).release }
	
	window { | makeFunc | ^UniqueWindow(this, makeFunc) }
	close { this.window.close }
}

+ Function {
	asKey { ^def.sourceCode.hash }

	// Function methods yet to test! : 
	doOnce { | ... args | ^UniqueFunction(this, *args) }
	// NOT YET TESTED
	remove {	^UniqueFunction.removeAtKey(this.asKey) }
	// NOT YET TESTED
	forkOnce { | clock ... args | ^UniqueRoutine(this, clock, *args); }
}

