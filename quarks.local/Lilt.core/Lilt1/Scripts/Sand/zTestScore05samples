/* (IZ 2006-03-02) { 

Usig Pactions to define a sequence of actions to be done at specified vertex-count intervals


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
// playbuf = session.addScriptAt('Sand', 'playbuf_looping');
playbuf = session.addScriptAt('Sand', 'playbuf (1-shot, constant)');
bounce = session.addScriptAt('Sand', 'softbounce');
drumming = session.addScriptAt('Sand', 'subtropicdrumming');
rez = session.addScriptAt('Sand', 'subtropicrez');

scoreStream = Pactions([ // a sequence of actions at specified intervals:
	{ 
		playbuf.loadBuffer('boing');
		playbuf.set(\amp, 1);
		playbuf.set(\rate, 1);
	},
	5,		// wait 5 vertices without doing anything
	{ playbuf.start },	// play a sampled sound
	5,		// wait 3 vertices without doing anything	
	{ 
		playbuf.set(\rate, 2); // change rate to 2x normal
		playbuf.start;		// play a sampled sound
	},
	], inf			// repeat sequence pattern for ever
).asStream;		// make a stream to play.

// array of rules for reacting to data input from sand program:
~conductor = [
{ 	// rule 1: play the next item from scoreStream when vertex reived
	scoreStream.next; // more streams can be added for other instuments
}
];	// end of rules array

