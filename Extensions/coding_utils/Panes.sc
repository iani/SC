/* 
Arrange Document windows conveniently for a laptop-sized monitor screen. 
*/

Panes {
	classvar <>listenerY = 300, <>onePaneListenerWidth = 640, <>twoPaneListenerHeight = 300;
	classvar <>panePos;
	classvar <>listenerPos, <>tryoutPos;
	classvar <session;		// for saving / restoring doc positions and doc texts to archive
	classvar <currentPositionAction;
	classvar <>tryoutName = "tryout.scd";

	*start { this.activate } // synonym
	*activate {
		NotificationCenter.register(this, \docOpened, this, { | doc | this.docOpened(doc) });
		Document.initAction = { | doc |
			NotificationCenter.notify(this, \docOpened, doc);  
		};
		this.addMenu;
		Code.addMenu;
		Dock.addMenu;
		BufferResource.addMenu;
		Document.allDocuments do: this.setDocActions(_);
//		this.arrange1Pane;
		this.arrange2Panes;
		Dock.showDocListWindow;
		// confuses post and Untitled windows if not deferred on startup:
		{ this.openTryoutWindow; }.defer(0.5); 
	}

	*stop { this.deactivate } // synonym
	*deactivate {
		NotificationCenter.unregister(this, \docOpened, this);
		Document.initAction = { | doc | doc.front; };
		this.removeMenu;
		Code.removeMenu;
		Dock.removeMenu.closeDocListWindow;
		BufferResource.removeMenu;
	}

	*menuItems { ^[
			CocoaMenuItem.addToMenu("Utils", "single-pane doc arrangement", ["<", false, false], {
				this.doRestoreTop({ this.arrange1Pane; });
			}),
			CocoaMenuItem.addToMenu("Utils", "multi-pane doc arrangement", [">", false, false], {
				this.doRestoreTop({ this.arrange2Panes; });
			}),
			CocoaMenuItem.addToMenu("Utils", "switch window pos (in 2 panes)", [">", true, false], {
				currentPositionAction.(Document.current);
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
		tryout.front;
	}

	*arrange1Pane {
		var width;
		width = Dock.width;
		listenerPos = Rect(0, listenerY, this.twoPaneWidth, 
			Window.screenBounds.height - listenerY);
		tryoutPos = Rect(0, 0, this.twoPaneWidth, listenerY - 28);
		panePos = Rect(this.twoPaneWidth, 0, this.twoPaneWidth, Window.screenBounds.height);
		this changeArrangement: { | doc | this.placeDoc(doc) };
		Dock.showDocListWindow;
	}

	*arrange2Panes {
		listenerPos = Rect(0, 0, this.twoPaneWidth, twoPaneListenerHeight - 25);
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
		Document.listener.front;
	}

	*twoPaneWidth { ^min(640, Window.screenBounds.width / 2) }

	*changeArrangement { | arrangeFunc |
		currentPositionAction = arrangeFunc;
		Document.allDocuments do: { | doc | currentPositionAction.(doc) };
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

	// TODO
	*flipTryoutWindow {}

	// TODO, via class Session, whose instance is stored in classvar session here.
	*storePositions {}
	*restorePositions {}
/*
	*saveDocPositions {
		Document.allDocuments do: { | doc | savedDocPositions.put(doc, doc.bounds) };
	}
	
	*restoreDocPositions {
		Document.allDocuments do: { | doc | doc.bounds = savedDocPositions[doc] }
	}
*/
}