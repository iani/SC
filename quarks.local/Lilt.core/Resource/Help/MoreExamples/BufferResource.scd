
10 do: {
	Buffer.play({ | b |  { | b | 0.1 * BufRd.ar(1, b, LFNoise1.ar(0.1.rrand(1).dup) * BufFrames.ir(b)) }.play });
};

