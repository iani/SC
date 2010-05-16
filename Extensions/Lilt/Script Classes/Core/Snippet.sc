/* IZ 2007-06-19 SC3 (Compeletely redone Monday, April 14, 2008)

Snippets implement a mechanism for binding a function that operates on any object, including a script, to a MIDIResponder.

Subclasses of this will enable binding to OSC or other type of trigger (GUI?). 

A Snippet is an object containing some code which creates a function, an object in whose context the function is to be evaluated, and a "binding" that creates a trigger which executes this function when a message is received via MIDI, OSC or other means. 

The user writes the code of the body of the function without any arguments. The arguments are provided by the snippet, and are: 
	self		An object that is passed to the function as "context", for example a Script
	args		any additional arguments received by the trigger, for example MIDI or OSC message parameters

So if the code is: 
"self.set('freq', args[2].midicps);"

Then the object self may be a Script, and args[2] may be the midi-note number passed from a MIDI note on message. 

Currently, the trigger 

The first test for this is found in script: Snippets/Snippet00

a = Snippet.new;
a.learnMIDI;
a.activate;
a.deactivate;
a.trigger.asScriptString;
a.asScriptString
*/

Snippet {
	classvar <snippets;	// identity dictionary binding objects to snippets
	var <receiver;	// an object that is passed to the function as first argument
	var <name;		// string indicative of action of this snippet
	var <code;		// the code that generates the function
	var <trigger;		// MIDIRespondeer or OSCresponder that runs function when activated by input
	var <function;	// the function compiled from the code

	*initClass {
//		Class.initClassTree(IdentityDictionary);
		snippets = IdentityDictionary.new;
	}

	*new { | receiver, name, code, trigger |
		^this.newCopyArgs(receiver ? this, name ? "an action", code ? "[self, args].postln", trigger).init;
	}

	init {
		this.compileFunction;
		this.add;
	}
	
	compileFunction {
		function = ("{ | self, args |\n" ++ code ++ "\n}").interpret;
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

	code_ { | argCode |
		code = argCode;
		this.compileFunction;
		if (trigger.notNil) { trigger.function = function }
	}

	trigger_ { | argTrigger |
		this.remove;
		trigger = argTrigger;
	}

	learnMIDI { // start listening to MIDI, 
		// bind to MIDI responder as soon as MIDI has been received
		MIDIListener.addDependant(this);
		MIDIListener.init.startListening;
	}
	
	update { | who, what |
	// if MIDIResponder received, then bind it as trigger
//		thisMethod.report(who, what);
		if (what.isKindOf(MIDIResponder)) {
			this.deactivate;
			what.function = { | ... args | this.value(args); };
			MIDIListener.removeDependant(this);
			MIDIListener.stopListening;
			this.trigger = what;
			thisMethod.report("bound to MIDI:", what);
		};
	}
	edit {
		// create GUI for editing the function. Also for binding to MIDI?
		"not yet implemented".postln;
	}
	asScriptString {
		// return string that can be stored in session script
		^"[\"" ++ code ++ "\", " ++ trigger.asScriptString ++ "]";
	}
}

