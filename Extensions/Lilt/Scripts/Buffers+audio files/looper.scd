// (SC3:) Generated from SynthDesc on: Sat Oct 25 00:28:37 2008
/* iz Sunday; October 26, 2008: 2:03 PM
Extended manually for piece. Using SynthDef looperx instead of looper. 
*/

var script;

script = ~script;

~controlSpecs = [
[ 'out', 0, 128, 'linear', 1, 0 ],
[ 'bufnum', 0, 1023, 'linear', 1, 0 ],
[ 'rate', -10.0, 10.0, 'linear', 0.0, 1.0 ],
[ 'mark1', 0.0, 1.0, 'linear', 0.0, 0.0 ],
[ 'mark2', 0.0, 1.0, 'linear', 0.0, 1.0 ],
[ 'amp', 0.0, 1.0, 'amp', 0.0, 0.1];
];

~start = { Synth("looperx", ['out', ~out, 'bufnum', ~bufnum, 'rate', ~rate, 'mark1', ~mark1, 'mark2', ~mark2, 'amp', ~amp],
	~target, #[\h, \t, \addBefore, \addAfter, \addReplace][~addAction]) };

~incrementAmp = { | inc, amp | script.set(\amp, amp + inc) };