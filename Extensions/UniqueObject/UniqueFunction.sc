/*
	Interpret a string only once (avoid doing the same initialization code marked by //:! 
	see DocListWindow ...
*/


UniqueFunction : UniqueObject {
	var <result;

	*mainKey { ^\functions }
	*removedMessage { ^\reset }

	*new { | function ... args |
		^super.new(function.hashKey, function, *args)
	}

	init { | func ... args |
		object = func;
		result = func.(*args);
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
			string.interpret;
			Library.put(uniqueCodeStringKey, hash, string);
		}
	}
}
