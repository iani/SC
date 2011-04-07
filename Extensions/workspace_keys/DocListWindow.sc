/*
DocPoller.stop;
*/
DocListWindow {
	classvar <>docListWidth = 150;
	classvar <>listenerY = 280, <>listenerWidth = 500;
	var <docBrowser, <docBrowserView, <docListView, <codeListView;
	var <docBounds, <docBrowserBounds, <listenerBounds;
	var <allDocs, <selectedDoc;
	var <codeStrings, <codeKeys;
	var <remakeCodeListMenuItem;


	*new { ^super.new.init; }
	init { this.makeGui }
	
	makeGui {
		var archivedDoc;
		archivedDoc = Archive.global.at(\currentDoc);
//		archivedDoc.postln;
		archivedDoc = Document.allDocuments.detect { | d | d.name == archivedDoc };
		if (archivedDoc.notNil) { Document.current = archivedDoc };
		this.makeUserMenuItems;
		docBrowser = Window("docs", 
			Rect(Window.screenBounds.width - docListWidth, 0, docListWidth, Window.screenBounds.height - 50));
		docBrowser.onClose = {
			docBrowser = nil;
			this.remove;
		};
		docBrowserView = docBrowser.view;
		docBrowserBounds = docBrowser.bounds;
		this.updateDocBounds;
		docListView = ListView(docBrowser, this.docListBounds);
		docListView.keyDownAction = { | me, char, mod, ascii ... rest |
			if (ascii == 127) {
				if (allDocs[me.value - 1].notNil) { allDocs[me.value - 1].close };
			}{
				me.defaultKeyDownAction(char, mod, ascii, *rest);
			}
		};
		docListView.action = { | me |
			if (me.value > 0) {
				this.selectDoc(allDocs[me.value - 1]) 
			};
		};
		codeListView = ListView(docBrowser, this.codeListBounds);
		codeListView.keyDownAction = { | me, char |
			this.selectAndPerformCodeAt(codeKeys indexOf: char);
		};
		codeListView.focusColor = Color.red;
		codeListView.action = {	 | me | this.performCodeAt(me.value); }; // only perform with keys
		
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
			this.selectDoc(Document.current);
			this.makeCodeList(selectedDoc);
			docBrowser.front;
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
	
	close {
		if (docBrowser.notNil) { docBrowser.close; };
		this.removeUserMenuItems;
	}

	add { if (DocPoller.dependants.includes(this).not) { DocPoller.add(this) } }
	remove { DocPoller.remove(this) }
	
	update { | docs |
		if (docBrowser.bounds != docBrowserBounds) {
			docBrowserBounds = docBrowser.bounds;
			docListView.bounds = this.docListBounds;
			codeListView.bounds = this.codeListBounds;
			this.updateDocBounds;
		};
		docs do: { | d, i |
			if (d.isListener) {
				if (listenerBounds != d.bounds) { d.bounds = listenerBounds };
			}{
				if (d.bounds != docBounds) { d.bounds = docBounds };
			};
		};
		if (docs.size != allDocs.size) {
			docListView.items = ["---"] ++ docs.collect(_.name);
			allDocs = docs.copy;	// Document modifies the original. Copy needed!
			this.selectDoc(Document.current);
		}{
			if (selectedDoc !== Document.current) {
				this.selectDoc(Document.current);
			};
		}
	}

	updateDocBounds {
		docBounds = Rect(listenerWidth, 
			docBrowserBounds.top,
			docBrowserBounds.left - listenerWidth,
			docBrowserBounds.height + 22
		);		
	}

	selectDoc { | doc |
		var index;
		index = allDocs.indexOf(doc);
		if (index.isNil) { ^this };
		selectedDoc = doc;
		Document.current = selectedDoc;
		selectedDoc.front;
		docListView.value = index + 1;
		this.makeCodeList(doc);	
		Archive.global.put(\currentDoc, selectedDoc.name);
	}
		
	
}