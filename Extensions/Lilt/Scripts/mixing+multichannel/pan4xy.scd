// (SC3:) Generated from SynthDesc on: Sun Aug 17 16:42:45 2008

~controlSpecs = [
[ 'in', 0.0, 127.0, 'linear', 1.0, 0.0 ],
[ 'out', 0.0, 4095.0, 'linear', 1.0, 0.0 ],
[ 'xpos', -1.0, 1.0, 'linear', 0.0, 0.0 ],
[ 'ypos', -1.0, 1.0, 'linear', 0.0, 0.0 ],
[ 'level', 0.0, 10.0, 'linear', 0.0, 1.0 ]
];

~start = { Synth("pan4xy", ['out', ~out, 'in', ~in, 'xpos', ~xpos, 'ypos', ~ypos, 'level', ~level],
	~target, #[\h, \t, \addBefore, \addAfter, \addReplace][~addAction]) };
