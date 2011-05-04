/*
Encapsulate the spectrogram drawing algorithm here for clarity

*/


DrawSpectrogram {

	classvar <>colorSize = 64, <colorScaleExp = 0.5, <>intensity = 1;
	var color, background, colors; // colints is an array of integers each representing a color
	var fftImageArray;
	// track the iteration of polling bus values and its relative position in the window: 
	
	init {
		// create fftImageArray, colors, background	
		
	}

	// FFTsynthPoller calls update on all objects added to it as listeners
	update { | image, windowIndex, magnitudes, fftBuf |

		(log10(1 + magnitudes.reverse).clip(0, 1) * intensity) do: { | val, i |
			fftImageArray[i] = colors.clipAt((val * colorSize).round);
		};
		image.setPixels(fftImageArray, Rect(windowIndex, 0, 1, fftImageArray.size));
	}	


}