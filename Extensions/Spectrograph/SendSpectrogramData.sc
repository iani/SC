

SendSpectrogramData : DrawSpectrogram {
	var <ofAddress;
	
	init {
		super.init;	
		ofAddress = NetAddr("127.0.0.7", 12345);
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
		//ofMagnitudes.maxItem.postln;
		//ofMagnitudes.round(0.001).postln;
		ofAddress.sendMsg('fft', *ofMagnitudes);
	}	

}