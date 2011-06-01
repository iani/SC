//:Run with Cmd-X and Cmd-Ctrl-X to see the difference

var start, lastnow, dtime;

start = Process.elapsedTime;
lastnow = start;

OSCresponder(nil, \test, { | ... args | args.postln; }).add;

1000 do: {
	[Process.elapsedTime - lastnow, Process.elapsedTime - start].round(0.0001).postln;
	NetAddr.localAddr.sendMsg(\test);
	lastnow = Process.elapsedTime;
	0.01.wait;
};
	
	
AppClock