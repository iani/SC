UniqueSynth : UniqueObject {
	*mainKey { ^\synths }
	*removedMessage { ^\n_end }
	*makeKey { | key, defName, args, target, addAction=\addToHead |
		^(target.asTarget.server.asString ++ ":" ++ key).asSymbol;
	}

	*new { | key, defName, args, target, addAction=\addToHead |
		^super.new(key, defName ?? { key.asSymbol }, args, target, addAction);
	}

	
	init { | what, args, target, addAction ... moreArgs |
		// moreArgs are used by subclass UniquePlay
		target = target.asTarget;
		if (target.asTarget.server.serverRunning) {
			this.makeObject(what, args, target, addAction, *moreArgs);
		}{
			target.server.waitForBoot({
				0.1.wait;	// ensure that \n_go works for notification of start of synth
				this.makeObject(what, args, target, addAction, *moreArgs);
			});
		}
	}

	makeObject { | defName, args, target, addAction |
		object = Synth(defName, args, target, addAction);
		this.registerObject;
	}

	registerObject {
		object addDependant: { | me, what |
			switch (what, 
				\n_go, {
					NotificationCenter.notify(key, \n_go, this);
				},
				\n_end, { 
					object = nil; 	// make this.free safe
					this.remove;
					object.release;	// clear dependants
				}
			);
		};
		object.register;
	}

	synth { ^object }						// synonym

	// Synchronization with start / stop events: 
	onStart { | func |
		NotificationCenter.registerOneShot(key, \n_go, this, func);
	}
	onEnd { | func | this.onRemove(func) }	// synonym
	rsync { | func, clock |
		var routine;
		this.onStart({
			routine = { func.(object, this) }.fork(clock ? AppClock);
		});
		this.onEnd({
			routine.stop;	
		});	
	}

	rsyncs { | func | this.rsync(func, SystemClock) }
	rsynca { | func | this.rsync(func, AppClock) }

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

	makeObject { | playFunc, args, target, addAction = \addToHead, outbus = 0, fadeTime = 0.02 |
		object = playFunc.play(target, outbus, fadeTime, addAction, args);
		this.registerObject;
	}
}

// Synonym - abbreviation for UniqueSynth :  
Uplay : UniquePlay {}

