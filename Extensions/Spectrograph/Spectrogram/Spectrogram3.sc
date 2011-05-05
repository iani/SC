// changelog:
//	- 30-Mar-10 made cross-platform, fixed relativeOrigin issue
// modifications by IZ 2011 04 17 f

/*
Redoing towards modularization

*/

Spectrogram3 {
	/* fft sizes > 1024 are not supported, because that is the largest size of a buffer that 
		can be obtained with buf.getn at the moment
	*/
	classvar <>defaultFFTBufSize = 1024, <>colorSize = 64, <colorScaleExp = 0.5;
	var <server;
	var <window, windowBounds;
	var <fftbuf, fftDataArray, fftSynth;
	var inbus = 0, <>rate = 40;
	var <bufSize, <binfreqs;	// size of FFT
	var <image, <imgWidth, <imgHeight, <>intensity = 1, runtask;
	var color, background, colints; // colints is an array of integers each representing a color
	var userview, mouseX, mouseY, freq, drawCrossHair = false; // mYIndex, mXIndex, freq;
	var <>crosshairColor, running = true;
	// track the iteration of polling bus values and its relative position in the window: 
	var <index = 0, <windowIndex = 0, <lastFrameIndex;
	// is windowparent really needed? what for?
	var <windowparent, <bounds;
	// cache the reverses of binfreqs and framemagnitudes for processes that calculate + draw frequencies
	// this is faster than calculating the reverse offset of the index when drawing
	var <binfreqsReversed;
	
	var <imageObjects, <penObjects;	// objects that draw additional stuff on the display

	// persistentWindowIndex is indispensable for the drawing of fft frames at the right index on the image
	// !!!!!!!!!!!!
	var <persistentWindowIndex;	// ALL image processes should use this to stay in sync with fft pixel setting

	var <drawSpectrogram, <drawCrosshair;	// the two built-in drawing objects


	*new { | parent, bounds, bufSize, color, background |
		^super.new.initSpectrogram(parent, bounds, bufSize, color, background);
	}

	initSpectrogram { arg parent, boundsarg, bufSizearg, col, bg;
		bufSize = bufSizearg ? defaultFFTBufSize; // fft window
		background = bg ? Color.black;
		color = col ? Color(1, 1, 1); // white by default
		crosshairColor = Color.white;

		windowparent = parent;
		bounds = boundsarg;
		
		server = Server.default;
		drawSpectrogram = DrawSpectrogram(bufSize, 64, 0.5, 1, Color.white, Color.black);

		CmdPeriod.add(this);
		ServerBoot.add(this, server);

		if (server.serverRunning) {
			this.doOnServerBoot;
		}{
			"spectrogram booting default server".postln;
			server.boot;
		};
	}

	cmdPeriod {
		runtask = nil;
		if (running) { this.startruntask };
	}
	
	doOnServerBoot {
		{
			0.1.wait;
			this.initServerStuff;
			if (running and: { runtask.isNil }) {
				0.1.wait;
				this.startruntask;
			};
		}.fork(AppClock);	
	}

	initServerStuff { 
		this.setBinfreqs; // = bufSize.collect({ | i | ((server.sampleRate / 2) / bufSize) * (i + 1) });
		this.sendSynthDef;
		fftDataArray = Int32Array.fill(bufSize / 2, 0);
		if (userview.isNil or: { userview.notClosed.not }) { this.createWindow(windowparent, bounds); };
	}

	sendSynthDef {
		Udef(\spectroscopeDef, { | inbus = 0, buffer = 0 |
			FFT(buffer, InFeedback.ar(inbus));
		}, server: server ? Server.default);
	}

	createWindow { | parent, bounds |
		windowBounds = bounds ?? { Window.centeredWindowBounds(100) };
		if (parent.isNil) {
			parent = Window("Spectrogram2", windowBounds);
			bounds = windowBounds.moveTo(0, 0);
		};
		window = parent;
		this setWindowImage: bounds.width;
		window.view.keyDownAction = { | view, char |
			switch (char, 
				$f, { this.toggleMaxScreen },
				$t, { this.toggle }
			);
		};
		this.setUserView(window, bounds);
		window.onClose_({
			image.free;
			userview = nil;
			this.stopruntask;
			fftbuf.free;
			this.freeNotifiedDependants;
		}).front;
	}

	setUserView {arg window, bounds;
		var testImage;
//		testImage = Int32Array.fill(imgHeight, Image colorToPixel: Color.red);
		userview = UserView(window, bounds)
			.focusColor_(Color.white.alpha_(0))
			.resize_(5)
			.drawFunc_({arg view;
				var b = view.bounds;
				lastFrameIndex = windowIndex - imgWidth + 1; 
				Pen.use {
					Pen.scale( b.width / imgWidth, b.height / imgHeight );
					Pen image: image;
				};
				// notify other polling objects that they can draw with other Pen operations here
				NotificationCenter.notify(this, \drawPen, b);
			})
			.mouseDownAction_({|view, mx, my|
				this.crosshairCalcFunc(view, mx, my);
				this.mouseTrigger(true); 		// experimental
				view.refresh;
			})
			.mouseMoveAction_({|view, mx, my| 
				this.crosshairCalcFunc(view, mx, my);
				view.refresh;
			})
			.mouseUpAction_({|view, mx, my|Ê 
				this.mouseTrigger(false);		// experimental
				view.refresh;
			});
	}

	/* 	This could just as well be written in-line as it was in Spectrogram(1).
		But the present way shows an example of how to attach further drawing methods 
		by other objects on the same spectrogram.
	*/
	mouseTrigger { | on | // experimental
		if (on) {
			NotificationCenter.register(this, \drawPen, thisMethod, { | vbounds |
				Pen.color = crosshairColor;
				Pen.addRect( vbounds.moveTo( 0, 0 ));
				Pen.clip;
				Pen.line(0@mouseY, vbounds.width@mouseY);
				Pen.line(mouseX@0, mouseX@vbounds.height);
				Pen.font = Font( "Helvetica", 10 );
				Pen.stringAtPoint( "freq: " + freq.asString, mouseX + 20@mouseY - 15);
				Pen.stroke;
			});
		}{
			NotificationCenter.unregister(this, \drawPen, thisMethod);
		}
	}

	toggle {
		if (running) { this.stop } { this.start } 
	}

	start { running = true; if (runtask.isNil) { this.startruntask; }; }
	
	startruntask {
		// these vars are needed for scrolling the bit image to the left: 
		var scrollImage, clearImage, scrollWidth;
		var fullScrollImage;
		fullScrollImage = Int32Array.fill(image.height * (image.width - 1), 0);

		if (server.serverRunning.not) {
			server.boot;
			^"booting server to start spectrogram task".postln;
		};
		"starting spectrogram task".postln;
		// does this scroll width setting belong here? it works but could be better organized?
		scrollWidth = (imgWidth * 0.25).round(1).asInteger;
		scrollImage = Int32Array.fill(imgHeight * (imgWidth - scrollWidth), 0);
		clearImage = Int32Array.fill(imgHeight * scrollWidth, 255);
		if (fftbuf.notNil) { fftbuf.free };
		fftbuf = UniqueBuffer(\fft, server, bufSize, 1);
		fftSynth = UniqueSynth(\fft, \spectroscopeDef, [\inbus, inbus, \buffer, fftbuf], server);
			
		fftSynth.rsynca({
			while {
				server.serverRunning and: { running } and: { userview.notClosed }
			}{

//				windowIndex = Scroll(index, image, imgWidth, scrollWidth, imgHeight, scrollImage, clearImage);
				fftbuf.object.getn(0, bufSize, { | buf |
					var magnitudes;
//				windowIndex = Scroll(index, image, imgWidth, scrollWidth, imgHeight, scrollImage, clearImage);
					persistentWindowIndex = windowIndex;
					magnitudes = FFTMagnitudes(buf);
					{	// correct: in sync with data, and index protected

				image.loadPixels(fullScrollImage, Rect(1, 0, image.width - 1, image.height));
				image.setPixels(fullScrollImage, Rect(0, 0, image.width - 1, image.height));
						drawSpectrogram.update(image, image.width - 1, magnitudes);
						// imageObjects are sent update_(image, persistentWindowIndex, magnitudes) here
//						if (scroll) { Scroll.scroll };
						if (userview.notNil) { userview.refresh; };
//						index = index + 1;
						}.defer;
				});
				index = index + 1;
				rate.reciprocal.wait; // framerate
			};
			this.cleanupServerObjects;
		});
	}

	cleanupServerObjects {
		"spectrogram stopped".postln;
		runtask = nil;
		if (server.serverRunning) { fftSynth.free; };
	}

	lastDisplayableFrame {
		// the last frame that can be displayed, relative to the index of the current frame is: 
		^index - imgWidth + 1 max: 0;
	}

	stop { this.stopruntask }
	stopruntask {
		running = false;
		runtask.stop;
		runtask = nil;
		{ fftSynth.free }.try;
	}

	addImageObject { | object | imageObjects = imageObjects add: object; }
	removeImageObject { | object | imageObjects remove: object; }
	addPenObject { | object | penObjects = penObjects add: object; }
	removePenObject { | object | penObjects remove: object; }

	inbus_ {arg inbusarg;
		inbus = inbusarg;
		fftSynth.set(\inbus, inbus);
	}

	color_ {arg colorarg; color = colorarg; }
	
	background_ {arg backgroundarg;
		background = backgroundarg;
		this.prCreateImage( userview.bounds.width );
		userview.refresh;
	}

	prCreateImage { arg width;
		if( image.notNil, { image.free });
		imgWidth = width;
		imgHeight = bufSize / 2; 
		image = Image.color(imgWidth@imgHeight, background);
	}

	setBufSize_ {arg buffersize, restart=true;
		if(buffersize.isPowerOfTwo) {
			this.stopruntask;
			bufSize = buffersize;
			{ fftbuf.free }.try;
			fftbuf = Buffer.alloc(server, bufSize, 1, { if(restart, {this.startruntask}) }) ;
			this.setBinfreqs;
			fftDataArray = Int32Array.fill(bufSize, 0);
			this.setWindowImage(userview.bounds.width);
		}{
			"Buffersize has to be power of two (256, 1024, 2048, etc.)".warn;
		};
	}

	setBinfreqs { | argfreqs |
		// this most closely matches actual frequency of a sine tone test: 
		binfreqs = bufSize.collect({ | i | server.sampleRate / bufSize * i });
		binfreqsReversed = binfreqs.reverse;
	}

	colorScaleExp_ { | argExp |
		colorScaleExp = argExp;
	}


	crosshairCalcFunc { | view, mx, my |
		mouseX = (mx - 1.5).clip(0, view.bounds.width);
		mouseY = (my - 1.5).clip(0, view.bounds.height); 
		freq = binfreqs[
			((view.bounds.height) - mouseY).round(1)
			// IZ adjusting display of numbers to match more closely actual frequency
			// based on sine tone pitch detection tests
				.linlin(0, view.bounds.height, 0, bufSize / 2).floor(1) - 2
		].round(0.01);
	}

	setWindowImage { arg width;
		this.prCreateImage( width );
		index = 0;
	}
	
	freeNotifiedDependants {
		// remove yourself from notifications when window closes. 
		CmdPeriod.remove(this);
		ServerBoot.remove(this, server);
		imageObjects do: _.spectrogramClosed;
		penObjects do: _.spectrogramClosed;
		imageObjects = nil;
		penObjects = nil;
	}
	
	toggleMaxScreen {
		if (window.bounds.width != Window.screenBounds.width and: {
			window.bounds.height < (Window.screenBounds.height - 60)
			}
		) {
			window.bounds = Window.screenBounds;
			window.front;
		}{
			window.bounds = windowBounds;
		};
	
	}

	
}

