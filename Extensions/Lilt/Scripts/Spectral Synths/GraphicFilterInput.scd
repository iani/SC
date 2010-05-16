/* 080516
Draft for ForbiddenPlanet type filtering with PVMagMul, code posted by Scott Wilson on Monday, May 5, 2008. IZ
Uses Conductor Quark by Ronald Kuivila!
*/

var script, bufsize = 2048, conductor; 

script = ~script;
script.allocBuffer('audio_in_fft_buf', bufsize);
script.allocBuffer('filter_frequencies_buf', bufsize);

~controlSpecs = [
[ 'in', 0, 127, 'linear', 1, 0.0 ],
[ 'out', 0, 127, 'linear', 1, 0.0 ],
];

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
	Synth("PV_MagMul", ['in', ~in, 'out', ~out, \bufnumA, ~audio_in_fft_buf.bufnum, \bufnumB, ~filter_frequencies_buf.bufnum],
		~target, #[\h, \t, \addBefore, \addAfter, \addReplace][~addAction]
	);
};
