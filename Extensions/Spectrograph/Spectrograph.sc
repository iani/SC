/* 

!!!!!!!!!!!!!!! UNDER DEVELOPMENT !!!!!!!!!!!!!!!!

Inspired by Spectrogram Quark of Thor Magnusson and Dan Stowell. 

This here is a redo using UniqueObject subclasses to simplify the 
synchronization between window, synth and routine. 

fft sizes > 1024 are not supported, because that is the largest size of a buffer that 
can be obtained with buf.getn at the moment.

Getting of buffers with larger sizes is documented in SC Help, but for spectrograph display purposes this would be overkill at the moment. 

*/

Spectrograph : UniqueWindow {
	classvar <current;
	var <bounds, <server, <rate, <bufsize, <>stopOnClose = true;
	var <userview, <image, <imgWidth, <imgHeight;
	var scrollWidth, scrollImage, clearImage;
	var <windowIndex = 0;

	var <drawProcesses; // array of objecs that add display graphics to the image
	
	*start { | name, bounds, server, rate = 0.025, bufsize = 1024 |
		^this.new	(name, bounds, server ? Server.default, rate, bufsize = 1024).start;
	}
	
	*new { | name, bounds, server, rate = 0.025, bufsize = 1024 |
		name = format("%:%", name = name ? "Spectrograph", server = server ? Server.default).asSymbol;
		^current = super.new(name, bounds, server, rate, bufsize);
	}
	
	init { | argBounds, argServer, argRate, argBufsize |
		var window; // just for naming convenience
		bounds = argBounds ?? { Window.centeredWindowBounds(100) };
		server = argServer;
		rate = argRate;
		bufsize = argBufsize;
		window = object = Window(this.name, bounds);
		this.initViews;
		this.addWindowOnCloseAction;
		this.front;
	}

	initViews {
		userview = UserView(object, object.view.bounds);
		userview.drawFunc = { | view |
			var b = view.bounds;
			Pen.use {
				Pen.scale( b.width / imgWidth, b.height / imgHeight );
				Pen image: image;
			};
			// notify other polling objects that they can draw with other Pen operations here
			// may be replaced with simple dependant array mechanism
			NotificationCenter.notify(this, \drawPen, b);
		};
		imgWidth = userview.bounds.width;
		imgHeight = bufsize / 2;
		scrollWidth = (imgWidth / 4).round(1).asInteger;
		scrollWidth = (imgWidth * 0.25).round(1).asInteger;
		// Scroll image is an array used when scrolling to copy the already drawn pixels 
		// from the right part of the image to the left part
		scrollImage = Int32Array.fill(imgHeight * (imgWidth - scrollWidth), 0);
		this.background = Color.black; // method can be called at any time to change color
		this onClose: { image.free; };
	}

	background_ { | color |
		// This method can be called at any time to change the background color
		if (image.notNil) { image.free };
		image = Image.color(imgWidth@imgHeight, color);
		// clearImage is an image used when scrolling to erase the right part of the screen 
		clearImage = Int32Array.fill(imgHeight * scrollWidth, Integer.fromColor(color));
	}	
		
	start {
		var poller;
		poller = FFTsynthPoller(this.name, server).rate_(rate).bufSize_(bufsize);
		this.name.postln;
		poller addListener: this;
		this onClose: { poller removeListener: this }; 
		poller.addNotifier(this, this.removedMessage, { 
			if (stopOnClose) { poller.stop; } });
		poller.start;
	}
	
	// Added for symmetry, but should only be used for debugging.
	// (Normally you just close the Spectrograph window.)
	*stop { if (current.notNil) { current.stop } }
	stop { // only for debugging purposes. Normally just close the Spectrograph window.
		var poller;
		if ((poller = FFTsynthPoller.at(this.name.postln, server)).postln.notNil) { poller.stop; };
	}
	
	update { | index, fftFrame, magnitudes |
		// Received from the FFTsynthPoller each time an fft frame is polled.
		// This message is sent by FFTsynthPoller within a deferred function. 
		// So graphics drawing primitives can run. 
		postf("Spectrogram updating: %, %, %\n", index, fftFrame.size, magnitudes.size);
		this.scroll(index);
		drawProcesses do: _.update(index, fftFrame, magnitudes);
		userview.refresh;
	}

	scroll { | index |
		windowIndex = index;
		if (windowIndex >= imgWidth) {
			windowIndex = windowIndex % scrollWidth; 
			if (windowIndex == 0) {	// the frame has reached the rightmost end of the drawing window ...
			// ... so scroll the rest of the image to the left
				"SCROLLING".postln;
				image.loadPixels(scrollImage, Rect(scrollWidth, 0, imgWidth - scrollWidth, imgHeight), 0);
				image.setPixels(scrollImage, Rect(0, 0, imgWidth - scrollWidth, imgHeight), 0); 
				image.setPixels(clearImage, Rect(imgWidth - scrollWidth, 0, scrollWidth, imgHeight), 0);
			};
			windowIndex = windowIndex + (imgWidth - scrollWidth);
		};
	}		

	name { ^key[1] }

}
