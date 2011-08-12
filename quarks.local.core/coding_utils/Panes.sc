/* 
Arrange Document windows so that they fill the entire area of the currently available main monitor screen. 

*/

Panes {
	classvar <>listenerY = 300, <>twoPaneListenerHeight = 300;
	classvar <>listenerXdelta=20;
	classvar <>panePos;
	classvar <>listenerPos, <>tryoutPos;
	classvar <currentPositionAction;
	classvar <>tryoutName = "tryout.scd";
	classvar <>defaultArrangementAction;

	*initClass { StartUp.add(this); }

	*doOnStartUp {
		this.addMenu;
		Code.addMenu;
		Dock.addMenu;
		BufferResource.addMenu;
		{ this.start; }.defer(2); // wait for Lion to reopen last session windows before starting
	}

	*start { this.activate } // synonym
	*activate {
		NotificationCenter.register(this, \docOpened, this, { | doc | this.docOpened(doc) });
		Document.initAction = { | doc |
			NotificationCenter.notify(this, \docOpened, doc);  
		};
		Document.allDocuments do: this.setDocActions(_);
		if (defaultArrangementAction.isNil) {
			defaultArrangementAction = { this.arrange2Panes; };
		};
		defaultArrangementAction.value;
		Dock.showDocListWindow;
		// confuses post and Untitled windows if not deferred on startup:
		{
			this.openTryoutWindow;
		}.defer(0.5); 
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
			this.arrange2Panes;
			this.rearrangeAllDocs;
		}),
		CocoaMenuItem.addToMenu("Utils", "switch window pos (in 2 panes)", ["A", false, false],
		{
			var doc = Document.current;
			var pos = doc.bounds;
			var done = false;
			currentPositionAction.(doc);
		}),
		CocoaMenuItem.addToMenu("Utils", "rearrange all docs", ["A", true, false],
		{	this.rearrangeAllDocs;
		}),
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

		]
	}

	*doRestoreTop { | func, doc |
		doc = doc ?? { Document.current };
		func.value;
		doc.front;
	}

	*openTryoutWindow {
		var tryout, path;
		if ((tryout = Document.allDocuments.detect({ | d | d.name == tryoutName })).isNil) {
		path = Platform.userAppSupportDir ++ "/" ++ tryoutName;
			if (path.pathMatch.size == 0) {
				tryout = Document(tryoutName).path_(path);
			}{
				tryout = Document.open(path);
			};
		};
	}

	*arrange1Pane {
		var width;
		width = Dock.width;
		listenerPos = Rect(0, listenerY, this.twoPaneWidth - listenerXdelta, //mc
			Window.screenBounds.height - listenerY);
		tryoutPos = Rect(0, 0, this.twoPaneWidth, listenerY - 28);
		panePos = Rect(this.twoPaneWidth, 0, this.twoPaneWidth, Window.screenBounds.height);
		this changeArrangement: { | doc | this.placeDoc(doc) };
	}

	*arrange2Panes {
		listenerPos = Rect(0, 0, this.twoPaneWidth - listenerXdelta, twoPaneListenerHeight - 25);//mc
		panePos = Rect(0, twoPaneListenerHeight, this.twoPaneWidth, 
			Window.screenBounds.height - twoPaneListenerHeight
		);
		tryoutPos = Rect(0, twoPaneListenerHeight, this.twoPaneWidth, 
			Window.screenBounds.height - twoPaneListenerHeight
		);
		this changeArrangement: { | doc | 
			this.placeDoc(doc);
			this.next2Pane;
		};
	}

	*twoPaneWidth { ^min(640, Window.screenBounds.width / 2) }

	*changeArrangement { | arrangeFunc |
		currentPositionAction = arrangeFunc;
	}
	
	*rearrangeAllDocs {
		this doRestoreTop: {
			Document.allDocuments do: currentPositionAction.(_);
		};
	}
	
	*placeDoc { | doc |
		if (doc.isListener) { ^doc.bounds = listenerPos };
		if (doc.name == tryoutName) { ^doc.bounds = tryoutPos };
		doc.bounds = panePos;
	}

	*next2Pane {
		var left;
		left = this.twoPaneWidth + panePos.left;
		if ((left + this.twoPaneWidth) > Window.screenBounds.width) {
			panePos.left = 0;
		}{
			panePos.left = left
		};
		if (panePos.left == 0) {
			panePos.top = twoPaneListenerHeight;
			panePos.height = Window.screenBounds.height - twoPaneListenerHeight;
		}{
			panePos.top = 0;
			panePos.height = Window.screenBounds.height;
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
