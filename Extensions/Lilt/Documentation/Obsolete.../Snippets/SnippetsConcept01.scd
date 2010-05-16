/* 
080424: The snippets concept is discontinued because it would need to repeat almost everything that Scripts have to be complete. Instead, I will implement linking of the output of one script to aonother script via setting, mapping or other mechanisms, and use scripts instead of snippets. 

Scripts stored in script group "init" of a session will be executed as soon as a session is loaded. 

 =======================================================================

IZ Monday, April 14, 2008:
nippets implement a mechanism for binding a function that operates on any object, including a script, to a MIDI, OSC or other type of trigger (GUI?). 

The present document contains thoughts about how to code this idea. 

A Snippet is an object containing some code which creates a function, an object in whose context the function is to be evaluated, and a "binding" that creates a trigger which executes this function when a message is received via MIDI, OSC or other means. 

The user writes the code of the body of the function without any arguments. The arguments are provided by the snippet, and are: 
	self		An object that is passed to the function as "context", for example a Script
	args		any additional arguments received by the trigger, for example MIDI or OSC message parameters

So if the code is: 
"self.set('freq', args[2].midicps);"

Then the object self may be a Script, and args[2] may be the midi-note number passed from a MIDI note on message. 

Currently, the trigger 

The first test for this is found in script: Snippets/Snippet00

*/

Snippet {
	classvar <snippets;	// identity dictionary binding objects to snippets
	var <receiver;	// an object that is passed to the function as first argument
	var <>code;		// the code that generates the function
	var <>trigger;	// a MIDIRespondeer or OSCresponder that triggers the function when activated by input
	var <function;	// the function compiled from the code

	*initClass {
//		Class.initClassTree(IdentityDictionary);
		snippets = IdentityDictionary.new;
	}

	*new { | receiver, code, trigger |
		^this.newCopyArgs(receiver, code, trigger).init;
	}
	
	init {
		this.compileFunction;
		this.add;
	}
	
	compileFunction {
		function = ("{ | self, args |\n" ++ code ++ "\n}".interpret);
	}

	value { | args |
		function.(receiver, args)
	}

	add {
		snippets[receiver] = snippets[receiver].add(this);
	}
	
	remove {
		this.deactivate;
		snippets[receiver].remove(this);
		if (snippets[receiver].size == 0) { snippets.removeAt(receiver) };
	}

	activate {
		if (trigger.notNil) { trigger.add }
	}
	
	deactivate {
		if (trigger.notNil) { trigger.remove }		
	}
	
	learnMIDI { // start listening to MIDI, 
		// bind to MIDI responder as soon as MIDI has been received
		MIDIListener.addDependant(this);
		MIDIListener.init.startListening;	// may be improved!
	}
	
	update { | who, what |
		thisMethod.report(who, what);
		// these checks will be improved when MIDIListener is rewritten to use 
		// Singleton pattern and be a kind of Model.
		if (what.isKindOf(Array)) {
		}{
			this.addNextResponder(what);
		}
	}
	
}