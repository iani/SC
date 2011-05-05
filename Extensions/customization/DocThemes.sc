DocThemes {
	classvar <defaultCustomTheme = \pinkString;
	classvar <currentTheme = \default;

	*initClass { StartUp.add(this); }

	*doOnStartUp {
		this.createDefaultCustomTheme;
	}

	*toggle {
		if (currentTheme === \default) {
			this setTheme: defaultCustomTheme;
		}{
			this.resetToSCdefault;
		}
	}

	*setTheme { | symbol |
		if (symbol == currentTheme) { ^this };
		if (Document.themes.at(symbol).isNil) {
			postf("Could not find Document theme named %\n", symbol);
			postf("These are the current themes: %\n", Document.themes.keys);
			^this;
		};
		currentTheme = symbol;
		this.activate;
		Document.openDocuments do: _.colorizeIfAppropriate;
	}

	*resetToSCdefault {
		this setTheme: \default;
		this.deactivate;
	}

	*activate {
		NotificationCenter.register(Panes, \docToFront, this, { | doc |
			"WILL COLORIZE".postln;
			doc.colorizeIfAppropriate;
		});
	}
	
	*deactivate {
		NotificationCenter.unregister(Panes, \docToFront, this);
	}

	*createDefaultCustomTheme {
		Document.themes[\pinkString] = Document.themes[\default].copy;
		Document.themes[\pinkString][\textColor] = Color(0.4, 0.4, 0.5);
		Document.themes[\pinkString][\textColor] = Color(0.9, 0.7, 0.6);
		Document.themes[\pinkString][\stringColor] = Color(0.9, 0.1, 0.6);
		Document.themes[\pinkString][\numberColor] = Color(0.7, 0.2, 0, 1);
		Document.themes[\pinkString][\classColor] = Color(0.1, 0.6, 0.9);
		Document.themes[\pinkString][\commentColor] = Color(0.99, 0.52, 0.14, 0.99);
		Document.themes[\pinkString][\background] = Color(0.1, 0.1, 0.1, 1);
		Document.themes[\default][\background] = Color.white;
		defaultCustomTheme = \pinkString;
	}


}

/*
		{
			if (doc.name.splitext.last != "html") {
				doc.background_(backgroundColor);
				if (doc.name.includes($.).not) {
					if (doc.name[..7] == "Untitled") {
						doc.string = " ";
						doc.selectLine(0);
					};
					doc.syntaxColorize;
				};
			};
		}.defer(0.1);


			if ("\.html".matchRegexp(Document.current.name)) {
			}{
				if (Document.theme !== Document.themes[customTheme]) {
					Document.setTheme(customTheme);
					selectionStart = doc.selectionStart;
					selectionSize = doc.selectionSize;	
					doc.selectRange(0, 2147483647); // select everything
					doc.syntaxColorize;  // restore selection: 
					doc.selectRange(selectionStart, selectionSize);
				};
			};

		Document.allDocuments do: this.setDocActions(_);
//		backgroundColor = backgroundColor ? Color.grey(0.05);

		{
			if (doc.name.splitext.last != "html") {
				doc.background_(backgroundColor);
				if (doc.name.includes($.).not) {
					if (doc.name[..7] == "Untitled") {
						doc.string = " ";
						doc.selectLine(0);
					};
					doc.syntaxColorize;
				};
			};
		}.defer(0.1);
*/