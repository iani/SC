/* This is DocTop by Ronald Kuivila, 
modified by IZ for use with LiltDoc
 */

// Document.current is not correctly updated on cmd-j
DocTop2 {
	classvar <browser, <widthViews, <docListView, <serverView, <currentServer;
	classvar <snippetView;
	classvar <browserBounds, <leftBounds, <rightBounds, <postBounds;
	classvar <docs;
	classvar <docStack;			// this should really go in SwingDocument
	classvar <task, <>dt;
	classvar <>snippetFileType = "scd";
	classvar <docText, <snippets, <snippetKeys, <documentSelectors;

	*start {	 | restore = false |
		var rects, t, h, offset;
		CocoaMenuItem.addToMenu("User Menu", "activate snippets", ["1", false, false], {
			this.loadSnippets(Document.current);
			{ if (snippetView.notNil) { browser.front; snippetView.focus }; }.defer(0.2);
		});
		CocoaMenuItem.addToMenu("User Menu", "choose document by key", ["2", false, false], {
			if (snippetView.notNil) {
				browser.front;
				docListView.focus;
				this.makeDocumentSelectors;
			}
		});

		if (task.notNil) { this.stop };
		Task({
			this.makeBrowser;
			// either set up positions of documents fron an archive
			if (restore) { 
				rects = Archive.global.at(\documentBrowserRects);
				#browserBounds, leftBounds, rightBounds, postBounds = rects;
				browser.bounds = browserBounds;
				widthViews[0].value = leftBounds.width;
				widthViews[1].value = rightBounds.width;
			};
				
			// or algorithmically
			if (rects.isNil) {
				0.1.wait;			// wait for OS to resize window
		 		this.determineBounds;
		 	};
			Document.listener.bounds_(postBounds ? rightBounds);
			docs = docStack = [Document.listener];
			this.pushDocStack(Document.current);

			this.documentPoller;
		}).play(AppClock);
	}

	*documentPoller { 	| argDt = 0.033 |
		var newDocs, bounds, widths, oldL, oldR, doc, index;
		dt = argDt;
		task = Task({
			loop { 				
				// check for added or deleted documents or documents with new names
				if ( (docs != Document.allDocuments) || 
					(docListView.items != Document.allDocuments.collect(_.title)) ) { 
					this.positionDocs;
				};	
				// keep list of documents up to date with current Document
				index = docs.indexOf(Document.current);
				if (docListView.value != index) {
					this.loadSnippets(Document.current);
				};
				if (index.notNil) { docListView.value_(index) } { browser.view.focus };
				browser.refresh;
	
				// check for change in document positioning
				if ( (bounds != browser.bounds) || (widths != widthViews.collect(_.value)) ) {
					bounds = browser.bounds;
					widths = widthViews.collect(_.value);
					oldL = leftBounds;
					oldR = rightBounds;
					this.determineBounds;
					docs.do {| d |
						if (d.bounds == oldL) { 
							d.bounds_(leftBounds)
						} {
							if (d.bounds == oldR) {
								d.bounds_(rightBounds);
							}
						}
					}					
				};
				dt.wait 
			} 
		}); 
		CmdPeriod.add(this);
		task.play(AppClock);		
	}

	*cmdPeriod { task.stop.play }	// keep polling going

	*stop {
		CmdPeriod.remove(this);
		task.stop; 
		task = nil;
		if (browser.notNil && {browser.isClosed.not }) { browser.close };
		browser = nil;
		if (CocoaDocument.notNil) {			// debuggin convenience
			GUI.cocoa; Document.implementationClass = CocoaDocument; 
		}
	}
	
	*swing {
		SwingOSC.default.waitForBoot {
			Task {
				GUI.swing;
				SwingDocument.startup;
				0.5.wait;
				DocTop.start;
			}.play(AppClock);
		}
	}
	
	*positionDocs  { | current |
		// collect and sort all documents
		var i, doc, oldDocs, newDocs;
		oldDocs = docs;
		docs = Document.allDocuments.select { | d | d.isClosed.not };
		docs = docs.sort { | a , b | a.title < b.title};
		Document.allDocuments = docs.copy;
		docListView.items = docs.collect(_.title);
		
		docStack = docStack.reject({ | d | docs.indexOf(d).isNil});
		newDocs = docs.difference(oldDocs);
		if (newDocs.size !=0) { 
			this.pushDocStack(newDocs.last);
			newDocs.do { | d | d.bounds_(leftBounds) }
		};
		doc = current ?? docStack.last;
		i = docs.indexOf(doc);
		docListView.value_(i);
		this.loadSnippets(doc);
		 
	}
	
	*pushDocStack { | doc |
		docStack = docStack.add(doc);
		if (docStack.size > 10) { docStack =  docStack[1..10] };
//		NotificationCenter.notify(this, \docChanged, [doc, docListView]);
	}

	*makeBrowser {
		var l, t, w, h;
		browser = Window("docs",Window.screenBounds.width_(150)).front; 
		browser.onClose_({ this.stop });
		if (	thisProcess.platform.respondsTo(\ideName).not || { thisProcess.platform.ideName == "none"}) { 
			browser.view.keyDownAction_({| view, key, mod, unicode |
				if ( key == $o && mod.bitTest(20)) { 
					SwingFileBrowser.open((docs[docListView.value].path ? "/").dirname) };
			});
		};		
		StaticText(browser, Rect(7, 0, 33, 20)).string_("lwidth");
		StaticText(browser, Rect(75, 0, 33, 20)).string_("rwidth");
		widthViews = [
			NumberBox(browser, Rect(40, 0, 30, 20)).value_(800),
			NumberBox(browser, Rect(108, 0, 30, 20)).value_(800),
			NumberBox(browser, Rect(176, 0, 30, 20)).value_(800)
		];
		widthViews.do { | v |
			v.background_(Color.gray(0.8)).typingColor_(Color.red(0.5))
		};

		docListView = ListView(browser, Rect(2, 25, browser.bounds.width - 6, Window.screenBounds.height - 45 - 480) )
			.items_([])
			.background_(Color.clear)
			.hiliteColor_(Color.gray(alpha:0.3))
			.resize_(2);

		docListView.action_({ | view |
			var doc; 
			if ( (doc = docs[view.value]).notNil) { 
				Document.current = doc;
				doc.unfocusedFront;	docListView.focus;
				this.pushDocStack(doc);
			};
			this.loadSnippets(doc);
		});

		docListView.keyDownAction =  { arg view, char, modifiers, unicode, keycode;
			var doc;
			if (char == $-) { if ( (doc = docs[view.value]) != Document.listener) { doc.close } };
			if (unicode == 16rF700 && (view.class == SCListView), { view.valueAction = view.value - 1;  });
			if (unicode == 16rF701 && (view.class == SCListView), { view.valueAction = view.value + 1;  });
			if (unicode == 16rF703) { 
				if ((modifiers & 0x20) != 0) {
					docs[docListView.value].bounds_(rightBounds).front;
				}{
					// Swing/Cocoa conflict here...Swing does not yet have a real unfocusedFront
					// cocoa does not seem to get the docListView back in focus after the other window gets focus
					docs[docListView.value].bounds_(rightBounds).unfocusedFront;
					defer({docListView.focus}, 0.01);
				};
			};
			
			if (unicode == 16rF702) { 
				doc = docs[docListView.value];
				if (doc.isKindOf(SwingDocument) ) {
					if ((modifiers & 0x20) != 0) {
						doc.bounds_(leftBounds);
						doc.front; 
					}{
						doc.bounds_(leftBounds);
						doc.front; docListView.focus;
					};
				
				} {
					if ((modifiers & 0x20) != 0) {
						docs[docListView.value].bounds_(leftBounds).front;
					}{
						docs[docListView.value].bounds_(leftBounds).unfocusedFront;
					};
				}
			};
			
			
		};

		h = docListView.bounds.top + docListView.bounds.height + 2;
		snippetView = ListView(browser, Rect(2, h, 
			browser.bounds.width - 6, 
			browser.view.bounds.height - h - 55)
		)
			.items_([])
			.background_(Color.blue(alpha: 0.1))
			.hiliteColor_(Color.gray(alpha:0.3))
			.resize_(5)
			.onClose_({ snippetView = nil });

		this.makeServerControls(browser);
	}		
	
	*determineBounds {
		var screenWidth, spacing, lbounds;
		var l, t, w, h; 
		spacing = 10;
		browserBounds = browser.bounds;
		screenWidth = Window.screenBounds.width - spacing;
		l = browserBounds.left + browserBounds.width + spacing;
		t = browserBounds.top; 		
		h = browserBounds.height + 20;	// include title bar in bounds
		
		// constrain document bounds to screen but keep width >= 100
		
		w = widthViews[0].value;
		w = l + w min: screenWidth - l max: 100;
		leftBounds = Rect(l,t,w,h);
		l = l + w + spacing min: (screenWidth - 100);
		w = widthViews[1].value;
		w = l + w + spacing min: screenWidth - l max: 100;
		rightBounds = Rect(l,t,w,h);
		if (Document.listener.isNil) { lbounds = rightBounds} {lbounds = Document.listener.bounds};
		Archive.global.put(\documentBrowserRects, [browser.bounds, leftBounds, rightBounds,lbounds]);
	}
			
	*reset {
		Archive.global[\documentBrowserRects] = nil;
		postBounds = rightBounds;
		this.determineBounds
	}

// server control add on
	*update { | changer, what | 
		switch(what)
			{\default} { 
				this.setServer(Server.default)
			 } 
			{\serverRunning} 	{ 
				this.setServerState(0.1); 
			}
	}

	*setServer { | server |
		var name;
		if (currentServer.notNil){ currentServer.removeDependant(this) };
		currentServer = server;
		server.addDependant(this);
		
		name = server.name;
		serverView
			.states_([ 
				[name, Color.white, Color.gray(0.5, 0.5)], 
				[name, Color.white, Color(1,0.26,0, 0.9), ],
				[name, Color.white, Color.green(0.75, 0.75)]
			] );
		this.setServerState;
	}

	*setServerState { | dt = 0 | 
		var val = 0;
		defer( {
			if (currentServer.serverBooting) { val = 2};
			if (currentServer.serverRunning) { val = 2};
			serverView.value_(val);
		}, dt );
	}
	
	*makeServerControls { |browser|
		var t = Window.screenBounds.height - 54;
		var h = 20;
		currentServer = Server.default;
		currentServer.addDependant(this);
			
		serverView = Button(browser, Rect( 5,  t, 75, 20) )
			.resize_(7)
			.action_({ | v | 
				switch (v.value ) 
					{ 0 } { currentServer.quit}
					{ 1 } { currentServer.boot }
					{ 2 } {  }
			}); 
		this.setServer(Server.default);
		Button(browser, Rect(85, t, 15, 20) )
			.resize_(7)
			.states_([ 
				["M", Color.white, Color.gray(0.5, 0.5)],
				["M", Color.white, Color.red(0.75, 0.75)]
			])
			.action_({ | v | if (v.value ==0) { currentServer.volume.unmute } { currentServer.volume.mute} });
			
		Button(browser, Rect(105, t, 15, 20) )
			.resize_(7)
			.states_([ 
				["R", Color.white, Color.gray(0.5, 0.5)],
				["R", Color.white, Color.red(0.75, 0.75)]
			])
			.action_({ | v | 
				if (v.value == 1) { currentServer.record } { currentServer.stopRecording };
			});
	}
	
	*loadSnippets { | document |
		var poslist, snippet;
		if (snippetView.isNil) { ^this };
		if (this canMakeSnippets: document) {
			docText = document.string;
			poslist = docText.findRegexp("^//:").slice(nil, 0) ?? { [0] };
			snippets = poslist collect: { | pos, i |
				docText[pos..(poslist[i + 1] ?? { docText.size }) - 1];
			};
			snippetView.items = snippets collect: { | s |
				if (s[3] == $!) { s.interpret };
				s[3..50];
			};
			snippetKeys = snippetView.items collect: _.first;
			snippetView.keyDownAction = { | view, char, mod, key |
				snippet = snippetKeys indexOf: char;
				if (snippet.notNil) {
					snippets[snippet].interpret;
				};
			};	
		}{
			snippetView.items = [];
		};
		snippetView.focus;
	}
	
	*canMakeSnippets { | document |
		^document.name.splitext.last == snippetFileType;
	}

	*makeDocumentSelectors {
		var keys;
		documentSelectors = [];
		Document.openDocuments do: { | d |
			if (this canMakeSnippets: d) {
				keys = d.string.findRegexp("^//Key:\(.*\)$");
				keys.postln;
//				documentSelectors add: d;
			}
		}
	}
}

