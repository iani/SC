
UniqueObject {
	
	var <key, <object;
	
	*mainKey { ^\objects }
	*removedMessage { ^\removed }

	*new { | key, makeFunc ... otherArgs |
		var object;
		key = this.makeKey(key, makeFunc, *otherArgs); // server objects include the server in the key
		object = this.getObject(key);
		if (object.isNil) {
			object = this.newCopyArgs(key).init(makeFunc, *otherArgs);
			Library.put(this.mainKey, key, object);
		};
		^object;
	}
	
	*makeKey { | key |
		/* server-related subclasses UniqueSynth etc. compose the key to include the server */
		^key.asSymbol;
	}
	
	*getObject { | key |
		^Library.global.at(this.mainKey, key);
	}

	init { | makeFunc | object = makeFunc.value }

	onRemove { | func |
		NotificationCenter.registerOneShot(key, this.class.removedMessage, this, func);
	}

	remove { ^this.class.remove(key); }
	
	*remove { | key |
		var removed, mainKey;
		removed = this.getObject(key);
		if (removed.notNil) { 
			NotificationCenter.notify(key, this.removedMessage, removed);
			^Library.global.removeAt(this.mainKey, key);
		};
		^nil;
	}
	
	*removeAll {
		// remove all objects by performing their remove method
		// this sends out notifications. See Meta_UniqueObject:remove
		Library.global.at(this.mainKey) do: _.remove;
	}

	*clear {
		// clear all objects stored under the mainKey
		Library.global.removeAt(this.mainKey);
	}

	printOn { arg stream;
		stream << this.class.name << "(" <<* [key, object] <<")";
	}

}

UniqueWindow : UniqueObject {
	*mainKey { ^\windows }
	*removedMessage { ^\closed }
	
	*new { | key, makeFunc ... onClose |
		/* this method just renames the nondescript argument "otherArgs" to "onClose", for clarity */
		^super.new(key, makeFunc, *onClose);
	}

	init { | windowFunc ... onClose |
		/*	
			note: onClose functionality can also be done with onRemove message, 
			but since it is used often, an additional simple mechanism is provided here
		*/
		super.init(windowFunc ?? { Window(key.asString).front });
		object.onClose = {
			this.remove(key);
			onClose do: _.(object, key);
		}
	}
	
	onClose { | func | this.onRemove(func) }	// synonym
	window { ^object } 					// synonym
}

