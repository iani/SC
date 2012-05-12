/* 2011 11 20

Simple way to toggle sound on and off for these examples: 
Type Command-Alt-Shift-b to boot / quit the default server.

Typing Command-Shift-x evaluates the present file and boots the server if needed.

TUIO.new.verbose = true; 	// post for debugging
TUIO.new.verbose = false;	// end posting

TUIO.deactivate;
TUIO.clear;
TUIO.activate;

ConfigCCV.setConfigPath; 		// choose the path of config.xml where the video path is stored for ccv

ConfigCCV.setVideoPath;		// choose the path of the video file to be used by ccv


*/


Udef("xy", { | freq = 400, amp = 0.1, gate = 1 |  
	var source, env;
	env = Env.adsr(releaseTime: 0.05);
	source = SinOsc.ar(freq, 0, amp);
	Out.ar([0, 1], source * EnvGen.kr(env, gate, doneAction: 2));
});

/*
a = Synth("xy");
a.release;
a.release(1);
*/

TUIO.new.sessionCreatedAction = { | blob |
	blob.displayProcesses = Synth("xy", [
		\freq, blob.data[1] * 1000 + 100,
		\amp, blob.data[2] / 10
	]);
};

TUIO.new.sessionChangedAction = { | blob, data |
	blob.displayProcesses.set(
		\freq, data[1] * 1000 + 100,
		\amp, data[2] / 10
	);
};

TUIO.new.sessionEndedAction = { | blob |
	blob.displayProcesses.release;
};

