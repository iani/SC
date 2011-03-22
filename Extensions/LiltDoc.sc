/*
LiltDoc.openDialog;
LiltDoc.install;
*/
LiltDoc {
	classvar isInstalled = false;
	var <document;

	*install { // install the global keyboard command for activation of the current document
		var group, item;
		group = CocoaMenuItem.topLevelItems detect: { | i | i.name == "Lilt Doc" };
		if (isInstalled) { ^"Lilt Doc already installed" };
		CocoaMenuItem.addToMenu("User Menu", 
			"Current Doc Palette", 
			["1", false, false],
			{ LiltDocPalette(Document.current) }
		);
		isInstalled = true;
	}

	*new { | path |
		^this.newCopyArgs(path);
	}
	
	*openDialog {
		Dialog.getPaths({ | paths | 
			^paths collect: LiltDoc(_);
		});
	}

	makeSnippets {
		var text;
		
	}

	findDocument {
		
	}

	parse {
	
	}
}

LiltText {
	var <text, <snippets;
	
	*new { | text |
		^this.newCopyArgs(text).init;	
	}
	
	init {
		var poslist;
		poslist = text.findRegexp("^//:").slice(nil, 0) ?? { [0] };
		snippets = poslist collect: { | pos, i |
			text[pos..(poslist[i + 1] ?? { text.size }) - 1]
		};
	}
	
}

LiltDocPalette {
	var <document;
	var <liltText;
	var <window, <listv, <keys;
	*new { | document |
		// first close any already existing palette for this document
		NotificationCenter.notify(document, \closePalette);
		^this.newCopyArgs(document).init;
	}
	
	init {
		liltText = LiltText(document.string);
		NotificationCenter.register(document, \closePalette, this, { this.closeWindow });
		window = GUI.window.new(document.name.splitext.first, Rect(0, 170, 150, 400)).front;
		listv = GUI.listView.new(window, window.view.bounds.insetBy(2, 2));
		listv.items = liltText.snippets collect: { | s | s[3..30] };
		keys = listv.items collect: _.first;
		window.view.keyDownAction = { | view, char, mod, key | listv.keyDown(char, mod, key); };
		listv.keyDownAction = { | view, char, mod, key |
			[keys, char, keys indexOf: char].postln;
		};
	}
	
	closeWindow { if (window.notNil) { window.close; window = nil; listv = nil; } }
}

