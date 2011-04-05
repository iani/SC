
DocProxy {
	var <name;
	var <path;
	var <text;
	
	*new { | document |
		^this.newCopyArgs(document.name, document.path, document.text);
	}

	open { | fromPath = true |
		^if (fromPath) { this.openFromPath } { this.openFromText };
	}

	openFromPath {
		var match;
		if (path.isNil) { ^this.openFromText };
		match = path.pathMatch;
		if (match.size == 0) { ^this.openFromText }; 
		^Document.open(path);
	}

	openFromText {
		^Document(name, text);
	}
}

DocSession {
	classvar <sessionArchiveRoot = \docSessions;
	var <name;
	var <docs;

	*makeMenuItems {
		CocoaMenuItem.addToMenu("User Menu", "Open Session ...", ["o", true, false], {
			this.loadAndOpenDialog;
		});
		CocoaMenuItem.addToMenu("User Menu", "Save Session ...", ["s", true, false], {
			this.saveAllDialog;
		});
	
	}

	*saveAllDialog {
		TextDialog(
			"input name of session",
			Date.getDate.stamp,
			{ | i | this.newWithAllDocs(i).save },
			{ "save cancelled".postln }
		);
	}

	*newWithAllDocs { | i |
		^this.newCopyArgs(i).getAllDocs;
	}
	
	getAllDocs {
		docs = Document.allDocuments.select({ | d | d.isListener.not }).collect(DocProxy(_));
	}
	
	save {
		Archive.global.put(sessionArchiveRoot, name.asSymbol, this);
		Archive.write;
	}
	
	*load { | name |
		^Archive.global.at(sessionArchiveRoot, name.asSymbol);
	}

	*loadDialog { | ref |
		Archive.global.at(sessionArchiveRoot).postln;
		ListSelectDialog("Select a session", Archive.global.at(sessionArchiveRoot).keys.asArray,
			{ | i, name |
				ref.value = this.load(name);
			},{
				"Loading cancelled".postln;
			}
		);
	}

	*loadAndOpenDialog {
		Archive.global.at(sessionArchiveRoot).postln;
		ListSelectDialog("Select a session", Archive.global.at(sessionArchiveRoot).keys.asArray,
			{ | i, name |
				this.load(name).openAllDocs;
			},{
				"Loading cancelled".postln;
			}
		);
	}


	openAllDocs {
		docs do: _.open;	
	}
}