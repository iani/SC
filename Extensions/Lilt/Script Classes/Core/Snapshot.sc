/* IZ 2007-01-26 { SC3
Stores the states of all parameters of a Script in a List as a "snapshot". It adds the capability of binding a MIDI command to load a Snapshot instance to its script. 
It is made as a subclass of List for simplicity in use with ListModel and Script. 
The structure of the Array inside a Snapshots array variable (inherited from List) is: 
	[<name of parameter>, [v1, v2, ... vn]] 
where v1, v2 ... vn are the values of the parameters taken by the snapshot. 

script: the Script that will load this snapshot when the MIDI command is received. 

------------------------- MIDI responder save / load implementation is under construction. 
As of 080428 it may be abandoned, to be replaced by binding the snapshot to via global midi binding mechanism. 
To implement the MIDI binding capability, the following 2 variables are added: 
midiResponder: the MIDIresponder which can be activated to load this snapshot via MIDI command.

} */

Snapshot : List {
	var <>script;			// Script that will load this snapshot when the MIDI command is received. 
 	var <midiResponder;	// MIDIresponder which can be activated to load this snapshot via MIDI command

	*fromScriptData { | script, name, paramValues, responderSpec |
		^this.newUsing([name.asSymbol, paramValues]).init(script, responderSpec);
	}
	init { | argScript, argResponderSpec |
		script = argScript;
		if (argResponderSpec.notNil) { this.makeResponder(argResponderSpec) };
	}
	load {
		// load to script
		script.loadSnapshot(array[1]);
	}
	makeResponder { | argResponderSpec |
		this.midiResponder = argResponderSpec.interpret;
	}
	midiResponder_ { | argResponder |
		if (midiResponder.notNil) { midiResponder.remove };
		// make your own copy because the same input may want to activate multiple snapshots!
		// activate immediately. 081024
		midiResponder = argResponder.class.new(nil, nil,
			argResponder.matchEvent.chan, 
			argResponder.matchEvent.b, 
			argResponder.matchEvent.c, 
			install: true
		);
		midiResponder.function = { this.load };
	}
	writeSnapshot { | file |
//		thisMethod.report("array:", array, "midiResponder:", midiResponder);
		file putAll: ["\t", this.asScriptData.asCompileString, ",\n"];
	}
	asScriptData {
		if (midiResponder.isNil) {
			^array;
		}{
			^array add: midiResponder.asSnapshotString;
		}
	}
	// TODO: Rewrite using MIDIHandler
	activateMIDI {
		if (midiResponder.notNil) { midiResponder.remove }
	}
	deactivateMIDI {
		if (midiResponder.notNil) { midiResponder.add }
	}
}
