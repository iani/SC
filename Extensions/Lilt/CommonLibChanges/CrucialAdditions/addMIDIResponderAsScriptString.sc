/* (IZ 2007-02-02) {
Saving responders to file and recreating them again from saved data, in format currently used for saving and loading Scripts in Sessions.
} */

+ MIDIResponder {
	asScriptString {
		// this is used for storing parameter's midi bindings in string
		// for snapshot midi bindings see asSnapshotString
		^String.streamContentsLimit({ | stream |
			stream << this.class.name
				<< "("
				<<<* [this.funcString, nil, matchEvent.chan, matchEvent.b, nil, false]
				<<")"
		})
	}
	asSnapshotString {
		// for storing midi bindings of snapshots of scipts. 081024
		// here we also store the c parameter of the MIDIEvent!
		^String.streamContentsLimit({ | stream |
			stream << this.class.name
				<< "("
				<<<* [this.funcString, nil, matchEvent.chan, matchEvent.b, matchEvent.c, false]
				<<")"
		})
	}
	displayString {
		^format("%: %-%-%-%", this.class.name, matchEvent.port,
			matchEvent.chan, matchEvent.b, matchEvent.c);
	}
	funcString {
		^if (function.def.sourceCode.notNil) {
			function.def.sourceCode
		}{ nil }
	}
	printOn { | stream |
		this.storeOn(stream);
	}
	storeArgs {
		^[matchEvent.port, matchEvent.chan, matchEvent.b, matchEvent.c]
	}
}

+ TouchResponder {
	asScriptString {
		^String.streamContentsLimit({ | stream |
			stream << this.class.name
				<< "("
				<<<* [this.funcString, nil, matchEvent.chan, nil, false]
				<<")"
		})
	}
}

+ Ref {
	saveMIDI { | file |
		var resp;
		if ((resp = this.getMIDIbinding).notNil) {
			value.saveMIDI(file, resp);
		}
	}
}
