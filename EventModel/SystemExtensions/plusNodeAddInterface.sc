/*

From SynthModel. For use in other situations also.

Draft. Not tested.
*/
+ Node {
	addInterface { | stateChanger, paramChanger, keys |
		this.addStateInterface(stateChanger);
		this.addParamInterface(paramChanger, keys)
	}

	addStateInterface { | stateChanger |
		NodeWatcher.register(this);
		this.addNotifier(stateChanger, \free, { | notification |
			this.free;
			this.objectClosed;
			stateChanger.changed(\synthEnded, this);
		});
		this.addNotifier(stateChanger, \release, { | fadeTime, notification |
			this.release(fadeTime);
			this.objectClosed;
			stateChanger.changed(\synthEnded, this);
		});
		stateChanger.addNotifier(this, \n_end, { | notification |
			stateChanger.changed(\synthEnded, this);
			this.objectClosed;
		});
		this.addNotifier(stateChanger, \run, { | flag = true, notification |
			this.run(flag);
			stateChanger.changed(\run, this, flag);
		});

	}

	addParamInterface { | paramChanger, keys |
		keys do: { | key |
			this.addNotifier(paramChanger, key, { | value | this.set(key, value) });
		}
	}
}

