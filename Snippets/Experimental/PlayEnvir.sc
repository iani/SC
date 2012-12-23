/* iz Mon 10 December 2012 10:02 AM EET


************************* UNDER CONSTRUCTION ***************************

PlayEnvir Class: Similar to NodeProxy, without using private output bus.

	var <source,  // Array of sources from which the processes are created
	var <envir;   // envir holding parameter settings for creating the processes
	var <process; // Array of playing processes (Synths

Source kinds:
[Source          ->      Process]:
Function         ->      Synth
Pbind            ->      EventSynthPlayer
PbindProxy       ->      EventSynthPlayer
Prout            ->      EventSynthPlayer

All of these notify when stopped, and this is used to update dependants.
Function is used to generate synth via new custom method that creates a synthdef like Function:eplay, and plays that as instrument within the envir event.

========== ?:
PlaySeq: Sequence an array of PlayEnvirs to play sequentially in time.

*/

PlayEnvir {
	var <source;  // Array of sources from which the processes are created
	var <envir;   // envir holding parameter settings for creating the processes
	var <target;  // Group that the process is playing in. Used for patching signal inputs/outputs
	var <process; // Array of playing processes (Synths, EventStreamPlayers)
 	var <inputs, <outputs; // control and audio signal inputs and outputs
	var <pbindProxies; // store PbindProxy instances here to send them \set messages.

	play {

	}

	stop {

	}

	set { | ... paramValPairs |
		paramValPairs pairsDo: { | param, val |
			envir[param] = val;

		};
		target.set(*paramValPairs);
		pbindProxies do: { | pbp | pbp.set(*paramValPairs) };
	}

	map { // | ... |

	}
}

PlaySeq {
	var <seq, <>loop = true;
	var <routine;

	play {

	}

	stop {

	}



}