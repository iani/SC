/* (IZ 2006-02-27) { 
This is the first score made to play with the new Script framework for Sand. It serves as example script. Other scores can be made based on this one.

test score 01: Play a short tick at each vertex, and post the arguments received

This script uses an array of 2 rules and no instrument scripts.

} */

var script;	// use this only if you need access to this script within a rule
script = ~script;	// script must be local variable to be used in rule

// array of rules for reacting to data input from sand program:
~conductor = [
{ | sender, tag, value |		// rule 1
	Synth("nohihat");
},
{ | ...args |					// rule 2
	// Last rule just print out reception from data reception script.
	Post << script.name << ": " <<* args << "\n";
}
];	// end of rules array. This score contains 2 rules. 

