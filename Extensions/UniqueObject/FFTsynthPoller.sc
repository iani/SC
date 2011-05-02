
/*

The FFTsynthPoller creates (and starts) an FFTpollSynth when it is created. 
It receives the fft data from the FFTpollSynth and sends it to any interested clients via: 

NotificationCenter.notify(<thisPoller's key>, \fft, data);

If synth processes are stopped with Cmd-., the FFTsignalPoller will restart the FFTpollSynth. 

If the bufSize is changed, it will stop the current FFTpollSynth and restart a new one with the new buf size. 
(This is safer than attempting to replace the current buffer with a new one, because one does not have to check if the server is running).


*/

FFTsynthPoller : AbstractUniqueServerObject {
	var <rate, <bufSize, <in = 0;

	var <asKey;	// this is what the FFTpollSynth uses as key
	
	*new { | key, server, rate = 0.025, bufSize = 1024, in = 0, start = true |
		server = server ? Server.default;
		^super.new(key, server, rate, bufSize, in, start);
	}

	init { | server, argRate, argBufSize, argIn, start |
		asKey = key[2];
		rate = argRate; bufSize = argBufSize; in = argIn; 
		postf("fftsynthpoller init server: %, rate: %, bufSize: %\n", server, rate, bufSize);
		if (start) { this.start; };
	}

	start {
		CmdPeriod.add(this);
		this.makeFFTpollSynth;
		NotificationCenter.notify(asKey, \start, this);
	}

	cmdPeriod {
		NotificationCenter.notify(asKey, \cmdPeriod, this);
		this.makeFFTpollSynth;
	}

	makeFFTpollSynth {
		FFTpollSynth(this, this.server, rate, bufSize, in);
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
		bufSize = argBufSize;
		NotificationCenter.notify(asKey, \bufSize, bufSize);
		this.makeFFTpollSynth;
	}

	fftData_ { | data |
		object = data; 	// data.postln; 
		NotificationCenter.notify(asKey, \fft, [object]);
	}
}