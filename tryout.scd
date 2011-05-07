
\test.addToServerTree({
var buffer, synthdef, object, server;
server = Server.default;
buffer = UniqueBuffer(\fft, Server.default, 1024);
synthdef = Udef(\fft, { | in = 0, buf = 0 |
	FFT(buf, InFeedback.ar(in));
}, server: Server.default);
object = UniqueSynth(\fft, \fft, [\in, 0, \buf, buffer.object.bufnum], server, \addToTail);
object.rsynca({
			var fftbuf;
			fftbuf = buffer.object;
			fftbuf.bufnum.postln;
			loop {
				fftbuf.getn(0, fftbuf.size, { | buf |
			buf.postln;
				});
			1.2.wait;
			};
	})
});

