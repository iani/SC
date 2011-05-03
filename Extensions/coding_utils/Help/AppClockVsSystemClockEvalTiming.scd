//:Run with Cmd-X and Cmd-Ctrl-X to see the difference

var start, lastnow, dtime;

start = Process.elapsedTime;
lastnow = start;

1000 do: {
	[Process.elapsedTime - lastnow, Process.elapsedTime - start].round(0.0001).postln;
	lastnow = Process.elapsedTime;
	0.01.wait;
};
	
	
