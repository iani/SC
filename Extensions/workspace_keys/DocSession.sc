
DocProxy {
	var <name;
	var <path;
	var <text;
	
	*new { | document |
		^this.newCopyArgs(document.name, document.path, document.text);
	}

	open { | fromArchive = false |
		var doc, extension;
		doc = if (fromArchive) { this.openFromArchive } { this.openFromPath };
		extension = doc.name.splitext.last;
		if (extension == "scd" or: { extension == "sc" }) { doc.syntaxColorize };
		^doc;
	}

	openFromPath {
		var match;
		if (path.isNil) { ^this.openFromArchive };
		match = path.pathMatch;
		if (match.size == 0) { ^this.openFromArchive }; 
		^Document.open(path);
	}

	openFromArchive {
		^Document(name, text);
	}
}

DocSession {
	classvar <sessionArchiveRoot = \docSessions;
	var <name;
	var <docs;

	*makeMenuItems {
		CocoaMenuItem.addToMenu("User Menu", "Open Session ...", ["o", true, false], {
			this.loadAndOpenDialog(fromArchive: false);
		});
		CocoaMenuItem.addToMenu("User Menu", "Open Session from Archive ...", ["O", true, false], {
			this.loadAndOpenDialog(fromArchive: true);
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
		ListSelectDialog("Select a session", Archive.global.at(sessionArchiveRoot).keys.asArray,
			{ | i, name |
				ref.value = this.load(name);
			},{
				"Loading cancelled".postln;
			}
		);
	}

	*loadAndOpenDialog { | fromArchive = false |
		Archive.global.at(sessionArchiveRoot).postln;
		ListSelectDialog("Select a session", Archive.global.at(sessionArchiveRoot).keys.asArray,
			{ | i, name |
				this.load(name).openAllDocs(fromArchive);
			},{
				"Loading cancelled".postln;
			}
		);
	}

	openAllDocs { | fromArchive = false |
		docs do: _.open(fromArchive);	
	}
}