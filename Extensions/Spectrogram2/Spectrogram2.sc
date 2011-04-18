// changelog:
//	- 30-Mar-10 made cross-platform, fixed relativeOrigin issue
// modifications by IZ 2011 04 17 f
Spectrogram2 {
	classvar <server;
	var window; //, bounds;
	var <fftbuf, fftDataArray, fftSynth;
	var inbus = 0, <>rate = 25;
	var <bufSize, binfreqs;	// size of FFT
	var <frombin, <tobin;
	var image, imgWidth, imgHeight, <>intensity = 1, runtask;
	var color, background, colints; // colints is an array of integers each representing a color
	var userview, mouseX, mouseY, freq, drawCrossHair = false; // mYIndex, mXIndex, freq;
	var <>crosshairColor, running;
	// track the iteration of polling bus values and its relative position in the window: 
	var <index = 0, <windowIndex = 0, <lastFrameIndex;
	var <frames;	// holds the times when each frame was drawn by drawFunc;
	*new { arg parent, bounds, bufSize = 1024, color, background, lowfreq=0, highfreq=inf;
		^super.new.initSpectrogram(parent, bounds, bufSize, color, background, lowfreq, highfreq);
	}
	
	initSpectrogram { arg parent, boundsarg, bufSizearg = 1024, col, bg, lowfreqarg, highfreqarg;
		server = Server.default;
		bufSize = bufSizearg; // fft window
		fftbuf = Buffer.alloc(server, bufSize);
		binfreqs = bufSize.collect({ | i | ((server.sampleRate / 2) / bufSize) * (i + 1) });
		background = bg ? Color.black;
		color = col ? Color(1, 1, 1); // white by default
		crosshairColor = Color.white;
		tobin = min(binfreqs.indexOf((highfreqarg/2).nearestInList(binfreqs)), bufSize.div(2) - 1);
		frombin = max(binfreqs.indexOf((lowfreqarg/2).nearestInList(binfreqs)), 0);
		fftDataArray = Int32Array.fill((tobin - frombin + 1), 0);
		running = false;		
		this.sendSynthDef;
		this.createWindow(parent, boundsarg);
		CmdPeriod.add(this);
		this.initFrames;
	}

	initFrames { frames = Frames.new }

	cmdPeriod {
		if (running) { this.startruntask };	
	}

	createWindow {arg parent, bounds;
		bounds = bounds ?? { Rect(200, 450, 600, 300) };
		window = parent ? Window("Spectrogram", bounds);
		this.setWindowImage( bounds.width );
		this.setUserView(window, bounds);
		window.onClose_({
			image.free;
			this.stopruntask;
			fftbuf.free;
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
//					frames.add(Process.elapsedTime);
//					NotificationCenter.notify(this, \imagePrep, image, imgWidth, frames, this);
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
	
	sendSynthDef {
		SynthDef(\spectroscope, {|inbus=0, buffer=0|
			FFT(buffer, InFeedback.ar(inbus));
		}).send(server);
	}
		
	startruntask {
		var blackpixels;
		running = true;
		this.recalcGradient;
		runtask = {
			0.1.wait;
			fftSynth = Synth(\spectroscope, [\inbus, inbus, \buffer, fftbuf]);
			loop {
				windowIndex = index % imgWidth; // tracks current position in window for any painting funcs
				// get fft data and draw them when received: 
				fftbuf.getn(0, bufSize, { | buf |
					var magarray, complexarray;
					magarray = buf.clump(2)[frombin .. tobin].flop;
					complexarray = ((((Complex( 
							Signal.newFrom( magarray[0] ), 
							Signal.newFrom( magarray[1] ) 
					).magnitude.reverse)).log10)*80).clip(0, 255); 
						
					complexarray.do({|val, i|
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

	stopruntask {
		running = false;
		runtask.stop;
		try{fftSynth.free };
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
		if(buffersize.isPowerOfTwo, {
			this.stopruntask;
			bufSize = buffersize;
			try {fftbuf.free};
			fftbuf = Buffer.alloc(server, bufSize, 1, { if(restart, {this.startruntask}) }) ;
			binfreqs = bufSize.collect({|i| ((server.sampleRate/2)/bufSize)*(i+1) });
			tobin = bufSize.div(2) - 1;
			frombin = 0;
			fftDataArray = Int32Array.fill((tobin - frombin + 1), 0);
			this.setWindowImage( userview.bounds.width );
		}, {
			"Buffersize has to be power of two (256, 1024, 2048, etc.)".warn;
		});
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
	
	start { this.startruntask }
	
	stop { this.stopruntask }
	
}

SpectrogramWindow2 : Spectrogram2 { 
	classvar <scopeOpen;
	var startbutt;
	
	*new { ^super.new }

	createWindow {
		var cper, font;
		var highfreq, lowfreq, rangeslider, freqtextarray;
		var freqstringview, bounds, paramW;
	
		paramW = if( GUI.id == \cocoa, 36, 52 );
	
		scopeOpen = true;
		window = Window("Spectrogram2",  Rect(200, 450, 548 + paramW, 328));
		bounds = window.view.bounds.insetAll(30, 10, paramW + 4, 10); // resizable
		font = Font("Helvetica", 10);
		mouseX = 30.5;
		mouseY = 30.5;
		
		this.setWindowImage( bounds.width );
		super.setUserView(window, bounds);
				
		startbutt = Button(window, Rect(545, 10, paramW, 16))
			.states_([["Power", Color.black, Color.clear], 
					 ["Power", Color.black, Color.green.alpha_(0.2)]])
			.action_({ arg view; if(view.value == 1, { this.startruntask }, { this.stopruntask }) })
			.font_(font)
			.resize_(3)
			.canFocus_(false);

		StaticText(window, Rect(545, 42, paramW, 16))
			.font_(font)
			.resize_(3)
			.string_("BusIn");

		NumberBox(window, Rect(545, 60, paramW, 16))
			.font_(font)
			.resize_(3)
			.action_({ arg view;
				view.value_(view.value.asInteger.clip(0, server.options.numAudioBusChannels));
				this.inbus_(view.value);
			})
			.value_(0);

		StaticText(window, Rect(545, 82, paramW, 16))
			.font_(font)
			.resize_(3)
			.string_("int");

		NumberBox(window, Rect(545, 100, paramW, 16))
			.font_(font)
			.resize_(3)
			.action_({ arg view;
				view.value_(view.value.asInteger.clip(1, 40));
				this.intensity_(view.value);
			})
			.value_(intensity);

		StaticText(window, Rect(545, 122, paramW, 16))
			.font_(font)
			.resize_(3)
			.string_("winsize");

		PopUpMenu(window, Rect(545, 140, paramW, 16))
			.items_(["256", "512", "1024", "2048"])
			.value_(2)
			.resize_(3)
			.font_(Font("Helvetica", 9))
			.background_(Color.white)
			.canFocus_(false)
			.action_({ arg ch; var inbus;
				this.setBufSize_( ch.items[ch.value].asInteger, startbutt.value.booleanValue );
				rangeslider.lo_(0).hi_(1).doAction;
			});

		highfreq = NumberBox(window, Rect(545, 170, paramW, 16))
			.font_(font)
			.resize_(3)
			.action_({ arg view;
				var rangedval; 
				rangedval = view.value.clip(lowfreq.value, (server.sampleRate/2));
				view.value_( rangedval.nearestInList(binfreqs).round(1) );
				rangeslider.hi_( view.value / (server.sampleRate/2) );
			})
			.value_(22050);

		rangeslider = RangeSlider(window, Rect(545 + (paramW - 26).div( 2 ), 192, 26, 80))
			.lo_(0.0)
			.range_(1.4)
			.resize_(3)
			.knobColor_(Color(0.40392156862745, 0.58039215686275, 0.40392156862745, 1.0))
			.action_({ |slider|
				var lofreq, hifreq, spec;
				lofreq = (slider.lo*(server.sampleRate/2)).nearestInList(binfreqs).round(1);
				hifreq = (slider.hi*(server.sampleRate/2)).nearestInList(binfreqs).round(1);
				lowfreq.value_( lofreq );
				highfreq.value_( hifreq );
//				frombin = max( (slider.lo * (bufSize/2)).round(0.1), 0);
//				tobin = min( (slider.hi * (bufSize/2)).round(0.1), bufSize/2 -1);
				frombin = max( (slider.lo * (bufSize/2)).asInteger, 0);
				tobin = min( (slider.hi * (bufSize/2)).asInteger, bufSize.div(2) -1);
				spec = [lofreq, hifreq].asSpec;
				freqtextarray = Array.fill(11, { arg i;
					var val;
					val = ((spec.map(0.1*(10-i))/1000).round(0.1)).asString; 
					if(val.contains(".").not, { val = val++".0"});
					val
				});
				freqstringview.refresh;
				this.setWindowImage( userview.bounds.width );
//				userview.backgroundImage_(image, 10);
				userview.refresh;
			});

		lowfreq = NumberBox(window, Rect(545, 278, paramW, 16))
			.font_(font)
			.resize_(3)
			.action_({ arg view;
				var rangedval; 
				rangedval = view.value.clip(0, highfreq.value);
				view.value_( rangedval.nearestInList(binfreqs).round(1) );
				rangeslider.activeLo_( view.value / (server.sampleRate/2) );
			})
			.value_(0);

		freqtextarray = Array.fill(11, { arg i;
				if(((((server.sampleRate/2) / 10000)*(10-i)).round(0.1)).asString.contains("."), {
					((((server.sampleRate/2) / 10000)*(10-i)).round(0.1)).asString;
				},{
					((((server.sampleRate/2) / 10000)*(10-i)).round(0.1)).asString++".0";
				});				
			});
				
		freqstringview = UserView(window, Rect(0, 10, 29, bounds.height))
			.resize_(4)
			.canFocus_( false )
			.drawFunc_({arg view;
				Pen.font = Font( "Helvetica", 9);
				Pen.color = Color.black;
				11.do({ arg i; 
					Pen.stringAtPoint(freqtextarray[i], Point(5, (i+0)*((view.bounds.height-12)/10))) 
				});
			});
		
		CmdPeriod.add( cper = { 
			if(startbutt.value == 1, {
				startbutt.valueAction_(0);
				AppClock.sched(0.5, { startbutt.valueAction_(1) });
			});
		 });
		
		window.onClose_({
			image.free;
			try{ fftSynth.free };
			try{ fftbuf.free };
			scopeOpen = false; 
			this.stopruntask;
			CmdPeriod.remove(cper);
		}).front;
	}
	
	start { {startbutt.valueAction_(1)}.defer(0.5) }
	
	stop { {startbutt.valueAction_(0)}.defer(0.5) }
	
}

+ Function {
	spectrogram2 { | target, outbus = 0, fadeTime = 0.02, addAction=\addToHead, args |
		var synth;
		synth = this.play(target, outbus, fadeTime, addAction, args);
		if(SpectrogramWindow2.scopeOpen != true, {
			SpectrogramWindow2.new.start;
		});
		^synth;
	}
}
