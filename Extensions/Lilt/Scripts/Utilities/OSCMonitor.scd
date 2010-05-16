/* IZ 080803
Post incoming OSC messages for debugging or troubleshooting
*/

var originalOSCfunc;

~start = {
	originalOSCfunc = thisProcess.recvOSCfunc;
	thisProcess.recvOSCfunc = { | ... args | args.postln; };
	"Printing of incoming OSC messages to post window is now on".postln;
};

~stop = {
	thisProcess.recvOSCfunc = originalOSCfunc;
	"Printing of incoming OSC messages to post window is now off".postln;
}

