// Generated from SynthDesc on: Sun Feb 26 21:09:27 2006

~controlSpecs = [
[ 'srcFreq', 0, 600, 'linear', 0, 300 ],
[ 'rezFreq', 0, 600, 'linear', 0, 300 ],
[ 'rq', 0.001, 2, 'exp', 0, 0.05 ],
[ 'amp', 0, 1, 'amp', 0, 0.1 ],
[ 'dur', 0, 16, 'linear', 0, 10 ]
];

~start = { Synth("subtropicrez", ['srcFreq', ~srcFreq, 'rezFreq', ~rezFreq, 'rq', ~rq, 'amp', ~amp, 'dur', ~dur],
	~target, #[\h, \t, \addBefore, \addAfter, \addReplace][~addAction]) };
