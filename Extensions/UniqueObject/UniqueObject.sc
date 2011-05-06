UniqueObject {
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
		key = this.makeKey(key, makeFunc, *otherArgs); // server objects include the server in the key
		object = this.atKey(key);
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

	*atKey { | key | ^objects.atPath(key) }
	
	*at { | key ... args | ^this.atKey(this.makeKey(key, *args)) }

	*all { 	// return all objects of the same kind as the receiver 
			// "Kind" is defined by the main key method of the receiver.
		if (objects.atPath(this.mainKey).isNil) { ^[] };
		^objects.leaves(this.mainKey);
	}

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
		// remove all notification connections added with Object:addNotifier, addListener :
		this.objectClosed;
	}
	// ====== printing ======

	printOn { arg stream;
		stream << this.class.name << "(" <<* [key.last, object] <<")";
	}

	// ========================= STUFF MOVED TO OBJECT =======================
	// evaluate function when object is removed
	// Next 3 methods to be have been moved to Object
/*
	onClose { | action | this.onRemove(UniqueID.next, action) } 
	onRemove { | key, func | this.doOnceOn(this.removedMessage, key, func); }
	doOnceOn { | message, receiver, func |
		NotificationCenter.registerOneShot(this, message, receiver, { func.(this) });
	}
*/

	// ====== notifying objects ======

	// this has been added to Object:
//	notify { | message, what | NotificationCenter.notify(this, message, what); }
	
	// ====== listening to notifications from other objects ======

	// These have been moved to Object
/*	addNotifier { | notifier, message, action |
		NotificationCenter.register(notifier, message, this, action);
		this onClose: { NotificationCenter.unregister(notifier, message, this); };
	}
	
	addMessage { | notifier, message |
		this.addNotifier(notifier, message, { | value | this.perform(message, value) });
	}

	// ====== sending notifications to other objecs ======
	addListener { | listener, message, action |
		NotificationCenter.register(this, message, listener, action); 
		this onClose: { NotificationCenter.unregister(this, message, listener); };
	}
*/

}
