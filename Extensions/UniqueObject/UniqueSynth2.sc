
/* 
Rewriting UniqueSynth to use ServerReady for booting synth, and ensure SynthDefs and Buffers are loaded
before it starts.
*/

UniqueSynth2 : AbstractUniqueServerObject {
	*mainKey { ^[UniqueSynth] } // subclasses store instances under UniqueSynth

	*new { | key, defName, args, target, addAction=\addToHead ... moreArgs |
		^super.new(key, target.asTarget, defName ?? { key.asSymbol }, args, addAction, *moreArgs);
	}

	init { | target, defName ... moreArgs |
		ServerReady.addSynth(this, { this.makeObject(target, defName, *moreArgs); });
	}

	makeObject { | target, defName, args, addAction ... otherArgs |
		this.prMakeObject(target, defName, args, addAction, *otherArgs);
		this.registerObject;
	}

	prMakeObject { | target, defName, args, addAction |
		object = Synth(defName, args, target, addAction);
	}

	synthStarted {
		object.isPlaying = true; // set status to playing when missed because started on boot time
		NotificationCenter.notify(this, \synthStarted, this);
	}
 
	registerObject {
		object addDependant: { | me, what |
			switch (what, 
				\n_go, {
					this.synthStarted
				},
				\n_end, {
					this.remove;
					object.releaseDependants; // clean up synth's dependants
				}
			);
		};
		object.register;
	}

	synth { ^object }						// synonym
	isPlaying { ^object.isPlaying; }

	// Synchronization with start / stop events: 
	onStart { | func |
		NotificationCenter.registerOneShot(this, \synthStarted, UniqueID.next, func);
	}
	onEnd { | func | this.onClose(func) }	// synonym

	wait { | dtime = 0 |
	/* wait dtime seconds after start of synth or after receiving wait, whichever is earlier
	makes routines wait safely when starting a UniqueSynth with unbooted server.
	Can only be called inside a routine.
	This includes running a code snippet by typing Command-Shift-x (see DocListWindow)
	*/
	// cannot use this.onStart({ dtime.wait }) because it calls .wait on a function, not a routine
		while { this.isPlaying.not } { 0.01.wait };
		dtime.wait;
	}	
	
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
	
	dur { | dtime = 1, message | this.onStart({ { this.perform(message ? \release) }.defer(dtime ? inf) }) }

	free { if (this.isPlaying ) { object.free } }	// safe free: only runs if not already freed
	release { | dtime = 0.02 |
		if (this.isPlaying ) { object.set(\decay, dtime, \gate, dtime.neg) } 
	}
	
}
