
/* 
Rewriting UniqueSynth to use ServerReady for booting synth and thereby ensure SynthDefs and Buffers are loaded
before it starts.
*/

AbstractUniqueServerObject : UniqueObject {
	var <server;
	*makeKey { | key, target |
		^this.mainKey ++ [target.asTarget.server, key.asKey];
	}

	*onServer { | server |
		var path;
		path = this.mainKey.add(server ? Server.default);
		if (objects.atPath(path).isNil) { ^[] };
		^objects.leaves(path);
	}	
}

UniqueSynth : AbstractUniqueServerObject {
	*mainKey { ^[UniqueSynth] } // subclasses store instances under UniqueSynth

	*new { | key, defName, args, target, addAction=\addToHead ... moreArgs |
		^super.new(key, target.asTarget, defName ?? { key.asSymbol }, args, addAction, *moreArgs);
	}

	init { | target, defName ... moreArgs |
		server = target.asTarget.server;
		ServerPrep(server).addSynth({ this.makeObject(target, defName, *moreArgs); });
		if (server.serverRunning.not) { server.boot };
	}

	makeObject { | target, defName, args, addAction ... otherArgs |
		this.prMakeObject(target, defName, args, addAction, *otherArgs);
		this.registerObject;
	}

	prMakeObject { | target, defName, args, addAction |
		object = Synth(defName, args, target, addAction);
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

	synthStarted {
		object.isPlaying = true; // set status to playing when missed because started on boot time
		NotificationCenter.notify(this, \synthStarted, this);
	}

	synth { ^object }						// synonym
	isPlaying { ^object.isPlaying; }

	// Synchronization with start / stop events: 
	onStart { | func |
		if (this.isPlaying) {
			func.(this);	
		}{
			NotificationCenter.registerOneShot(this, \synthStarted, UniqueID.next, { func.(this) });
		}
	}
	onEnd { | func | this.onClose(func) }	// synonym

	wait { | dtime = 0 |
	/* wait dtime seconds after start of synth or after receiving wait, whichever is earlier
	makes routines wait for server to boot before they start.
	Can only be called inside a routine.
	This includes running a code snippet by typing Command-Shift-x (see DocListWindow)
	*/
	// cannot use this.onStart({ dtime.wait }) because it calls .wait on a function, not a routine
		while { this.isPlaying.not } { 0.01.wait };
		dtime.wait;
	}	
	
	rsync { | func, clock |
		var routine;
		ServerPrep(server).addRoutine({
			routine = { func.(object, this) }.fork(clock ? AppClock);
		});
		this.onEnd({
			routine.stop;	
		});	
	}

	rsyncs { | func | this.rsync(func, SystemClock) }
	rsynca { | func | this.rsync(func, AppClock) }
	
	dur { | dtime = 1, fadeOut = 0.2, message |
		this.onStart({ { this.perform(message ? \release, fadeOut) }.defer(dtime ? inf) }) }

	free { if (this.isPlaying ) { object.free } }	// safe free: only runs if not already freed
	release { | dtime = 0.02 |
		if (this.isPlaying ) { object.release(dtime) } 
	}	
}
