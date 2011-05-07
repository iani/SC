/* 
A UniqueSynth which starts an FFT analysis synth on any audio input, polls the fft buffer at a steady rate, and sends the resulting buffer data to an FFTsignalPoller. 

It is started implicitly by FFTsynthPoller.

It is not designed to be used on its own. 

*/

FFTpollSynth : UniqueObject {
	var <poller, <server, <rate = 0.025, <synthdef, <buffer, <bufSize, <index = 0;
	var <in = 0;

	// TODO: List of timestamps recorded since starting the synth
	// Useful for displaying the time of a certain frame on mouseover: 
//	var <frames; // ... TODO
	*new { | poller, server, rate = 0.04, bufSize = 1024, in = 0 |
		^super.new((poller ? 'test').asKey, server, rate, bufSize, in, poller)
	}

	init { | argServer, argRate, argBufSize, argIn = 0, argPoller |
		server = argServer.asTarget.server;
		rate = argRate;
		bufSize = argBufSize;
		in = argIn;
		poller = argPoller;
//		frames = Frames.new;
//		buffer = UniqueBuffer(key[2], server, bufSize);
//		synthdef = Udef(\fft, { | in = 0, buf = 0 |
//			FFT(buf, InFeedback.ar(in));
//		}, server: server);
//		this.makeSynth;
//		this.connectToPoller;
		ServerPrep(server).addToServerTree(this, { this.makeSynth }); // run as long as server is booted
	}

	makeSynth {
		buffer = UniqueBuffer(key[2], server, bufSize);
		synthdef = Udef(\fft, { | in = 0, buf = 0 |
			FFT(buf, InFeedback.ar(in));
		}, server: server);
//		{ 
		object = UniqueSynth(\fft, \fft, [\in, in, \buf, buffer.object.bufnum], server, \addToTail);
		postf("% added synth %\n", this, object);
		object.rsynca({
			var fftbuf;
			fftbuf = buffer.object;
			loop {
				fftbuf.getn(0, bufSize, { | buf |
//					frames.add;
//					buf.sum.postln;
					poller.update(index, buf);
					index = index + 1;
//					if (index % 25 == 0) { index.postln; }
				});
				rate.wait;
			};
		});
		this.connectToPoller;
		\default.play;
		poller.postln;
			{ server.queryAllNodes; }.defer(2);
			
//		}.defer(1);
	}

/* 
The advantage of notifying via NotifiationCenter using a symbol as key is that 
other objects (clients) can register for notification from this Poller, based on its key, 
even before the FFTsynthPoller or the FFTpollSynth are created. 
*/
	connectToPoller {
		var asKey;
		asKey = poller.asKey;
		this.addNotifier(asKey, \stop, { this.free });
		this.addNotifier(asKey, \rate, { | argRate | rate = argRate });
		this.addNotifier(asKey, \bufSize, { | argBufSize |
			if (argBufSize != bufSize) {
				NotificationCenter.notify(asKey, \makeFFTpollSynth);
				this.free;
			};
		});
		poller.addNotifier(asKey, \makeFFTpollSynth, { poller.makeFFTpollSynth });
	}
}

