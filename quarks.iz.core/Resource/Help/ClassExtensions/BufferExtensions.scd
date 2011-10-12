
//:a playing the default buffer

Buffer.default.play; // 

//:b playing the default buffer on another server

Buffer.default(Server.internal).play;

//:c current buffer is the last selected buffer - or the default

Buffer.current.play;

//:d Buffer.play;

Buffer.play; 	// plays the current buffer - or the default

//:Play with custom arguments

10 do: { Buffer.play(args: [\rate, 0.75 + 1.0.rand]); };

//:Play with custom arguments, with a delay of 0.1 between each iteration

10 do: { Buffer.play(args: [\rate, 0.75 + 1.0.rand]); 0.1.wait };

//:Play with custom function

Buffer.play({ | bufnum | BufRd.ar(1, bufnum, LFNoise1.ar(1) * BufFrames.ir(bufnum)) });

//:Long form: Play with custom function

BufferResource.default.play({ | bufnum | BufRd.ar(1, bufnum, LFNoise1.ar(1) * BufFrames.ir(bufnum)) });

//:playing the same buffer with a custom function many times: 

10 do: {
	Buffer.play({ | bufnum | BufRd.ar(1, bufnum, SinOsc.ar(0.02.rrand(0.09)) * BufFrames.ir(bufnum)) });
};

//:Variant
10 do: {
	Buffer.play({ | bufnum | BufRd.ar(1, bufnum, LFNoise1.ar(0.3).range(-1, 1) * BufFrames.ir(bufnum)) });
};