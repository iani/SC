/* 
Support for Code class in evaluating string snippets.
 
*/

+ String {
	fork { | clock | Code.fork(this, clock); }
	evalPost {
		if (History.started) { History.enter(this) };
		^this.eval.postln;
	}
	eval { ^this.interpret; }
}