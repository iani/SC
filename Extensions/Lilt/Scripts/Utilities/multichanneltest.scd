// (SC3:) Generated from SynthDesc on: Sat Oct 25 16:41:51 2008

~controlSpecs = [
[ 'out', 0.0, 4095.0, 'linear', 1.0, 0.0 ],
[ 'amp', 0.0, 1.0, 'amp', 0.0, 0.1 ],
[ 'rate', 0.125, 8.0, 'exp', 0.0, 1.0 ]
];

~start = { Synth("multichanneltest", ['out', ~out, 'amp', ~amp, 'rate', ~rate],
	~target, #[\h, \t, \addBefore, \addAfter, \addReplace][~addAction]) };
