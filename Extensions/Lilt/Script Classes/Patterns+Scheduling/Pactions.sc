/* IZ 060306

Series of functions to be performed at predetermined count intervals: 

(

a = Pactions([
	2,						// wait 2 counts
	{ "starting ... ".post }, 		// start something
	3,						// wait 3 counts
	{ "set x! - ".post }, 	// change something
	2,						// wait 2 counts
	{ "... stopping".postln }		// stop something
], 2							// repeat the above 2 times
).asStream;

Routine({
	20.do { 
		0.25.wait;
		a.next;
	}
}).play;

)

091229: 
Extending to permit functions to be repeated several times, and to permit embedding of patterns. 
*/

Pactions : Pattern {
	*new { | countsOrActions, repeats = 1 |
		^Pseq(
			countsOrActions.collect { | ca |
				case 
				{ ca isKindOf: Function } { Pfuncn(ca, 1) }
				{ ca isKindOf: Integer } { Pcount(ca) }
				{ ca isKindOf: Array } { Pfuncn(ca[0], ca[1]) }
				{ true } { ca }	// for all other cases, return the element itself
			},
			repeats
		)
	}
}

