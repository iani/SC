// (SC3:) Generated from SynthDesc on: Sat Oct 25 11:06:48 2008

~controlSpecs = [
[ 'out', 0.0, 4095.0, 'linear', 1.0, 0.0 ],
[ 'bufnum', 0.0, 1023.0, 'linear', 1.0, 0.0 ],
[ 'rate', -10.0, 10.0, 'linear', 0.0, 1.0 ],
[ 'mark1', 0.0, 1.0, 'linear', 0.0, 0.0 ],
[ 'mark2', 0.0, 1.0, 'linear', 0.0, 1.0 ],
[ 'offset', 0.0, 1.0, 'amp', 0.0, 0.0 ]
];

~start = { Synth("difflooper", ['out', ~out, 'bufnum', ~bufnum, 'rate', ~rate, 'mark1', ~mark1, 'mark2', ~mark2, 'offset', ~offset],
	~target, #[\h, \t, \addBefore, \addAfter, \addReplace][~addAction]) };
