/* 2011 11 20

Noise with filter


Simple way to toggle sound on and off for these examples: 
Type Command-Alt-Shift-b to boot / quit the default server.

Typing Command-Shift-x evaluates the present file and boots the server if needed.

TUIO.new.verbose = true; 	// post for debugging
TUIO.new.verbose = false;	// end posting

TUIO.deactivate;
TUIO.clear;
TUIO.activate;
TUIO.deactivate.clear;

ConfigCCV.setConfigPath; 		// choose the path of config.xml where the video path is stored for ccv
ConfigCCV.setVideoPath;		// choose the path of the video file to be used by ccv

*/


Udef("xyringz", { | freq = 400, amp = 0.1, ring = 1, gate = 1 |  
	var source, env, filter;
	env = Env.adsr(sustainLevel: 0.15, releaseTime: 0.05);
	source = PinkNoise.ar(amp / 20);
	filter = Ringz.ar(source, freq, ring);
	Out.ar([0, 1], filter * EnvGen.kr(env, gate, doneAction: 2));
});

/*
a = Synth("xyringz", [\ring, 0.06]);
a.release;
a.release(1);
*/


TUIO.new.sessionCreatedAction = { | blob |
	blob.displayProcesses = Synth("xyringz", [
		\freq, blob.data[1] * 1000 + 100,
		\amp, blob.data[2] / 3
		, \ring, blob.data[4] + blob.data[3] * 2 + 0.2
	]);
};

TUIO.new.sessionChangedAction = { | blob, data |
	blob.displayProcesses.set(
		\freq, data[1] * 1000 + 100,
		\amp, data[2] / 3
//		, \ring, blob.data[4] + blob.data[3] * 2
	);
};

TUIO.new.sessionEndedAction = { | blob |
	blob.displayProcesses.release(0.5);
};

