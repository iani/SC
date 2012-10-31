/*
Edit the class from master IZ

Aris Bezas 111216 Igoumeninja

s.boot;
f = SendSpectrogramData.new;
f.stopSending;
f.connectToPoller
x={Out.ar(0,In.ar(8)*0.1)}.play;
x.free;

Server
*/

SendSpectrogramData : Resource {

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
		"-SendSpectrogramData".postln;
		super.init(server);
		binSize = aBinSize;
		colorSize = aColorSize;
		colorScaleExponent = aColorScaleExponent;
		intensity = aIntensity;
		this.makeColors;
		//this.connectToPoller;	// do this last: be ready to send
	}

	makeColors {
		colors = (1..colorSize).pow(colorScaleExponent).normalize;
	}

	connectToPoller {
		pollFFT = PollFFT(this.key.last, Server.default);
		//pollFFT = PollFFT(this, Server.default);
		pollFFT addDependant: this; //What is this?
	}

	// Called by Spectrograph. Draw 1 fft frame magnitudes as pixeled colors on image
	update { | index, fftData, magnitudes |
		var pixelArray;
		pixelArray = (1 + magnitudes).log10.clip(0, 1) * intensity collect: { | val, i |
			colors.clipAt((val * colorSize).round);
		};
		OF.mlab('fftData', *pixelArray);
	}

	stopSending	{
		pollFFT.remove;
	}

	// Setting customization variables. Update the required internal variables

	binSize_ { | size = 1024 | binSize = size; this.makeFFTimageArray; }
	colorSize_ { | size = 64 | colorSize = size; this.makeColors; }
	colorScaleExponent_ { | exp = 64 | colorScaleExponent = exp; this.makeColors; }
	intensity_ { | factor = 64 | intensity = factor; this.makeColors; }
	rate_ { | rate = 0.04 | pollFFT.rate = rate; }

}