/* 
A UniqueSynth which starts an FFT analysis synth on any audio input, polls the fft buffer at a steady rate, and sends the resulting buffer data to an FFTsignalPoller. 

Usage: 

FFTpollSynth(SignalPoller('fftp'));

*/

FFTpollSynth : UniqueSynth {
	var <poller, <server, <>rate = 0.025, <buffer, <bufSize;
	
	*new { | poller, server, rate = 0.025, bufSize = 1024, in = 0 |
		^super.new(poller.asKey, \fft, nil, server ? Server.default, \addToHead, rate, bufSize, in, poller)
	}

	init { | target, defName, args, addAction, argRate, argBufSize, in, poller |
		server = target.server;
		rate = argRate;
		bufSize = argBufSize;
		buffer = UniqueBuffer(key[2], server, bufSize);
		this.onEnd({ buffer.free });
		Udef(\fft, { | in = 0, buffer = 0 |
			FFT(buffer, InFeedback.ar(in));
		});
		super.init(server, \fft, [\in, in, \buffer, buffer], \addToHead);
		this.rsynca({
			var fftbuf, bufnum, notifyKey;
			fftbuf = buffer.object;
			bufnum = fftbuf.bufnum;
			notifyKey = key[2];
			loop {
				fftbuf.getn(bufnum, bufSize, { | buf |
					poller.fftData = buf;
				});
				rate.wait;	
			};
		})
	}
}
