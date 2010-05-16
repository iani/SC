// This part added by hand for buffer allocation: 

~script.allocBuffer;

// Generated from SynthDesc on: Fri Dec  9 09:34:00 2005

~controlSpecs = [
[ 'in', 0, 127, 'linear', 1, 0 ],
[ 'out', 0, 4095, 'linear', 1, 0 ],
//[ 'bufnum', 0, 1023, 'linear', 1, 0 ],
[ 'bins', 0, 100, 'linear', 0, 50 ],
[ 'pos', 0, 1, 'linear', 0, 0],
['level', 0, 2, 'linear', 0, 1],
['panwidth', 0, 8, 'linear', 0, 2],
['orientation', 0, 1, 'linear', 0, 0.5]
];

~start = {
 Synth("PV_MagSmear8",
      ['in', ~in, 'out', ~out, 'bufnum', ~buffer.bufnum, 'bins', ~bins,
		'pos', ~pos, 'level', ~level, 'panwidth', ~panwidth, 'orientation', ~orientation],
	  ~target, #[\h, \t, \addBefore, \addAfter, \addReplace][~addAction])
};
