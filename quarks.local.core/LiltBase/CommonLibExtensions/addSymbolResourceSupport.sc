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
	
	// ========== FadeSynth support ===========
	-< { | func | ^FadeSynth(this) -< func; }


	// occupied by JITlib: 	
//	ar { | in = 0, out = 0, fadeTime = 0.02, target, addAction = \addToHead | 
//		^FadeSynth(this, in, out, fadeTime, target, addAction);
//	}

	// (fadeTime_ occupied by JITlib)
	fadetime_ { | ft = 0.02 |
		^FadeSynth(this).fadeTime_(ft);
	}

	fadeTime_ { | ft = 0.02 |
		^FadeSynth(this).fadeTime_(ft);
	}
	
	remove { | fadeTime | ^FadeSynth(this).releaseSynth(fadeTime).remove }
	
	// ====== Synth support ======

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

	free { | server | ^this.synth(server).free }
 
	releaseSynth { | dur, server | this.synth(server).releaseSynth(dur) }
	// Note: avoid overwriting release method from Object ?? Do we want to have the method below?
	release { | dur, server | this.releaseSynth(dur, server) }

	set { | ... args | this.synth.object.set(*args) }
	setn { | ... args | this.synth.object.setn(*args) }
	
	// ====== Bus support ======
	audio { | numChannels = 1, server |
		^BusResource.audio(this, numChannels, server)
	}

	control { | numChannels = 1, server |
		^BusResource.control(this, numChannels, server)
	}

	k { | numChannels = 1, server |
		^this.control(numChannels, server);		
	}

	index { | numChannels = 1, server |
		^this.control(numChannels, server).index;
	}

	map { | param, bus |
		this.synth.map(param, bus);	
	}

	mapDef { | param, defname, args |
		this.synth.mapDef(param, defname, args);	
	}

	mapFunc { | param, func, args |
		this.synth.mapFunc(param, func, args);	
	}
	
		// ====== Buffer support ======
	playBuf { | func, target, outbus = 0, fadeTime = 0.02, addAction=\addToHead, args |
		/* Play a buffer by name with a function */
		^BufferResource(this, target.asTarget.server).play(
			func, target, outbus, fadeTime, addAction, args
		);
	}
	playBufd { | defName, args, target, addAction=\addToHead, name |
		/* Play a buffer by name with a synthdef */
		^(name ? this).playDef(defName,
			[\buf, BufferResource(this, target.asTarget.server).object] ++ args,
			target, addAction
		);
	}

	buffer { | target | ^this.bufr(target).object; }

	bufr { | target |
		^switch (this,
			\default, { BufferResource.default(target.asTarget.server) },
			\current, { BufferResource.current(target.asTarget.server) },
			{ BufferResource(this, target.asTarget.server) }
		)
	}

	// ====== Window support ======
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
