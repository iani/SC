/* (IZ 2006-05-07) {
Accessing vertex data from within a Paction stream, 
a temporal pattern synced on vertices progress
} */


var hihat; // the first instrument
var bounce; // the second instrument
var envir; // listener's envir - contains vertex data
var stream;	// the action stream


hihat = ~session.addScriptAt('Sand', 'nohihat');
bounce = ~session.addScriptAt('Sand', 'softbounce');
envir = ~envir;	// get the envir locally for faster access
stream = Pactions([
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

// The 1 rule: play the next action in the stream 
~conductor = {
	stream.next;
};
