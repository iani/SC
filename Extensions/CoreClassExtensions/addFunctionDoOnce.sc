/*

f = { "Hello world".postln };

10 do: { f.doOnce };

f.resetDoOnce;

*/

+ Function {
	doOnce {
		^UniqueFunction(this);		
	}
	
	removeDoOnce {
		UniqueFunction.remove(this);	
	}
		
}