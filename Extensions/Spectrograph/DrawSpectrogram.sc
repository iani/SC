/*
Encapsulate the spectrogram drawing algorithm here for clarity
*/

DrawSpectrogram {

	// fft data and display customization: 
	var <binSize = 1024, <colorSize = 64, <colorScaleExponent = 0.5, <intensity = 1;
	var <binColor, <background; // colors of FFT bin and of background
	
	// internal variables
	var <colors; // Integer array holding colors for coloring pixels on the image
	var <fftImageArray; // image representing one FFT frame, colored via colors
	
	*new { | binSize = 1024, colorSize = 64, colorScaleExponent = 0.5, 
		intensity = 1, binColor, background |
		^this.newCopyArgs(binSize, colorSize, colorScaleExponent, 
			intensity, binColor, background
		).init;
	}
	
	init {
		binColor = binColor ?? { Color.white };
		background = background ?? { Color.black };
		this.initImageData;
	}

	initImageData {
		this.makeFFTimageArray;	
		this.makeColors;
	}

	makeFFTimageArray { fftImageArray = Int32Array.fill(binSize / 2, 0); }

	makeColors {
		colors = (1..colorSize).pow(colorScaleExponent).normalize collect: { | blendFactor |
			Image colorToPixel: background.blend(binColor, blendFactor);
		};
	}

	// Called by Spectrograph. Draw 1 fft frame magnitudes as pixeled colors on image
	update { | windowIndex, image, magnitudes |
		(1 + magnitudes.reverse).log10.clip(0, 1) * intensity do: { | val, i |
			fftImageArray[i] = colors.clipAt((val * colorSize).round);
		};
		image.setPixels(fftImageArray, Rect(windowIndex, 0, 1, fftImageArray.size));
	}	

	// Setting customization variables. Update the required internal variables

	binSize_ { | size = 1024 | binSize = size; this.makeFFTimageArray; }
	colorSize_ { | size = 64 | colorSize = size; this.makeColors; }
	colorScaleExponent_ { | exp = 64 | colorScaleExponent = exp; this.makeColors; }
	intensity_ { | factor = 64 | intensity = factor; this.makeColors; }
	binColor_ { | color | binColor = color; this.makeColors; }
	background_ { | color | background = color; this.makeColors; }
}