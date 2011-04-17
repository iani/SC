
DocListWindow {
	classvar >default;
	classvar <>docListWidth = 150;
	classvar <>listenerY = 280, <>listenerWidth = 500;
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
			Document.allDocuments do: this.addDoc(_);
			0.5.wait;
			if ((lastCurrentDocName = Archive.global.at(\currentDocName)).notNil) {
				lastCurrentDoc = Document.allDocuments detect: { | d | d.name == lastCurrentDocName };
				if (lastCurrentDoc.notNil) { lastCurrentDoc.front; lastCurrentDoc.toFrontAction.value };
			};
		}.fork(AppClock); 
		// at startup we also need to refresh the doc list to get the right name for the post window:
		{ this.updateDocListView }.defer(1);	// the name of the post list window is set with some delay at startup (!?);
		this.startAutosaveRoutine;
		CmdPeriod.add(this);
		NotificationCenter.notify(this, \started);
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
//			doc.addNotifications;
			doc.toFrontAction = {
				NotificationCenter.notify(Document, \toFront, doc); 
			};
			doc.endFrontAction = { 
				NotificationCenter.notify(Document, \endFront, doc); };
			doc.onClose = { NotificationCenter.notify(Document, \closed, doc); };
			NotificationCenter.notify(this, \docAdded, doc);
		}.defer(0.1);
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
		{ Archive.global.put(\currentDocName, Document.current.name ? "-"); }.defer(3); 
	}
	
	activateDocActions { | doc |
		var selectionStart;
/*		doc.mouseUpAction_({arg doc;
			var line;
			line = doc.currentLine;
			if (line[0..2] == "//:") {
				this.makeCodeList(doc);	
				this.selectAndPerformCodeAt(codeKeys indexOf: line[3]);
			};
		});
*/
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
		prPoslist = string.findRegexp("^//:").slice(nil, 0);
		prPoslist = prPoslist.asArray;
		if (prPoslist.size > 0) { 
			prCodeParts = prPoslist collect: { | pos, i |
				string[pos..(prPoslist[i + 1] ?? { string.size }) - 1];
			};
			prItems = prCodeParts collect: { | s |
				if (s[3] == $!) { s.interpret };
				s[3..50];
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
			})
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
}