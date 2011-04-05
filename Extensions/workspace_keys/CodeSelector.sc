/* IZ 110324 

CodeSelector.install;


start: 


stop: 
	close window
	stop routine



 */

CodeSelector {
	classvar <docs;
	classvar <selectedDoc, <docKeys;
	classvar <browser, <docListView;
	classvar <snippetView;
	classvar <>snippetFileType = "scd";
	classvar <docText, <snippets, <snippetKeys, selectedSnippet;
	classvar <pollRoutine;

	*install {
		var rects, t, h, offset;
		CocoaMenuItem.addToMenu("User Menu", "Select Code", ["1", false, false], {
//			Document.current.name.postln;
//			this.loadSnippets(Document.current);
			this.selectDoc(docs detect: (_ == Document.current));
			{ if (browser.notNil) { browser.front; snippetView.focus }; }.defer(0.2);
		});
		CocoaMenuItem.addToMenu("User Menu", "Code Selector Window", ["2", false, false], {
			this.makeWindow;
			docListView.focus;
		});
	}

	*makeWindow {
		var key;
		var index = 0;
		if (browser.notNil) { browser.close };
		docs = [];
		docKeys = [];
		Document.openDocuments do: { | d |
			if (this canMakeSnippets: d) {
				key = d.string.findRegexp("^//Doc:\(.*\)$");
				docKeys = docKeys.add(
					if (key.size > 1) {
						key[1][1][0];
					}{
						index.asString.first.asAscii;
					}
				);
				docs = docs add: d;
				d.onClose = { if (browser.notNil) { this.makeWindow } };
				index = index + 1;
			}
		};
		this.start;
		docListView = GUI.listView.new(browser, Rect(2, 2, 146, 320));
		docListView.action = { | me |
//			postf("doc list value: %\n", me.value);
			if (me.value > 0) {
//				docs[me.value - 1].postln;
				this selectDoc: docs[me.value - 1];
			};
		};
		docListView.keyDownAction = { | view, char, mod, key |
			var index;
//			[thisMethod.name, view, char, mod, key].postln;
			index = docKeys indexOf: char;
			if (index.notNil) {
				this selectDoc: docs[index];
			}{
				view.defaultKeyDownAction(char, mod, key);
			};
		};
		snippetView = GUI.listView.new(browser, Rect(2, 324, 146, 368));
		snippetView.action = { | me |
//			postf("snippet value: %\n", me.value);
			if (me.value > 0) {
				snippets[me.value - 1].interpret;
			};
		};
		snippetView.keyDownAction = { | view, char, mod, key |
			selectedSnippet = snippetKeys indexOf: char;
//			[thisMethod.name, view, char, mod, key, "selectedSnippet index:", selectedSnippet].postln;
//			snippets[selectedSnippet].postln;
//			postf("char: %\n", char);
			if (char == $ ) {
				this.selectDocView
			}{
				if (selectedSnippet.notNil) {
					snippets[selectedSnippet].interpret;
				}{
					view.defaultKeyDownAction(char, mod, key);
				};
			}
		};
		docListView.items = ["---"] ++ (docs collect: { | sd, i |
			docKeys[i].asString ++ " " ++ sd.name;
		});
		this selectDoc: selectedDoc;
	}

	*start {
		browser = GUI.window.new("", Rect(0, 100, 150, 700)).front;
		browser.onClose = {
			NotificationCenter.unregister(this, \changed, browser);
		};
	}

	*stop {
	}

	*selectDocView {
		browser.front;
		docListView.focus;
//		thisMethod.name.postln;
	}

	*canMakeSnippets { | document |
		^document.name.splitext.last == snippetFileType;
	}
/*	
	*docClosed { | doc |
		if (selectedDoc == doc) {
			this.selectDoc(nil);	
		}			
	}
*/
	*selectDoc { | argDoc |
		var index;
		selectedDoc = argDoc;
		index = ((docs indexOf: selectedDoc) ? -1) + 1;
		if (selectedDoc.isNil) {
			this.clearSnippets;
			if (browser.notNil) { docListView.value = 0 };
		}{
			selectedDoc.front;
			if (browser.notNil) {
				docListView.value = index; // .postln;
				this.loadSnippets(selectedDoc);
			};
		};
	}

	*clearSnippets {
		if (browser.isNil) { ^this };
		snippetView.items = [];	
	}

	*loadSnippets { | document |
		var poslist, snippet, items;
		if (browser.isNil) { ^this };
		docText = document.string;
		poslist = docText.findRegexp("^//:").slice(nil, 0) ?? { [0] };
		snippets = poslist collect: { | pos, i |
			docText[pos..(poslist[i + 1] ?? { docText.size }) - 1];
		};
		items = snippets collect: { | s |
			if (s[3] == $!) { s.interpret };
			s[3..50];
		};
		snippetKeys = items collect: _.first;
		snippetView.items = ["---"] ++ items;
		browser.front;
		snippetView.focus;
	}
	
}

