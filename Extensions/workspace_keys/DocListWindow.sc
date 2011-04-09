/*
DocListWindow.stop;
DocListWindow.toggle;
*/
DocListWindow {
	classvar >default;
	classvar <>docListWidth = 150;
	classvar <>listenerY = 280, <>listenerWidth = 500;
	var <docBrowser, <docBrowserView, <docListView, <codeListView;
	var <docBounds, <docBrowserBounds, <listenerBounds;
	var <allDocs, <selectedDoc;
	var <codeStrings, <codeKeys;
	var <remakeCodeListMenuItem;
	// autosave all docs to archive every 60 seconds, per default
	var <>autosave = true, <>autosave_rate = 60, autosave_routine; 

	*initClass {
		this.makeUserMenuItems;	
	}

	*makeUserMenuItems {
		CocoaMenuItem.addToMenu("User Menu", "Toggle Doc List", ["0", false, false], { 
			this.toggle;
		});

	}
	*saveSession { 
		DocSession.newWithAllDocs("recent").save;
	}

	*default {
		if (default.isNil) { default = this.new };
		^default;	
	}
	
	*new { ^super.new.init }
	
	init {
		allDocs = SortedList(8, { | a, b | a.name < b.name });
	}

	*toggle { this.default.toggle }
	*start { this.default.start }
	*stop { this.default.stop }

	toggle {
		if (docBrowser.isNil) { this.start } { this.stop }
	}

	start {
		this.makeGui;
		{
		NotificationCenter.register(Document, \opened, this, { | doc | 
			{ this.addDoc(doc); }.defer(0.01); 	// must wait for Doc to get its name
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
		Document.allDocuments do: _.addNotifications;
		}.defer(0.1); // defer needed when DocListWindow is started at SC startup
		// at startup we also need to refresh the doc list to get the right name for the post window:
		{ this.updateDocListView }.defer(1);	// the name of the post list window is set with some delay at startup (!?);
		this.startAutosaveRoutine;
		CmdPeriod.add(this);
	}

	startAutosaveRoutine {
		{
			loop {
				autosave_rate.wait;
				if (autosave) { this.class.saveSession; };
			}	
		}.fork(AppClock);
	}
	
	cmdPeriod { this.startAutosaveRoutine; }

	addDoc { | doc |
		allDocs = allDocs add: doc;
		this.updateDocListView;
		this.placeDoc(doc);
		this.selectDoc(doc);
	}

	updateDocListView {
		// check for docBrowser not nil because on start we perform re-update delayed by 1 second. 
		// the user may have closed the docBrowser during the re-update interval.
		if (docBrowser.notNil) { docListView.items = allDocs collect: _.name; };
	}
	
	placeDoc { | doc |
		if (docBrowser.bounds != docBrowserBounds) {
			docBrowserBounds = docBrowser.bounds;
			docListView.bounds = this.docListBounds;
			codeListView.bounds = this.codeListBounds;
			this.updateDocBounds;
		};
		if (doc.isListener) {
			if (listenerBounds != doc.bounds) { doc.bounds = listenerBounds };
		}{
			if (doc.bounds != docBounds) { doc.bounds = docBounds };
		};
	}
	
	removeDoc { | doc |
		var newFront;
		allDocs.remove(doc);
		this.updateDocListView;
		{ 	// defer needed for closed document to register that it is no longer front! 
			this.selectDoc(Document.allDocuments detect: { | d | d.isFront; });
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
//		{ 	// defer needed for docListView value to update properly when creating new Documents
			// now defer centrally added once to NotificationCenter.register(... \opened ...)
		docListView.value = index;
		codeListView.enabled = false;
//		}.defer(0.01);
		
	}
	
	unselectDoc { | doc |
	}

	makeGui {
		this.makeUserMenuItems;
		docBrowser = Window("docs", 
			Rect(Window.screenBounds.width - docListWidth, 0, docListWidth, Window.screenBounds.height - 50));
		docBrowser.onClose = {
			this.stopped;
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
		var docText, poslist, snippet, items;
		if (doc.isListener) { ^this }; // do not make code list for post window;
		docText = doc.string;
		poslist = docText.findRegexp("^//:").slice(nil, 0);
		poslist = poslist.asArray;
		if (poslist.size > 0) { 
			codeStrings = poslist collect: { | pos, i |
				docText[pos..(poslist[i + 1] ?? { docText.size }) - 1];
			};
			items = codeStrings collect: { | s |
				if (s[3] == $!) { s.interpret };
				s[3..50];
			};
			codeKeys = items collect: _.first;
		}{
			codeStrings = [docText];
			items = ["0 " ++ doc.name];
			codeKeys = [$0];
			
		};
		codeListView.items = items;
	}

	selectAndPerformCodeAt { | index |
		if (index.isNil) { ^this };
		codeListView.value = index;
		this.performCodeAt(index);	
	}

	performCodeAt { | index |
		codeStrings[index].interpret;
	}

	makeUserMenuItems {
		remakeCodeListMenuItem = CocoaMenuItem.addToMenu("User Menu", "Activate Code List", ["1", false, false], {
			this.makeCodeList(Document.current);
			docBrowser.front;
			codeListView.enabled = true;
			codeListView.focus;
		});
	}

	removeUserMenuItems {
		if (remakeCodeListMenuItem.notNil) { remakeCodeListMenuItem.remove; };
	}

	docListBounds {
		^Rect(2, 2, docBrowserBounds.width - 4, docBrowserBounds.height / 2 - 4);
	}

	codeListBounds {
		^Rect(2, docBrowserBounds.height / 2 - 2, docBrowserBounds.width - 4, docBrowserBounds.height / 2 - 4);
	}

	stop { docBrowser.close; autosave_routine.stop }

	stopped {
		NotificationCenter.unregister(Document, \opened, this);
		NotificationCenter.unregister(Document, \closed, this);		NotificationCenter.unregister(Document, \toFront, this);		NotificationCenter.unregister(Document, \endFront, this);
	}
	
	close { // ????????????????????????????????????? remove this method?
		if (docBrowser.notNil) { docBrowser.close; };
		this.removeUserMenuItems;
	}

	updateDocBounds {
		docBounds = Rect(listenerWidth, 
			docBrowserBounds.top,
			docBrowserBounds.left - listenerWidth,
			docBrowserBounds.height + 22
		);		
	}
}