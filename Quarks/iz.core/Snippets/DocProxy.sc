
/*
Provide utilities for: 

- Creating a ProxySpace that is only active whenever a selected Document is front
- Starting and stopping the current ProxySpace
- Switching between ProxySpaces whenever a Document with a different ProxySpace comes to the front
- Creating shortcuts for selecting ProxySpace Documents and for cycling between them
- Starting and stopping all current ProxySpaces
- Starting, ending, auto-saving at quit, File-Dialog-loading History. 

*/

DocProxy {
	classvar <proxies;		// IdentityDictionary holding the proxies that correspond to each Document
	classvar <docs;
	*initClass {
		StartUp add: this;
		ShutDown add: this;
	}

	*doOnStartUp { 
		proxies = IdentityDictionary.new;
		docs = Array.newClear(10);
			// Create and start a new ProxySpace on the current document
		CocoaMenuItem.add(["JITlib", "Make Doc ProxySpace"], {
			var doc, name, proxySpace;
			doc = Document.current;
			if (proxies[doc].notNil) {
				postf("ProxySpace already created for '%'\n", doc.name); 
			}{
				name = "".catList(doc.name.findRegexp("[^- !.]*").slice(nil, 1)).asSymbol;
				proxySpace = ProxySpace(Server.default, name, TempoClock.new);
				this.enterHistory(
					format("ProxySpace(Server.default, %, TempoClock.new).play\n;",
						name.asCompileString
					);
				);
				proxies[doc] = proxySpace;
				if (Server.default.serverRunning.not) { Server.default.boot; };
				Server.default.waitForBoot({ 
					{ 
						this switchToProxy: proxySpace;
						proxySpace.play;
					}.defer(0.2);
				});
				this.addShortcutForDoc(doc);
				if (doc.background == Color.white) {
					doc.background = Color.rand(0.85);
				};
				postf("Started ProxySpace % for document: %\n", name, doc.name);
			};
		}).setShortCut("p", false, true);
		CocoaMenuItem.add(["JITlib", "Stop current ProxySpace"], {
			var proxySpace;
			proxySpace = proxies[Document.current];
			if (proxySpace isKindOf: ProxySpace) { proxySpace.stop };
		}).setShortCut(".", false, true);
		CocoaMenuItem.add(["JITlib", "Start current ProxySpace"], {
			var proxySpace;
			proxySpace = proxies[Document.current];
			if (proxySpace isKindOf: ProxySpace) {
				proxySpace.play;
				proxySpace.envir.keys do: _.play;
			};
		}).setShortCut(",", false, true);
		CocoaMenuItem.add(["JITlib", "Stop all ProxySpaces"], {
			ProxySpace.all do: _.stop;
		}).setShortCut(">", false, true);
		CocoaMenuItem.add(["JITlib", "Start all ProxySpaces"], {
			ProxySpace.all do: this.startAllNodes(_);
		}).setShortCut("<", false, true);
		CocoaMenuItem.add(["JITlib", "Start History Recording"], {
			History.start;
		}).setShortCut("h", false, true);
		CocoaMenuItem.add(["JITlib", "End History Recording"], {
			History.end;
		}).setShortCut("H", false, true);
		CocoaMenuItem.add(["JITlib", "Play History"], {
			History.play;
		}).setShortCut("i", false, true);
		CocoaMenuItem.add(["JITlib", "Stop History Playback"], {
			History.stop;
		}).setShortCut("I", false, true);
		CocoaMenuItem.add(["JITlib", "Show History"], {
			History.document;
		}).setShortCut("h", true, true);
		CocoaMenuItem.add(["JITlib", "Load History"], {
			Dialog.getPaths({ | paths |
				if (paths.size > 0) {
					History.loadCS(paths.first);
					// TODO: check consistency and necessity of 
					// reversing order of history when recording / saving / loding etc. 
					History.current.lines = History.current.lines.reverse;
				}				
			});
		}).setShortCut("H", true, true);
		CocoaMenuItem.add(["Next ProxySpace Doc"], {
			this.goToAdjacentProxyDoc(1);
		}).setShortCut(/*[*/ "]", false, true);
		CocoaMenuItem.add(["Previous ProxySpace Doc"], {
			this.goToAdjacentProxyDoc(-1);
		}).setShortCut("[" /*]*/, false, true);
		
		this.addNotifier(Panes, \docToFront, { | doc |
			this.switchToProxy(proxies[doc]);
		});
		this.addNotifier(Panes, \docClosed, { | doc |
			docs remove: doc;
		});
	}

	*switchToProxy { | proxySpace |
		if (proxySpace.isNil) { ^this };
		this.enterHistory(
			format("ProxySpace.pop;\nProxySpace.all[%].push;", proxySpace.name.asCompileString)
		);
		ProxySpace.pop;
		proxySpace.push;
	}

	*enterHistory { | string |
		if (History.started.not) {
			History.start;
		};
		History.enter(string) 
	}

	*startAllNodes { | proxySpace |
		proxySpace.play; 
		proxySpace.envir.keys do: _.play;
	}
	
	*addShortcutForDoc { | doc |
		var index;
		index = proxies.size %10;
		docs[index] = doc;
		CocoaMenuItem.add([format("Proxy Doc %", index)], {
//			index.postln;
			docs[index].front.didBecomeKey;
		}).setShortCut(index.asString, false, true);
	}

	*goToAdjacentProxyDoc { | increment = 1 |
		var doc, pDocs, index;
		doc = proxies.findKeyForValue(currentEnvironment);
		if (doc.isNil) { ^this };
		pDocs = docs select: _.notNil;
		index = pDocs indexOf: doc + increment;
		pDocs.wrapAt(index).front.didBecomeKey;
	}

	*doOnShutDown {
		History.end;	
	}	
}