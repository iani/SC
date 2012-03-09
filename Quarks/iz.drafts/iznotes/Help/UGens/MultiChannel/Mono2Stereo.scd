/* IZ 20120309

Distribute the mono output of a synth to 2 channels, when playing in a stereo setting. 

See also ReplaceOut example in MultiChannel folder. 

*/



//:Move the output gradually between left and right 

\panmoving.playFunc({ 
	ReplaceOut.ar(0, Pan2.ar(In.ar(0), LFNoise1.kr(0.2).range(-1, 1.0)));
}, addAction: \addToTail);

//:--

\panmoving.free;

