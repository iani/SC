
CachedStream {
	var <pattern, <stream, <value;
	
	*new { | pattern |
		^this.newCopyArgs(pattern).init;
	}

	init { this.reset; }

	reset { stream = pattern.asStream }
	
	next {
		value = stream.next;
		^value;
	}
	
	pattern_ { | argPattern |
		pattern = argPattern;
		this.reset;	
	}	
}

Counter : CachedStream {
	*new { | start = 0, step = 1, repeats = inf |
		^super.new(Pseries(start, step, repeats));
	}
}