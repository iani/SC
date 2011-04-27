/* 

Provide a list of currently open windows to choose from 
Code
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
			{ Document.allDocuments.sort({ | a, b | a.name < b.name }) collect: { | d | d.name->{ d.front } }; },
			{ | items |
				var doc;
				doc = Document.current.name;
				items.indexOf(items detect: { | d | d.key == doc });
			},
			Panes, [\docOpened, \docToFront, \docClosed],
			delay: 0.1;
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