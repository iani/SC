/*
Matrix of inputs to outputs. Simple copy, no volume control.


IZ 2011 03 15

{ Matrix.ar(0, [SinOsc.ar(400, 0, 0.1), WhiteNoise.ar(0.2)], [[0, 0], [1, 1]]) }.play;

*/

Matrix { 
	*ar { | out = 0, inputs, iopairs = #[[0, 0], [1, 1]] |

		var outputs;
/*		var test1;
		var test2;
		outputs = Silent.ar(iopairs.flop[1].maxItem + 1);
		
		test1 = outputs[iopairs[0][0]];
		test2 = outputs[0] + test1;
		
		iopairs do: { | io |
//			outputs[io[0]] = outputs[io[0]] + inputs[io[1]];
			io[0].postln;
			outputs[io[0]].postln;
			outputs[io[0]] + inputs[0];
		};
*/
//		outputs = SinOsc.ar([400, 500], 0, 0.1);
		
		outputs = [WhiteNoise.ar(0.1), SinOsc.ar(400, 0, 0.1)];
		outputs.postln;
		
		^Out.ar(out, Mix.ar([outputs[0], outputs[1]])); 			
	}
}