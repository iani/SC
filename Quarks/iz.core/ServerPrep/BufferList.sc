BufferList { 
	var <>name;
	var <>buffers;

	*new { | name, buffers |
		^this.newCopyArgs(name, buffers);
	}

	asString { ^"BufferList(" ++ name ++ ")" }	
	printOn { | stream |
		if (stream.atLimit, { ^this });
		stream << ("BufferList(" ++ name ++ ")")
	}	
}