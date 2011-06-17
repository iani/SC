
/* 
Use ServerPrep for booting synth and thereby ensure SynthDefs and Buffers are loaded
before it starts.
*/

AbstractServerResource : Resource {
	var <target, <server;
	*makeKey { | key, target |
		^this.mainKey ++ [target.asTarget.server, key.asKey];
	}
	
	init { | argTarget |
		this.initTarget(argTarget);
	}
	
	initTarget { | argTarget |
		target = argTarget.asTarget;
		server = target.server;
	}

	*onServer { | server |
		var path;
		path = this.mainKey.add(server ? Server.default);
		if (objects.atPath(path).isNil) { ^[] };
		^objects.leaves(path);
	}	
}

SynthResource : AbstractServerResource {
	*mainKey { ^[SynthResource] } // subclasses store instances under SynthResource

	*new { | key, defName, args, target, addAction=\addToHead |
		^super.new(key, target.asTarget, defName ?? { key.asSymbol }, args, addAction);
	}

	init { | argTarget, defName, args, addAction |
		super.initTarget(argTarget);
		this.makeSynth(defName, args, addAction);
	}

	makeSynth { | defName, args, addAction |
		ServerPrep(server).addSynth({ this.makeObject(target, defName, args, addAction); });
		if (server.serverRunning.not) { server.boot };
	}

	makeObject { | target, defName, args, addAction |
		this.prMakeObject(target, defName, args, addAction);
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
					this.synthEnded
				}
			);
		};
		object.register;
	}

	synthStarted {
		object.isPlaying = true; // set status to playing when missed because started on boot time
		NotificationCenter.notify(this, \synthStarted, this);
	}
	
	synthEnded {
		this.remove;
		object.releaseDependants; // clean up synth's dependants
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
	
	dur { | dtime = 1, fadeOut = 0.2, message |
		this.onStart({
			{ this.perform(message ? \releaseSynth, fadeOut); nil; }.sched(dtime);
		});
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

	sched { | func, dtime = 0, clock |
		clock = clock ? AppClock;
		this.onStart({
			{ if (this.isPlaying) { func.(object, this) }{ nil } }.sched(dtime, clock);
		})
	}
	scheds { | func, dtime = 0 | this.sched(func, dtime, SystemClock) }
	scheda { | func, dtime = 0 | this.sched(func, dtime, AppClock) }

	stream { | func, times, envir, dtime = 0, clock, onEnd, key = \dur |
		clock = clock ? AppClock;
		this.onStart({
			{ | envir |
				if (this.isPlaying) { envir use: { func.(object, this, envir) } } { nil } 
			}.schedEnvir({ key.stream(times.value ?? { Pn(0, 1) }) }, envir, dtime, clock, onEnd);
		});
	}

	streams { | func, times, envir, dtime = 0, onEnd | 
		this.stream(func, times, envir, dtime, SystemClock, onEnd)
	}
	streama { | func, times, envir, dtime = 0, onEnd |
		this.stream(func, times, envir, dtime, AppClock, onEnd)
	}
	releaseSynth { | dtime |
		// Use  name releaseSynth in order not to mofify release method inherited from Object
		if (this.isPlaying ) { object.release(dtime ? 0.02) } 
	}

	set { | ... args | if (this.isPlaying) { object.set(*args) } }
	map { | param, index |
		/* map parameter to bus. Make sure that the synth has started, otherwise map won't work */
		index = index ?? { this.getParamBus(param).index }; 
		this.onStart({ object.map(param, index) });
	}

	getParamBus { | param |
		^BusResource.control(this.key.last ++ '_' ++ param, 1, server);
	}
/*
	mapDef { | param, defname, args |
		var index;
		index = this.getParamBus(param).index;
		this.map(param, index);
		;	
	}
*/	

	free { if (this.isPlaying ) { object.free } }	// safe free: only runs if not already freed
	
	// Chain support
	stopLink { /* this.free; */ }

/*	stopLink {
	if (this.isPlaying) {
//			this.removeAllNotifications;
//			object.releaseDependants;
			this.free;	
		};
	}
*/
}
