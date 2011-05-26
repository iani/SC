
/*
SendSpectrogramData : DrawSpectrogram {
	var <ofAddress;
	
	init {
		super.init;	
		ofAddress = NetAddr("127.0.0.1", 12345);
	}

	makeColors {
		colors = (1..colorSize).pow(colorScaleExponent).normalize;
		colors.postln;
	}

	
	update { | index, ffData, magnitudes |
		var ofMagnitudes;
		ofMagnitudes = ((1 + magnitudes).log10.clip(0, 1) * intensity collect: { | val, i |
			colors.clipAt((val * colorSize).round)
		});
		ofMagnitudes.maxItem.postln;
		ofMagnitudes.round(0.1).postln;
		ofAddress.sendMsg('fft', *ofMagnitudes);
	}	

}
*/
/*
SendSpectrogramData('fft').free 
Redo of SendSpectrogramData as UniqueObject to prevent double instances from being added 
to the same PollFFT
*/

SendSpectrogramData : AbstractServerResource {

	// fft data and display customization: 
	var <binSize = 1024, <colorSize = 64, <colorScaleExponent = 0.5, <intensity = 1;
	
	// internal variables
	var <colors; /* Integer array holding colors for coloring pixels on the image
		as floating point values 0-1, for use in oF */
	var <pollFFT; 	// instance of PollFFT that sends me my data
	var ofAddress;	// address of of for sending it the data via OSC
	
	*new { | name = 'fft', server, binSize = 1024, colorSize = 64, colorScaleExponent = 0.5, 
		intensity = 1 |
		^super.new(name, server, binSize, colorSize, colorScaleExponent, intensity);
	}

	
	init { | server, aBinSize = 1024, aColorSize = 64, aColorScaleExponent = 0.5, aIntensity = 1 |
		super.init(server);
		binSize = aBinSize;
		colorSize = aColorSize;
		colorScaleExponent = aColorScaleExponent;
		intensity = aIntensity;
		this.makeColors;
		ofAddress = NetAddr("127.0.0.1", 12345);
		//ofAddress = NetAddr("127.0.0.1", 57120);		
		this.connectToPoller;	// do this last: be ready to send
	}

	makeColors {
		colors = (1..colorSize).pow(colorScaleExponent).normalize;
	}

	connectToPoller {
		pollFFT = PollFFT(this.key.last, server);
		pollFFT addDependant: this;
	}

	// Called by Spectrograph. Draw 1 fft frame magnitudes as pixeled colors on image
	update { | index, fftData, magnitudes |
		var pixelArray;
		pixelArray = (1 + magnitudes).log10.clip(0, 1) * intensity collect: { | val, i |
			colors.clipAt((val * colorSize).round);
		};
		ofAddress.sendMsg('fftpixels', *pixelArray);
	}	

	// Setting customization variables. Update the required internal variables

	binSize_ { | size = 1024 | binSize = size; this.makeFFTimageArray; }
	colorSize_ { | size = 64 | colorSize = size; this.makeColors; }
	colorScaleExponent_ { | exp = 64 | colorScaleExponent = exp; this.makeColors; }
	intensity_ { | factor = 64 | intensity = factor; this.makeColors; }
	rate_ { | rate = 0.04 | pollFFT.rate = rate; }	

}