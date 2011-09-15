/* (IZ 2006-02-27) { 
This is the first score made to play with the new Script framework for Sand. It serves as example script. Other scores can be made based on this one.

test score 00: just post the arguments received

This script uses no instrument subscripts.
} */

var script;	// use this only if you need access to this script within a rule
script = ~script;	// script must be local variable to be used in rule

// The rules. Here just one rule: 
~conductor = { | ...args |
	// Just print out reception from data reception script.
	Post << script.name << ": " <<* args << "\n";
}
