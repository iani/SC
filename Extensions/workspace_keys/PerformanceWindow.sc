
PerformanceWindow {
	classvar default;
	var <docListWindow;
	var <window;
	var <docPane;
	var <codePane;
	var <codeParts, <codeKeys;
	var <docPaneSearchString, keystrokeWaitInterval = 1.0;
	
	*makeGui { | docListWindow |
		if (default.isNil) { default = this.new(docListWindow) };
		default.makeGui;
	}
	
	*new { | docListWindow |
		^this.newCopyArgs(docListWindow).init;	
	}
	
	init {
		NotificationCenter.register(docListWindow, \stopped, this, {
			this.stop;
		});
		NotificationCenter.register(docListWindow, \items, this, { | ... items |
			docPane.items = items;
		});
		NotificationCenter.register(docListWindow, \index, this, { | index |
			docPane.value = index;
		});
		this.makeGui;
	}

	stop { window.close; }

	makeGui {
		if (window.notNil) { ^window.front };
		window = Window("Code Performer", Rect(0, 0, 500, 350));
		window.onClose = { this.closed };
		docPane = ListView(window, Rect(2, 2, 248, 496));
		docPane.resize = 4;
		docPane.focusColor = Color.red;
		docPane.items = docListWindow.docListView.items;
		docPane.action = { | me | 
			this.selectDoc(me.value);	
		};
		docPane.keyDownAction = { | me, char, mod, ascii, key |
			this.selectDocByKey(char, mod, ascii, key);
		};
		codePane = ListView(window, Rect(250, 2, 248, 496));
		codePane.resize = 4;
		codePane.focusColor = Color.blue;
		codePane.action = { | me | this.performCodeAt(me.value); };
		codePane.keyDownAction = { | me, char |
			this.selectAndPerformCodeAt(codeKeys indexOf: char);
		};
		docPane.valueAction = docListWindow.docListView.value;
		window.front;
		docPane.focus;
		^window;
	}

	selectDoc { | index |
		var items;
		#items, codeParts, codeKeys = docListWindow.parseCode(docListWindow.allDocs[index]);
		docPane.value = index;
		codePane.items = items;
	}

	selectDocByKey { | char, mod, unicode, key |
		var items, match, endPos;
		if (unicode == 16rF700, { docPane.valueAction = docPane.value - 1; ^this });
		if (unicode == 16rF703, { docPane.valueAction = docPane.value + 1; ^this });
		if (unicode == 16rF701, { docPane.valueAction = docPane.value + 1; ^this });
		if (unicode == 16rF702, { docPane.valueAction = docPane.value - 1; ^this });
		if (docPaneSearchString.isNil or: { unicode == 127 }) {
			docPaneSearchString = char.asString;
			{ docPaneSearchString = nil }.defer(keystrokeWaitInterval);
		}{
			docPaneSearchString = docPaneSearchString ++ char.asString;
		};
		endPos = docPaneSearchString.size;
		items = docPane.items;
		match = items detect: { | i |
			docPaneSearchString.matchRegexp(i, 0, endPos);
		};
		if (match.notNil) { this.selectDoc(items indexOf: match) };
		
	}

	selectAndPerformCodeAt { | index |
		if (index.isNil) { ^this };
		codePane.value = index;
		this.performCodeAt(index);	
	}

	performCodeAt { | index |
		if (index.isNil) { ^this };
		codeParts[index].interpret;
	}

	closed {
		window = nil;
		NotificationCenter.unregister(docListWindow, \stopped, this);
		NotificationCenter.unregister(docListWindow, \items, this);
		NotificationCenter.unregister(docListWindow, \index, this);
	}
}