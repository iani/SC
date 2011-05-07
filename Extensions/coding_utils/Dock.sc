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
			CocoaMenuItem.addToMenu("Utils", "show doc list window", ["\"", false, false], {
				this.showDocListWindow;
			}),
			CocoaMenuItem.addToMenu("Utils", "browse / open user classes", ["b", true, true], {
				this.browseUserClasses;
			}),
			CocoaMenuItem.addToMenu("Utils", "open / create class help", ["D", true, false], {
				this.openCreateHelpFile;
			}),
			CocoaMenuItem.addToMenu("Utils", "open scope", ["s", true, false], {
				{ 
					var u;
					Server.default = Server.internal;
					WaitForServer(Server.internal);
					u = UniqueObject('stethoscope', { 
						var s; 
						s = Stethoscope(Server.internal);
						s.window.onClose_(s.window.onClose addFunc: { u.remove });
						s;
					});
					u.object.window.front;
					// restart if server re-booted with scope on
					ServerReady(Server.internal).addObjectAction(u, { u.object.run.postln });
				}.fork(AppClock);
			}),
			CocoaMenuItem.addToMenu("Utils", "open spectrograph", ["s", true, true], {
				Spectrograph.small;
			}),
			
		]		
	}
	*showDocListWindow {
		var listwin;
		listwin = ListWindow('Documents', 
			Rect(Window.screenBounds.width - width, 0, width, Window.screenBounds.height), 
			{ Document.allDocuments.sort({ | a, b | a.name < b.name }) collect: { | d | 
				d.name->{
					d.front; 
// sending a document to front does not make it current. Therefore compensate here: 
					Document.current = d;
//					d.didBecomeKey;
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
		.addNotifier(Code, \openedCodeListWindow, {
			listwin.window.bounds = listwin.window.bounds.height = Window.screenBounds.height / 2 - 70;
		})
		.addNotifier(Code, \closedCodeListWindow, {
			listwin.window.bounds = listwin.window.bounds.height = Window.screenBounds.height;
		});
	}
	
	*closeDocListWindow {
		var window;
		window = Window.allWindows.detect({ | w | w.name == "Documents" });
		if (window.notNil) { window.close };
	}
	
	*browseUserClasses {
		var windowName = 'User Classes';
		ListWindow(windowName, nil, {
			Class.allClasses.select({ | c |
				"SuperCollider/Extensions/".matchRegexp(c.filenameSymbol.asString)
				and: { "Meta*".matchRegexp(c.name.asString).not }
			}).collect({ | c | 
				c.name.asSymbol->{ 
					{ 
						c.openCodeFile;
						{ if (ListWindow.at(windowName).notNil) { ListWindow.at(windowName).close; }; 
						}.defer(0.5)
					}.doOnceIn(0.75);
				}
			});
		});
	}
	
	*openCreateHelpFile {
		var windowName = 'Select Class to open Help';
		ListWindow(windowName, nil, {
			Class.allClasses.select({ | c |
				"SuperCollider/Extensions/".matchRegexp(c.filenameSymbol.asString)
				and: { "Meta*".matchRegexp(c.name.asString).not }
			}).collect({ | c | 
				c.name.asSymbol->{ 
					{ 
						c.openHelpFileLocally;
						{ if (ListWindow.at(windowName).notNil) { ListWindow.at(windowName).close; }; 
						}.defer(0.5)
					}.doOnceIn(0.75);
				}
			});
		});		
	}

}