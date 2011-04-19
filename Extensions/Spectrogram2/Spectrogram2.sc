// changelog:
//	- 30-Mar-10 made cross-platform, fixed relativeOrigin issue
// modifications by IZ 2011 04 17 f

Spectrogram2 {
	var <server;
	var window; //, bounds;
	var <fftbuf, fftDataArray, fftSynth;
	var inbus = 0, <>rate = 25;
	var <bufSize, binfreqs;	// size of FFT
	var <frombin, <tobin;
	var image, imgWidth, imgHeight, <>intensity = 5, runtask;
	var color, background, colints; // colints is an array of integers each representing a color
	var userview, mouseX, mouseY, freq, drawCrossHair = false; // mYIndex, mXIndex, freq;
	var <>crosshairColor, running = true;
	// track the iteration of polling bus values and its relative position in the window: 
	var <index = 0, <windowIndex = 0, <lastFrameIndex;
	var <frames;	// holds the times when each frame was drawn by drawFunc;
	var <windowparent, <bounds, <lowfreq, <highfreq;

	*new { | parent, bounds, bufSize = 1024, color, background, lowfreq = 0, highfreq = inf |
		^super.new.initSpectrogram(parent, bounds, bufSize, color, background, lowfreq, highfreq);
	}

	initSpectrogram { arg parent, boundsarg, bufSizearg = 1024, col, bg, lowfreqarg, highfreqarg;
		bufSize = bufSizearg; // fft window
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
		ServerQuit.add(this, server);

		server.waitForBoot({
			this.initServerStuff;
		});
	}

	cmdPeriod {
		if (running) { this.startruntask };
	}
	doOnServerQuit {
//		this.stop;
// 		only stop the poll routine, but keep the started status
		if (runtask.notNil) { "spectrogram stopped".postln; runtask.stop; runtask = nil };
	}
	
	doOnServerBoot {
		if (running and: { runtask.isNil }) {
			
			"starting spectrogram".postln;
			this.initServerStuff;
			
			this.startruntask;	
		}	
	}

	initServerStuff { 
		binfreqs = bufSize.collect({ | i | ((server.sampleRate / 2) / bufSize) * (i + 1) });
		this.sendSynthDef;
		fftbuf = Buffer.alloc(server, bufSize);
		tobin = binfreqs.indexOf((highfreq / 2).nearestInList(binfreqs)) min: (bufSize.div(2) - 1);
		frombin = binfreqs.indexOf((lowfreq / 2).nearestInList(binfreqs)) max: 0;
		fftDataArray = Int32Array.fill((tobin - frombin + 1), 0);
		this.createWindow(windowparent, bounds);
		this.initFrames;
//		if (running) { this.start };
	}

	sendSynthDef {
		SynthDef(\spectroscope, {|inbus=0, buffer=0|
			FFT(buffer, InFeedback.ar(inbus));
		}).send(server);
	}

	initFrames { frames = Frames.new }

	createWindow { | parent, bounds |
		bounds = bounds ?? { Rect(200, 450, 600, 300) };
		window = parent ? Window("Spectrogram2", bounds);
		this setWindowImage: bounds.width;
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
					// any more pixels on the image must be set here before it is sent to pen:
//					frames.add([index, windowIndex]);
					NotificationCenter.notify(this, \drawImage, image, imgWidth, frames, this);
					Pen image: image;
					// experimental draw function added here: 
/*					Pen.color = Color.red;
					Pen.fillOval(Rect(windowIndex - 2, 50, 20, 20));
					Pen.stroke;
*/

				};
				// second experimental draw function, outside the scale, to preserve shape proportions: 
/*				Pen.use {
					Pen.color = Color.blue;
					Pen.fillOval(Rect(0, 0, 10, 10).moveTo(
						windowIndex * b.width / imgWidth, 
						80 * b.height / imgHeight)
					);
				};
*/
				if( drawCrossHair, {
					Pen.color = crosshairColor;
					Pen.addRect( b.moveTo( 0, 0 ));
					Pen.clip;
					Pen.line( 0@mouseY, b.width@mouseY);
					Pen.line(mouseX @ 0, mouseX @ b.height);
					Pen.font = Font( "Helvetica", 10 );
					Pen.stringAtPoint( "freq: " + freq.asString, mouseX + 20 @ mouseY - 15);
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

	start { running = true; this.startruntask }
	
	startruntask {
		// these vars are for temporary tests of osc round trip time:
		var oscSentTime, oscReceivedTime, oscLapseTime, scrollWidth;
		var scrollImage, clearImage;
		var tmp;
		scrollWidth = (imgWidth * 0.25).round(1).asInteger;
		scrollImage = Int32Array.fill(imgHeight * (imgWidth - scrollWidth), 0);
		clearImage = Int32Array.fill(imgHeight * scrollWidth, 255);
		if (runtask.notNil) { ^this };
		runtask = {
			while { server.serverRunning.not } { "Spectrogram: waiting for server to boot".postln; 0.5.wait; };
			0.5.wait;
			this.recalcGradient;
			fftSynth = Synth(\spectroscope, [\inbus, inbus, \buffer, fftbuf]);
			0.1.wait;
			windowIndex = index % imgWidth;
			loop {
				windowIndex = index;
				if (windowIndex >= imgWidth) {
					windowIndex = windowIndex % scrollWidth; // + (imgWidth * 0.75).round(1).asInteger;
					if (windowIndex == 0) {
						imgHeight.postln;
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
					oscReceivedTime = Process.elapsedTime;
					oscLapseTime = oscReceivedTime - oscSentTime;
// temporary timing tests: 
/*					postf("trip dur: % was smaller than poll time by: %\n", 
						oscLapseTime, 
						rate.reciprocal - oscLapseTime
					);
*/
					magarray = buf.clump(2)[frombin .. tobin].flop;
					complexarray = ((((Complex( 
							Signal.newFrom( magarray[0] ), 
							Signal.newFrom( magarray[1] ) 
					).magnitude.reverse)).log10)*80).clip(0, 255); 
					complexarray.do({ | val, i |
						val = val * intensity;
						fftDataArray[i] = colints.clipAt((val/16).round);
					});
					{
						image.setPixels(fftDataArray, Rect(windowIndex, 0, 1, (tobin - frombin + 1)));
						// other pixel setting functions could be added here
						// order of setting pixels is significant. 
						// must not set pixels outside this func, because they may be overwritten
//						image.setPixel([255, 0, 0, 255].asRGBA, windowIndex, tobin - frombin / 2);
					}.defer;
				});
				if (userview.notClosed) { userview.refresh }; // must be here to cleanly erase previous frames
				index = index + 1;
				// here testing how to set marks that will be drawn later:
/*				if (index % rate == 0) { 
					"a second has passed - will set a test mark".postln;
					postf("the current frame is: %, the last displayable frame is: %\n",
						index, this.lastDisplayableFrame
					);
				};
*/
				rate.reciprocal.wait; // framerate
			}; 
		}.fork(AppClock); // must be AppClock for consistent timing in the userview.refresh call.
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
			binfreqs = bufSize.collect({|i| ((server.sampleRate/2)/bufSize)*(i+1) });
			tobin = bufSize.div(2) - 1;
			frombin = 0;
			fftDataArray = Int32Array.fill((tobin - frombin + 1), 0);
			this.setWindowImage( userview.bounds.width );
		}{
			"Buffersize has to be power of two (256, 1024, 2048, etc.)".warn;
		};
	}

	recalcGradient {
		var colors;
		colors = (0..16).collect({ | val | blend(background, color, val / 16)});
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
		// to be written: remove yourself from notifications when window closes. 
		CmdPeriod.remove(this);
		ServerQuit.remove(this, server);
		ServerBoot.remove(this, server);
	}
	
}

