

+ Function {

	hashKey { ^def.sourceCode.hash }
	
	uplay { | target, outbus = 0, fadeTime = 0.02, addAction=\addToHead, args, dur |
		^UniquePlay(this, target, outbus, fadeTime, addAction=\addToHead, args, dur);
	}

/* EXPERIMENTAL !!!!

*/
	chain { | links |
		links = [this] ++ links;
		
	}
	
	chain1 { 

	}

	doOnce { | ... args |
		^UniqueFunction(this, *args);		
	}
	
	remove {
		UniqueFunction.remove(this.hashKey);	
	}
}



// temporary: for testing the chain algorithm

+ Object {
	onLoad { | func |
		1.wait;
		func.value;
	}
}

