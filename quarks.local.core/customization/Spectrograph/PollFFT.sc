/* 
A SynthResource which starts an FFT analysis synth on any audio input, polls the fft buffer at a steady rate, and sends the resulting buffer data to any dependants. 

It is always running as long as its server is booted. It stops and removes itself when sent the messages free or remove. 

*/

PollFFT : Resource {
	var <server, <>rate = 0.04, <bufSize, <in = 0;
	var <synthdef, <buffer, <index = 0;
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
		this.clearDependants;		// run as long as server is booted:
		ServerPrep(server).addToServerTree(this, { this.makeSynth }); 
	}

	makeSynth {
		buffer = BufferResource(key[2], server, bufSize);
		synthdef = Udef(\fft, { | in = 0, buf = 0 |
			FFT(buf, InFeedback.ar(in));
		}, server: server);
		object = SynthResource(\fft, \fft, [\in, in, \buf, buffer.object.bufnum], 
			server, \addToTail);
		object.onStart({ this.notify(\synthStarted); });
		object.rsyncs({
			var fftbuf;
			fftbuf = buffer.object;
			loop {
				fftbuf.getn(0, bufSize, { | buf |
					#real, imaginary = buf.clump(2).flop;
					magnitudes = Complex(Signal.newFrom(real),
						Signal.newFrom(imaginary)).magnitude;
					dependants do: _.update(index, buf, magnitudes);
					index = index + 1;
				});
				rate.wait;
			};
		});
	}
	
	addDependant { | object | dependants add: object }
	removeDependant { | object | dependants remove: object }
	clearDependants { dependants = Set.new }
	
	free { this.remove }
	remove {
		dependants = nil;
		this.freeSynthAndBuffer;
		super.remove;	
	}
	
	freeSynthAndBuffer {
		object.free;
		buffer.free;
	}

}

