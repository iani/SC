/*

f = { "Hello world".postln };

10 do: { f.doOnce };

f.resetDoOnce;

*/

+ Function {
	doOnce { | ... args |
		^UniqueFunction(this, *args);		
	}
	
	remove {
		UniqueFunction.remove(this.hashKey);	
	}

	hashKey { ^def.sourceCode.hash }
	
	uplay { | target, outbus = 0, fadeTime = 0.02, addAction=\addToHead, args |
		^Uplay(this, target, outbus = 0, fadeTime = 0.02, addAction=\addToHead, args);
	}
}

+ String {
	usynth { | args, target, addAction=\addToHead |
		^this.asSymbol.usynth(args, target, addAction);
	}
}

+ Symbol {
	usynth { | args, target, addAction=\addToHead |
		^UniqueSynth(this, this, args, target, addAction);
	}
}
