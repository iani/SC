
/*

The FFTsynthPoller creates (and starts) an FFTpollSynth when it is created. 
It receives the fft data from the FFTpollSynth and sends it to any interested clients via: 

NotificationCenter.notify(<thisPoller's key>, \fft, data);

If synth processes are stopped with Cmd-., the FFTsignalPoller will restart the FFTpollSynth. 

If the bufSize is changed, it will stop the current FFTpollSynth and restart a new one with the new buf size. 
(This is safer than attempting to replace the current buffer with a new one, because one does not have to check if the server is running).


*/

FFTsynthPoller : AbstractUniqueServerObject {
	var <rate; 			// how long to wait between each poll.
	var <bufSize; 		// the size of the FFT buffer. Should not be larger than 1024
	var <in = 0;			// The number of the audio bus to analyse and poll
	var <asKey;			// The FFTpollSynth uses as key. Clients can use it to get notified
						// about every change in the state and to receive the data
	var <fftMagnitudes;	// the fftMagnitude array of the last FFT buffer polled
	var <listeners;		// these are the clients that get the data, for drawing or other processing
	var <>post = false;	// turn on posting of polling for debugging purposes
	
	*new { | key, server, rate = 0.025, bufSize = 1024, in = 0, start = true |
		server = server ? Server.default;
		^super.new(key, server, rate, bufSize, in, start);
	}

	init { | server, argRate, argBufSize, argIn, start |
		rate = argRate; bufSize = argBufSize; in = argIn; 
		asKey = key[2];
		if (start) { this.start; };
		listeners = Set.new;
		this.onClose({
//			listeners.asArray do: this.removeListener(_); // (TODO)
			listeners = nil;
		});
	}

/* All communication except fft data updates is done via NotificationCenter using asKey as 
The id of this poller. This means objects can register to be notified by the poller under
its "asKey" even before the poller exists. 
Under such a scheme, a listener may add itself as listener to the poller as soon as it receives
the message notification \start. 
*/

	start {
		CmdPeriod.add(this);
		this.makeFFTpollSynth;
		NotificationCenter.notify(asKey, \start, this);
	}

	cmdPeriod {
		NotificationCenter.notify(asKey, \cmdPeriod, this);
		/* FFTpollSynth has been stopped by cmdPeriod, remake it here */
		this.makeFFTpollSynth;
	}

	makeFFTpollSynth {
		FFTpollSynth(this, this.server.asTarget, rate, bufSize, in);
	}

	stop {
		CmdPeriod.remove(this);
		NotificationCenter.notify(asKey, \stop, this);
	}

	// setting instance variables and notifying of changes
	rate_ { | argRate = 0.025 |
		rate = argRate;	
		NotificationCenter.notify(asKey, \rate, rate);
	}

	bufSize_ { | argBufSize = 1024 |
		// FFTpollSynth receives this and restarts. see FFTpollSynth:connectToPoller
		bufSize = argBufSize;
		NotificationCenter.notify(asKey, \bufSize, bufSize);
	}

	update { | index, data |
		// fft data received from FFTpollSynth. 
		// Caclulate magnitudes and send data to all listeners. 
		// Increment index of poll count
		var real, imaginary;
		object = data; 
		#real, imaginary = data.clump(2).flop;
		fftMagnitudes = Complex(Signal.newFrom(real), Signal.newFrom(imaginary)).magnitude;
		if (post) { 
			postf("% polled. Index: %, magsize: %, fftsize: %\n", 
				asKey, index, fftMagnitudes.size, data.size);
		};
		listeners do: _.update(index, fftMagnitudes, data);
	}

	// A simplified Observer pattern (Model-dependants) using just an array
	// Note: This overrides the addListener method of superclass UniqueObject.
	addListener { | object | listeners add: object; }

	removeListener { | object | listeners remove: object; }

	// TODO: Enable listeners to set your rate, bufSize etc. through 
	// NotificationCenter. 

}