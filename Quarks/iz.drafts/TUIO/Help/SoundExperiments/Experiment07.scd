/* 2011 11 20

40 random resonance / additive synthesis synths. 

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

// Create and send to server 40 random synthdefs, named "rperc0" to "rperc39"
40.do({ arg i;
  Udef(format("rperc%", i), { | i_bus = 0, amp = 0.1, rate = 1 |
	  var n = 12;
	  var exc, out;
	  exc = WhiteNoise.ar * Decay.kr(Impulse.kr(0,0,amp*0.1), rrand(0.2,1.0));
	  out = Klank.ar(`[   // resonator
        { exprand(100.0, 10000.0) } ! n, { rrand(0.1,1.0) } ! n, { exprand(0.05,1.0) } ! n
      ], exc, rate);
      DetectSilence.ar(out, 0.0001, 0.1, 2);
      Out.ar(i_bus, Pan2.ar( out, rrand(-1.0,1.0)));
  })
});

/*
a = Synth("rperc10", [\rate, 0.4]);
*/

{	// select def
	var defs, gridspec;
	defs = { | i | format("rperc%", i) } ! 40;
	gridspec = ControlSpec(0, 39, \linear, 1, 0);
	TUIO.new.behavior = { | x, y |
		Synth(defs[gridspec.map(x).round(1).asInteger]);
	};
}.value;

TUIO.new.sessionCreatedAction = { | blob |
	blob.sessionManager.behavior.(*blob.data[1..2]);
};

TUIO.new.sessionChangedAction = nil;

TUIO.new.sessionEndedAction = nil;

