/* (IZ 2006-03-02) { 

Usig Pactions to define a sequence of actions to be done at specified vertex-count intervals


} */

var session;		// the sand session as local variable
// store the instrument scripts in local variables for fast access
var kaze, sin, dust, winter, hihat, nosign, playbuf, bounce, drumming, rez;
var scoreStream; 	// The stream that counts vertices and does actions;
var envir;			// the listeners environment, to get vertex data 
					// from inside the scoreStream

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

envir = ~envir;
Post << "Debugging: this is the envir: " << envir << "\n";

scoreStream = Pactions([ // a sequence of actions at specified intervals:
	{ Post << "Hello from inside the scoreStream " << envir << "\n"},
	5,		// wait 5 vertices without doing anything
	{ sin.start },	// start sin
	3,		// wait 3 vertices without doing anything	
	{ sin.set(\freq, 200 rrand: 2000) }, // change sin frequency
	2,		// wait 2 vertices without doing anything	
	{ sin.set(\attack, 0.001 rrand: 5) }, // change sin attack
	2,		// wait 2 vertices without doing anything	
	{ sin.set(\decay, 0.001 rrand: 5) }, // change sin decay
	2,		// wait 2 vertices without doing anything	
	{ sin.set(\vibamp, 0.0 rrand: 1000) }, // change sin vibrato amplitude
	5,		// wait 5 vertices without doing anything	
	{ sin.set(\vibfreq, 0.01 rrand:  25) }, // change sin vibfreq
	5,		// wait 5 vertices without doing anything	
	{ sin.stop }		// 	stop decaysin script and sound
	], inf			// repeat sequence pattern for ever
).asStream;		// make a stream to play.

// array of rules for reacting to data input from sand program:
~conductor = [
{ 	// rule 1: play the next item from scoreStream when vertex reived
	scoreStream.next; // more streams can be added for other instuments
}
];	// end of rules array

