
UniqueObject {
	
	var <key, <object;
	
	*mainKey { ^\objects }

	*new { | key, makeFunc |
		var object;
		object = Library.at(this.mainKey, key);
		if (object.isNil) {
			object = this.newCopyArgs(key).init(makeFunc);
			Library.put(this.mainKey, key, object);
		};
		^object.object;
	}
	
	init { | makeFunc | object = makeFunc.value }
	
	remove {
		^this.class.remove(key);	
	}
	
	*remove { | key |
		^Library.global.removeAt(this.mainKey, key);
	}
}

UniqueWindow : UniqueObject {
	*mainKey { ^\windows }
	*new { | key, windowFunc |
		^super.at(key, windowFunc).onClose = { | me |
			UniqueObject.remove(key);
			NotificationCenter.notify(me, \closed);
		};
	}
}

UniqueFunction : UniqueObject {
	var <hash;
	var <function;
	var <>results; 	// if this fun returns a large data collection, 
					// then set this to nil to free memory
	var <resetKeys;

	*mainKey { ^\functions }

	*new { | function, funcArgs ... resetKeys |
		var hash;
		^this.at(hash = this.getHashFor(function), {
			this.newCopyArgs(hash, function, function.(*funcArgs)).init(resetKeys);
		});
	}
	
	init { | argResetKeys |
		argResetKeys do: { | keys | this.addRemoveAction(*keys) }
	}
	
	*getHashFor { | function | ^function.def.code.hash; }
	
	addRemoveAction { | sender, key, action |
		resetKeys = resetKeys.add([sender, key, action]);
		NotificationCenter.register(sender, key, this, {
			this.remove;		
			action.(this);
		})	
	}

	remove {
		
	}

	*removeAtHash { | hash | Library.global.removeAt(this.mainKey, hash) }

	*remove { | function |
		this.removeAtHash(this.getHashFor(function));
	}
}

// Experimental / Drafts 

UniqueSynth : UniqueObject {
	var <key;
	var <synth;
	*mainKey { ^\synths }
	*new { | key, defName, args, target, addAction=\addToHead |
		^this.at(key, {
			this.newCopyArgs(key).init(defName, args, target, addAction=\addToHead);
		});
	}
	
	
	init { | defName, args, target, addAction=\addToHead |
/*		synth = Synth(defName ? key, args, target, addAction).register;
		synth addDependant: { | me, what |
			[me, what].postln;
			switch (what, 
				\n_end, {
					[me, "stopped"].postln;
					this.remove;
		}
*/
	}

	remove {
		this.class.remove(key);			
	}
}



// Synonym - abbreviation for UniqueSynth :  
Usynth : UniqueSynth {}

UniquePlay : UniqueSynth {
	*mainKey { ^\playFuncs }
	*new { | func ... args |
			
	}
}

// Synonym - abbreviation for UniqueSynth :  
Uplay : UniquePlay {}



/*
	Interpret a string only once (avoid doing the same initialization code marked by //:! 
	see DocListWindow ...
*/

UniqueCodeString : UniqueObject {
	classvar <>uniqueCodeStringKey = \uniqueCodeStrings;
	*new { | string |
		var hash;
		hash = string.hash;
		if (Library.at(uniqueCodeStringKey, hash).isNil) {
			string.interpret;
			Library.put(uniqueCodeStringKey, hash, string);
		}
	}
}


UniqueBuffer : UniqueObject {

	
}

