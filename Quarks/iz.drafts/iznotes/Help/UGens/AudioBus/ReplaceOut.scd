/* IZ 20120309

ReplaceOut overwrites its own output to the channels specified. The previous signal contents of these channels are erased. 

Here is an example demonstrating its use. 

*/

//:Source to work on: Output a sine on channel 0

\source playFunc: { Mix(SinOsc.ar([400, 500], 0, 0.1)) };

//:Example 1: Not using ReplaceOut. The source sound is not erased. Its shifted version is added
// *Add* a pitch-shifted version of the signal of channel 0 to channel 0

\fx1.playFunc({ LFNoise0.kr(5) * PitchShift.ar(In.ar(0), 0.1, 3 / 2) }, addAction: \addToTail);

//:Stop example 1

\fx1.free;

//:Example 2: Using ReplaceOut. The source sound is erased. Only its shifted version is output
// *Replace* channel 0 by a pitch-shifted version 

\fx2.playFunc({ 
	ReplaceOut.ar(0, 
		LFNoise0.kr(5) * PitchShift.ar(In.ar(0), 0.1, 3 / 2)
	);
}, addAction: \addToTail);

//:Stop example 2
\fx2.free;

//:Stop the source sound
\source.free

