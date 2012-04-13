
+ Function {
	repeat { | repeats = 1, rate = 0.1, globalCount |
		// Shorthand for timed repeating of a function. 
		// Can only be used inside a routine.
		var count;
		count =  CachedStream(Pseries(0, 1, inf));
		if (repeats isKindOf: Integer) { repeats = Pn(rate, repeats) };
		repeats = repeats.asStream;
		while { (rate = repeats.next).notNil } {
			this.(count.next, globalCount.next, rate, count, globalCount);
			rate.wait;
		};
	}
}

/*

//:--- 
{
	var counter;
	counter = Counter.new;
	{ | count, globalCount |
		postf("count: % global count: %\n", count, globalCount);
	}.repeat(5, globalCount: counter);
}.fork;


//: --- 
*/