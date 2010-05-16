// (SC3:) Generated from SynthDesc on: Tue Feb 13 01:23:54 2007

~controlSpecs = [
[ 'k_out', 0, 4095, 'linear', 1, 0 ],
[ 'freq', 0.001, 100.0, 'exp', 0.0, 1.0 ],
[ 'mul', -10.0, 10.0, 'linear', 0, 1.0 ],
[ 'add', -10.0, 10.0, 'linear', 0, 0.0 ]
];

~start = { Synth("k_lfdnoise1", ['k_out', ~k_out, 'freq', ~freq, 'mul', ~mul, 'add', ~add],
	~target, #[\h, \t, \addBefore, \addAfter, \addReplace][~addAction]) };
