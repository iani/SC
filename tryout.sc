//:f first

NotifyOnce(\sender, \message, \listener, { "hello".postln; });

NotifyOnce(\sender, \message, \listener, { "hello 2".postln; });




//:a
10 do: {
	Buffer.play({ | b |  { | b | 0.1 * BufRd.ar(1, b, LFNoise1.ar(0.1.rrand(1).dup) * BufFrames.ir(b)) }.play });
};

//:b
Library.postTree;
