/* (IZ 2006-03-02) { 

First test using Pcount and Pbracket to define series of actions depending on number of vertices past. This here is a low-level way for doing this. It is actually more convenient to use Pactions, as shown in the next test score example.

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
playbuf = session.addScriptAt('Sand', 'playbuf_looping');
bounce = session.addScriptAt('Sand', 'softbounce');
drumming = session.addScriptAt('Sand', 'subtropicdrumming');
rez = session.addScriptAt('Sand', 'subtropicrez');

scoreStream = Pseq([ // a sequence of 2 sub-patterns:
	Pcount(5),		// 1: wait five vertices without doing anything
	Pbracket(			// 2: start a script, wait 5 vertices, then stop it
		Pcount(10),	// 		this is the wait interval
		{ sin.start },	// 		this is done at the start
		{ sin.stop }		// 		this is done at the end
	)				// end of 2nd sub-pattern
	], inf			// repeat sequence pattern for ever
).asStream;		// make a stream to play.

// array of rules for reacting to data input from sand program:
~conductor = [
{ 	// rule 1: play the next item from scoreStream when vertex reived
	scoreStream.next; // more streams can be added for other instuments
}
];	// end of rules array

