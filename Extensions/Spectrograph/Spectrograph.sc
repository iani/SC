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
	classvar <minWidth = 400, <>backgroundColor, <>binColor;
	var <bounds, <server, <rate, <bufsize, <>stopPollerOnClose = false;
	var <userview, <image, <imgWidth, <imgHeight;
	var <scrollWidth; // , scrollImage, clearImage;
	var <index;	// running count of the currently polled fft frame. 
				// received from FFTsynthPoller. Cached for asynchronous use by penObjects
	var <windowIndex;	// index of x pixel on image where current frame is being drawn

	var <imageObjects; 	// array of objecs that add display graphics to pixels on the image
	var <penObjects; 		// array of objecs that add display graphics using Pen
	var <drawSpectrogram, <drawCrosshair;	// the two built-in drawing objects
	var <scroll;			// object that scrolls the image when drawind has reached the end
	
	*initClass {
		Class.initClassTree(Color);
		backgroundColor = Color.black;
		binColor = Color.white;
	}


	*big { | server |
		if (this.current.isNil) { ^this.start(nil, nil, server ? Server.default); };
		^this.current.bounds_(Window.centeredWindowBounds(1000)).front;
	}

	*small { | server |
		if (this.current.isNil) { 
			^this.start(nil, this.smallBounds, server: server ? Server.default);
		};
		^this.current.bounds_(this.smallBounds).front;
	}
	
	*smallBounds { ^Rect(0, 0, 570, 200) }

	*background_ { | color |
		color = color ? Color.black;
		backgroundColor = color;
		if (current.notNil) { current.background = color };
	}

	*onServer { | server |
		server = server ? Server.default;
		^this.all.select({ | s | s.server == server }) // .do(_.front);
	}

	*start { | name, bounds, server, rate = 0.04, bufsize = 1024 |
		^this.new(name, bounds, server ? Server.default, rate, bufsize = 1024).start.front;
	}
	
	
	*new { | name, bounds, server, rate = 0.04, bufsize = 1024 |
		name = format("%:%", name = name ? "Spectrograph", server = server ? Server.default).asSymbol;
		^current = super.new(name, bounds, server, rate, bufsize);
	}

	name { ^key[1] }
	
	init { | argBounds, argServer, argRate, argBufsize |
		var window; // just for naming convenience
		bounds = argBounds ?? { Window.centeredWindowBounds(1000) };
		bounds.width = bounds.width max: minWidth;
		server = argServer;
		rate = argRate;
		bufsize = argBufsize;
		window = object = Window(this.name, bounds);
		this.initViews;
		drawSpectrogram = DrawSpectrogram(bufsize, 64, 0.5, 1, binColor, backgroundColor);
		scroll = Scroll(image, (bounds.width / 4).round(1).asInteger, backgroundColor);
		this.addWindowOnCloseAction;
		this.addImageObject(drawSpectrogram);
		NotificationCenter.notify(this, \viewsInited);
		this.front;
	}

	initViews {
		imageObjects = Set.new;
		penObjects = Set.new;
		penObjects add: PenObjectTest(this);
		userview = UserView(object, object.view.bounds)
			.resize_(5)
			.focus(true)
			.drawFunc = { | view |
			var b = view.bounds;
			Pen.use {
				Pen.scale( b.width / imgWidth, b.height / imgHeight );
				Pen image: image;
			};
			penObjects do: _.update(this); 	// let objects draw with Pen here
		};
		imgWidth = userview.bounds.width;
		imgHeight = bufsize / 2;
		scrollWidth = (imgWidth / 4).round(1).asInteger;
		scrollWidth = (imgWidth * 0.25).round(1).asInteger;
		// Setting the background also creates the image
		this.background = backgroundColor; // method can be called at any time to change color
		this onClose: {
			if (current === this) { current = nil; };
		};
	}

	background_ { | color |
		// This method can be called at any time to change the background color
		if (image.notNil) { image.free };
		image = Image.color(imgWidth@imgHeight, color);
		if (scroll.notNil) { scroll setColor: color; };
		if (drawSpectrogram.notNil) { drawSpectrogram.background = color; }
	}

	addImageObject { | object | imageObjects = imageObjects add: object }

	rebuildScreen { // does not seem to work? 
		if (scroll.notNil) { scroll.initImageData; };
		if (drawSpectrogram.notNil) { drawSpectrogram.initImageData; }
	}

	start { 
//		this.rebuildScreen; // rebuildScreen does not work yet  
		ServerPrep(server).addAction({ this.prStart });
	}

	prStart {	
		var poller;
		postf("Spectrograph prStart server is: %\n", server);
		//  key, server, rate = 0.04, bufSize = 1024, in = 0 
		poller = PollFFT(this.name, server, rate, bufsize, 0);
		poller addDependant: this;
		this onClose: {
			poller removeDependant: this;
			if (stopPollerOnClose) { poller.free };
		}; 
		poller.addMessage(this, 'rate_');
	}

	rate_ { | argRate = 0.04 | this.notify('rate_', rate = argRate); }

	update { | argIndex, fftData, magnitudes |
		{
			if (this.isOpen) { // if user has closed me during this defer, catch it here
				argIndex = scroll.update(argIndex, image);
				drawSpectrogram.update(argIndex, image, magnitudes);
				userview.refresh;
			};
		}.defer;		
	}
		// Should only be used for debugging.
	// Normally you just close the Spectrograph window.
	*stopPoller { if (current.notNil) { current.stopPoller } }
	stopPoller { // only for debugging purposes. Normally just close the Spectrograph window.
		var poller;
		if ((poller = PollFFT.at(this.name, server)).notNil) { poller.free; };
	}

}
