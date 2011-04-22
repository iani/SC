/*

f = { "Hello world".postln };

10 do: { f.doOnce };

f.resetDoOnce;

*/

+ Function {
	doOnce { | args ... resetKeys |
		^UniqueFunction(this, args, *resetKeys);		
	}
	
	removeDoOnce {
		UniqueFunction.remove(this);	
	}
		
}