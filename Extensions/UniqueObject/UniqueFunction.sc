/*
	Interpret a string only once (avoid doing the same initialization code marked by //:! 
	see DocListWindow ...

*/

// Somewhat similar to Thunk, but not the same

UniqueFunction : UniqueObject {
	var <value;

	*mainKey { ^\functions }
	*removedMessage { ^\reset }

	*new { | function ... args |
		^super.new(function.hashKey, function, *args)
	}

	init { | func ... args |
		object = func;
		value = func.(*args);
	}

	function { ^object }

}

// DRAFT!!!!!
UniqueCodeString : UniqueObject {
	classvar <>uniqueCodeStringKey = \uniqueCodeStrings;
	*new { | string |
		var hash;
		hash = string.hash;
		if (Library.at(uniqueCodeStringKey, hash).isNil) {
			string.fork;
			Library.put(uniqueCodeStringKey, hash, string);
		}
	}
}
