/*

Will be replaced by EventStream.

*/

UniqueStream : UniqueObject {
	var <pattern;
	init { | argPattern |
		pattern = argPattern;
		this.reset;
	}
	
	next { 
		var next;
		next = object.next;
		if (next.isNil) { this.remove };
		^next;
	}
	reset { object = pattern.asStream }
}

