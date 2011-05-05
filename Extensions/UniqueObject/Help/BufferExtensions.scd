
//:a playing the default buffer

Server.default.quit; 	// works even if the server is not booted;
Buffer.default.play;

//:b playing the default buffer on another server

Buffer.default(Server.internal).play;

//:c current buffer is the last selected buffer - or the default

Buffer.current.play;

//:d Buffer.play;

Buffer.play; 	// plays the current buffer - or the default

//:e Play with custom function

Buffer.play({ | b |  { | b | BufRd.ar(1, b, LFNoise1.ar(1) * BufFrames.ir(b)) }.play });

//:f Long form: Play with custom function

// same as Buffer.play(....)

UniqueBuffer.default.play({ | b |  { | b | BufRd.ar(1, b, LFNoise1.ar(1) * BufFrames.ir(b)) }.play });

//:g playing the same buffer with the same function many times: 

10 do: {
	Buffer.play({ | b |  { | b | 0.1 * BufRd.ar(1, b, LFNoise1.ar(0.1.rrand(1)) * BufFrames.ir(b)) }.play });
};