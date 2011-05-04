
/* 

Inspired by Spectrogram Quark of Thor Magnusson and Dan Stowell. 

This here is a redo using UniqueObject subclasses to simplify the 
synchronization between window, synth and routine. 

fft sizes > 1024 are not supported, because that is the largest size of a buffer that 
can be obtained with buf.getn at the moment

NOT YET DONE!

*/

Spectrograph : UniqueWindow {
	var <bounds, <server, <rate, <bufsize, <>stopOnClose = true;
	var <userview, <image, <imgWidth, <imgHeight;
	var scrollWidth, scrollImage, clearImage;
	var <windowIndex = 0;
	
	var <drawProcesses; // array of objecs that add display graphics to the image
	
	*start { | name, bounds, server, rate = 0.25, bufsize = 1024 |
		^this.new	(name, bounds, server ? Server.default, rate = 1, bufsize = 1024).start;
	}
	
	*new { | name, bounds, server, rate = 0.025, bufsize = 1024 |
		name = format("%:%", name = name ? "spectrogram", server = server ? Server.default).asSymbol;
		^super.new(name, bounds, server, rate, bufsize);
	}
	
	init { | argBounds, argServer, argRate, argBufsize |
		var window; // just for naming convenience
		bounds = argBounds ?? { Window.centeredWindowBounds(600) };
		server = argServer;
		rate = argRate;
		bufsize = argBufsize;
		window = object = Window(this.name, bounds);
//		this.initViews;
		this.addWindowOnCloseAction;
		this.front;
	}

	initViews {
		// userview = ...
		// image = ...
		scrollWidth = (imgWidth * 0.25).round(1).asInteger;
		scrollImage = Int32Array.fill(imgHeight * (imgWidth - scrollWidth), 0);
		clearImage = Int32Array.fill(imgHeight * scrollWidth, 255);
	}
	
		
	start {
		var poller;
		poller = FFTsynthPoller(this.name, server).rate_(rate).bufSize_(bufsize);
		poller addListener: this;
		this onClose: { poller removeListener: this }; 
		poller.addNotifier(this, this.removedMessage, { 
			if (stopOnClose) { poller.stop; } });
		poller.start;
	}
	
	update { | index, fftFrame, magnitudes |
		// Received from the FFTsynthPoller each time an fft frame is polled.
		// This message is sent by FFTsynthPoller within a deferred function. 
		// So graphics drawing primitives can run. 
		postf("Spectrogram updating: %, %, %\n", index, fftFrame.size, magnitudes.size);
//		this.scroll(index);
//		drawProcesses do: _.update(index, fftFrame, magnitudes);
//		userview.refresh;
	}

	scroll { | index |
		windowIndex = index;
		if (windowIndex >= imgWidth) {
			windowIndex = windowIndex % scrollWidth; 
			if (windowIndex == 0) {	// the frame has reached the rightmost end of the drawing window ...
			// ... so scroll the rest of the image to the left
				image.loadPixels(scrollImage, Rect(scrollWidth, 0, imgWidth - scrollWidth, imgHeight), 0);
				image.setPixels(scrollImage, Rect(0, 0, imgWidth - scrollWidth, imgHeight), 0); 
				image.setPixels(clearImage, Rect(imgWidth - scrollWidth, 0, scrollWidth, imgHeight), 0);
			};
			windowIndex = windowIndex + (imgWidth - scrollWidth);
		};
	}		

	name { ^key[1] }

}
