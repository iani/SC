/* 
Dock.showDocListWindow: 
	Provide an automatically updating list of currently open windows to choose from
Dock.browseUserClasses:
	Provide a list of Classes defined in the current Users' Extensions diractory
	(in MacOS X: ~/Library/Application Support/SuperCollider/Extensions/)
*/

Dock {
	classvar <>width = 150;
	*menuItems { ^[
			CocoaMenuItem.addToMenu("User Menu", "show doc list window", ["\"", false, false], {
				this.showDocListWindow;
			}),
			CocoaMenuItem.addToMenu("User Menu", "browse user classes", ["b", true, true], {
				this.browseUserClasses;
			}),
			
		]		
	}
	*showDocListWindow {
		UniqueWindow.listWindow('Documents', 
			Rect(Window.screenBounds.width - width, 0, width, Window.screenBounds.height), 
			{ Document.allDocuments.sort({ | a, b | a.name < b.name }) collect: { | d | 
				d.name->{
					d.front; 
// NotificationCenter.notify(Panes, \docToFront, d);
// sending a document to front does not make it current. Therefore compensate here: 
					d.didBecomeKey;
				} };
			},
			{ | items |
				var doc;
				doc = Document.current.name;
				items.indexOf(items detect: { | d | d.key == doc });
			},
			Panes, [\docOpened, \docToFront, \docClosed],
			delay: 0.1; // leave some time for Documents to update their name etc.
		)
		.addNotifier(Code, \openedCodeListWindow, { | listwin |
			listwin.window.bounds = listwin.window.bounds.height = Window.screenBounds.height / 2 - 70;
		})
		.addNotifier(Code, \closedCodeListWindow, { | listwin |
			listwin.window.bounds = listwin.window.bounds.height = Window.screenBounds.height;
		});
	}
	*browseUserClasses { 
		UniqueWindow.listWindow('User Classes', nil, {
			Class.allClasses.select({ | c |
				"SuperCollider/Extensions/".matchRegexp(c.filenameSymbol.asString)
				and: { "Meta*".matchRegexp(c.name.asString).not }
			}).collect({ | c | c.name.asSymbol->{ c.openCodeFile; } });	
		});
	}
}