//:0 list conet files

"/Users/iani/sounds/osmosis_sounds/conet_normalized/*.aiff".pathMatch.asCompileString.postln;

//:a load a sound file into buffer and play it;
Server.default.waitForBoot({
	Buffers.loadOnce("/Users/iani/sounds/osmosis_sounds/conet_normalized/tcp_d1_01_the_swedish_rhapsody_irdial.aiff");
	b = Buffers.default.asArray.first;
	b.updateInfo( { { b.play }.defer(0.01) } );
});

//:A open file's directory
("open " ++ b.path.dirname.asCompileString).unixCmd


//:b open in Audacity
if (~audacity.isNil) { 
		~audacity = ("open -a Audacity.app " ++ b.path.asCompileString).unixCmd;
	}{
		"open -a Audacity.app ".unixCmd;
};

//:c test score

SynthDef("features", { | bufnum = 0 |
	Out.ar(0, PlayBuf.ar(1, bufnum, BufRateScale.kr(bufnum), doneAction: 2)); 
}).load(s);

Score([
		[0.1, [\s_new, \features, 1000, 0, 0, \bufnum, Buffers.default.asArray.first.bufnum]],
		[3, [\n_free, 1000]] // finish
]).play;

//:C Write file with feature data

SynthDef("ping", { | freq = 440 |
	Out.ar(0,
		SinOsc.ar(freq, 0, 0.2) * Line.kr(1, 0, 0.5, doneAction: 2)
	)
}).load(s);


SynthDef("features", { | bufnum = 0 |
	Out.ar(0, PlayBuf.ar(1, bufnum, BufRateScale.kr(bufnum), doneAction: 2)); 
}).load(s);

/*
Score.play(
	[
		[0.0, [\s_new, \ping, 2000, 0, 0, \freq, 1000]],
		[15, [\c_set, 0, 0]] // finish
	]
);
*/

Score.recordNRT(
	[
		[0.0, [\s_new, \ping, 2000, 0, 0, \freq, 1000]],
		[0.1, ["/b_allocRead", 0, 
			"/Users/iani/sounds/osmosis_sounds/conet_normalized/tcp_d1_22_5_dashes_irdial.aiff",
			0, -1]],
		[1.0, [\s_new, \features, 1000, 0, 0, \bufnum, 0]],
		[10.0, [\n_free, 1000]],
		[15, [\c_set, 0, 0]] // finish
	],
	Date.getDate.stamp,
	Date.getDate.stamp ++ ".aiff"
);

"open /Applications/SuperCollider/".unixCmd;

//:d cleanup

SynthDef("features", { | bufnum = 0 |
	Out.ar(0, PlayBuf.ar(1, bufnum, BufRateScale.kr(bufnum), doneAction: 2)); 
}).load(s);

Score.recordNRT(
	[
		[0.0, ["/b_allocRead", 0, 
			"/Users/iani/sounds/osmosis_sounds/conet_normalized/tcp_d1_22_5_dashes_irdial.aiff",
			0, -1]],
		[0.5, [\s_new, \features, 1000, 0, 0, \bufnum, 0]],
		[10.0, [\n_free, 1000]],
		[10.01, [\c_set, 0, 0]] // finish
	],
	Date.getDate.stamp,
	Date.getDate.stamp ++ ".aiff"
);

"open /Applications/SuperCollider/".unixCmd;

//:e try completion message

SynthDef("features", { | bufnum = 0 |
	Out.ar(0, PlayBuf.ar(1, bufnum, BufRateScale.kr(bufnum), doneAction: 2)); 
}).load(s);

Score.recordNRT(
	[
		[0.0, ["/b_allocRead", 0, 
			"/Users/iani/sounds/osmosis_sounds/conet_normalized/tcp_d1_22_5_dashes_irdial.aiff",
			0, -1,
			[\s_new, \features, 1000, 0, 0, \bufnum, 0]
			]],
//		[0.5, [\s_new, \features, 1000, 0, 0, \bufnum, 0]],
		[10.0, [\n_free, 1000]],
		[10.01, [\c_set, 0, 0]] // finish
	],
	Date.getDate.stamp,
	Date.getDate.stamp ++ ".aiff"
);

"open /Applications/SuperCollider/".unixCmd;

//:f direct file i/o

SynthDef("iofeatures", { | in = 1 |
	Out.ar(0, In.ar(in)); 
}).load(s);

{
	var options;
	options = ServerOptions.new;
	options.numOutputBusChannels = 1;
	options.numInputBusChannels = 1;
	Score.recordNRT(
		[
			[0.1, [\s_new, \iofeatures, 1000, 0, 0, \in, 1]],
			[2.0, [\n_free, 1000]],
			[3.0, [\c_set, 0, 0]] // finish
		],
		Date.getDate.stamp,
		Date.getDate.stamp ++ ".aiff",
		inputFilePath: "sounds/a11wlk01-44_1.aiff",
		options: options
	);
}.defer(0.5);

"open /Applications/SuperCollider/".unixCmd;

//:g get input file duration


// SoundFile.collect("sounds/*").do { |f| f.path.postln; (f.numFrames / f.sampleRate).postln };


SynthDef("iofeatures", { | in = 1 |
	Out.ar(0, In.ar(in)); 
}).load(s);

{	var path = "/Users/iani/sounds/osmosis_sounds/conet_normalized/tcp_d1_22_5_dashes_irdial.aiff";
	var options, soundfile, dur;
	options = ServerOptions.new;
	options.numOutputBusChannels = 1;
	options.numInputBusChannels = 1;
	soundfile = SoundFile.collect(path).first;
	dur = soundfile.numFrames / soundfile.sampleRate;
	Score.recordNRT(
		[
			[0.1, [\s_new, \iofeatures, 1000, 0, 0, \in, 1]],
			[dur, [\n_free, 1000]],
			[dur + 0.001, [\c_set, 0, 0]] // finish
		],
		Date.getDate.stamp,
		Date.getDate.stamp ++ ".aiff",
		inputFilePath: path,
		options: options
	);
}.defer(0.5);

"open /Applications/SuperCollider/".unixCmd;



