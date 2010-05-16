// This part added by hand for buffer allocation: 

~script.allocBuffer;

// Generated from SynthDesc on: Fri Dec  9 09:09:25 2005

~controlSpecs = [
[ 'in', 0, 127, 'linear', 1, 0 ],
[ 'out', 0, 4095, 'linear', 1, 0 ],
//[ 'bufnum', 0, 1023, 'linear', 1, 0 ],
[ 'numTeeth', 0, 127, 'linear', 0, 15 ],
[ 'phase', 0, 6.28319, 'linear', 0, 0.5 ],
[ 'width', 0, 1, 'amp', 0, 0.2 ],
[ 'source_level', 0, 1, 'amp', 0, 0.2],
[ 'fx_level', 0, 1, 'amp', 0, 0.2],
[ 'pos', 0, 1, 'linear', 0, 0],
['level', 0, 2, 'linear', 0, 1],
['panwidth', 0, 8, 'linear', 0, 2],
['orientation', 0, 1, 'linear', 0, 0.5]
];

~start = { 
       Synth("PV_RectComb8",
         ['in', ~in, 'out', ~out, 'bufnum', ~buffer.bufnum, 'numTeeth', ~numTeeth, 'phase', ~phase, 'width', ~width,
         'source_level', ~source_level, 'fx_level', ~fx_level,
		'pos', ~pos, 'level', ~level, 'panwidth', ~panwidth, 'orientation', ~orientation],
	  ~target, #[\h, \t, \addBefore, \addAfter, \addReplace][~addAction]) 
};
