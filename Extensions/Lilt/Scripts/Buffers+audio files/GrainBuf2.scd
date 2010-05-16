// (SC3:) Generated from SynthDesc on: Sat May 17 19:55:13 2008

~controlSpecs = [
[ 'out', 0.0, 4095.0, 'linear', 1.0, 0.0 ],
[ 'trigger', 0.0, 1.0, 'linear', 1.0, 0.0 ],
[ 'dur', 0.0, 16.0, 'linear', 0.0, 1.0 ],
[ 'bufnum', 0.0, 1023.0, 'linear', 1.0, 0.0 ],
[ 'rate', 0.125, 8.0, 'exp', 0.0, 1.0 ],
[ 'pos', -1.0, 1.0, 'linear', 0.0, 0.0 ],
[ 'interp', 0.0, 4.0, 'linear', 0.0, 2.0 ],
[ 'pan', -1.0, 1.0, 'linear', 0.0, 0.0 ],
[ 'envbufnum', 0.0, 1.0, 'linear', 0.0, -1.0 ],
[ 'mul', 0.0, 1.0, 'linear', 0.0, 1.0 ],
[ 'add', 0.0, 1.0, 'linear', 0.0, 0.0 ]
];

~start = { Synth("GrainBuf2", ['out', ~out, 'trigger', ~trigger, 'dur', ~dur, 'bufnum', ~bufnum, 'rate', ~rate, 'pos', ~pos, 'interp', ~interp, 'pan', ~pan, 'envbufnum', ~envbufnum, 'mul', ~mul, 'add', ~add],
	~target, #[\h, \t, \addBefore, \addAfter, \addReplace][~addAction]) };
