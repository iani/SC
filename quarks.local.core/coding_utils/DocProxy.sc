
/*
Provide utilities for: 

- Creating a ProxySpace that is only active whenever a selected Document is front
- Starting and stopping the current ProxySpace
- Switching between ProxySpaces whenever a Document with a different ProxySpace comes to the front
- Starting and stopping all current ProxySpaces

*/

DocProxy {
	classvar <proxies;		// IdentityDictionary holding the proxies that correspond to each Document
	classvar <docs;
	*initClass { StartUp add: this }
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
				proxies[doc] = proxySpace;
				if (Server.default.serverRunning.not) { Server.default.boot; };
				Server.default.waitForBoot({ 
					{ 
						this switchToProxy: proxySpace;
						proxySpace.play;
					}.defer(0.2);
				});
				this.addShortcutForDoc(doc);
			};
		}).setShortCut("p", true);
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
		ProxySpace.pop;
		proxySpace.push;
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

	*goToAdjacentProxyDoc { | increment = 1 
| 		var doc, pDocs, index;
		doc = proxies.findKeyForValue(currentEnvironment);
		if (doc.isNil) { ^this };
		pDocs = docs select: _.notNil;
		index = pDocs indexOf: doc + increment;
		index.postln;
		pDocs.wrapAt(index).front.didBecomeKey;
	}
	
}