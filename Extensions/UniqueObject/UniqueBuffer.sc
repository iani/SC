
// DRAFT!!!

UniqueBuffer : UniqueObject {
	*mainKey { ^\buffers }
	*removedMessage { ^\b_free }

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
	
}

Ubuffer : UniqueBuffer {} // synonym for UniqueBuffer