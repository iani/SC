/* A simple matrix: 

Project cancelled for now. Won't be needed for Klang Dom this time.

Copy any input from an array of inputs to any output in an array of outputs. 
No amplitude scaling of the signals is done. 

IZ 2011 0315 



Matrix.ar;

*/

Matrix {
	*ar { | out = 0, inputs, iopairs = #[[0, 0], [1, 1]] |
		var numOutputs, outputs;

		iopairs.postln;
//		numOutputs = iopairs.flop[1].maxItem + 1;

//		outputs = Array.newClear(numOutputs);
//		[numOutputs, outputs].postln;	
//		^Out.ar(out, Silent.ar(2))
	}	
}