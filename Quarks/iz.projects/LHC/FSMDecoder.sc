/*

Usage: 

f = FSMdecoder(receiver);

The receiver is an object that receives the output of the decoder, when there is an output. 
The output is sent by calling sending to the receiver the message newSymbol like this: 

receiver newSymbol: x // where x is one of: \A, \B, \C, \D


f.value(input); 	// where input is 0 or 1. 
				// Calculate the output for this input if any, and the next state. 
*/

FSMdecoder {

	var receiver;			// receives message \newSymbol whenever a new symbol is produced
	var state = 0;		// scalar. Contains the integers 0, 1, or 2
	var algorithm;		// this is the algorithm that produces the states and outputs
						// it consists of instances of FSMstate, in an array;	
	*new { | receiver |
		^this.newCopyArgs(receiver);
	}

	value { | input | 
		/* calculate the output and the next state, 
		based on the input (argument), and the current state */
		this.calcNextState(input); // not yet sure about this
		this.output;				// not yet sure about this
	}

	
	
	
}

FSMstate {
	var <fsmDecoder;	// the FSMdecoder running the algorithm that I belong to. 
	var <name = \S0;
	var <>nextStates = #[\S0, \S1];
	var <>outputs = #[\A, nil];
	
	input { | binaryValue = 0 |
		this nextState: binaryValue;
		this.output;
	}
	
	nextState { | binaryValue = 0 |
		var output;
		fsmDecoder nextState: nextStates[binaryValue];
		output = outputs[binaryValue];
		if (output) { fsmDecoder output: output };
	}
	
}