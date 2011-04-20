/* 

an additional process accessing data in a spectrogram, and possibly drawing some results on it.

+ a test class for pre-testing this
*/

ImageDrawTest {
	var <spectrogram;
		var mags, maxMag, freqIndex, freq;
		var testImage;
	
	*new { | spectrogram |
		^this.newCopyArgs(spectrogram).init;	
	}
	
	init {
		testImage = Int32Array.fill(1, Image colorToPixel: Color.red);
		this.start;
	}
	start { 
		spectrogram addImageObject: this;
	}

	stop { 
		spectrogram removeImageObject: this;
	}

	drawImage { | image, currentFFTframeMagnitudes, binfreqs, 
		currentFFTframeMagnitudesReversed, binfreqsReversed, 
		persistentWindowIndex, imgWidth, imgHeight |
		
		mags = currentFFTframeMagnitudesReversed;
				maxMag = mags.maxItem;
				freqIndex = mags indexOf: maxMag;
		if (maxMag > 0 and: { freqIndex < imgHeight }) {
			image.setPixels(testImage, Rect(persistentWindowIndex.clip(0, imgWidth), freqIndex, 1, 1), 0);
		};
			
	}
	
	
}

SpectrogramDataTest {
	var <spectrogram;
		var mags, maxMag, freqIndex, freq;
		var testImage;
	
	*new { | spectrogram |
		^this.newCopyArgs(spectrogram).init;	
	}
	
	init { this.start;
		testImage = Int32Array.fill(4, Image colorToPixel: Color.red);
//		spectrogram.drawtest = this;
		
	}
	
	start {
		var mags, maxMag, freqIndex, freq;
		var testImage;
		testImage = Int32Array.fill(4, Image colorToPixel: Color.red);
	
/*		NotificationCenter.register(spectrogram, \drawImage, this, {
			mags = spectrogram.currentFFTframeMagnitudes;
				maxMag = mags.maxItem;
				freqIndex = mags indexOf: maxMag;
				freq = spectrogram.binfreqs[freqIndex];
				freq.cpsmidi.round(0.01);
			
		// the dots are lost when resizing the window to a large size. try a different approach by calling 
		// a draw method directly inside the update loop:
		spectrogram.image.setPixels(testImage, Rect(spectrogram.persistentWindowIndex, (spectrogram.imgHeight - freqIndex)
			.clip(5, spectrogram.imgHeight - 5), 1, 4), 0);

*/

/*			[//this, thisMethod.name, 
				maxMag = mags.maxItem,
				freqIndex = mags indexOf: maxMag,
				freq = spectrogram.binfreqs[freqIndex],
				freq.cpsmidi.round(0.01),
			].postln;
			
		// the dots are lost when resizing the window to a large size. will try a different approach by calling 
		// a draw method directly inside the update loop
		spectrogram.image.setPixels(testImage, Rect(spectrogram.windowIndex, (spectrogram.imgHeight - freqIndex)
			.clip(5, spectrogram.imgHeight - 5), 1, 4), 0);
			
		});

*/
		NotificationCenter.register(spectrogram, \closed, this, {
			this.stop;
		});
	}
	
	// alternative approach, to ensure correct drawing even when resizing the window
	drawImage {
	
			mags = spectrogram.currentFFTframeMagnitudes;
				maxMag = mags.maxItem;
				freqIndex = mags indexOf: maxMag;
				freq = spectrogram.binfreqs[freqIndex];
				freq.cpsmidi.round(0.01);
			
		// the dots are lost when resizing the window to a large size. try a different approach by calling 
		// a draw method directly inside the update loop:
//		spectrogram.image.setPixels(testImage, 
//			Rect(spectrogram.persistentWindowIndex - 3 max: 0, (spectrogram.imgHeight - freqIndex)
//			.clip(5, spectrogram.imgHeight - 5), 1, 4), 0);
		spectrogram.image.setPixels(testImage, 
			Rect(spectrogram.persistentWindowIndex, (spectrogram.imgHeight - freqIndex)
			.clip(5, spectrogram.imgHeight - 5), 1, 4), 0);
		
	}
	
	stop {
		NotificationCenter.unregister(spectrogram, \drawImage, this);
		NotificationCenter.unregister(spectrogram, \closed, this);
	}
	
}