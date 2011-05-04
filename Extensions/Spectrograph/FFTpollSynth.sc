/* 
A UniqueSynth which starts an FFT analysis synth on any audio input, polls the fft buffer at a steady rate, and sends the resulting buffer data to an FFTsignalPoller. 

It is started implicitly by FFTsynthPoller.

It is not designed to be used on its own. 

*/

FFTpollSynth : UniqueSynth {
	var <poller, <server, <rate = 0.025, <buffer, <bufSize, <index = 0;
	
	*new { | poller, server, rate = 0.025, bufSize = 1024, in = 0 |
		^super.new(poller.asKey, \fft, nil, server ? Server.default, \addToHead, rate, bufSize, in, poller)
	}

	init { | target, defName, args, addAction, argRate, argBufSize, in, argPoller |
		server = target.server;
		rate = argRate;
		bufSize = argBufSize;
		poller = argPoller;
		buffer = UniqueBuffer(key[2], server, bufSize);
		Udef(\fft, { | in = 0, buf = 0 |
			FFT(buf, InFeedback.ar(in));
		});
		super.init(server, \fft, [\in, in], \addToHead);

		this.rsync({
			var fftbuf, bufnum, notifyKey;
			fftbuf = buffer.object;
			bufnum = fftbuf.bufnum;
			notifyKey = key[2];
			loop {
				fftbuf.getn(0, bufSize, { | buf |
					poller.update(index, buf);
					index = index + 1;
				});
				rate.wait;
			};
		}, SystemClock); // Using AppClock may cause index sync problems (DEBUGGING!)
		this.connectToPoller;
	}

	prMakeObject { | target, defName, args, addAction |
		// need to provide the bufnum here or else it is not properly initialized
		object = Synth(defName, args ++ [\buf, buffer.object.bufnum], target, addAction);
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

