/* 

Arrange Document windows conveniently for a laptop-sized monitor screen. 

*/

Panes {
	classvar <>listenerY = 300, <>onePaneListenerWidth = 500, <>twoPaneListenerHeight = 300;
	classvar <>docListWidth = 0; // <>onePaneWidth = 790, 
	classvar <>panePos;
	classvar <>listenerPos, <>tryoutPos;
	classvar <session;					// for saving / restoring doc positions and doc texts to archive
	classvar <currentPositionAction;

	*initClass {
		StartUp.add(this);	
	}
	
	*doOnStartUp {
		this.activate;
	}

	*activate {
		NotificationCenter.register(this, \docOpened, this, { | doc | this.docOpened(doc) });
		Document.initAction = { | doc |
			// give Document some time to get its name and bounds - otherwise error occurs
			{ NotificationCenter.notify(this, \docOpened, doc);  }.defer(0.1);
		};
		this.addMenu;
		Code.addMenu;
		Dock.addMenu;
		this.arrange1Pane;
	}

	*deactivate {
		NotificationCenter.unregister(this, \docOpened, this);
		Document.initAction = nil;
		this.removeMenu;
		Code.removeMenu;
		Dock.removeMenu;
	}

	*menuItems { ^[
			CocoaMenuItem.addToMenu("User Menu", "1-pane doc arrangement", ["<", false, false], {
				this.doRestoreTop({ this.arrange1Pane; });
			}),
			CocoaMenuItem.addToMenu("User Menu", "2-pane doc arrangement", [">", false, false], {
				this.doRestoreTop({ this.arrange2Panes; });
			}),
			CocoaMenuItem.addToMenu("User Menu", "switch window pos", ["<", false, true], {
				currentPositionAction.(Document.current);
			}),
		]
	}

	*doRestoreTop { | func, doc |
		doc = doc ?? { Document.current };
		func.value;
		doc.front;
	}

	*arrange1Pane {
		listenerPos = Rect(0, listenerY, this.twoPaneWidth, Window.screenBounds.height - listenerY);
		tryoutPos = Rect(0, 0, this.twoPaneWidth, listenerY - 28);
		panePos = Rect(this.twoPaneWidth, 0, this.twoPaneWidth, Window.screenBounds.height);
		this changeArrangement: { | doc | this.placeDoc(doc) };
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
	}

	*twoPaneWidth { ^Window.screenBounds.width / 2 }

	*changeArrangement { | arrangeFunc |
		currentPositionAction = arrangeFunc;
		Document.allDocuments do: { | doc | currentPositionAction.(doc) };
	}
	
	*placeDoc { | doc |
		if (doc.isListener) { ^doc.bounds = listenerPos };
		if (doc.name == "tryout.sc") { ^doc.bounds = tryoutPos };
		doc.bounds = panePos;
	}

	*next2Pane {
		panePos.left = this.twoPaneWidth + panePos.left % Window.screenBounds.width;
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
		doc.toFrontAction = { NotificationCenter.notify(this, \docToFront, doc); };
		doc.endFrontAction = { NotificationCenter.notify(this, \docEndFront, doc); };
		doc.mouseUpAction = { NotificationCenter.notify(this, \docMouseUp, doc); };
		doc.onClose = { | me |
//			postf("doc closed: %, %\n", me, me.name);
			{
				NotificationCenter.notify(this, \docClosed, doc); 
			}.defer(0.1); // must defer for changes to register
		};
		currentPositionAction.(doc);
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