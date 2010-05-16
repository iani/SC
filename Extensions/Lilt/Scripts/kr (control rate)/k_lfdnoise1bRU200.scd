/* (SC3:) Generated from SynthDesc on: Tue Feb 13 01:23:54 2007
081025 version with lower range of mul and add, for rate modulation in looper.
*/

~controlSpecs = [
[ 'k_out', 0, 4047, 'linear', 1, 0],
[ 'freq', 0.01, 50.0, 'exponential', 0, 0.5 ],
[ 'lo', 0.0, 200.0, 'linear', 0, 0.0 ],
[ 'hi', 0.0, 200.0, 'linear', 0, 10.0 ]
];

~start = { Synth("k_lfdnoise1R", ['k_out', ~k_out, 'freq', ~freq, 'mul', ~mul, 'add', ~add],
	~target, #[\h, \t, \addBefore, \addAfter, \addReplace][~addAction]) };
