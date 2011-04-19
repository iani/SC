// changelog:
//	- 30-Mar-10 made cross-platform, fixed relativeOrigin issue
// modifications by IZ 2011 04 17 f

Spectrogram2 {
	classvar <>defaultFFTBufSize = 1024, <>colorSize = 64, <colorScaleExp = 0.5;
	var <server;
	var <window, windowBounds;
	var <fftbuf, fftDataArray, fftSynth;
	var inbus = 0, <>rate = 25;
	var <bufSize, binfreqs;	// size of FFT
	var <frombin, <tobin;
	var image, imgWidth, imgHeight, <>intensity = 1, runtask;
	var color, background, colints; // colints is an array of integers each representing a color
	var userview, mouseX, mouseY, freq, drawCrossHair = false; // mYIndex, mXIndex, freq;
	var <>crosshairColor, running = true;
	// track the iteration of polling bus values and its relative position in the window: 
	var <index = 0, <windowIndex = 0, <lastFrameIndex;
	var <windowparent, <bounds, <lowfreq, <highfreq;

	*new { | parent, bounds, bufSize, color, background, lowfreq = 0, highfreq = inf |
		^super.new.initSpectrogram(parent, bounds, bufSize, color, background, lowfreq, highfreq);
	}

	initSpectrogram { arg parent, boundsarg, bufSizearg, col, bg, lowfreqarg, highfreqarg;
		bufSize = bufSizearg ? defaultFFTBufSize; // fft window
		background = bg ? Color.black;
		color = col ? Color(1, 1, 1); // white by default
		crosshairColor = Color.white;

		windowparent = parent;
		bounds = boundsarg;
		lowfreq = lowfreqarg;
		highfreq = highfreqarg;
		
		server = Server.default;

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
		binfreqs = bufSize.collect({ | i | ((server.sampleRate / 2) / bufSize) * (i + 1) });
		this.sendSynthDef;
		tobin = binfreqs.indexOf((highfreq / 2).nearestInList(binfreqs)) min: (bufSize.div(2) - 1);
		frombin = binfreqs.indexOf((lowfreq / 2).nearestInList(binfreqs)) max: 0;
		fftDataArray = Int32Array.fill((tobin - frombin + 1), 0);
		if (userview.isNil or: { userview.notClosed.not }) { this.createWindow(windowparent, bounds); };
	}

	sendSynthDef {
		SynthDef(\spectroscope, { | inbus = 0, buffer = 0 |
			FFT(buffer, InFeedback.ar(inbus));
		}).send(server);
	}

	createWindow { | parent, bounds |
		windowBounds = bounds ?? { Rect(0, 0, 500, 200) };
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
			this.stopruntask;
			fftbuf.free;
			this.removeFromNotifiers;
		}).front;
	}

	setUserView {arg window, bounds;

		userview = UserView(window, bounds)
			.focusColor_(Color.white.alpha_(0))
			.resize_(5)
			.drawFunc_({arg view;
				var b = view.bounds;
				lastFrameIndex = windowIndex - imgWidth + 1; 
				Pen.use {
					Pen.scale( b.width / imgWidth, b.height / imgHeight );
					// notify other polling objects that they can draw with Pen.setPixels  here
					NotificationCenter.notify(this, \drawImage, image, this);
					Pen image: image;
					// notify other polling objects that they can draw with other Pen operations here
					NotificationCenter.notify(this, \drawPen, userview, this);
				};
				if( drawCrossHair, {
					Pen.color = crosshairColor;
					Pen.addRect( b.moveTo( 0, 0 ));
					Pen.clip;
					Pen.line(0@mouseY, b.width@mouseY);
					Pen.line(mouseX@0, mouseX@b.height);
					Pen.font = Font( "Helvetica", 10 );
					Pen.stringAtPoint( "freq: " + freq.asString, mouseX + 20@mouseY - 15);
					Pen.stroke;
				});
			})
			.mouseDownAction_({|view, mx, my|
				this.crosshairCalcFunc(view, mx, my);
				drawCrossHair = true;
				view.refresh;
			})
			.mouseMoveAction_({|view, mx, my| 
				this.crosshairCalcFunc(view, mx, my);
				view.refresh;
			})
			.mouseUpAction_({|view, mx, my|Ê 
				drawCrossHair = false;
				view.refresh;
			});
	}

	toggle { if (running) { this.stop } { this.start } }

	start { running = true; if (runtask.isNil) { this.startruntask; }; }
	
	startruntask {
		// these vars are for temporary tests of osc round trip time:
		var oscSentTime, oscReceivedTime, oscLapseTime, scrollWidth;
		// these var are for tweaking the coloring mechanism: 
		var scaled, min, max;
		// these vars are needed for scrolling the bit image to the left: 
		var scrollImage, clearImage;
		if (server.serverRunning.not) {
			server.boot;
			^"booting server to start spectrogram task".postln;
		};
		"starting spectrogram task".postln;
		// does this scroll width setting belong here? it works but could be better organized?
		scrollWidth = (imgWidth * 0.25).round(1).asInteger;
		scrollImage = Int32Array.fill(imgHeight * (imgWidth - scrollWidth), 0);
		clearImage = Int32Array.fill(imgHeight * scrollWidth, 255);
		if (runtask.notNil) { ^this };
		runtask = {
			this.recalcGradient;
			// todo: use LocalBuf for compactness of code.
			// this means the fftSynth has to be re-created each time that the 
			// fft window size is changed by the user
			if (fftbuf.notNil) { fftbuf.free };
			fftbuf = Buffer.alloc(server, bufSize);
			0.1.wait;
			fftSynth = Synth(\spectroscope, [\inbus, inbus, \buffer, fftbuf]);
			0.1.wait;
			windowIndex = index % imgWidth;
			while {
				server.serverRunning and: { running } and: { userview.notClosed }
			}{
				windowIndex = index;
				if (windowIndex >= imgWidth) {
					windowIndex = windowIndex % scrollWidth; 
					if (windowIndex == 0) {
						image.loadPixels(scrollImage, Rect(scrollWidth, 0, imgWidth - scrollWidth, imgHeight), 0);
						image.setPixels(scrollImage, Rect(0, 0, imgWidth - scrollWidth, imgHeight), 0); 
						image.setPixels(clearImage, Rect(imgWidth - scrollWidth, 0, scrollWidth, imgHeight), 0);
					};
					windowIndex = windowIndex + (imgWidth - scrollWidth);
				};

				// get fft data and draw them when received:
				oscSentTime = Process.elapsedTime;
				fftbuf.getn(0, bufSize, { | buf |
					var magarray, complexarray;
					var persistentWindowIndex;
					persistentWindowIndex = windowIndex;
					oscReceivedTime = Process.elapsedTime;
					oscLapseTime = oscReceivedTime - oscSentTime;
					magarray = buf.clump(2)[frombin .. tobin].flop;

					complexarray  = log10(
						1 + 
						Complex(
							Signal.newFrom(magarray[0]), Signal.newFrom(magarray[1])
						).magnitude.reverse
					).clip(0, 1) * intensity;
						
  					complexarray.do({ | val, i |
						fftDataArray[i] = colints.clipAt((val * colorSize).round);
					});
					{	// correct: in sync with data, and index protected
						image.setPixels(fftDataArray, Rect(persistentWindowIndex, 0, 1, (tobin - frombin + 1)));
						userview.refresh;	
					}.defer;
				});
				// WRONG!: frames are missed if we defer out of sync of receiving the fft data:
//				if (userview.notClosed) { 
//					userview.refresh
//				}; // must be here to cleanly erase previous frames
				index = index + 1;
				rate.reciprocal.wait; // framerate
			};
			this.cleanupServerObjects;
		}.fork(AppClock); // must be AppClock for consistent timing in the userview.refresh call.
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

	inbus_ {arg inbusarg;
		inbus = inbusarg;
		fftSynth.set(\inbus, inbus);
	}

	color_ {arg colorarg;
		color = colorarg;
		this.recalcGradient;
	}	
	
	background_ {arg backgroundarg;
		background = backgroundarg;
		this.prCreateImage( userview.bounds.width );
		this.recalcGradient;
//		userview.backgroundImage_(image, 10);
		userview.refresh;
	}

	prCreateImage { arg width;
		if( image.notNil, { image.free });
		imgWidth = width;
		imgHeight = (tobin - frombin + 1); // bufSize.div(2);
		image = Image.color(imgWidth@imgHeight, background);
	}

	setBufSize_ {arg buffersize, restart=true;
		if(buffersize.isPowerOfTwo) {
			this.stopruntask;
			bufSize = buffersize;
			{ fftbuf.free }.try;
			fftbuf = Buffer.alloc(server, bufSize, 1, { if(restart, {this.startruntask}) }) ;
			binfreqs = bufSize.collect({ | i | ((server.sampleRate / 2) / bufSize)*(i + 1) });
			tobin = bufSize.div(2) - 1;
			frombin = 0;
			fftDataArray = Int32Array.fill((tobin - frombin + 1), 0);
			this.setWindowImage( userview.bounds.width );
		}{
			"Buffersize has to be power of two (256, 1024, 2048, etc.)".warn;
		};
	}

	colorScaleExp_ { | argExp |
		colorScaleExp = argExp;
		this.recalcGradient;	
	}

	recalcGradient {
		var colors;
		colors = (1..colorSize).pow(colorScaleExp).normalize.collect(blend(background, color, _));
		colints = colors.collect({ | col | Image colorToPixel: col });
	}

	crosshairCalcFunc { | view, mx, my |
		mouseX = (mx - 1.5).clip(0, view.bounds.width);
		mouseY = (my - 1.5).clip(0, view.bounds.height); 
		freq = binfreqs[
			((view.bounds.height) - mouseY).round(1)
				.linlin(0, view.bounds.height, frombin * 2, tobin * 2).floor(1)
		].round(0.01);
	}

	setWindowImage { arg width;
		this.prCreateImage( width );
		index = 0;
	}
	
	removeFromNotifiers {
		// remove yourself from notifications when window closes. 
		CmdPeriod.remove(this);
		ServerBoot.remove(this, server);
		/* TODO: must also remove NotificationCenter registrations and notify all objects listening 
			to this that it has closed
		*/
//		NotificationCenter.remove(this); // ???
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

