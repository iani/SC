// (SC3:) Generated from SynthDesc on: Sat Oct 25 12:10:49 2008

~controlSpecs = [
[ 'k_out', 0, 4095, 'linear', 1, 0 ],
[ 'freq', 0.001, 100.0, 'exp', 0.0, 1.0 ],
[ 'lo', 0.0, 200.0, 'linear', 0, 0.0 ],
[ 'hi', 0.0, 200.0, 'linear', 0, 10.0 ]
];

~start = { Synth("k_lfdnoise0R", ['k_out', ~k_out, 'freq', ~freq, 'mul', ~mul, 'add', ~add],
	~target, #[\h, \t, \addBefore, \addAfter, \addReplace][~addAction]) };
