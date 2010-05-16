// (SC3:) Generated from SynthDesc on: Sat Oct 25 01:10:26 2008

~controlSpecs = [
[ 'out', 0, 128, 'linear', 1, 0 ],
[ 'bufnum', 0, 1023, 'linear', 1, 0 ],
[ 'rate_1', -10.0, 10.0, 'linear', 0.0, 1.0 ],
[ 'mark1_1', 0.0, 1.0, 'linear', 0.0, 0.0 ],
[ 'mark2_1', 0.0, 1.0, 'linear', 0.0, 1.0 ],
[ 'rate_2', -10.0, 10.0, 'linear', 0.0, 1.0 ],
[ 'mark1_2', 0.0, 1.0, 'linear', 0.0, 0.0 ],
[ 'mark2_2', 0.0, 1.0, 'linear', 0.0, 1.0 ]
];

~start = { Synth("looper2", ['out', ~out, 'bufnum', ~bufnum, 'rate_1', ~rate_1, 'mark1_1', ~mark1_1, 'mark2_1', ~mark2_1, 'rate_2', ~rate_2, 'mark1_2', ~mark1_2, 'mark2_2', ~mark2_2],
	~target, #[\h, \t, \addBefore, \addAfter, \addReplace][~addAction]) };
