UniqueObject {
	classvar <objects;
	var <key, <object;

	*initClass { StartUp.add(this); }
	
	*doOnStartUp { objects = MultiLevelIdentityDictionary.new; }

	*clear { // clear all objects
		objects = MultiLevelIdentityDictionaty.new;
	}	
	
	*mainKey { ^[\objects] }
	removedMessage { ^\objectRemoved }

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

	*at { | key | ^objects.atPath(key) }

	*makeKey { | key |
		/* server-related subclasses UniqueSynth etc. compose the key to include the server */
		^this.mainKey ++ key.asKey;
	}

	init { | makeFunc | object = makeFunc.value; }

	remove {
		var reallyRemoved;
		reallyRemoved = objects.removeEmptyAtPath(key);
		if (reallyRemoved.notNil) {
			this.notify(this.removedMessage);
		};
	}
	
	notify { | message | NotificationCenter.notify(UniqueObject, message, this); }

	onRemove { | func | this.doOnceOn(this.removedMessage, func); }
	
	doOnceOn { | message, func |
		NotificationCenter.registerOneShot(UniqueObject, message, this, { func.(this) });
	}
	
	addNotifier { | notifier, message, action |
		NotificationCenter.register(notifier, message, this, { | args | action.(this, args) });
		this onRemove: { NotificationCenter.unregister(notifier, message, this); };
	}
	
	*removeAll { | keys |
		// remove all objects by performing their remove method
		// this sends out notifications. See Meta_UniqueObject:remove
		objects.leaves(keys) do: _.remove;
	}

	printOn { arg stream;
		stream << this.class.name << "(" <<* [key, object] <<")";
	}

}
