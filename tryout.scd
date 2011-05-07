//:first test

\default.play;

//:test def

Udef(\default2, { Out.ar(0, LFNoise2.ar(2000, 0.1)) });

\default2.play;

//:---- OK!

var buffer;
buffer = UniqueBuffer('fft', Server.default, 1024);
Udef(\fft, { | in = 0, buf = 0 | FFT(buf, InFeedback.ar(in)); }, server: Server.default);
UniqueSynth(\fft, \fft, [\buf, buffer.object.bufnum]);
//:next

Server.default.queryAllNodes

//:....

		Udef(\fft, { | in = 0, buf = 0 |
			FFT(buf, InFeedback.ar(in));
		}, server: Server.default);
		super.init(server.asTarget, \fft, [\in, in], \addToTail);
