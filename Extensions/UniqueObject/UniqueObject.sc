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
			this.notify(this.removedMessage);
		};
	}
	onRemove { | func | this.doOnceOn(this.removedMessage, func); }

	// ====== notifying objects ======

	doOnceOn { | message, func |
		NotificationCenter.registerOneShot(UniqueObject, message, this, { func.(this) });
	}

	notify { | message | NotificationCenter.notify(UniqueObject, message, this); }
	
	addNotifier { | notifier, message, action |
		postf("% adding notifier: %, %, %\n", this, notifier, message, action);
		NotificationCenter.register(notifier, message, this, { | args | action.(this, args) });
		this onRemove: { NotificationCenter.unregister(notifier, message, this); };
	}

	// ====== printing ======

	printOn { arg stream;
		stream << this.class.name << "(" <<* [key, object] <<")";
	}

}
