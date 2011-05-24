/* Add support for Code, UniqueObject and a number of its subclasses. */

+ Server { asKey { ^this } }

+ Magnitude { asKey { ^this } }

+ String { 
	asKey { ^this.hash }
	fork { | clock | Code.fork(this, clock); }
	evalPost { | clock | this.eval.postln; }
	eval { | clock | ^this.interpret; }
	window { | makeFunc | ^this.asSymbol.window(makeFunc); }
	close { this.asSymbol.close }
}

+ Symbol {
	asKey { ^this }
	// access a unique synth if present, without starting it
	synth { | server | ^SynthResource.at(this, server) }
	
	// Start a unique synth if not already running, using self as SynthDef name
	play { | args, target, addAction = \addToHead |
		^this.playDef(this, args, target, addAction);
	}

	// Start a synth using self as DefName, but on a unique id
	// This allows starting a new instance before the old one has stopped
	mplay { | args, target, addAction = \addToHead |
		^SynthResource(UniqueID.next, this, args, target, addAction);
	}

	playFunc { | func, target, outbus = 0, fadeTime = 0.02, addAction=\addToHead, args |
		^func.play(target, outbus, fadeTime, addAction, args, this);
	}	

	// start a unique synth, use symbol as key for storing and first arg as name of SynthDef
	playDef { | def, args, target, addAction=\addToHead |
		^SynthResource(this, def ? this, args, target, addAction);
	}

	// to be tested!
	playBuf { | func, target, outbus = 0, fadeTime = 0.02, addAction=\addToHead, args |
		^BufferResource(func, target, outbus, fadeTime, addAction, args, this);
	}	

	buffer { | target |
		^switch (this, 
			\default, { BufferResource.default(target.asTarget.server) },
			\current, { BufferResource.current(target.asTarget.server) },
			{ BufferResource(this, target.asTarget.server) }
		)
	}

	free { | server | ^this.synth(server).free }
	wait { | dtime, server | ^this.synth(server).wait(dtime) }

	set { | ... args | this.synth.object.set(*args) }
	setn { | ... args | this.synth.object.setn(*args) }
	// Note: avoid overwriting release method from Object
	releaseSynth { | dur = 0.02, server | this.synth(server).releaseSynth(dur) }
	
	window { | makeFunc | ^WindowResource(this, makeFunc) }
	close { this.window.close }
}

+ Function {
	// Playing UGen functions as synths, as in standard Function:play
	play { | target, outbus = 0, fadeTime = 0.02, addAction=\addToHead, args, name |
		// rewriting Function:play to work with ServerPrep
		target = target.asTarget;
		this.asSynthDef(
			fadeTime: fadeTime,
			name: name = name ?? { SystemSynthDefs.generateTempName };
		).addToServer(target.server);
		^name.asSymbol.play([\i_out, outbus, \out, outbus] ++ args, target, addAction);
	}

	asKey { ^def.sourceCode.hash }
	doOnce { | ... args | ^UniqueFunction(this, *args) }
	doOnceIn { | duration = 0.5 |
		^TimedFunction(this, duration);
	}

	// Function methods yet to test! : 
	// NOT YET TESTED
	remove {	^FunctionResource.removeAtKey(this.asKey) }
	// NOT YET TESTED
	forkOnce { | clock ... args | ^RoutineResource(this, clock, *args); }
}

+ SynthDef {
	addToServer { | server | ServerPrep(server.asTarget.server).addDef(this); }
	sendTo { | server | this.send(server.asTarget.server); }
}
