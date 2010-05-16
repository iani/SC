// This part added by hand for buffer allocation: 

~script.allocBuffer;

// This part adapted from SynthDesc on: Fri Oct 28 21:38:11 2005

~controlSpecs = [
[ 'in', 0, 127, 'linear', 1, 0 ],
[ 'out', 0, 4095, 'linear', 1, 0 ],
// [ 'bufnum', 0, 1023, 'linear', 1, 0 ], // done internally with allocBuffer
[ 'wipe', 0, 10, 'linear', 0, 0.5 ],
[ 'width', 0, 10, 'linear', 0, 0.2 ],
[ 'trigger', 0, 1, 'linear', 1, 0 ],
[ 'source_level', 0.0, 1.0, 'linear', 0.0, 0.0 ],
[ 'fx_level', 0.0, 2.0, 'linear', 0.0, 1.0 ],
[ 'pos', 0, 1, 'linear', 0, 0],
['level', 0, 2, 'linear', 0, 1],
['panwidth', 0, 8, 'linear', 0, 2],
['orientation', 0, 1, 'linear', 0, 0.5]
];

// pos = 0, level = 1, panwidth = 2, orientation = 0.5;

~start = {
	Synth("PV_BinScramble8", [\in, ~in, \out, ~out, \bufnum, ~buffer.bufnum, 
		\wipe, ~wipe, \width, ~width, \trigger, ~trigger,
		'source_level', ~source_level, 'fx_level', ~fx_level,
		'pos', ~pos, 'level', ~level, 'panwidth', ~panwidth, 'orientation', ~orientation],
		~target, #[\h, \t, \addBefore, \addAfter, \addReplace][~addAction]
	);
};

