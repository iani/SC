/* 
A UniqueSynth which starts an FFT analysis synth on any audio input, polls the fft buffer at a steady rate, and sends the resulting buffer data to an FFTsignalPoller. 

Usage: 

FFTpollSynth(SignalPoller('fftp'));

*/

FFTpollSynth : UniqueSynth {
	var <poller, <server, <rate = 0.025, <buffer, <bufSize;
	
	*new { | poller, server, rate = 0.025, bufSize = 1024, in = 0 |
		^super.new(poller.asKey, \fft, nil, server ? Server.default, \addToHead, rate, bufSize, in, poller)
	}

	init { | target, defName, args, addAction, argRate, argBufSize, in, argPoller |
		server = target.server;
		rate = argRate;
		bufSize = argBufSize;
		poller = argPoller;
//		postf("init FFTpollSynth bufSize: %\n", bufSize);
		buffer = UniqueBuffer(key[2], server, bufSize);
//		postf("init FFTpollSynth buffer created: %\n", buffer);
//		this.onEnd({ buffer.free });
		Udef(\fft, { | in = 0, buf = 0 |
			FFT(buf, InFeedback.ar(in));
		});
//		postf("FFTpollSynth init, posting buf args sent to fft synth(def): key, buffer\n");
//{
			super.init(server, \fft, [\in, in, \buf.postln, buffer.postln], \addToHead);

		this.rsynca({
			var fftbuf, bufnum, notifyKey;
			fftbuf = buffer.object;
			bufnum = fftbuf.bufnum;
			notifyKey = key[2];
			1.wait;	// strange error /b_getn index out of range when starting after playing other buffers ?
			loop {
				postf("fftbuf: %, bufSize: %, bufnum, %\n", fftbuf, bufSize, bufnum);
				fftbuf.getn(0, bufSize, { | buf |
					poller.fftData = buf;
				});
				rate.wait;	
			};
		});


// }.defer(1);
//{ "deferred creation of synth by 1 second! --- ".post } ! 10;
//"--------------".postln;
		this.connectToPoller;
	}

	prMakeObject { | target, defName, args, addAction |
		var b;
		postln("fftpollsynth ebugging prmakeobject");			args.postln;
		b = args.detect({ | a | a.isKindOf(UniqueBuffer) }).postln;

		object = Synth(\fft, [\out, 0, \buf, b.object.bufnum.postln]);
//		object = { Out.ar(59, FFT(b.object, InFeedback.ar(0))) }.play;
//		object = b.object.play;
//				object = Synth(\default);

//		object = Synth(defName, args, target, addAction);
	}

/* 
The advantage of notifying via NotifiationCenter using a symbol as key is that 
other objects (clients) can register for notification from this Poller, based on its key, 
even before the FFTsynthPoller or the FFTpollSynth are created. 
*/
	
	connectToPoller {
/*
		NotificationCenter.notify(asKey, \start, this);
		NotificationCenter.notify(asKey, \cmdPeriod, this);
		NotificationCenter.notify(asKey, \stop, this);
		NotificationCenter.notify(asKey, \rate, rate);
		NotificationCenter.notify(asKey, \bufSize, bufSize);
		NotificationCenter.notify(asKey, \fft, [object]);
*/
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

