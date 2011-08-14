/* Add support for Code, Resource, ServerPrep */

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
	// ========== Ndef support ===========
	ndef { | object |
		// create / access an Ndef and play it, booting server if needed
		var ndef, server;
		ndef = Ndef(this, object);
		server = ndef.server;
		if (ndef.rate == \audio) {
			ServerPrep(server).addSynth({ ndef.play });
			if (server.serverRunning.not) { server.boot };
		};
		^ndef;
	}
}

+ Function {

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
