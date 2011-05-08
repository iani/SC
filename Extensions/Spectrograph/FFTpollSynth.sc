/* 
A UniqueSynth which starts an FFT analysis synth on any audio input, polls the fft buffer at a steady rate, and sends the resulting buffer data to an FFTsignalPoller. 

It is started implicitly by FFTsynthPoller.

It is not designed to be used on its own. 

*/

FFTpollSynth : UniqueObject {
	var <server, <rate = 0.04, <synthdef, <buffer, <bufSize, <in = 0, <index = 0;
	var <dependants;
	var <real, <imaginary, <magnitudes;	// also make and send the magnitudes of the spectrum

	*new { | key, server, rate = 0.04, bufSize = 1024, in = 0 |
		^super.new(key ? 'fft_poll', server, rate, bufSize, in);
	}

	init { | argServer, argRate, argBufSize, argIn = 0 |
		server = argServer.asTarget.server;
		rate = argRate;
		bufSize = argBufSize;
		in = argIn;
		dependants = Set.new;
		ServerPrep(server).addToServerTree(this, { this.makeSynth }); // run as long as server is booted
	}

	makeSynth {
		buffer = UniqueBuffer(key[2], server, bufSize);
		synthdef = Udef(\fft, { | in = 0, buf = 0 |
			FFT(buf, InFeedback.ar(in));
		}, server: server);
		object = UniqueSynth(\fft, \fft, [\in, in, \buf, buffer.object.bufnum], server, \addToTail);
		object.onStart({ this.notify(\synthStarted); });
		object.rsyncs({
			var fftbuf;
			fftbuf = buffer.object;
			loop {
				fftbuf.getn(0, bufSize, { | buf |
					#real, imaginary = buf.clump(2).flop;
					magnitudes = Complex(Signal.newFrom(real), Signal.newFrom(imaginary)).magnitude;
					dependants do: _.update(index, buf, magnitudes);
					index = index + 1;
				});
				rate.wait;
			};
		});
	}
	
	addDependant { | object | dependants add: object }
	
	free { this.remove }
	remove {
		dependants = Set.new;
		this.freeSynthAndBuffer;
		super.remove;	
	}
	
	freeSynthAndBuffer {
		object.free;
		buffer.free;
	}

}

