
/*
Provide utilities for: 

- Creating a ProxySpace that is only active whenever a selected Document is front
- Starting and stopping the current ProxySpace
- Switching between ProxySpaces whenever a Document with a different ProxySpace comes to the front
- Starting and stopping all current ProxySpaces

*/

DocProxy {
	classvar <proxies;		// IdentityDictionary holding the proxies that correspond to each Document
	
	*initClass { StartUp add: this }
	*doOnStartUp { 
		proxies = IdentityDictionary.new;
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
		
		this.addNotifier(Panes, \docToFront, { | doc |
			this.switchToProxy(proxies[doc]);
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

}