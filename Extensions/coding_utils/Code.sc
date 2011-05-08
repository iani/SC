/* 
Select and execute code in a doc by typing command keys. 
*/

Code {
	classvar <>autoBoot = true;	// if true, forking a string from code will boot the server
	var <doc, <string, <canEvaluate = true;
	var <headers, <positions; // , <functions, <keys;
	
	*new { | doc |
		^this.newCopyArgs(doc).init;	
	}
	
	*fork { | string, clock |
		// compile a string into a function and then fork it as a routine
		var func;
		func = string.compile;
		clock = clock ? AppClock;
		if (autoBoot) { 
			ServerPrep(Server.default).addRoutine({ func.fork(clock) });
			if (Server.default.serverRunning.not) { Server.default.boot };
		}{
			func.fork(clock);
		}
	}

	init {
		var prItems;
		string = doc.string;
		// REMOVED: note: [^ ] means: ignore whitespace after the ":"
		#positions, headers = string.findRegexp("^//:[^\n]*").flop;
		if (positions.size == 0) {
			// prevent evaluation of non-.scd documents with no snippets:
			if ( doc.name.splitext.last != "scd" ) { canEvaluate = false; };
			positions = [0];
			headers = ["//:" ++ doc.getSelectedLines(0, 1)];
		};
		positions = positions add: (string.size + 1);
	}

	*menuItems {
		^[
			CocoaMenuItem.addToMenu("Code", "snippet list view", [/*{*/ "}", false, false], {
				this.showCodeListWindow;
			}),
			CocoaMenuItem.addToMenu("Code", "previous snippet", ["J", false, false], {
				this.selectNextSnippet;
			}),
			CocoaMenuItem.addToMenu("Code", "next snippet", ["K", false, false], {
				this.selectPreviousSnippet;
			}),
			CocoaMenuItem.addToMenu("Code", "fork current snippet (AppClock)", ["X", false, false], {
				this.forkCurrentSnippet(AppClock);
			}),
			CocoaMenuItem.addToMenu("Code", "fork current snippet (SystemClock)", ["X", true, false], {
				this.forkCurrentSnippet(SystemClock);
			}),
			CocoaMenuItem.addToMenu("Code", "eval+post current snippet", ["V", false, false], {
				this.evalPostCurrentSnippet;
			}),
			CocoaMenuItem.addToMenu("Code", "toggle server auto-boot", ["B", true, true], {
				autoBoot = autoBoot.not;
				postf("Server auto-boot set to %\n", autoBoot);
			}),
			CocoaMenuItem.addToMenu("Code", "toggle light/dark color theme", ["T", true, true], {
				DocThemes.toggle;
			}),
		];
	}

	*showCodeListWindow {
		var listWindow;
		listWindow = ListWindow('Code Selector', 
			Rect(
				Window.screenBounds.width - Dock.width, Window.screenBounds.height / 2, 
				Dock.width, Window.screenBounds.height / 2
			), 
			{ | ulistwin |
				var code;
				code = this.new(Document.current);
				if (code.canEvaluate) {
					ListWindow.front('Code Selector');
					code.headers collect: { | h, i | (h[3..] + " ")->{ code.performCodeAt(i) } };
				}{
					["---"->{ }]
				};
			},
			nil,
			Panes, [\docOpened, \docToFront, \docClosed],
			delay: 0.1;
		).onClose({ 
			NotificationCenter.notify(this, \closedCodeListWindow, listWindow);
		});
		NotificationCenter.notify(this, \openedCodeListWindow, listWindow);
	}

	*forkCurrentSnippet { | clock |
		^this.new(Document.current).forkCurrentSnippet(clock);
	}

	forkCurrentSnippet { | clock |
		this.performCodeAt(this.findIndexOfSnippet(doc), \fork, clock);
	}

	performCodeAt { | index = 0, message = \fork, clock |
		if (index.isNil or: { canEvaluate.not }) { ^this };
		(string[positions[index]..(positions[index + 1] - 1)] ?? { { } })
			.perform(message, clock ? AppClock);
	}

	*evalPostCurrentSnippet {
		^this.new(Document.current).evalPostCurrentSnippet;
	}

	evalPostCurrentSnippet {
		this.performCodeAt(this.findIndexOfSnippet(doc), \evalPost );
	}

	*selectNextSnippet {
		^this.new(Document.current).selectNextSnippet;	
	}

	*selectPreviousSnippet {
		^this.new(Document.current).selectPreviousSnippet;
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
	
}