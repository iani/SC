/* 

Provide a list of currently open windows to choose from 
Code
*/

Dock {
	classvar <>dockWidth = 200;
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
		var width = 200;
		"toggleDocListWindow Dock ".post;
		UniqueWindow.listWindow('Documents', 
			Rect(Window.screenBounds.width / 2 - width, 0, width, Window.screenBounds.height), 
			{ Document.allDocuments collect: { | d | d.name->{ d.front } }; },
//			{ Document.addDocuments },
			nil,
			Panes, Panes.docNotifiers;
		);
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