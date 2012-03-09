/* 
Arrange Document windows so that they fill the entire area of the currently available main monitor screen. Provide 2 arrangement schemes: 

1. post + tryout window + 1 window for editing 

2. post + tryout window + as many windows as fit side by side horizontally to fill the screen, where each window is 640 pixels wide. 


*/

Panes {
	classvar <prefs, prefsFile = "PanesPrefs.scd";
	classvar <>panePos, <>protoPanePos, <>listenerPos, <>tryoutPos;
	classvar <currentPositionAction, <>defaultArrangementAction, <multiPaneAreaWidth;
	classvar <>miniServerWindow = false; // if true, rightmost pane will leave space for 
	// Sergio Luque's modivied Server:makeWindow at the bottom right part of the screen

	*defaults {
		 ^(
			listenerY: 200, listenerXdelta: 20, menuHeight: 22, multiPaneListenerHeight: 300
			, multiPaneHeight: Window.screenBounds.height - 22
			, multiPaneAreaWidth: Window.screenBounds.width //mc
			, defaultArrangementMethod: \arrangeMultiPanes, tryoutName: "tryout.scd"
		)
	}
	
	*updatePrefs { // iz 120304
		// update preferences from Window.screenBounds to fit, 
		// when using a computer monitor with different screen dimensions than previously
		multiPaneAreaWidth = Window.screenBounds.width;
		prefs.multiPaneAreaWidth = multiPaneAreaWidth;
		prefs.multiPaneHeight = Window.screenBounds.height - prefs.menuHeight;
		this.savePrefs;
	}
	
	*savePrefs { // iz 120304
		UserPrefs.save(prefsFile, prefs);
	}
	
	*initClass { StartUp.add(this); }

	*doOnStartUp {
		this.loadPrefs; //mc
		this.addMenu;
		Code.addMenu;
		Dock.addMenu;
		BufferResource.addMenu;
		{ this.start; }.defer(2); // wait for Lion to reopen last session windows before starting
	}
	*loadPrefs{ 
		prefs = ().putAll(UserPrefs.load(prefsFile, this.defaults));
		multiPaneAreaWidth = prefs.multiPaneAreaWidth;
	}

	*start { this.activate } // synonym
	*activate {
		NotificationCenter.register(this, \docOpened, this, { | doc | this.docOpened(doc) });
		Document.initAction = { | doc |
			NotificationCenter.notify(this, \docOpened, doc);  
		};
		Document.allDocuments do: this.setDocActions(_);
//		postf("Panes: activate method, defaultArrangementAction is: %\n", defaultArrangementAction);
		if (defaultArrangementAction.isNil) {
			defaultArrangementAction = { this.perform(prefs.defaultArrangementMethod) };
		};
		defaultArrangementAction.value;
		Dock.showDocListWindow(multiPaneAreaWidth); //mc
		// confuses post and Untitled windows if not deferred on startup:
		{ this.openTryoutWindow }.defer(0.5); 
	}

	*stop { this.deactivate } // synonym
	*deactivate {
		NotificationCenter.unregister(this, \docOpened, this);
		Document.initAction = { | doc | doc.front; };
//		this.removeMenu;
//		Code.removeMenu;
//		Dock.removeMenu.closeDocListWindow;
//		BufferResource.removeMenu;
	}

	*menuItems { ^[
		CocoaMenuItem.addToMenu("Utils", "activate pane placement", nil, {
			this.activate;
		}),
		CocoaMenuItem.addToMenu("Utils", "single-pane doc arrangement", ["<", true, false], {
			this.arrange1Pane;
			this.rearrangeAllDocs;
		}),
		CocoaMenuItem.addToMenu("Utils", "multi-pane doc arrangement", [">", true, false], {
			this.arrangeMultiPanes;
			this.rearrangeAllDocs;
		}),
		CocoaMenuItem.addToMenu("Utils", "switch window pos (multi-pane mode)", ["A", false, false],
		{
			var doc = Document.current;
//mc ?!		var pos = doc.bounds;
//mc	?!		var done = false;
			currentPositionAction.(doc);
		}),
		CocoaMenuItem.addToMenu("Utils", "maximize window (multi-pane mode)", ["M", false, false],
		{
			this.maximizeDocHight(Document.current);
		}),
		CocoaMenuItem.addToMenu("Utils", "toggle pane area width (multi-pane mode)", 
			["M", false, true], {	this.togglePaneAreaWidth;
		}),
		CocoaMenuItem.addToMenu("Utils", "rearrange all docs", ["R", false, false],
		{	this.rearrangeAllDocs;
		}),
		
//quick add, but very helpful...	
		CocoaMenuItem.addToMenu("Utils", "all GUIs front", ["g", true, false],
		{	Window.allWindows.do{|w| w.front}
		}),
		
//why is all the following in Panes? Could we not modularise Panes any further? 
		CocoaMenuItem.addToMenu("Utils", "Boot/Quit default server", ["B", true, false], { 
			if (Server.default.serverRunning) { Server.default.quit } { Server.default.boot };
		}),
		
		CocoaMenuItem.addToMenu("Utils", "Edit startup file", ["S", true, true], { 
			Document.open(UserPath("startup.scd"))
		}),
		
		CocoaMenuItem.addToMenu("Utils", "Open User Directory", ["o", true, true], { 
			"open ~/Library/Application\\ Support/SuperCollider".unixCmd;
		}),
		
		CocoaMenuItem.addToMenu("Utils", "Toggle OSC input posting", ["O", true, true], { 
			if (thisProcess.recvOSCfunc.isNil) {
				thisProcess.recvOSCfunc = { | time, addr, msg |
					if (msg[0].asString.contains("status.reply").not) {			postf("time: % sender: % message: %\n", time, addr, msg)
					};
				};
				"OSC Posting is ON".postln;
			}{
				thisProcess.recvOSCfunc = nil;
				"OSC Posting is OFF".postln;
			}
		}),
		
		CocoaMenuItem.addToMenu("Utils", "Start OSC input test", ["I", true, true], { 
			{
				var a;
				a = NetAddr.localAddr;
				loop { a.sendMsg(\test); 0.25.wait };
			}.fork
		}),
		
		CocoaMenuItem.addToMenu("Utils", "Open log file in orgmode", ["l", true, true], { 
			{
				var logname;
				logname = Platform.userAppSupportDir ++ "/sclog.org";
				if (logname.pathMatch.size == 0) {
					format("touch %", logname.asCompileString).unixCmd;
					0.1.wait;
				};
				format("open -a Emacs.app %", logname.asCompileString).unixCmd;
			}.fork
		}),

		CocoaMenuItem.addToMenu("Utils", "Show tryout window", ["T", true, true], {
			this.openTryoutWindow.front;
		}),
		]
	}

	*doRestoreTop { | func, doc |
		doc = doc ?? { Document.current };
		func.value;
		doc.front;
	}

	*openTryoutWindow {
		var tryout, path, tryoutName = prefs.tryoutName;
		if ((tryout = Document.allDocuments.detect({ | d | d.name == tryoutName })).isNil) {
		path = Platform.userAppSupportDir ++ "/" ++ tryoutName;
			if (path.pathMatch.size == 0) {
				tryout = Document(tryoutName).path_(path);
			}{
				tryout = Document.open(path);
			};
		};
		^tryout;
	}

	*arrange1Pane {
		var width, multiPaneWidth = this.multiPaneWidth, listenerY = prefs.listenerY;
		width = Dock.width;
		listenerPos = Rect(0, listenerY, multiPaneWidth - prefs.listenerXdelta, //mc
			Window.screenBounds.height - listenerY);
		tryoutPos = Rect(0, 0, multiPaneWidth, listenerY - 28);
		panePos = Rect(multiPaneWidth, 0, multiPaneWidth, Window.screenBounds.height);
		this changeArrangement: { | doc | this.placeDoc(doc) };
	}

	*arrangeMultiPanes {
		var multiPaneWidth = this.multiPaneWidth, mPLH = prefs.multiPaneListenerHeight;
		var multiPaneHeight = prefs.multiPaneHeight;
		var screenTop = Window.screenBounds.height - prefs.menuHeight;

		listenerPos = Rect(0, 0, multiPaneWidth - prefs.listenerXdelta, mPLH); //mc
		tryoutPos = Rect(0, mPLH, multiPaneWidth, screenTop - mPLH);
		panePos = Rect(multiPaneWidth, screenTop - multiPaneHeight, multiPaneWidth, multiPaneHeight);
		protoPanePos = panePos.copy;
		this changeArrangement: { | doc | 
			this.placeDoc(doc);
			this.nextPane;
		};
	}

	*multiPaneWidth { ^min(640, Window.screenBounds.width / 2) }

	*changeArrangement { | arrangeFunc |
		currentPositionAction = arrangeFunc;
	}
	
	*rearrangeAllDocs {
		this doRestoreTop: {
			Document.allDocuments do: currentPositionAction.(_);
		};
	}
	
	*placeDoc { | doc |
		if (doc.reallyIsListener) { ^doc.bounds = listenerPos };
		if (doc.name == prefs.tryoutName) { ^doc.bounds = tryoutPos };
		doc.bounds = panePos;
	}

	*nextPane {
		var left, top, multiPaneWidth, multiPaneHeight, screenTop;
		multiPaneWidth = this.multiPaneWidth;
		multiPaneHeight = prefs.multiPaneHeight;
		screenTop = Window.screenBounds.height - prefs.menuHeight;
		if (panePos.left == 0) {
			panePos = protoPanePos.copy
		}{
			top = panePos.top - multiPaneHeight;
			if (top >= 0) { panePos.top = top }{
				left = panePos.left + multiPaneWidth;
				if (left + this.multiPaneWidth > multiPaneAreaWidth) {
					panePos = tryoutPos.copy;		
				}{
					panePos.left = left;
					panePos.top = screenTop - multiPaneHeight;
				};
				if (miniServerWindow and: {
					panePos.width + panePos.left == Window.screenBounds.width
				}) {
					panePos.height_(panePos.height - 45).top_(45);
				}				
			}
		};
	}
	*maximizeDocHight{
		var height, doc = Document.current;
		if (doc.reallyIsListener.not && (doc.name != prefs.tryoutName) && doc.bounds.left != 0) {
			height = Window.screenBounds.height - prefs.menuHeight;
			doc.bounds = doc.bounds.top_(height).height_(height)
		}	
	}
	*togglePaneAreaWidth{
		var newMultiPaneAreaWidth;
		if (multiPaneAreaWidth == Window.screenBounds.width) {
			newMultiPaneAreaWidth = prefs.multiPaneAreaWidth
		}{ 	newMultiPaneAreaWidth = Window.screenBounds.width };
		if (newMultiPaneAreaWidth != multiPaneAreaWidth) {
			multiPaneAreaWidth = newMultiPaneAreaWidth;
//			this.rearrangeAllDocs;
			Dock.positionDocListWindowLeftFrom(multiPaneAreaWidth);
		} 
	}
	
	*docOpened { | doc |
// why does this post twice always???????????
//		postf("Panes init doc actions docOpened: %\n", doc.name).postln;
		this.setDocActions(doc);
		currentPositionAction.(doc);
	}
	
	*setDocActions { | doc |
		doc.toFrontAction = {
			var selectionStart, selectionSize;
			NotificationCenter.notify(this, \docToFront, doc);
		};
		doc.endFrontAction = { NotificationCenter.notify(this, \docEndFront, doc); };
		doc.mouseUpAction = { NotificationCenter.notify(this, \docMouseUp, doc); };
		doc.onClose = { NotificationCenter.notify(this, \docClosed, doc); };
	}
	
	*docNotifiers { ^[\docOpened, \docToFront, \docEndFront, \docMouseUp, \docClosed] }
}
