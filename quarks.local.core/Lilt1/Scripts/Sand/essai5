/* (IZ 2006-05-07) {
Accessing vertex data from several Paction streams (temporal pattern synced on vertices progress)
selected by conductor differently for different parts of the whole piece
} */

var hihat; // the first instrument
var bounce; // the second instrument
var sin; // the third instrument
var envir; // listener's envir - contains vertex data
var stream1;	// the first action stream
var stream2;	// the second action stream

hihat = ~session.addScriptAt('Sand', 'nohihat');
bounce = ~session.addScriptAt('Sand', 'softbounce');
sin = ~session.addScriptAt('Sand', 'decaysin');

envir = ~envir;	// get the envir locally for faster access

stream1 = Pactions([
	{
		hihat.set(\dur, 3 * envir.wait); // extend duration 
		hihat.set(\freq, envir.aa.abs * 800 + 250);
		hihat.start;
	},
	2,	// skip 2 vertices
	{
		bounce.set(\dur, 5 * envir.wait); // extend duration 
		bounce.set(\freq, envir.aa.abs * 300 + 100);
		bounce.start;
	},
	2	// skip 2 vertices
], inf).asStream;	// inf: repeat actions array for ever

stream2 = Pactions([ // a sequence of actions at specified intervals:
	{ sin.start },	// start sin
	1,		// wait 3 vertices without doing anything	
	{ sin.set(\freq, 200 rrand: 2000) }, // change sin frequency
	2,		// wait 2 vertices without doing anything	
	{ sin.set(\attack, 0.001 rrand: 5) }, // change sin attack
	1,		// wait 2 vertices without doing anything	
	{ sin.set(\decay, 0.001 rrand: 5) }, // change sin decay
	2,		// wait 2 vertices without doing anything	
	{ sin.set(\vibamp, 0.0 rrand: 1000) }, // change sin vibrato amplitude
	1,		// wait 5 vertices without doing anything	
	{ sin.set(\vibfreq, 0.01 rrand:  25) }, // change sin vibfreq
	2,		// wait 5 vertices without doing anything	
	{ sin.stop }		// 	stop decaysin script and sound
	], inf			// repeat sequence pattern for ever
).asStream;		// make a stream to play.

// The 1 rule: play the next action in the stream 
~conductor = {
	if (envir.index<34 || envir.index>66,
	{ stream1.next },
	{ stream2.next });
};
