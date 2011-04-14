/*



!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

TODO: enable playing multiple posc instances in parallel. See Psend:embedInStream, commented code. 

!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

p = Posc(\msg, Pseq([['alpha', 100], [['beta', 200], [\gamma, 300]]], inf)).play;


p = Psend(Pseq([['alpha', 100], [['beta', 200], [\gamma, 300]]], inf)).play;

p = Psend(Pseq([\a, \b, Pn(\beat, 2)], 3)).play;
p = Psend(Pseq([\a, \b, Pwhite(1, 5, 2)], 3)).play;

p = Psend(Pseq([\a, \b, Pwhite(1, 3, 1), Pseq([\bli, \bla, \blo], 2)], 3)).play;

p = Psend(Pseq([\a, \b, Psend(Pseq([3], 1))], 3)).play;


p = Psend(Pseq([\a, \b, Prand([\x, \y], 3)], 2)).play;

p = Psend(Pseq([\a, \b, Pseq([\x, \y], 3)], 2)).play;

p = Psend(Pseq([\a, \b, Psend(Pseq([\x, \y], 3))], 2)).play;



p = Psend(Pseq([[\a], [\b]], inf)).play;
// // p = Psend(Pseq([[\a, Pwhite(0.1, 100.0, inf), 2, 3], [\b]], inf)).play;
p = Psend(Pseq([[\a, 1, 2, 3], \b, 5], inf)).play;
p = Psend(Pseq([[\a, 1, 2, 3], [\b], [['double', 1], ['chord']]], inf)).play;




p = Psend(Pseq([['alpha', 100], [['beta', 200], [\gamma, 300]]], inf)).play;
p = Psend(Pseq([['alpha', 100], [['beta', 200], [\gamma, 300]]], inf)).play;
p = Psend(Pseq([['alpha', 100], [['beta', 200], [\gamma, 300]]], inf)).play;


Pbind(\degree, Pseq([1, 2, Pwhite(10, 20, 3), Pn(-10, 2)], 2)).play;

Posc(\msg, Psend([\bla])).play;
Posc(\msg, Psend(Pseq([[\bla], [\blo, 3], [[\chord1, 2], [\chord2, 2]]], 2))).play;


Posc.play([\a, \b, \c]);


*/

Psend  : FilterPattern {
	var <>name = \phrase;
	
	embedInStream { arg event;
		
//		[this, \startEMBED, event, /* event.parent, */ event[\posctest]].postln;
		
/* Following code should be used to enable playing many Posc instances in parallel: 
		var posc;
		posc = event[\posc] ? Posc;
		Posc.broadcast(\start, name);

*/			
		Posc.broadcast(\start, name);
		event = pattern.embedInStream(event);
//		[this, \endEMBED, event].postln;
		Posc.broadcast(\end, name);
		^event;
	}
	
	asStream { // | pOsc |
//		[this, thisMethod.name, pOsc].postln;
		^Routine({ arg inval;
//			inval.[\pOsc] = pOsc;
//			[this, \start, inval].postln;
//			pOsc.broadcast(\start);
			this.embedInStream(inval);
//			[this, \end, inval].postln;
//			pOsc.broadcast(\end);
		}) 
	}

}



/*
Psend : Pattern {
	var <>name, <>dest;
	*new { | pattern |
		^super.new(*(pairs addAll: 
			[\msg, if (beatList isKindOf: Array) {
				Pseq(beatList, 1)
			}{
				beatList;
			}]
		)).init;
	}


	init {
		// find name and destination and store it.
		dest = patternpairs[patternpairs.indexOf(\dest) + 1].asArray;
		name = patternpairs.indexOf(\name);
		if (name.isNil) {
			name = \phrase;
		}{
			name = patternpairs[name + 1]
		}
	}
	
	asStream { 
		^Routine({ arg inval;
			this.sendNameWithTag(\start);
			this.embedInStream(inval);
			this.sendNameWithTag(\end);
		})
	}
	
	sendNameWithTag { | tag |
		dest do: _.sendMsg(tag, name);
	}
}
*/

