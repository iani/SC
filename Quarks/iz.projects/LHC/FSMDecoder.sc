/*

Usage: 

f = FSMdecoder(receiver);

The receiver is an object that receives the output of the decoder, when there is an output. 
The output is sent by calling sending to the receiver the message newSymbol like this: 

receiver newSymbol: x // where x is one of: \A, \B, \C, \D


f.value(input); 	// where input is 0 or 1. 
				// Calculate the output for this input if any, and the next state. 
*/

FSMdecoder  {

//	var receiver;			// receives message \newSymbol whenever a new symbol is produced
	// no receiver: use changed/update mechanism to broadcast \newSymbol to dependants. 

	var state;			// current FSMstate instance, i.e. current state of the fsm. 
	var <fsm;				// this is the algorithm that produces the states and outputs
						// it consists of instances of FSMstate, in a dictionary	
	*new {
		^super.new.init;
	}
	
	init {
		fsm = IdentityDictionary.new;
		/* construct the fsm. may be parametrized later */
		FSMstate(this, \S0, [\S0, \S1], [\A, nil]);
		FSMstate(this, \S1, [\S0, \S2], [\B, nil]);
		FSMstate(this, \S2, [\S0, \S0], [\C, \D]);
		fsm do: _.getSuccessorStates;
		state = fsm[\S0];
	}

	input { | input | 
		/* calculate the output and the next state, 
		based on the input (argument), and the current state */
		state input: input;
	}

	state_ { | newState |
		state = newState;
//		postf("the new state is now: %\n", state.name);
//		this.changed(\state, state);
	}

	output { | output |
		postf("my output is: %\n", output);
		this.changed(\symbol, output);
	}
}

FSMstate {
	var <fsmDecoder;	// the FSMdecoder running the algorithm that I belong to. 
	var <name = \S0;
	var <>nextStates = #[\S0, \S1];
	var <>outputs = #[\A, nil];
	
	*new { | decoder, name = \S0, states = #[\S0, \S1], outputs = #[\A, nil] |
		^this.newCopyArgs(decoder, name, states, outputs).init;
	}
	
	
	init {
		fsmDecoder.fsm[name] = this;	
	}
	
	getSuccessorStates {
		nextStates = nextStates collect: { | name |
			fsmDecoder.fsm[name];
		}
	}
	
	input { | input = 0 |
		var output;
		output = outputs[input];
		if (output.notNil) { fsmDecoder output: output };
		fsmDecoder.state = nextStates[input];
	}	
}
