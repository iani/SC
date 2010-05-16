// (SC3:) Generated from SynthDesc on: Tue Feb 13 01:24:00 2007

~controlSpecs = [
[ 'k_out', 0, 4095, 'linear', 1, 0 ],
[ 'freq', 0.001, 100.0, 'exp', 0.0, 1.0 ],
[ 'phase', 0.0, 6.28319, 'linear', 0.0, 0.0 ],
[ 'lo', 0.0, 200.0, 'linear', 0, 0.0 ],
[ 'hi', 0.0, 200.0, 'linear', 0, 10.0 ]
];

~start = { Synth("k_sawR", ['k_out', ~k_out, 'freq', ~freq, 'phase', ~phase, 'mul', ~mul, 'add', ~add],
	~target, #[\h, \t, \addBefore, \addAfter, \addReplace][~addAction]) };
