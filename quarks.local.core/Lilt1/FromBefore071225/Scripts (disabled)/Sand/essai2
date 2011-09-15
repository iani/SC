/* (IZ 2006-03-02) { 

Using Pactions to define a sequence of actions to be done at specified vertex-count intervals
Adding several instruments and buffers

} */

var session;		// the sand session as local variable
// store the instrument scripts in local variables for fast access
var kaze, sin, dust, winter, hihat, nosign, playbuf, bounce, drumming, rez;
var scoreStream; 	// this is the stream that counts vertices and does actions;

session = ~session; // get session locally to save time accessing it all the time

kaze = session.addScriptAt('Sand', 'burblekaze');
sin = session.addScriptAt('Sand', 'decaysin');
dust = session.addScriptAt('Sand', 'dust0');
winter = session.addScriptAt('Sand', 'martianwinter');
hihat = session.addScriptAt('Sand', 'nohihat');
nosign = session.addScriptAt('Sand', 'nosign');
playbuf = session.addScriptAt('Sand', 'playbuf (1-shot, constant)');
bounce = session.addScriptAt('Sand', 'softbounce');
drumming = session.addScriptAt('Sand', 'subtropicdrumming');
rez = session.addScriptAt('Sand', 'subtropicrez');

scoreStream = Pactions([ // a sequence of actions at specified intervals:
	{   playbuf.loadBuffer('cricket1');
		playbuf.set(\amp, 1);
		playbuf.set(\rate, 1); },
	1,		// wait 1 vertices without doing anything
	{ sin.start },	// start sin
	{ playbuf.start },	// play a sampled sound
	2,		// wait 2 vertices without doing anything	
	{ sin.set(\freq, 200 rrand: 2000) }, // change sin frequency
	{ hihat.set(\dur, 2); hihat.set(\freq, 200 rrand: 2000); hihat.start },	 
	1,		// wait 1 vertices without doing anything	
	{ sin.set(\attack, 0.001 rrand: 5) }, // change sin attack
	{ playbuf.set(\rate, 2); // change rate to 2x normal
	  playbuf.start;},		 // play a sampled sound
	2,		// wait 2 vertices without doing anything	
	{ sin.set(\decay, 0.001 rrand: 5) }, // change sin decay
	{ hihat.set(\dur, 4); hihat.set(\freq, 200 rrand: 2000); hihat.start },
	{ playbuf.set(\rate, 1); // change rate to normal
	  playbuf.start;},		 // play a sampled sound
	1,		// wait 1 vertices without doing anything	
	{ sin.set(\vibamp, 0.0 rrand: 1000) }, // change sin vibrato amplitude
	2,		// wait 2 vertices without doing anything	
	{ sin.set(\vibfreq, 0.01 rrand:  25) }, // change sin vibfreq
	{ hihat.set(\dur, 8); hihat.set(\freq, 200 rrand: 2000); hihat.start },
	1,		// wait 1 vertices without doing anything	
	{ sin.stop }		// 	stop decaysin script and sound
	], inf			// repeat sequence pattern for ever
).asStream;		// make a stream to play.

// array of rules for reacting to data input from sand program:
~conductor = [
{ 	// rule 1: play the next item from scoreStream when vertex reived
	scoreStream.next; // more streams can be added for other instuments
}
];	// end of rules array

