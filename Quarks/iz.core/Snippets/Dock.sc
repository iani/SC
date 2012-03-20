/* 
Dock.showDocListWindow: 
	Provide an automatically updating list of currently open windows to choose from
Dock.browseUserClasses:
	Provide a list of Classes defined in the current Users' Extensions diractory
	(in MacOS X: ~/Library/Application Support/SuperCollider/Extensions/)
*/

Dock {
	classvar <>width = 160;
	classvar <shortcutDocs;
	classvar <shortcutDocPaths;
	classvar <shortcutDocMenuItems;

	*initClass { StartUp add: this }
	
	*doOnStartUp {
		// Install Qt GUI if available, so that scope and freqscope work on local server:
		if (GUI respondsTo: \qt) { GUI.qt };

		shortcutDocMenuItems = Array.newClear(10);
		shortcutDocs = Array.newClear(10);
		if (File.exists(this.shortcutDocDir)) {
			shortcutDocPaths = Object.readArchive(this.shortcutDocDir);
		};
		if (shortcutDocPaths.isNil) {
			shortcutDocPaths = Array.newClear(10);
			shortcutDocPaths[0] = Platform.userAppSupportDir +/+ "tryout.scd";
		};
		shortcutDocPaths do: this.makeDocShortcutMenuItem(_, _);
	}

	*shortcutDocDir { ^Platform.userExtensionDir +/+ "ShortcutDocs.scd" }

	*makeDocShortcutMenuItem { | path, i |
		if (path.isNil) { ^this };
		shortcutDocMenuItems[i] = CocoaMenuItem.add([path.basename], { 
			this showDoc: i 
		}).setShortCut(i.asString);
	}

	*showDoc { | i |
		var docPath, doc;
		doc = shortcutDocs[i];
		if (doc.notNil and: { Document.allDocuments includes: doc }) { ^doc.front.didBecomeKey };
		docPath = shortcutDocPaths[i];
		if (docPath.notNil) {
			shortcutDocs[i] = Document open: docPath;
		}
	}
	
	*addDocShortcut { | doc, i |
		if (shortcutDocMenuItems[i].notNil) { shortcutDocMenuItems[i].remove };
		shortcutDocs[i] = doc;
		shortcutDocPaths[i] = doc.path;
		shortcutDocPaths.writeArchive(this.shortcutDocDir);
		this.makeDocShortcutMenuItem(doc.path ?? { doc.name }, i);
	}


	*menuItems {
		^{ | i | 
			CocoaMenuItem.add(["Add Doc shortcut", i.asString], { 
				this.addDocShortcut(Document.current, i);
			}).setShortCut(i.asString, true)
		} ! 10 
		addAll: 
		[
			CocoaMenuItem.addToMenu("Utils", "show doc list window", ["\"", false, false], {
				this.showDocListWindow;
			}),
			CocoaMenuItem.addToMenu("Utils", "browse / open user classes", ["b", true, true], {
				this.browseUserClasses;
			}),
			CocoaMenuItem.addToMenu("Utils", "open / create class help", ["D", true, false], {
				this.openCreateHelpFile;
			}),
/*			CocoaMenuItem.addToMenu("Utils", "insert class help template", nil, {
				this.insertClassHelpTemplate;
			}),
*/			
			CocoaMenuItem.addToMenu("Utils", "open scope", ["s", true, false], {
				Server.default.scope;
			}),
			CocoaMenuItem.addToMenu("Utils", "open spectrograph", ["s", true, true], {
				Server.default.freqscope;
			}),
			
		]		
	}

	*showDocListWindow { | multiPaneAreaWidth |
		var listwin;
		multiPaneAreaWidth = multiPaneAreaWidth ?? { Window.screenBounds.width };
		listwin = ListWindow('Documents', 
			Rect(multiPaneAreaWidth - width, 87, width, Window.screenBounds.height - 87),  
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
			listwin.window.bounds = listwin.window.bounds.height = Window.screenBounds.height / 2 - 70
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
	*positionDocListWindowLeftFrom {|xPos|
		var window;
		window = Window.allWindows.detect({ | w | w.name == "Documents" });
		if (window.notNil) { 
			window.bounds = Rect((xPos - width).max(0), 87, width, Window.screenBounds.height - 87)
		}
	}

	*placeDocWindow { | x = -160, y = 200 |
		/* 	IZ 120308 Hack to send Document list window to the other computer monitor screen. Try: 
			Dock.placeDocWindow(-160, 200);
			Note: Compare with Dock.positionDocListWindowLeftFrom which does not place the 
			window entirely outside the current main monitor screen. 
		*/
		var docwin;
		docwin = Window.allWindows select: { | w | w.name == "Documents" };
		if (docwin.size > 0) {
			docwin = docwin.first;
			docwin.bounds = docwin.bounds.moveTo(x, y);
		}
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
						{ 
							if (ListWindow.at(windowName).notNil) { 
								ListWindow.at(windowName).close;
							}; 
						}.defer(0.5)
					}.doOnceIn(0.75);
				}
			});
		});
	}
	
	*openCreateHelpFile {
		var windowName = 'Select Class to open Help';
		var class;
		class = this.findClassFromSelection;
		if (class.notNil) {
			^class.openHelpFileLocally;	
		};
		ListWindow(windowName, nil, {
			Class.allClasses.select({ | c |
				"SuperCollider/Extensions/".matchRegexp(c.filenameSymbol.asString)
				and: { "Meta*".matchRegexp(c.name.asString).not }
			}).collect({ | c | 
				c.name.asSymbol->{ 
					{ 
						c.openHelpFileLocally;
						{
							if (ListWindow.at(windowName).notNil) {
								ListWindow.at(windowName).close;
							}; 
						}.defer(0.5)
					}.doOnceIn(0.75);
				}
			});
		});		
	}

	*findClassFromSelection { // taken from Process class
		var string, class, method, words;
		string = Document.current.selectedString;
		Document.current.selectedString;
		if (string includes: $:) {
			string.removeAllSuchThat(_.isSpace);
			words = string.delimit({ arg c; c == $: });
			class = words.at(0).asSymbol.asClass;
			if (class.notNil) {
				method = class.findMethod(words.at(1).asSymbol);
				if (method.notNil) {
					method.filenameSymbol.asString.openTextFile(method.charPos, -1);
				};
			}{
				^nil;
			}
		}{
			class = string.asSymbol.asClass;
			if (class.notNil) {
				class = class.classRedirect;
				^class;
			}{
				^nil
			};
		};
	}


	*insertClassHelpTemplate {
		var doc, class;
		doc = Document.current;
		class = Document.current.name.splitext.first.asSymbol.asClass;
		if (class.isNil) { ^this };
		{
			0.2.wait;
			doc.string_(doc.string ++ format("%\n\nInherits from: %
			
Purpose

Usage

Related Classes

Instance Creation

Class variables

%

Instance variables

%

Class methods

%

Instance methods

%

Examples

  ",
				class.name.asString,
				"".strcatList(class.superclasses collect: _.name),
				"\t".strcatList((class.classVarNames ? [" -- "]).sort, " : \n\n\t"),
				"\t".strcatList((class.instVarNames ? [" -- "]).sort, " : \n\n\t"),
				"\t*".strcatList((class.class.methods.collect(_.name) ? [" -- "]).sort, " : \n\n\t*"),
				"\t".strcatList((class.methods.collect(_.name) ? [" -- "]).sort, " : \n\n\t")			));
			0.2.wait;
			doc.selectLine(1);	 0.2.wait;
			doc.font_(Font("Helvetica-Bold", 18), 
				doc.selectionStart, doc.selectionSize);
			0.2.wait;
			doc.selectLine(2);		0.2.wait;
			doc.font_(Font("Helvetica-Bold", 18), 
				doc.selectionStart, doc.selectionSize); 0.2.wait;
			doc.selectLine(3); 0.2.wait;
			doc.font_(Font("Helvetica-Bold", 12), 
				doc.selectionStart, doc.selectionSize); 
			doc.selectLine(4); 0.2.wait;
			doc.font_(Font("Helvetica", 12), 
				doc.selectionStart, doc.selectionSize); 
			doc.selectLine(5); 0.2.wait;
			doc.selectLine(5); 0.2.wait;
			doc.font_(Font("Helvetica", 12), 
				doc.selectionStart, doc.selectionSize); 0.2.wait; 
			doc.font_(Font("Helvetica-Bold", 12), 
				doc.selectionStart, doc.string.size - doc.selectionStart); 
		}.fork(AppClock);		
		
	}


}