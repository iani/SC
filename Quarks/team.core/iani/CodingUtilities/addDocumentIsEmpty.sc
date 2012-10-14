
+ Document {
	colorizeIfAppropriate { | initColorize = false |
		var start, size;
		if (this.shouldColorize) {
			Document.setTheme(DocThemes.currentTheme);
			this.background = Document.theme[\background];
			Document.setTheme(DocThemes.currentTheme);
			if (this.isEmpty) {
				this.string = " ";
				this.selectLine(1);
			};
			start = this.selectionStart;
			size = this.selectionSize;	
			this.selectRange(0, 2147483647); // select everything
			this.syntaxColorize; 			// colorize
			this.selectRange(start, size);	// restore selection
		}{
			Document.setTheme(\default);
		}
	}

	shouldColorize { ^"\.html$|\.rtf$".matchRegexp(this.name).not }

	isEmpty { ^this.currentLine.isEmpty and: { this.selectionStart == 0 } }
}