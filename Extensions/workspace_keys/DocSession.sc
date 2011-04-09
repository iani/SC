
DocProxy {
	classvar <docBounds;
	var <name;
	var <path;
	var <text;
	var <>bounds;		// for restoring bounds of documents saved in sessions;
	var <timestamp;	// timestamp of date last saved in archive. (Date instances themselves cannot be archived)

	*initClass {
		docBounds = IdentityDictionary.new;	
	}

	*new { | document |
		^this.newCopyArgs(document.name, document.path, document.text, document.bounds, Date.getDate.stamp).init(document);
	}

	init { | document |
		name = document.name; path = document.path;
	}

	open { | fromArchive = false |
		var doc, extension;
		if (Document.allDocuments.detect({ | d | d.name == name }).notNil) {
			^postf("Document \"%\" is already open\n", name);
		};
		doc = if (fromArchive) { this.openFromArchive } { this.openFromPath };
		docBounds.put(doc, bounds);
		^doc;
	}
	
	*boundsFor { | doc |
		^docBounds.at(doc);	
	}
	
	*removeDocBounds { | doc |
		docBounds.put(doc, nil);
	}
	
	*loadDefaultBounds {

		DocSession.default.docs do: { | d | 
			docBounds[d.name.asSymbol] = d.bounds;
		};
		Document.allDocuments do: { | d |
			docBounds[d] = docBounds[d.name.asSymbol];
		};

	}

	openFromPath {
		var match;
		if (path.isNil) { ^this.openFromArchive };
		match = path.pathMatch;
		if (match.size == 0) { ^this.openFromArchive }; 
		^Document.open(path);
	}

	openFromArchive {
		var doc, extension;
		postf("opening % from archive\n", name);
		doc = Document(name, text);
		extension = doc.name.splitext.last;
		if (extension == "scd" or: { extension == "sc" }) { doc.syntaxColorize };
		^doc;
	}
}



DocWithBounds {
	var <doc;
	var <docProxy;
	
	*new { | doc, docProxy | 
		^this.newCopyArgs(doc, docProxy ?? { DocProxy(doc) });
	}
	
	name { ^doc.name }
	path { ^doc.path }
	bounds { ^doc.bounds }
	text { ^doc.text }
	string { ^doc.string }
	
	bounds_ { | bounds |
		doc.bounds = bounds;
		docProxy.bounds = bounds;	
	}
	
	front { doc.front }
	
	isListener { ^doc.isListener }
	
	close { doc.name.postln; "closing".postln; doc.close }
}


DocSession {
	classvar <sessionArchiveRoot = \docSessions, defaultSessionName = \recent;
	var <name;
	var <docs;

	*new { | name, docProxies |
		^this.newCopyArgs(name, docProxies ?? { this.getAllDocs });
	}

	*getAllDocs { 
		^Document.allDocuments collect: DocProxy(_);	
	}

	*saveDialog { | docProxies |
		TextDialog(
			"input name of session",
			Date.getDate.stamp,
			{ | name | this.new(name, docProxies).save },
			{ "save cancelled".postln }
		);
	}
	
	save {
		Archive.global.put(sessionArchiveRoot, name.asSymbol, this);
		Archive.write;
	}

	
	*load { | name |
		^Archive.global.at(sessionArchiveRoot, name.asSymbol);
	}

	*default { ^this.load(defaultSessionName) }

	*loadDialog { | ref |
		ListSelectDialog("Select a session", Archive.global.at(sessionArchiveRoot).keys.asArray,
			{ | i, name |
				ref.value = this.load(name);
			},{
				"Loading cancelled".postln;
			}
		);
	}

	*loadAndOpenDialog { | fromArchive = false, docListWindow |
		ListSelectDialog("Select a session", Archive.global.at(sessionArchiveRoot).keys.asArray,
			{ | i, name |
				this.load(name).openAllDocs(fromArchive, docListWindow);
			},{
				"Loading cancelled".postln;
			}
		);
	}

	openAllDocs { | fromArchive = false, docListWindow |
		docs do: _.open(fromArchive, docListWindow);	
	}
}