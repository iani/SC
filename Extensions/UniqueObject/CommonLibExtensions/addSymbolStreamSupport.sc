/*
Pattern and stream support for looping Functions

UniqueStream to be phased out in favor of EnvirStream. 

All of the below to be changed to work with EventStream instead of UniqueStream. 

*/

+ Symbol {
	stream { | pattern | ^EventStream(this, pattern).next }
	replaceStream { | pattern | ^EventStream(this).init(pattern) } // next not called!
	resetStream { ^EventStream(this).reset } 	// next not called: design choice.
	removeStream { ^UniqueStream(this).remove }

/*
	stream { | pattern | ^UniqueStream(this, pattern).next }
	replaceStream { | pattern | ^UniqueStream(this).init(pattern) } // next not called!
	resetStream { ^UniqueStream(this).reset } 	// next not called: design choice.
	removeStream { ^UniqueStream(this).remove }
*/
	// Shortcuts for creating all kinds of patterns can be added.
	pseq { | array, repeats = 1 | ^this.stream(Pseq(array, repeats)); }
	pseq1 { | ... elements | ^this.pseq(elements, 1); }
	pser { | array, repeats = 1 | ^this.stream(Pser(array, repeats)); }
	pser1 { | ... elements | ^this.stream(Pser(elements, inf)); }
	pwhite { | lo, hi, length = inf | ^this.stream(Pwhite(lo, hi, length)) }
	prand { | array, repeats = inf | ^this.stream(Prand(array, repeats)) }
	prand1 { | ... elements | ^this.stream(Prand(elements, inf)) }
}

