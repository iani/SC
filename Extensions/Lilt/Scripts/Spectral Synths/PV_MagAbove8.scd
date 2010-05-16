// (SC3:) Generated from SynthDesc on: Thu Jun  7 14:32:47 2007

~script.allocBuffer;

~controlSpecs = [
[ 'in', 0.0, 127.0, 'linear', 1.0, 0.0 ],
[ 'out', 0.0, 4095.0, 'linear', 1.0, 0.0 ],
// [ 'bufnum', 0.0, 1023.0, 'linear', 1.0, 0.0 ],
//[ 'threshold', 0.0, 10.0, 'linear', 0.0, 5.0 ],
[ 'threshold', 0.0001, 10.0, 'exponential', 0.0, 5.0 ],
[ 'source_level', 0.0, 1.0, 'linear', 0.0, 0.0 ],
[ 'fx_level', 0.0, 2.0, 'linear', 0.0, 1.0 ],
[ 'pos', 0, 1, 'linear', 0, 0],
['level', 0, 2, 'linear', 0, 1],
['panwidth', 0, 8, 'linear', 0, 2],
['orientation', 0, 1, 'linear', 0, 0.5]
];

~start = { Synth("PV_MagAbove8", ['in', ~in, 'out', ~out, 'bufnum', ~buffer.bufnum,
	'threshold', ~threshold, 'source_level', ~source_level, 'fx_level', ~fx_level,
	'pos', ~pos, 'level', ~level, 'panwidth', ~panwidth, 'orientation', ~orientation],
	~target, #[\h, \t, \addBefore, \addAfter, \addReplace][~addAction]) };
