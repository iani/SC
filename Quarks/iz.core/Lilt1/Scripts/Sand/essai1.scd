/* (IZ 2006-05-01) { 
Showing how to get data of current vertex in a rule
the sand trace driving two instruments
} */

// the instruments
var hihat;
var bounce;

hihat = ~session.addScriptAt('Sand', 'nohihat');
bounce = ~session.addScriptAt('Sand', 'softbounce');

// The rules. Here just one rule: (conductor is a function and so { | argument1, argument2 ... | body of function }
// is correct)

~conductor = { | envir |
	// Just print out reception of 'aa', 'ai', and 'wait' data from vertex:
	Post << "Voila aa, ai et wait " <<* [envir.aa, envir.ai, envir.wait] << "\n";
	hihat.set(\dur, envir.wait);
	hihat.set(\freq, envir.aa.abs * 800 + 250);
	hihat.start;
	bounce.set(\dur, 2 * envir.wait);
	bounce.set(\freq, envir.aa.abs * 700 + 150);
	bounce.start;
};
