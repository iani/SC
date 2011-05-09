//:--


//:--
UniqueBuffer.default.bufnum.postln;
UniqueBuffer.default.server.postln;
UniqueBuffer.default.play;
//:---
\test5.playFunc({ | bufnum | PlayBuf.ar(bufnum.numChannels, bufnum, 1, 0, 0, 0, 2); });
//:--
Buffer.play;
//:--
UniqueBuffer.default.makePlayFunc.postln.play(args:[\bufnum, UniqueBuffer.default]);
//:--

UniqueBuffer.default.play;

//:---
Buffer.play;

//:----

UniqueBuffer.default.play;
//:---


UniqueBuffer.current;
//:-------
10 do: { Buffer.play(args: [\rate, 0.75 + 1.0.rand]); 0.1.wait };

