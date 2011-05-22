
/*
Testing 

PollFFT('of') addDependant: SendSpectrogramData.new;

\test.playFunc({ LFSaw.ar(LFNoise2.kr(0.5).range(100, 1000), 0, 0.01) });

n = NetAddr("127.0.0.1", 57120); // the url should be the one of computer of app 2 (or nil)
o = OSCresponderNode(n, '/b_setn', { |t, r, msg| ("time:" + t).postln; msg[10].postln }).add;


*/

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
		//ofMagnitudes.maxItem.postln;
		//ofMagnitudes.round(0.001).postln;
		ofAddress.sendMsg('fft', *ofMagnitudes);
	}	

}