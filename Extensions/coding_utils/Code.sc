/* 
Select and execute code in a doc by typing command keys. 
*/

Code {
	classvar <>autoBoot = true;	// if true, forking a string from code will boot the server
	var <doc, <string, <canEvaluate = true;
	var <positions; // , <functions, <keys;
	var <snippetstart, <snippetend;
	var <prevsnippetstart, <prevsnippetend;
	var <thissnippetstart, <thissnippetend;
	var <nextsnippetstart, <nextsnippetend;
	
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
		positions = string.findRegexp("^//:");
	}
	
	headers {
		^string.findRegexp("^//:[^\n]*").flop[1];
	}

	*menuItems {
		^[
			CocoaMenuItem.addToMenu("Code", "Configure core quarks", nil, {
				GitQuarks(localPath: Platform.userAppSupportDir +/+ "quarks.local.core").gui;
			}),
			CocoaMenuItem.addToMenu("Code", "Configure project quarks", nil, {
				GitQuarks(localPath: Platform.userAppSupportDir +/+ "quarks.local.projects").gui;
			}),
			CocoaMenuItem.addToMenu("Code", "snippet list view", [/*{*/ "}", false, false], {
				this.showCodeListWindow;
			}),
			CocoaMenuItem.addToMenu("Code", "snippet buttons", [/*{*/ "}", true, false], {
				this.showCodeButtonsWindow;
			}),
			CocoaMenuItem.addToMenu("Code", "make osc snippet commands", [/*{*/ "}", true, true], {
				this.makeCodeOSC;
			}),
			CocoaMenuItem.addToMenu("Code", "previous snippet", ["J", false, false], {
				this.selectPreviousSnippet;
			}),
			CocoaMenuItem.addToMenu("Code", "next snippet", ["K", false, false], {
				this.selectNextSnippet;
			}),
			CocoaMenuItem.addToMenu("Code", "fork current snippet (AppClock)", ["X", false, false],
				{ this.forkCurrentSnippet(AppClock); }
			),
			CocoaMenuItem.addToMenu("Code", "fork current snippet (SystemClock)", 
				["X", true, false], 
				{ this.forkCurrentSnippet(SystemClock); }
			),
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
					code.headers
						.collect({ | h, i | (h[3..] + " ")->{ code.performCodeAt(i + 1) } })
						.select({ | h | h.key[0] != $  });
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

	*showCodeButtonsWindow { ^CodeButtons(Document.current); }

	*makeCodeOSC { ^CodeOSC(Document.current); }

	*forkCurrentSnippet { | clock |
		^this.new(Document.current).forkCurrentSnippet(clock);
	}

	forkCurrentSnippet { | clock |
		this.performCodeAt(this.findIndexOfSnippet(doc), \fork, clock);
	}

	findIndexOfSnippet {
		var selectionStart, pos;
		selectionStart = doc.selectionStart;
		pos = positions.detect({ | p | selectionStart < p[0] });
		if (pos.isNil) { ^positions.size } { ^positions.indexOf(pos) };
	}

	performCodeAt { | index = 0, message = \fork, clock |
		var snippet;
		var start, end;
		#start, end = this.getSnippetAt(index);
		snippet = string[start..end];
		postf("snippet: %\n", snippet);
		this.notify(\snippet, snippet);
		(string[start..end] ?? { "{ }" }).perform(message, clock ? AppClock);
	}

	getSnippetAt { | index |
		if (positions.size == 0) { ^[0, string.size - 1] };
		if (index <= 0) { ^[0, positions[0][0] - 1] };
		if (index >= positions.size) { ^[positions.last[0], string.size - 1] };
		^[positions[index - 1][0], positions[index][0] - 1];
	}

	getSnippetStringAt { | index |
		var begin, end;
		#begin, end = this.getSnippetAt(index);
		^string[begin..end];
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

	selectNextSnippet {
		var start, length;
		this.findSnippets;
		start = nextsnippetstart;
		length = nextsnippetend - nextsnippetstart;
		doc.selectRange(start, length);
	}

	selectPreviousSnippet {
		var start, length;
		this.findSnippets;
		start = prevsnippetstart;
		length = prevsnippetend - prevsnippetstart;
		doc.selectRange(start, length);
	}
	
	findSnippets {
		var curpos;
		var prevend, curend, nextend;
		if (positions.size == 0) {
			prevsnippetstart = thissnippetstart = nextsnippetstart = 0;
			prevsnippetend = thissnippetstart = thissnippetend = string.size;
			^this;
		};
		curpos = doc.selectionStart;
		curend = positions indexOf: positions.detect({ | p | p[0] > curpos });
		if (curend.isNil) { 	// we are at the last snippet
			#thissnippetstart, thissnippetend = this.getSnippetAt(positions.size);
			#prevsnippetstart, prevsnippetend = this.getSnippetAt(positions.size - 1);
							// wrap to first snippet
			#nextsnippetstart, nextsnippetend = this.getSnippetAt(0);
			^this;
		};
		if (curend == 0) { 	// we are at the first snippet
			#thissnippetstart, thissnippetend = this.getSnippetAt(0);
							// wrap to last snippet
			#prevsnippetstart, prevsnippetend = this.getSnippetAt(positions.size);
			#nextsnippetstart, nextsnippetend = this.getSnippetAt(1);
			^this;
		};
		#thissnippetstart, thissnippetend = this.getSnippetAt(curend);
		#prevsnippetstart, prevsnippetend = this.getSnippetAt(curend - 1);
		#nextsnippetstart, nextsnippetend = this.getSnippetAt(curend + 1);
	}
}