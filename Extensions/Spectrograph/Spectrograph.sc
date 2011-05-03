
/* 

Inspired by Spectrogram Quark of Thor Magnusson and Dan Stowell. 

This here is a redo using UniqueObject subclasses to simplify the 
synchronization between window, synth and routine. 

fft sizes > 1024 are not supported, because that is the largest size of a buffer that 
can be obtained with buf.getn at the moment

NOT YET DONE!

*/

Spectrograph : UniqueWindow {
	var <bounds, <server, <rate, <bufsize, <image, <>stopOnClose = true;
	
	*start { | name, bounds, server, rate = 0.025, bufsize = 1024 |
		^this.new	(name, bounds, server ? Server.default, rate = 0.025, bufsize = 1024).start;
	}
	
	*new { | name, bounds, server, rate = 0.025, bufsize = 1024 |
		name = format("%:%", name = name ? "spectrogram", server = server ? Server.default).asSymbol;
		^super.new(name, bounds, server, rate, bufsize)
	}
	
	init { | argBounds, argServer, argRate, argBufsize |
		var window; // just for naming convenience
		bounds = argBounds ?? { Window.centeredWindowBounds(600) };
		server = argServer;
		rate = argRate;
		bufsize = argBufsize;		
		window = object = Window(this.name, bounds);
		window.onClose = { this.remove };
		window.front;
	}
	
	name { ^key[1] }
	
	start {
		var poller;
		poller = FFTsynthPoller(this.name, server).rate_(rate).bufSize_(bufsize); 
		if (stopOnClose) {
			poller.addNotifier(this, this.removedMessage, { poller.stop; })
		};
	}
	
}
