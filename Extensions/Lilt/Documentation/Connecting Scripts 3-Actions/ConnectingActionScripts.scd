/*
In brief: 
A script can have a trigger-rate (t-rate) output instead of an audio-rate or control-rate output. This is done by including in the script's control specs a spec whose name starts with 't_'. for example:  

~controlSpecs = [
	['t_out'] // 't_out' as name creates a TrOutput parameter
];

Dragging the output box next to the script's name from the GUI to any input parameter of another script will link the t_output to the input parameter of that script. The writing script with the t_out can then output numeric values to the input parameters of the linked reader scripts in the following way: 

~script.routine {
	loop {
		0.25.wait;
		'broadcasting'.postln;
		script.set('t_out', 1.0.rand);	// set value, update gui, broadcast to linked readers
	}
}

Alternatively, 


 =======================================================================
Older stuff: 
IZ 080426
Drafting the mechanism with Marinos G. 

Main idea: 
A script can direct its action output to a set of other scripts. 
It needs to keep: 
- The set of scripts
- For each script, which parameter it connects to! 
	-> Generalization: For each script, a function that is applied to the script with the data of the output? 
	-> Second possible generalization: Set of actions coupled with the target script and also taking the output 
	 	script as parameter? 



Example: 


var targets, script;
targets = ~targets; // or ~script.targets ...
script = ~script;

~script.routine {
	var random_value;
	loop {
		0.1.wait;
		random_value = 1.rrand(100);
		targets do: _.(script, random_value); // this is the mechanism that should be coded in a method. 
	}
} 

// draft of method from above: 
	update_action_readers { | ... args |
		action_readers do: { | script, action | action.(script, this, *args) }
	}
*/