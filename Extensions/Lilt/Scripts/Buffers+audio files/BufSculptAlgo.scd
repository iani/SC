/* IZ Friday, May 16, 2008 
Trying to sculpt the contents of a buffer algorithmically. 

This script demonstrates how to reach the bin-array of script GraphicFilterNoise and alter it. 
Drag the output of this script to any input of GraphicFilterNoise to connect it. 
The present demo just attenuates the amplitude of the bin-array through division by 4.

*/

var conductor;

~controlSpecs = [
[\t_out]
];

~conductor = conductor = Conductor make: { | conductor |
	
//	button1 = AV(["env1", { "env1".postln });
};


~start = {
	var frequencies;
	frequencies = ~script.output.output.first.script.envir[\conductor][\frequencies];
	frequencies.postln;
	frequencies.value = frequencies.value / 4;
};

