
DocListWindow {
	classvar >default;
	classvar <>docListWidth = 150;
	classvar <functionKey = 8388864;
	classvar <>listenerY = 500, <>listenerWidth = 500;
	classvar <>tryoutBounds;
	var <docBrowser, <docBrowserView, <docListView, <codeListView;
	var <docBounds, <docBrowserBounds, <listenerBounds;
	var <allDocs, <selectedDoc;
	var <codeStrings, <codeKeys, <codePositions, <docKeys;
	var <menuItems;
	// autosave all docs to archive every 300 seconds (5 minutes), per default
	var <>autosave = true, <>autosave_rate = 300, autosave_routine; 

	*initClass {
		Class.initClassTree(Document);
		Document.initAction = { | me | 
			NotificationCenter.notify(Document, \opened, me);
		};
		tryoutBounds = Rect(0, 260, 494, 212);
		this.makeUserMenuItems;	
	}

	*makeUserMenuItems {
		CocoaMenuItem.addToMenu("User Menu", "Toggle Doc List", ["0", false, false], { 
			this.toggle;
		});
	}

	*default {
		if (default.isNil) { default = this.new };
		^default;	
	}
	
	*new { ^super.new.init }

	init { allDocs = SortedList(8, { | a, b | a.name < b.name; }); }

	*toggle { this.default.toggle }
	*start { this.default.start }
	*stop { this.default.stop }


	stop {
		docBrowser.close;
		autosave_routine.stop;
		this.removeUserMenuItems;
		this.init;
		NotificationCenter.notify(this, \stopped);
	}

	toggle {
		if (docBrowser.isNil) { this.start } { this.stop }
	}

	start {
		if (Document.allDocuments.size > 1) {
			DocProxy.loadDefaultBounds;		
		};
		this.makeGui;
		{
			var lastCurrentDocName, lastCurrentDoc;
			0.1.wait; // defer needed when DocListWindow is started at SC startup
			NotificationCenter.register(Document, \opened, this, { | doc |
				this.addDoc(doc);
			});
			NotificationCenter.register(Document, \closed, this, { | doc |
				this.removeDoc(doc);
			});
			NotificationCenter.register(Document, \toFront, this, { | doc | 
				this.selectDoc(doc);
			});
			NotificationCenter.register(Document, \endFront, this, { | doc | 
				this.unselectDoc(doc);
			});
			this.makeTryoutWindow;
			Document.allDocuments do: this.addDoc(_);
			0.5.wait;
			if ((lastCurrentDocName = Archive.global.at(\currentDocName)).notNil) {
				lastCurrentDoc = Document.allDocuments detect: { | d | d.name == lastCurrentDocName };
				if (lastCurrentDoc.notNil) { lastCurrentDoc.front; lastCurrentDoc.toFrontAction.value };
			};
			this.updateDocListView; // update name of post list window here, catching delay of startup
		}.fork(AppClock); 
		this.startAutosaveRoutine;
		CmdPeriod.add(this);
		NotificationCenter.notify(this, \started);
	}

	makeTryoutWindow {
		var tryout, path;
		{
			if ((tryout = Document.allDocuments.detect({ | d | d.name == "tryout.sc" })).isNil) {
				path = Platform.userAppSupportDir ++ "/tryout.sc";
				if (path.pathMatch.size == 0) {
					tryout = Document("tryout.sc").path_(path);
				}{
					tryout = Document.open(path);
				};
			};
			1.wait;
			if (tryout.bounds.left != 0) { tryout.bounds = tryoutBounds };
		}.fork(AppClock);
	}

	startAutosaveRoutine {
		{
			loop {
				autosave_rate.wait;
				if (autosave) { this.saveSnapshot; };
			}	
		}.fork(AppClock);
	}
	
	cmdPeriod { this.startAutosaveRoutine; }

	addDoc { | doc |
		{ // must wait for Doc to get its name
			allDocs = allDocs add: doc; 
			this.setDocBounds(doc, DocProxy.boundsFor(doc));
			this.updateDocListView;
			this.selectDoc(doc);
/*			doc.keyDownAction = { | doc, char, mod, unicode, key |
				[doc, char, mod, unicode, key].postln;
				if (mod == functionKey) {
					switch (char, 
						$x, { this.makeCodePalette(doc) },
						$c, { this.zoomTryOutDoc }
					);
				};  

			};
*/
			doc.toFrontAction = {
				NotificationCenter.notify(Document, \toFront, doc); 
			};
			doc.endFrontAction = { 
				NotificationCenter.notify(Document, \endFront, doc); };
			doc.onClose = { NotificationCenter.notify(Document, \closed, doc); };
			NotificationCenter.notify(this, \docAdded, doc);
		}.defer(0.1);
	}

	zoomTryOutDoc {
		var tryout;
		tryout = Document.allDocuments detect: { | d | d.name == "tryout.sc" };
		if (tryout.notNil) { 
			if (tryout.bounds == tryoutBounds) {
				tryout.bounds = docBounds;			
			}{
				tryout.bounds = tryoutBounds;
			};
			tryout.front;
			this.selectDoc(tryout); // make sure code palette opens on this
		};
	}
	
	makeCodePalette { | doc |
		var cpalette, window;
		var items, codeStrings, codeKeys, codePositions;
		if (doc.isNil) { ^this };
		window = Window(doc.name, Rect(0, 0, 250, 400));
		cpalette = EZListView(window, window.view.bounds.insetBy(3, 3));
		cpalette.widget.keyDownAction = MultiKeySearch(keystrokeWaitInterval: 0.1);
		cpalette.widget.parent.resize = 5;
		cpalette.widget.resize = 5;
		cpalette.widget.focusColor = Color.green;	
		window.toFrontAction = {
			#items, codeStrings, codeKeys, codePositions = this.parseCode(doc);
			cpalette.items = items collect: { | s, i | s->{ codeStrings[i].interpret; } };
		};
		window.front;
		cpalette.widget.focus;
	}

	updateDocListView {
		var items;
		items = allDocs collect: _.name;
		// check for docBrowser not nil because on start we perform re-update delayed by 1 second. 
		// the user may have closed the docBrowser during the re-update interval.
		if (docBrowser.notNil) {
			docListView.items = items;
			NotificationCenter.notify(this, \items, items);
		};
	}
	
	setDocBounds { | doc, bounds |
		if (bounds.notNil) {
			DocProxy.removeDocBounds(doc);
			^doc.bounds = bounds;
		};
		if (docBrowser.bounds != docBrowserBounds) {
			docBrowserBounds = docBrowser.bounds;
			docListView.bounds = this.docListBounds;
			codeListView.bounds = this.codeListBounds;
			this.updateDocBounds;
		};
		if (doc.isListener) {
			doc.bounds = listenerBounds;
		}{
			doc.bounds = docBounds;
		};
	}

	updateDocBounds {
		docBounds = Rect(listenerWidth, 
			docBrowserBounds.top,
			docBrowserBounds.left - listenerWidth,
			docBrowserBounds.height + 22
		);		
	}
	
	removeDoc { | doc |
		var newFront;
		allDocs remove: doc;
		this.updateDocListView;
		NotificationCenter.notify(this, \docRemoved, doc);
		{ 	// defer needed for closed document to register that it is no longer front!
			newFront = Document.allDocuments detect: { | d | d.isFront; };
			this.selectDoc(doc);
		}.defer(0.1);
	}
	
	selectDoc { | doc |
		var index;
		index = allDocs.indexOf(doc);
		if (index.isNil) { ^this };
		selectedDoc = doc;
		Document.current = selectedDoc;
		selectedDoc.front;
		this.makeCodeList(doc);	
		docListView.value = index;
		codeListView.enabled = false;
		this.activateDocActions(doc);
		NotificationCenter.notify(this, \index, index);
		// defer prevents all documents saving themselves here when they close at shutdown:
		{ Archive.global.put(\currentDocName, Document.current.name ? "-"); }.defer(3); // defer, yes!
	}
	
	activateDocActions { | doc |
		var selectionStart;
		doc.keyDownAction = { | me, char, mod, ascii, key |
			var selectionStart;
			if (ascii == 14) { // control-n
				this.makeCodeList(doc);	
				selectionStart = doc.selectionStart;
				this.selectAndPerformCodeAt(codePositions.indexOf(codePositions.detect({ | n | selectionStart < n })) - 1);
			};
		};
	}
	
	unselectDoc { | doc |
		this.deactivateDocActions(doc);
	}

	deactivateDocActions { | doc |
		doc.mouseUpAction = nil;
		doc.keyDownAction = nil;
	}

	makeGui {
		this.makeUserMenuItems;
		docBrowser = Window("docs", 
			Rect(Window.screenBounds.width - docListWidth, 0, docListWidth, Window.screenBounds.height - 50));
		docBrowser.onClose = {
			[\opened, \closed, \toFront, \endFront] do: NotificationCenter.unregister(Document, _, this);
			docBrowser = nil;
		};
		docBrowserView = docBrowser.view;
		docBrowserBounds = docBrowser.bounds;
		this.updateDocBounds;
		docListView = ListView(docBrowser, this.docListBounds);
		docListView.keyDownAction = { | me, char, mod, ascii ... rest |
			if (ascii == 127) {
				if (allDocs[me.value].notNil) { allDocs[me.value].close };
			}{
				me.defaultKeyDownAction(char, mod, ascii, *rest);
			}
		};
		docListView.action = { | me |
				this.selectDoc(allDocs[me.value]) 
		};
		codeListView = ListView(docBrowser, this.codeListBounds);
		codeListView.keyDownAction = { | me, char |
			this.selectAndPerformCodeAt(codeKeys indexOf: char);
		};
		codeListView.focusColor = Color.red;
		codeListView.action = {	 | me | this.performCodeAt(me.value); };
		
		docBrowserBounds = docBrowser.bounds;
		listenerBounds = Rect(0, listenerY, listenerWidth, Window.screenBounds.height - listenerY);
		docBrowser.front;
	}

	makeCodeList { | doc |
		var items;
		if (doc.isListener) { ^this }; // do not make code list for post window;
		#items, codeStrings, codeKeys, codePositions = this.parseCode(doc);
		codeListView.items = items;
	}
	
	parseCode { | doc |
		var prPoslist, prCodeParts, prCodeKeys, prItems, string;
		string = doc.string;
		// note: [^ ] means: ignore whitespace after the ":"
		prPoslist = string.findRegexp("^//:[^ ]").slice(nil, 0);
		prPoslist = prPoslist.asArray;
		if (prPoslist.size > 0) { 
			prCodeParts = prPoslist collect: { | pos, i |
				string[pos..(prPoslist[i + 1] ?? { string.size }) - 1];
			};
			prItems = prCodeParts collect: { | s |
				if (s[3] == $!) { s.interpret };
				s[3..50];   //.tr($\n, $ );
			};
			prCodeKeys = prItems collect: _.first;
		}{
			prCodeParts = [string];
			prItems = ["0 " ++ doc.name];
			prCodeKeys = [$0];
			prPoslist = [0];
		};
		^[prItems, prCodeParts, prCodeKeys, prPoslist add: (string.size + 1)];
	}

	selectAndPerformCodeAt { | index |
		if (index.isNil) { ^this };
		codeListView.value = index;
		this.performCodeAt(index);	
	}

	performCodeAt { | index |
		if (index.isNil) { ^this };
		codeStrings[index].interpret;
	}

	makeUserMenuItems {
		menuItems = [
			CocoaMenuItem.addToMenu("User Menu", "Open Session ...", ["o", true, false], {
				DocSession.loadAndOpenDialog(fromArchive: false);
			}),
			CocoaMenuItem.addToMenu("User Menu", "Open Session from Archive ...", ["O", true, false], {
				DocSession.loadAndOpenDialog(fromArchive: true);
			}),
			CocoaMenuItem.addToMenu("User Menu", "Open Session snapshot", ["o", true, true], {
				"DocListWindow opening recent session".postln;
				DocSession.load(\recent).openAllDocs(fromArchive: false);
			}),
			CocoaMenuItem.addToMenu("User Menu", "Save Session ...", ["s", true, false], {
				DocSession.saveDialog(this.docProxies);
			}),
			CocoaMenuItem.addToMenu("User Menu", "Save to Session snapshot", ["s", true, true], {
				this.saveSnapshot;
			}),
			CocoaMenuItem.addToMenu("User Menu", "Activate Code List", ["1", false, false], {
				this.makeCodeList(Document.current);
				docBrowser.front;
				codeListView.enabled = true;
				codeListView.focus;
			}),
			CocoaMenuItem.addToMenu("User Menu", "Make Performance Window", ["2", false, false], {
				this.makePerformanceWindow;
			}),
			CocoaMenuItem.addToMenu("User Menu", "Open Related .scd Window", ["D", true, false], {
				this.openScdWindow;
			}),
			CocoaMenuItem.addToMenu("User Menu", "Open Related .html Window", ["D", true, true], {
				this.openHtmlWindow;
			}),
			CocoaMenuItem.addToMenu("User Menu", "Browse my Classes", ["b", true, true], {
				this.browseMyClasses;
			}),
			CocoaMenuItem.addToMenu("User Menu", "Zoom tryout doc", ["z", false, true], {
				this.zoomTryOutDoc;
			}),
			CocoaMenuItem.addToMenu("User Menu", "make code palette", ["x", false, true], {
				this.makeCodePalette(selectedDoc);
			}),
		]
	}

	saveSnapshot {
		postf("Saving snapshot at: % ... ", Date.getDate.stamp);
		DocSession(\recent, this.docProxies).save;
		" ... done".postln;
	}

	docProxies {
		^allDocs collect: DocProxy(_);
	}

	removeUserMenuItems {
		if (menuItems.notNil) { menuItems do: _.remove; };
	}

	docListBounds {
		^Rect(2, 2, docBrowserBounds.width - 4, docBrowserBounds.height / 2 - 4);
	}

	codeListBounds {
		^Rect(2, docBrowserBounds.height / 2 - 2, docBrowserBounds.width - 4, docBrowserBounds.height / 2 - 4);
	}

	makePerformanceWindow { ^PerformanceWindow.makeGui(this); }
	
	openScdWindow {
		this.openRelatedDocWithNewExtension(selectedDoc, "sc", "scd");
	}

	openHtmlWindow {
		this.openRelatedDocWithNewExtension(selectedDoc, "sc", "html");
	}
	
	openRelatedDocWithNewExtension { | doc, oldExtension, newExtension |
		var pathname, extension;
		pathname = PathName(selectedDoc.path ? "/nothing");
		extension = pathname.extension;
		// dirty trick: also work the reverse way for "scd" or "html" files: 
		if (extension == "scd" or: { extension == "html" }) {
			oldExtension = extension;
			newExtension = "sc";	
		}; // end dirty trick
		if (pathname.extension != oldExtension) {
			^postf("Cancelled: this document's extension is not %\n", oldExtension);
		};
		pathname = format("%.%", pathname.withoutExtension, newExtension);
		if (pathname.pathMatch.size == 1) {
			Document.open(pathname);
		}{
			Document.new(pathname.basename).path = pathname;
		}		
	}
	
	browseMyClasses { 
		var cbrowser;
		cbrowser = EZListView(bounds: Rect(0, 0, 250, 400));
		cbrowser.items = Class.allClasses.select({ | c |
			"SuperCollider/Extensions/".matchRegexp(c.filenameSymbol.asString)
			and: { "Meta*".matchRegexp(c.name.asString).not }
		}).collect({ | c | c.name.asSymbol->{ c.openCodeFile; } });
		cbrowser.widget.parent.resize = 5;
		cbrowser.widget.resize = 5;		
	}
	
}
