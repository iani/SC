/*
IZ 080424

The linking of control outputs to input parameters works now. 

Notes: 

The linking of a 'writing' (output) script with kr output to the input parameter of a 'reading' (input) script is done by mapping the control of the synth (or node) process corresponding to the input parameter of the reading script to the index of the bus of the writing output. The algorithm for creating and freeing link busses is the same as for audio links between scripts. The algorithm for doing the mapping (Script:map) is different than that of setting a parameters value (Script:set) and triggered when either of the reading or writing scripts starts. 

Implementation notes: 

Script keeps all indexes of mapped parameters in variable krInputBusses, in an environment whose keys are the parameters that are mapped and values the indices to which they are mapped. This is used to map the relevant controls of the process of the script from the names of the parameters to the right bus numbers. This is done in method start, after the process has started, by sending it the messages onStart. 
Methods map and unMap of Script take care of adding and removing the parameters to the krInputBusses and of mapping or unmapping the controls to them. 

KrOutput attaches a dependant function to its Script upon creation, which maps all its running readers when the script starts. 
Following Parameter methods are part of the implementation: 
input_ { | argInput | ... } // when setting an input to a parameter, decide whether to mute it (if input is nil) or to map it
resetInputBusIndex { | argIndex | ... }	// takes care of mapping whenever a bus index changes, also when a link is created
applyMap {...}		// if script running, apply registed mapping to parameter. 
					// Called by KrOutput writing to a Parameter, when the KrOutput's script starts
					// see KrOutput:init

krInputRunning {...}	// // called by Script:start to test whether a parameter should be mapped on start
// ------------------------- muting and unmuting inputs
muteInput { script.unMap(this); } // ArInput adds self to MuteBus here!





