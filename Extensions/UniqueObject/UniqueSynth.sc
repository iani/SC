UniqueSynth : UniqueObject {
	*mainKey { ^\synths }
	*removedMessage { ^\n_end }
	*new { | key, defName, args, target, addAction=\addToHead |
		^super.new(key, defName ?? { key.asSymbol }, args, target, addAction);
	}

	*makeKey { | key, defName, args, target, addAction=\addToHead |
		^(target.asTarget.server.asString ++ ":" ++ key).asSymbol;
	}
	
	init { | what, args, target, addAction ... moreArgs |
		// moreArgs are used by subclass UniquePlay
		target = target.asTarget;
		if (target.asTarget.server.serverRunning) {
			this.makeSynth(what, args, target, addAction, *moreArgs);
		}{
			target.server.waitForBoot({
				this.makeSynth(what, args, target, addAction, *moreArgs);
			});
		}
	}

	makeSynth { | defName, args, target, addAction |
		object = Synth(defName, args, target, addAction);
		this.registerOnEnd;
	}

	registerOnEnd {
		object.register;
		object addDependant: { | me, what |
			switch (what, \n_end, { 
				object = nil; 	// make this.free safe
				this.remove;
				object.release;	// clear dependants
			});
		};
	}

	synth { ^object }						// synonym
	onEnd { | func | this.onRemove(func) }	// synonym

	free { if (object.notNil) { object.free } }	// safe free: only runs if not already freed
	
	*onServer { | server |
		var regexp;
		regexp = format("^%:", server.asTarget.server);
		^Library.global.at(this.mainKey).values select: { | node |
			regexp.matchRegexp(node.key.asString);
		};	
	}
	
}

// Synonym - abbreviation for UniqueSynth :  
Usynth : UniqueSynth {}

// Experimental / Drafts 

UniquePlay : UniqueSynth {
//	*mainKey { ^\playFuncs }
	
	*new { | playFunc, target, outbus = 0, fadeTime = 0.02, addAction=\addToHead, args |
		^super.new(playFunc.hashKey, playFunc, args, target, addAction, outbus, fadeTime);
	}

	makeSynth { | playFunc, args, target, addAction = \addToHead, outbus = 0, fadeTime = 0.02 |
		object = playFunc.play(target, outbus, fadeTime, addAction, args);
		this.registerOnEnd;
	}
}

// Synonym - abbreviation for UniqueSynth :  
Uplay : UniquePlay {}

