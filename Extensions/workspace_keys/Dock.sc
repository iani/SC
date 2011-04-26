/* Redo of DocListWindow, (!?!??) 

DRAFT !!!
Use the doc list window as a popup, to maximize space.

Use keyboard commands to reorganize windows.

AbstractServerAction



*/

Dock {
	classvar <>listenerY = 500, <>onePaneListenerWidth = 500, <>twoPaneListenerHeight = 300;
	classvar <>onePaneWidth = 790, <>docListWidth = 150;
	classvar <savedDocPositions, <>onePanePos, <>twoPanePos;
	classvar <>listenerPos, <>tryoutPos;
	classvar <sessionName;
	classvar <currentPositionAction;
	classvar <menuItems;

	*initClass {
		StartUp.add(this);	
	}
	
	*doOnStartUp {
		savedDocPositions = IdentityDictionary.new;
		this.activate;
		{ this.saveDocPositions }.defer(0.1);
	}

	*activate {
		[this, \activating].postln;
		NotificationCenter.register(this, \docOpened, this, { | doc | this.docOpened(doc) });
		Document.initAction = { | doc | 
			NotificationCenter.notify(this, \docOpened, doc);
		};
		this.addMenus;
	}
	
	*addMenus {
		menuItems = [
			CocoaMenuItem.addToMenu("User Menu", "1-pane doc arrangement", ["<", false, false], {
				this.arrange1Pane;
			}),
			CocoaMenuItem.addToMenu("User Menu", "2-pane doc arrangement", [">", false, false], {
				this.arrange2Panes;
			}),
			CocoaMenuItem.addToMenu("User Menu", "show doc list window", ["\"", false, false], {
				this.showDocListWindow;
			}),
		]
	}
	
	*arrange1Pane {
		listenerPos = Rect(0, listenerY, onePaneListenerWidth, Window.screenBounds.height - listenerY);
		onePanePos = Rect(onePaneListenerWidth, 0, onePaneWidth, Window.screenBounds.height);
		this changeArrangement: { | doc | this.placeDoc1Pane(doc) };
	}

	*arrange2Panes {
		listenerPos = Rect(0, 0, this.twoPaneWidth, twoPaneListenerHeight - 20);
		twoPanePos = Rect(0, twoPaneListenerHeight, this.twoPaneWidth, 
			Window.screenBounds.height - Window.screenBounds.width);
		this changeArrangement: { | doc | this.placeDoc2Panes(doc) };
	}

	*twoPaneWidth { ^Window.screenBounds.width / 2 }

	*changeArrangement { | arrangeFunc |
		currentPositionAction = arrangeFunc;
		Document.allDocuments do: { | doc | currentPositionAction.(doc) };
	}
	
	*placeDoc1Pane { | doc |
		if (doc.isListener) { doc.bounds = listenerPos } { doc.bounds = onePanePos };
	}

	*placeDoc2Panes { | doc |
		if (doc.isListener) {
			doc.bounds = listenerPos;
		}{
			doc.bounds = twoPanePos;
			twoPanePos.left = this.twoPaneWidth + twoPanePos.left % Window.screenBounds.width;
			if (twoPanePos.left == 0) {
				twoPanePos.top = twoPaneListenerHeight;
				twoPanePos.height = Window.screenBounds.height - twoPaneListenerHeight;
			}{
				twoPanePos.top = 0;
				twoPanePos.height = Window.screenBounds.height;
			}
		};
	}
	
	*showDocListWindow {
		"toggleDocListWindow Dock ".post;
		UniqueWindow(\docListWindow, {
			var w;
			w = Window("docs", Rect(
				Window.screenBounds.width - docListWidth, 0, docListWidth, Window.screenBounds.height
			));	
		})
	}

	*deactivate {
		[this, \deactivating].postln;
		NotificationCenter.unregister(this, \docOpened, this);
		Document.initAction = nil;
		this.removeMenus;
	}



	*removeMenus {
		menuItems do: { }		
	}

	*saveDocPositions {
		Document.allDocuments do: { | doc | savedDocPositions.put(doc, doc.bounds) };
	}
	
	*restoreDocPositions {
		Document.allDocuments do: { | doc | doc.bounds = savedDocPositions[doc] }
	}

	*docOpened { | doc |
//		"Dock docOpened ".post;
//		doc.name.postln; 	doc.class.postln;
		{ currentPositionAction.(doc) }.defer(0.1);
		
	}

	*storePositions {}
	*restorePositions {}
	
	
	*flipTryoutWindow {}
}