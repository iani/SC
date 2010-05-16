// (SC3:) Generated from SynthDesc on: Fri May 16 15:43:57 2008

~controlSpecs = [
[ 'in', 0.0, 127.0, 'linear', 1.0, 0.0 ],
[ 'out', 0.0, 4095.0, 'linear', 1.0, 0.0 ],
[ 'amp', 0.0, 10.0, 'amp', 0.0, 1.0 ]
];

~start = { Synth("input_monitor", ['in', ~in, 'out', ~out, 'amp', ~amp],
	~target, #[\h, \t, \addBefore, \addAfter, \addReplace][~addAction]) };
