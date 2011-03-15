/* A simple matrix: 
Copy any input from an array of inputs to any output in an array of outputs. 
No amplitude scaling of the signals is done. 

IZ 2011 0315 
*/

Matrix {
	*ar { | out = 0, inputs, iopairs = #[[0, 0], [1, 1]] |
	
	
		^Out.ar(out, Silent.ar(2))
	}	
}