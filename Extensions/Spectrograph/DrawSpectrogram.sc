/*
Encapsulate the spectrogram drawing algorithm here for clarity

*/


DrawSpectrogram {

	classvar <>colorSize = 64, <colorScaleExp = 0.5;
	var <bufSize, <binfreqs;	// size of FFT
	var <image, <>intensity = 1;
	var color, background, colors; // colints is an array of integers each representing a color
	var fftDataArray;
	// track the iteration of polling bus values and its relative position in the window: 
	
	init {
		// create fftDataArray, colors, background	
		
	}

	// FFTsynthPoller calls update on all objects added to it as listeners
	update { | windowIndex, fftBuf, magnitudes |

		(log10(1 + magnitudes.reverse).clip(0, 1) * intensity) do: { | val, i |
			fftDataArray[i] = colors.clipAt((val * colorSize).round);
		};
		image.setPixels(fftDataArray, Rect(windowIndex, 0, 1, fftDataArray.size));
	}	


}