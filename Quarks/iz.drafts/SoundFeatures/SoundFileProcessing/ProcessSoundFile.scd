
//:a testing: process

a = ProcessSoundFile.new;
a.process;

//:b opening file+folder
a.openOutputFileWithAudacity;
a.openOutputFolder;

//:x process a long file

b = ProcessSoundFile(nil, "/Users/iani/sounds/osmosis_sounds/seals_normalized/weddell123436.aiff");
b.process;
b.openOutputFolder;


//:d try amp processing
c = ProcessSoundFile(
	SynthDef("amp", { | in = 1 |
		var input;
		input = In.ar(in);
		Out.ar(0, [input, Amplitude.ar(input)]);
	}),
	numOutputChannels: 2
);
c.process;

//:e open amp file

c.openOutputFileWithAudacity;

//:f try amp processing
c = ProcessSoundFile(
	SynthDef("amp", { | in = 1 |
		var input;
		input = In.ar(in);
		Out.ar(0, [input, Amplitude.ar(input), PeakFollower.ar(input, 0.001)]);
	}),
	numOutputChannels: 3
);
c.process;

//:g open amp file

c.openOutputFileWithAudacity;

//:h onsets


x = {
	var sig, chain, onsets, pips;
	
	// A simple generative signal
	sig = 
	// or, uncomment this line if you want to play the buffer in
	//sig = PlayBuf.ar(1, d, BufRateScale.kr(d), loop: 1);
	
	chain = FFT(LocalBuf(512), sig);
	
	onsets = Onsets.kr(chain, MouseX.kr(0,1), \rcomplex);
	
	// You'll hear percussive "ticks" whenever an onset is detected
	pips = WhiteNoise.ar(EnvGen.kr(Env.perc(0.001, 0.1, 0.2), onsets));
	Out.ar(0, Pan2.ar(sig, -0.75, 0.2) + Pan2.ar(pips, 0.75, 1));
}.play;
