/* 
Draft for ForbiddenPlanet type filtering with PVMagMul, code posted by Scott Wilson on Monday, May 5, 2008. IZ
Uses Conductor Quark by Ronald Kuivila!
*/

var script, bufsize = 2048, conductor; 

script = ~script;
script.allocBuffer('audio_in_fft_buf', bufsize);
script.allocBuffer('filter_frequencies_buf', bufsize);

/* ~controlSpecs = [
[ 'in', 0, 127, 'linear', 1.0, 0.0 ],
[ 'out', 0, 4095, 'linear', 1, 0.0 ],
// [ 'bufnum', 0, 1023, 'linear', 1, 0.0 ],
//[ 'stretch', 0.0125, 4.0, 'linear', 0.0, 1.5 ],
//[ 'shift', -128, 128, 'linear', 0.0, 100.0 ],
//[ 'source_level', 0.0, 1.0, 'linear', 0.0, 0.0 ],
//[ 'fx_level', 0.0, 2.0, 'linear', 0.0, 1.0 ]
];
*/

conductor = ~conductor = Conductor.make { | conductor, frequencies |
	frequencies.value = (((1..bufsize) / 100).sin.abs * 0.5);
	frequencies.action = { | myself |
		if (script.isRunning) { 
			script.envir.filter_frequencies_buf.loadCollection(myself.value);
		}
	};
	conductor.gui.use { ~scMultislider =  {  |win, name, cv, rect |
		rect = rect ?? ~msliderRect;
			cv.asArray.do ( SCMultiSliderView(win, rect).isFilled_(true).connect(_) );
		}
	};
};

script.onStart({ { conductor[\frequencies].changed(\synch) }.defer });

~start = {
	~conductor.show1;
	SynthDef("help-magMul", { arg out=0, bufnumA=0, bufnumB=1;
		var inA, chainA, inB, chainB, chain;
		inA = WhiteNoise.ar(0.2);
		chainA = FFT(bufnumA, inA);
		chain = PV_MagMul(chainA, bufnumB); 
		Out.ar(out, 1.5 * IFFT(chain).dup);
	}).play(s,[\out, 0, \bufnumA, ~audio_in_fft_buf.bufnum, \bufnumB, ~filter_frequencies_buf.bufnum ]);
};
