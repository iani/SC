// (SC3:) Generated from SynthDesc on: Sat May 17 19:55:31 2008

~controlSpecs = [
[ 'out', 0.0, 4095.0, 'linear', 1.0, 0.0 ],
[ 'bufnum', 0.0, 1023.0, 'linear', 1.0, 0.0 ],
[ 'pointer', 0.0, 1.0, 'linear', 0.0, 0.0 ],
[ 'freqScale', 0.01, 10.0, 'linear', 0.0, 1.0 ],
[ 'windowSize', 0.0, 1.0, 'linear', 0.0, 0.2 ],
[ 'envbufnum', 0.0, 1.0, 'linear', 0.0, -1.0 ],
[ 'overlaps', 0.0, 16.0, 'linear', 0.0, 8.0 ],
[ 'windowRandRatio', 0.0, 1.0, 'linear', 0.0, 0.0 ],
[ 'interp', 0.0, 2.0, 'linear', 0.0, 1.0 ],
[ 'mul', 0.0, 1.0, 'linear', 0.0, 1.0 ],
[ 'add', 0.0, 1.0, 'linear', 0.0, 0.0 ]
];

~start = { Synth("Warp12", ['out', ~out, 'bufnum', ~bufnum, 'pointer', ~pointer, 'freqScale', ~freqScale, 'windowSize', ~windowSize, 'envbufnum', ~envbufnum, 'overlaps', ~overlaps, 'windowRandRatio', ~windowRandRatio, 'interp', ~interp, 'mul', ~mul, 'add', ~add],
	~target, #[\h, \t, \addBefore, \addAfter, \addReplace][~addAction]) };
