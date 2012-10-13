/* 
Select and execute code in a doc by typing command keys. 
*/

Code {
	classvar <>autoBoot = false;	// if true, forking a string from code will boot the server
	var <doc, <string, <canEvaluate = true;
	var <positions; // , <functions, <keys;
	var <snippetstart, <snippetend;
	var <prevsnippetstart, <prevsnippetend;
	var <thissnippetstart, <thissnippetend;
	var <nextsnippetstart, <nextsnippetend;
	var <snippetSeparator = ":";

	*initClass {
		StartUp add: { this.menuItems }
	}

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
		};
		if (History.started) { History.enter(string) };
	}

	init {
		string = doc.string;
		if (string[52..61] == "// History") { snippetSeparator = " - " };
		positions = string.findRegexp(format("^//%", snippetSeparator));
		// if findRegexp returns an array of strings (which is not what we want), then
		// repeat init. 
		/// This is a workaround for an erratic error (bug in findRegexp?)
		if ((positions ? [])[0].isKindOf(String)) { this.init; };
	}
	
	headers {
		^string.findRegexp(format("^//%[^\n]*", snippetSeparator)).flop[1];
	}

	*menuItems {
		^[
			CocoaMenuItem.addToMenu("Code", "snippet list view", [/*{*/ "}", false, false], {
				this.showCodeListWindow;
			}),
			CocoaMenuItem.addToMenu("Code", "snippet buttons", [/*{*/ "}", true, false], {
				this.showCodeButtonsWindow;
			}),
			CocoaMenuItem.addToMenu("Code", "make osc snippet commands", [/*{*/ "}", true, true], {
				this.makeCodeOSC;
			}),
			/* IZ 20110810: J for next, K for previous: compatibility with de facto standard
				for movement by keyboard in most software since before the mouse: 
				vi, gmail, google labs, ebib, many other apps and games. See HJKL keys: 
				http://en.wikipedia.org/wiki/HJKL_keys#HJKL_keys */
			CocoaMenuItem.addToMenu("Code", "next snippet", ["J", false, false], {
				this.selectNextSnippet;
			}),
			CocoaMenuItem.addToMenu("Code", "previous snippet", ["K", false, false], {
				this.selectPreviousSnippet;
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
			CocoaMenuItem.addToMenu("Code", "open proxy mixer", ["W", true, false], {
				ProxyCode(Document.current).proxyMixer;
			}),
			CocoaMenuItem.addToMenu("Code", "edit proxy", ["e", true, false], {
				ProxyCode(Document.current).openProxySourceEditor;
			}),
			CocoaMenuItem.addToMenu("Code", "load all proxies", ["l", true, false], {
				ProxyCode(Document.current).loadAll;
			}),
			CocoaMenuItem.addToMenu("Code", "eval in proxy space", ["W", false, false], {
//				Document.current.name.postln;
				ProxyCode(Document.current).evalInProxySpace;
			}),
			CocoaMenuItem.addToMenu("Code", "start doc proxy", ["<", false, false], {
				ProxyCode.new.playCurrentDocProxy;
			}),
			CocoaMenuItem.addToMenu("Code", "stop doc proxy", [">", false, false], {
				ProxyCode.new.stopCurrentDocProxy;
			}),
			CocoaMenuItem.addToMenu("Code", "vol +0.1 doc proxy", ["<", false, true], {
				ProxyCode.new.changeVol(0.1);
			}),
			CocoaMenuItem.addToMenu("Code", "vol -0.1 doc proxy", [">", false, true], {
				ProxyCode.new.changeVol(-0.1);
			}),
//			CocoaMenuItem.addToMenu("Code", "vol +0.3 doc proxy", ["<", true, true], {
//				ProxyCode.new.changeVol(0.3);
//			}),
//			CocoaMenuItem.addToMenu("Code", "vol -0.3 doc proxy", [">", true, true], {
//				ProxyCode.new.changeVol(-0.3);
//			}),
			CocoaMenuItem.addToMenu("Code", "toggle server auto-boot", ["B", true, true], {
				autoBoot = autoBoot.not;
				postf("Server auto-boot set to %\n", autoBoot);
			}),
			CocoaMenuItem.addToMenu("Code", "Insert //:--", ["p", true, true], {
				Document.current.string_("//:--\n", Document.current.selectedRangeLocation, 0);
			}),
		];
	}

	*showCodeListWindow {
		var doc;
		doc = Document.current;
		AppModel().stickyWindow(doc, \snippetList, { | window, app |
			window.bounds = Rect(Window.screenBounds.width - 300, 50, 300, 400);
			window.name = doc.name ++ " : snippets";
			window.layout = VLayout(
				app.listView(\snippets)
					.updateAction_({ | view, sender, adapter |
						view.items = adapter.adapter.items collect: { | s | 
							s[3..80].replace("\n", "-")
						}
					}).items_(Code(doc).getAllSnippetStrings)
					.view.keyDownAction_({ | view, char | 
						if (char.ascii == 13) { // when return key is pressed: evaluate snippet
							app.getAdapter(\snippets).adapter.item.interpret
						};
						if (char == $ ) { // when space key is pressed: select snippet in document
							Code(doc).selectSnippetAt(app.getAdapter(\snippets).value + 1);
						};
					}).font_(Font.default.size_(10))
			)
		});
	}

	*showCodeButtonsWindow {
		var doc, code, headers, font;
		font = Font.default.size_(10);
		doc = Document.current;
		code = Code(doc);
		headers = code.headers;
		AppModel().stickyWindow(doc, \snippetButtons, { | window, app |
			window.name = doc.name ++ " : snippet buttons";
			window.layout = VLayout(
				*(headers collect: { | h, i | 
					Button().states_([[h[3..]]])
					.action_({ Code(doc).performCodeAt(i + 1, \fork, AppClock) })
					.font_(font) })
			)	
		})

	}

	*makeCodeOSC {
		var doc, path;
		doc = Document.current;
		Library.at(doc, \osc) do: _.free;
		Library.put(doc, \osc, this.new(doc).headers collect: { | h, i |
			path = h.findRegexp("//:([A-Za-z0-9]+)")[1];
			if (path.size == 0) {
				path = "snippet" ++ i.asString
			}{
				path = path[1]
			};
			OSCFunc({ "test".postln;
				// error without defer. Why? 
				{ Code(doc).performCodeAt(i + 1, \fork, AppClock) }.defer(0.0001); 
			}, path)
		});
		"OSCFuncs generated with following paths:".postln;
		Library.at(doc, \osc).collect(_.path).asCompileString.postln;
	}

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
		postf("doc: %, snippet: %\n", doc.name, snippet);
		this.changed(\snippet, snippet);
		^(string[start..end] ?? { "{ }" }).perform(message, clock ? AppClock);
	}

	getAllSnippetStrings { | skipFirstSnippet = true |
		// Note: skipping first snippet, as this is before the first //: comment separator
		var start;
		if (skipFirstSnippet) { start = 1 } { start = 0 };
		^(start..positions.size) collect: { | i | this.getSnippetStringAt(i) }
	}

	selectSnippetAt { | index |
		var begin, end;
		#begin, end = this.getSnippetAt(index);
		doc.selectRange(begin, end - begin);
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
		^this.performCodeAt(this.findIndexOfSnippet(doc), \evalPost );
	}

	*currentDocumentOpenProxyDocs {
		^ProxyDocGroup(Document.current);
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
		length = (nextsnippetend ?? { string.size }) - nextsnippetstart;
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
