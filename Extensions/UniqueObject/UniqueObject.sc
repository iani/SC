/*
MultiLevelIdentityDictionary
	atPath { arg path; }
	putAtPath { arg path, val; }
	removeAtPath { arg path; }
	removeEmptyAtPath { arg path; }
	leaves { arg startAt; }
	leafDo { arg func; }
	leafDoFrom { arg folderpath, func; }
*/

UniqueObject {
	classvar <objects;
	var <key, <object;
	*iniClass { this.clear; }
	
	
	*mainKey { ^[\objects] }
	*removedMessage { ^\removed }

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

	onRemove { | func |
		NotificationCenter.registerOneShot(key, this.class.removedMessage, this, func);
	}

	remove { ^this.class.remove(key); }
	
	*remove { | key |
		var removed;
		removed = this.at(key);
		if (removed.notNil) { 
			NotificationCenter.notify(key, this.removedMessage, removed);
			^Library.global.removeAt(this.mainKey, key);
		};
		^nil;
	}
	
	*removeAll {
		// remove all objects by performing their remove method
		// this sends out notifications. See Meta_UniqueObject:remove
		objects.at(this.mainKey) do: _.remove;
	}

	*clear { // clear all objects
		objects = MultiLevelIdentityDictionaty.new;
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

