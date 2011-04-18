
DefaultBuffer.play;

DefaultBuffer.spectrogram;

DefaultBuffer.loadDialog;

DefaultBuffer.postInfo;

// Following should not be used: 
DefaultBuffer.loadDialog.play; // plays the old buffer because loading is asynchronous

DefaultBuffer loadDialog: _.play; // plays new buffer

DefaultBuffer loadDialog: _.postInfo;

DefaultBuffer.buffer;

DefaultBuffer loadDialog: _.postInfo;

DefaultBuffer.openWithAudacity;

DefaultBuffer.showInFinder;


DefaultBuffer.play({ | b |
	{ | b | Out.ar(0, 100 * Resonz.ar(PlayBuf.ar(1, b, 0.5), LFNoise0.kr(5).range(200, 2000), 0.01)) }.play(args: [\b, b])
})

DefaultBuffer.play({ | b |
	{ | b | Out.ar(0, 100 * Resonz.ar(PlayBuf.ar(1, b, 0.5), LFNoise1.kr(5).range(200, 2000), 0.01)) }.play(args: [\b, b])
})


DefaultBuffer.play({ | b |
	{ | b | Out.ar(0, 100 * Resonz.ar(PlayBuf.ar(1, b, 1), LFNoise1.kr(0.25).range(200, 9000), 0.1)) }.play(args: [\b, b])
})

DefaultBuffer.play({ | b |
	{ | b | Out.ar(0, 100 * Resonz.ar(PlayBuf.ar(1, b, 1), LFNoise1.kr(0.25).range(200, 9000), 0.01)) }.play(args: [\b, b])
})


DefaultBuffer.play(
	SynthDef("tmp", { | bufnum |
		Out.ar(0, 100 * Resonz.ar(PlayBuf.ar(1, bufnum, 1), LFNoise1.kr(0.25).range(200, 9000), 0.01)) 
	});
)

DefaultBuffer.play;

DefaultBuffer.loadDialog;

DefaultBuffer.loadDialog.play; // plays the old buffer because loading is asynchronous

DefaultBuffer loadDialog: _.play; // plays new buffer


DefaultBuffer.loadTo($a);

a;

DefaultBuffer.loadTo(\buffer);

~buffer;
