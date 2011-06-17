Resource {
	classvar <objects;
	var <key, <object;

	// ====== configuration ======

	*initClass { this.clear }
	*clear { // clear all objects, no notifications happen
		objects = MultiLevelIdentityDictionary.new;
	}	
	*mainKey { ^[this] /* ^[\objects] */ }
	removedMessage { ^this.class.removedMessage }
	*removedMessage { ^\objectRemoved }

	// ====== creating objects ======

	*new { | key, makeFunc ... otherArgs |
		var object;
		key = this.makeKey(key ? this.name, makeFunc, *otherArgs);		object = this.atKey(key);
		if (object.isNil) {
			object = this.newCopyArgs(key).init(makeFunc, *otherArgs);
			objects.putAtPath(key, object);
		};
		^object;
	}

	*makeKey { | key |
		/* server-related subclasses SynthResource etc. compose the key to include the server */
		^this.mainKey ++ key.asKey;
	}

	*atKey { | key | ^objects.atPath(key) }
	
	*at { | key ... args | ^this.atKey(this.makeKey(key, *args)) }

	*all { 	// return all objects of the same kind as the receiver 
			// "Kind" is defined by the main key method of the receiver.
		if (objects.atPath(this.mainKey).isNil) { ^[] };
		^objects.leaves(this.mainKey);
	}	// could also use objects.leaves.select({ | o | o.isKindOf ... }) ...

	init { | makeFunc | object = makeFunc.(this); }

	// ====== removing objects ======

	*removeAll { | keys |
		// remove all objects by performing their remove method
		// this sends out notifications.
		objects.leaves(keys) do: _.remove;
	}

	remove {
		var reallyRemoved;
		reallyRemoved = objects.removeEmptyAtPath(key);
		if (reallyRemoved.notNil) {
			this.notify(this.removedMessage, this);
		};
		// remove all notification connections added with Object:addNotifier, addListener :
		this.objectClosed;
	}
	// ====== printing ======

	printOn { arg stream;
		stream << this.class.name << "(" <<* [key.last, object] <<")";
	}
	
	// ====== chain and other process support =======
	stop { object.stop; this.remove; }
	free { object.free; this.remove; }
	release { | fadeout = 0.2 | object.release(fadeout); this.remove; }
}
