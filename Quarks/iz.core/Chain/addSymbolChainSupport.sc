/* Add support for Code, UniqueObject and a number of its subclasses. */

/*
+ Object {
	fadeDefArgValue { ^this }
}
*/

+ Symbol {
//	fadeDefArgValue { ^this.buffer }
	
	// ========== FadeSynth support ===========
	asSynthDesc { ^SynthDescLib.global[this] }

	<> { | func | ^FadeSynth(this) <> func; }
	// trying a nicer looking symbol combination ?: 

	// occupied by JITlib: 	
//	ar { | in = 0, out = 0, fadeTime = 0.02, target, addAction = \addToHead | 
//		^FadeSynth(this, in, out, fadeTime, target, addAction);
//	}

	// (fadeTime_ occupied by JITlib)
	fadetime_ { | ft = 0.02 |
		^FadeSynth(this).fadeTime_(ft);
	}

//	fadeTime_ { | ft = 0.02 |
//		^FadeSynth(this).fadeTime_(ft);
//	}
	
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
 
	stop { | dur, server | ^this.release(dur, server) }
	start { | args, server |
		^FadeSynth(this, args, server).start;
	}
	// Note: avoid overwriting release method from Object ?? Do we want to have the method below?
	release { | dur, server | this.releaseSynth(dur, server) }
	releaseSynth { | dur, server | this.synth(server).releaseSynth(dur) }

	set { | ... args | this.synth.set(*args) }
	setn { | ... args | this.synth.setn(*args) }
	
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
		^this.bufr(target).play(func, target, outbus, fadeTime, addAction, args);
	}
	playBufd { | defName, args, target, addAction=\addToHead, name |
		/* Play a buffer by name with a synthdef */
		^(name ? this).playDef(defName,
			[\buf, this.bufr(target).object] ++ args, target, addAction);
	}

	buffer { | target | ^this.bufr(target).object; }

	bufnum { | target | ^this.buffer(target).bufnum; }

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
	
	asSynthDesc { | rates, prependArgs, outClass=\Out, fadeTime, name, server |
		var udef;
		udef = Udef.fromFunc(this, rates, prependArgs, outClass, fadeTime, name, server);
		^SynthDescLib.global[udef.name.asSymbol];
	}
	
	udef { | rates, prependArgs, outClass=\Out, fadeTime, name, server |
		^Udef.fromFunc(this, rates, prependArgs, outClass, fadeTime, name, server);
	}

	// Playing UGen functions as ResourceSynths, as in standard Function:play
	play { | target, outbus = 0, fadeTime = 0.02, addAction=\addToHead, args, name |
		// rewriting Function:play to work with ServerPrep
		target = target.asTarget;
		this.asSynthDef(
			fadeTime: fadeTime,
			name: name = name ?? { SystemSynthDefs.generateTempName };
		).addToServer(target.server);
		^name.asSymbol.play([\i_out, outbus, \out, outbus] ++ args, target, addAction);
	}

}
