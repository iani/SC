
UniqueObject {
	*at { | keys, makeFunc |
		var object;
		object = Library.at(*keys);
		if (object.isNil) { 
			object = makeFunc.value;
			Library.put(*(keys.asArray.copy add: object));
		};
		^object;
	}
	
	*remove { | keys |
		Library.global.removeAt(*keys.asArray);
	}
}

UniqueWindow : UniqueObject {
	*new { | keys, windowFunc |
		^super.at(keys, windowFunc).onClose = { | me |
			UniqueObject.remove(keys);
			NotificationCenter.notify(me, \closed);
		};
	}
}

/* 
UniqueFunction does not actually inherit anything from UniqueObject but it is made a subclass 
of it because it implements a functionality that is related to that of UniqueObject
*/

UniqueFunction : UniqueObject {
	classvar <>uniqueFuncKey = \uniqueFunctions;
	var <hash;
	var <function;
	var <>results; // results can be set to nil to get rid of large data collections
	var <removeActions;
	*new { | function ... removeActions |
		var uniqueFunc, hash;
		uniqueFunc = Library.at(uniqueFuncKey, hash = this.getHashFor(function));
		if (uniqueFunc.isNil) {
			Library.put(
				uniqueFuncKey, 
				hash, 
				uniqueFunc = this.newCopyArgs(hash, function, function.value).init(removeActions)
			);
		};
		^uniqueFunc;	
	}
	
	*getHashFor { | function | ^function.def.code.hash }
	
	init { | argRemoveActions |
		argRemoveActions do: this.addRemoveAction(*_);
	}
	
	addRemoveAction { | sender, key, action |
		removeActions = removeActions.add([sender, key, action]);
		NotificationCenter.register(sender, key, this, {
			this.class.removeAtHash(hash);		
			action.(this);
		})	
	}


	*removeAtHash { | hash | Library.global.removeAt(uniqueFuncKey, hash) }

	*remove { | function |
		this.removeAtHash(this.getHashFor(function));
	}
}

// Experimental: 

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

