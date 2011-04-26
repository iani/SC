/* 
Select and execute code in a doc by typing command keys. 

Will replace part of code of DocListWindow


TODO: don't need to collect all strings all the time. 
But: run following regexp to get the headers and their positions: 

"string ... ".findRegexp("^//:[^ ][^\n]*");

*/

Code {
	classvar <menuItems;
	var <doc, <string;
	var <headers, <positions; // , <functions, <keys;
	
	*new { | doc |
		^this.newCopyArgs(doc).init;	
	}
	
	*test {
		^this.new(Document.current);			
	}

	*runCurrentSnippet {
		^this.new(Document.current).runCurrentSnippet;	
	}

	*selectNextSnippet {
		^this.new(Document.current).selectNextSnippet;	
	}

	*selectPreviousSnippet {
		^this.new(Document.current).selectPreviousSnippet;
	}

	init {
		var prItems;
		string = doc.string;
		// note: [^ ] means: ignore whitespace after the ":"
		#positions, headers = string.findRegexp("^//:[^ ][^\n]*").flop;
		if (positions.size == 0) {
			positions = [0];
			headers = [doc.getSelectedLines(0, 1)];
		};
		positions = positions add: (string.size + 1);
//		positions.postln;
//		headers.postln;
	}

	runCurrentSnippet {
		this.performCodeAt(this.findIndexOfSnippet(doc));
	}

	performCodeAt { | index = 0 |
		if (index.isNil) { ^this };
//		string[positions[index]..(positions[index + 1] - 1)].postln;
		string[positions[index]..(positions[index + 1] - 1)].fork;
	}

	findIndexOfSnippet {
		var selectionStart;
		selectionStart = doc.selectionStart;		
		^positions.indexOf(positions.detect({ | n | selectionStart < n })) - 1
	}

	selectNextSnippet {
		var start, length;
		#start, length = positions[
			[0, 1] + (this.findIndexOfSnippet(doc) + 1).min(headers.size - 1)
		].differentiate;
		doc.selectRange(start, length); // - 1
	}

	selectPreviousSnippet {
		var start, length;
		#start, length = positions[
			[0, 1] + (this.findIndexOfSnippet(doc) - 1).max(0)
		].differentiate;
		doc.selectRange(start, length); // - 1
	}

	*addMenuItems {
		menuItems = [
//			CocoaMenuItem.addToMenu("User Menu", "test Code Class", ["T", false, false], {
//				this.test;
//			}),
			CocoaMenuItem.addToMenu("User Menu", "run current snippet", ["X", false, false], {
				this.runCurrentSnippet;
			}),
			CocoaMenuItem.addToMenu("User Menu", "previous snippet", ["J", false, false], {
				this.selectNextSnippet;
			}),
			CocoaMenuItem.addToMenu("User Menu", "next snippet", ["K", false, false], {
				this.selectPreviousSnippet;
			}),

		];
	}
	*removeMenuItems { menuItems do: _.remove; }
}