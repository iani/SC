/* (IZ 2006-05-01) { 
Showing how to get data of current vertex in a rule
the sand trace driving two instruments and plays one buffer all at once
} */

// the instruments
var hihat;
var bounce;
var buf;


hihat = ~session.addScriptAt('Sand', 'nohihat');
bounce = ~session.addScriptAt('Sand', 'softbounce');
buf = ~session.addScriptAt('Sand', 'playbuf (1-shot, constant)');

// The rules. Here just one rule: 
~conductor = { | envir |
	// Just print out reception of 'aa', 'ai', and 'wait' data from vertex:
	Post << "Voila aa, ai et wait " <<* [envir.aa, envir.ai, envir.wait] << "\n";
	buf.loadBuffer('cricket1');
	buf.set(\amp, 1);
	buf.set(\rate, 1);
	buf.start;
	
	hihat.set(\dur, envir.wait);
	hihat.set(\freq, envir.aa.abs * 800 + 250);
	hihat.start;
	
	bounce.set(\dur, envir.wait);
	bounce.set(\freq, envir.aa.abs * 300 + 150);
	bounce.start;
};
