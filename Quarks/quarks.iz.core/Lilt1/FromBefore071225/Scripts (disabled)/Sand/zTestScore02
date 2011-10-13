/* (IZ 2006-03-02) { 
First attempt at a score with many instruments and rules.

} */

var session;		// the sand session as local variable
// store the instrument scripts in local variables for fast access
var kaze, sin, dust, winter, hihat, nosign, playbuf, bounce, drumming, rez;

session = ~session; // get session locally to save time accessing it all the time

kaze = session.addScriptAt('Sand', 'burblekaze');
sin = session.addScriptAt('Sand', 'decaysin');
dust = session.addScriptAt('Sand', 'dust0');
winter = session.addScriptAt('Sand', 'martianwinter');
hihat = session.addScriptAt('Sand', 'nohihat');
nosign = session.addScriptAt('Sand', 'nosign');
playbuf = session.addScriptAt('Sand', 'playbuf_looping');
bounce = session.addScriptAt('Sand', 'softbounce');
drumming = session.addScriptAt('Sand', 'subtropicdrumming');
rez = session.addScriptAt('Sand', 'subtropicrez');

// array of rules for reacting to data input from sand program:
~conductor = [
{ | envir |		// rule 1
	var value;
	value = envir.index + 1;
	if (value % 30 == 0) { 
		drumming.set(\freq, 300 rrand: 400);
		drumming.start;
	};
	if (value + 15 % 10 == 0) {
		drumming.stop;
	};
	if (value % 5 == 0) {
		hihat.set(\dur, 0.1 rrand: 16);
		hihat.set(\freq, 300 rrand: 4000);
		hihat.start;
	};
	if (value % 20 == 0) { 
		winter.set(\freq, 300 rrand: 4000);
		winter.start;
	};
	if (value % 25 == 0) {
		winter.stop;
	};
	if (value % 13 == 0) { 
		bounce.set(\freq, 300 rrand: 4000);
		bounce.start;
	};
}
];	// end of rules array

