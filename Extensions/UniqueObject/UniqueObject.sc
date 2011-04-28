UniqueObject {
	classvar <objects;
	var <key, <object;

	// ====== configuration ======

	*initClass { this.clear }
	*clear { // clear all objects, no notifications happen
		objects = MultiLevelIdentityDictionary.new;
	}	
	*mainKey { ^[\objects] }
	removedMessage { ^\objectRemoved }

	// ====== creating objects ======

	*new { | key, makeFunc ... otherArgs |
		var object;
		key = this.makeKey(key, makeFunc, *otherArgs); // server objects include the server in the key
		object = this.at(key);
		if (object.isNil) {
			object = this.newCopyArgs(key).init(makeFunc, *otherArgs);
			objects.putAtPath(key, object);
		};
		^object;
	}

	*makeKey { | key |
		/* server-related subclasses UniqueSynth etc. compose the key to include the server */
		^this.mainKey ++ key.asKey;
	}

	*at { | key | ^objects.atPath(key) }

	init { | makeFunc | object = makeFunc.value; }

	// ====== removing objects ======

	*removeAll { | keys |
		// remove all objects by performing their remove method
		// this sends out notifications. See Meta_UniqueObject:remove
		objects.leaves(keys) do: _.remove;
	}
	remove {
		var reallyRemoved;
		reallyRemoved = objects.removeEmptyAtPath(key);
		if (reallyRemoved.notNil) {
			this.notify(this.removedMessage, this);
		};
	}
	// evaluate function when object is removed
	onClose { | action | this.onRemove(UniqueID.next, action) } 
	onRemove { | key, func | this.doOnceOn(this.removedMessage, key, func); }
	doOnceOn { | message, receiver, func |
		NotificationCenter.registerOneShot(this, message, receiver, { func.(this) });
	}

	// ====== notifying objects ======

	notify { | message, what | NotificationCenter.notify(this, message, what); }
	
	addNotifier { | notifier, message, action |
		NotificationCenter.register(notifier, message, this, { | args | action.(this, args) });
		this onClose: { NotificationCenter.unregister(notifier, message, this); };
	}

	// ====== printing ======

	printOn { arg stream;
		stream << this.class.name << "(" <<* [key, object] <<")";
	}

}
