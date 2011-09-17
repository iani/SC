/* (IZ 2006-05-01) { 
Showing how to get data of current vertex in a rule
} */

// the instruments
var hihat;

hihat = ~session.addScriptAt('Sand', 'nohihat');

// The rules. Here just one rule: 
~conductor = { | envir |
	// Just print out reception of 'aa', 'ai', and 'wait' data from vertex:
	Post << "Voila aa, ai et wait " <<* [envir.aa, envir.ai, envir.wait] << "\n";
	hihat.set(\dur, envir.wait);
	hihat.set(\freq, envir.aa.abs * 800 + 250);
	hihat.start;
};
