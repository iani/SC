/* (IZ 2006-02-27) { 
This is the first score made to play with the new Script framework for Sand. It serves as example script. Other scores can be made based on this one.
} */

var script;	// use this only if you need access to this script within a rule
script = ~script;	// script must be local variable to be used in rule

// array of rules for reacting to data input from sand program:
~conductor = [
{ | sender, data |
	var tag, value;
	#tag, value = data;
	if (tag == \index) { Synth("nohihat") };
},

{ | ...args |
	// Last rule just print out reception from data reception script.
	Post << script.name << ": " <<* args << "\n";
}

];

