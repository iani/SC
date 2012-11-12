/* iz Fri 28 September 2012  9:18 PM EEST

Store lists of ProxyCode-style script files. 
The lists are stored in Platform.userAppSupportDir +/+ "Scripts.sctxar", together with the scripts themselves. Scripts edited through ProxyCodeEditor are thus automatically saved there. 

===== TODO: =====
(Wed 03 October 2012 11:07 AM EEST)
- Rename ScriptListGui to ScriptLibGui
- Possibly split into 2 classes: ScriptLib (data) and ScriptLibGui (gui)
- Make new Library menu items like this: 

	- Library
		- ScriptLib
			- new 
			- open
			- open recent

- Add new mixer type: 
	- has 4 preset sets of 8 channels each. 
	- Each preset set corresponds to the keys of a row on the computer keyboard: 
		(1) 1, 2, 3, 4, 5, 6, 7, 8, (2) q, w, e, r, t, ,y, u, i (3) a, s, d, f, g, h, j (etc.) 
	- Each preset is pre-initialized to a proxy that is named after that key. 
	- Typing one of the keys above when a snippet is selected from the snippet list of the
		ScriptListGui sends the snippet to the corresponding proxy on the keyboard.
	- Issue: How to display the names of the proxy/snippets on the top row of the mixer? 
		This requires some modification!
	
	Implementation classes: ProxyKeyMixer, ProxyKeyStrip. 
	SEE ProxyKeyStrip Class FOR TEMPLATE AND INTERFACE IDEA DETAILS

	- Evaluating a new snippet checks proxy for channel number mismatch and clears previous
	  proxy if needed, to restart proxy with new number of channel. 
	  This must be done at NodeProxy method level. 
		
- Backward compatibility as the format of ScriptLib data changes: 
  Make separate class for storing and loading ScriptLib instances into archive. 
  To reload ScriptLib instances from different formats, 
  one would use a different class that corresponds to the format.
  


*/
ScriptListGui : AppModel {
	classvar <>font;

	var <>archivePath;
	var <files; 		// Value holding current files list
	var snippetViews; // StackLayout containing snippet list + snippet TextView
	var test;			// test hiding / showing of new proxy name TextField

	*initClass {
		StartUp add: {
//			CocoaMenuItem.add(["Scripts"], { this.new });
			{	// compatibility with 3.5
				GUI.qt;
				QtGUI.style = \CDE;
				font = Font.default.size_(10);
			}.defer(0.5);

		};
	}

	*new { | archivePath |
		^super.new.init(archivePath).makeWindow;
	}

	init { | argArchivePath |
		var bufferScript, scriptData, scriptLists;
		#bufferScript, scriptData = this.openScriptLists(argArchivePath);
		bufferScript !? { bufferScript.interpret };
		scriptLists = this.getValue(\scriptLists, ListAdapter());
		scriptLists.items_(nil, scriptData);
		files = this.getValue(\scripts, ListAdapter());
		files.items_(scriptLists.adapter.item);
		files.sublistOf(scriptLists);
	}

	// ========= View methods ==========

	makeWindow { | argArchivePath |
		this.stickyWindow(this.class, \scriptListGui, { | w, app |
			w.bounds = Rect(300, 50, 800, 800);
			w.layout = VLayout(
				HLayout(this.listButtons, this.scriptButtons),
				this.listAndScriptNamingDisplays,
				HLayout(
					this.listDisplay.fixedWidth_(200),
					[this.scriptDisplay, s: 5],
					[this.proxyDisplay, s: 1],
				),
				this.snippetButtons,
				this.proxyNamingDisplays,
				this.bufferMenus,
				[this.snippetViews, s: 5],	
			);
			// open current file when re-opening
			files.item !? { files.changed(\index, files); };
			this.windowClosed(w, { this.saveLists });
			ShutDown add: { this.saveLists };
		})
	}

	listButtons {
		^HLayout(
			StaticText().string_("Lists:").font_(font),
			this.makeNameButton(
				"Edit name, press 'return' key to append new list:",
				"ScriptList_", \scriptLists,
				 { | list, string | list append: this.makeList(string) }, "append"
			), 
			this.makeNameButton(
				"Edit name, press 'return' key to insert new list:",
				"ScriptList_", \scriptLists,
				 { | list, string | list insert: this.makeList(string) }, "insert"
			), 
			this.makeNameButton(
				"Edit name, press 'return' key to rename selected list:",
				{ this.getValue(\scriptLists).item.name ++ "_" }, \scriptLists,
				{ | list, string | list.item.name = string; list.changed(\list) }, "rename"
			),
			this.button(\scriptLists).action_({ | me | me.value.adapter.delete })
				.view.states_([["delete"]]).font_(font),
			Button().action_({ 
				this.init(archivePath);
				this.updateListeners;
			}).states_([["revert"]]).font_(font),
			Button().action_({ this.saveLists }).states_([["save"]]).font_(font),
		)
	}

	makeNameButton { | label, namePrefix, valueSelector, action, buttonString |
			^this.button(\nameList).toggleShow.resetOn.addAction({ | me | 
				me.value.changed(\changeTo, 
					label,
					namePrefix.value ++ Date.getDate.stamp,
					{ | namer | action.(this.getValue(valueSelector), namer.view.string) }
				)
			})
			.view.states_([[buttonString], ["cancel", Color.red, Color.black]]).font_(font);
	}

	scriptButtons {
		^HLayout(
			StaticText().string_("Scripts:").font_(font),
			this.makeNameButton(
				"Edit name, press 'return' key to append new script:",
				"Script_", \scripts,
				 { | list, string | list append: ProxyDoc(string) }, "append"
			), 
			this.makeNameButton(
				"Edit name, press 'return' key to insert new script:",
				"Script_", \scripts,
				 { | list, string | list insert: ProxyDoc(string) }, "insert"
			),
			this.button(\scripts).changedAction(\loadProxyDoc).view.states_([["load"]]).font_(font),
			this.makeNameButton(
				"Edit name, press 'return' key to rename selected list:",
				{ this.getValue(\scripts).item.name ++ "_" }, \scripts,
				{ | list, string | list.item.name = string; list.changed(\list) }, "rename"
			),
			this.button(\scripts).changedAction(\delete).view.states_([["delete"]]).font_(font),
			this.button(\scripts)
				.action_({ | me | me.item !? { me.item.proxySpace.openHistoryInDoc; } })
				.view.states_([["make doc"]]).font_(font),
			// TODO: Revisit this: Not all Scripts have paths any more. What to do?
//			this.button(\scripts)
//			.action_({ | me | me.item !? {
//				// HOW TO FIND IF THERE IS NO VALID PATH? Must change unique check to ID.
//				Document.open(me.item.path.asString);
//			} })
//			.view.states_([["open doc"]]).font_(font),
		)
	}

	listAndScriptNamingDisplays {
		^HLayout(
			[this.staticText(\nameList).showOn(show: false)
				.updateActionArray(\changeTo, { | l, n, a, me | me.view.string = l; })
				.view.font_(font),
			s: 2],
			[this.textField(\nameList).action_({}) // initialize with empty action for safety
				// string and action are sent by several buttons in first row
				.updateActionArray(\changeTo, { | label, name, action, me | 
					me.view.string = name;
					me.action = action addFunc: { | me | me.reset; me.show(false) };
				}) 
				.showOn(show: false).view.font_(font),
			s: 3],
		)
	}	

	listDisplay {
		^this.listView(\scriptLists, { | me | me.items collect: _.name }).view.font_(font)
	}

	scriptDisplay {
		^this.listView(\scripts, { | me | me.items collect: _.name })
		.updateAction(\loadProxyDoc, { | me |
			Dialog.getPaths({ | paths |
				paths do: { | p | this.loadScript(me.value.adapter, p) };
				me.value.updateListeners;
			});
		})
		.updateAction(\makeProxyDoc, { | me |
			this.makeScript(me);
		})
		.updateAction(\readDefaults, { | me | this.readDefaults(me.value.adapter) })
		.updateAction(\openAll, { | me |
			me.value.adapter.items do: _.open;
		})
		.updateAction(\openSelected, { | me |
			me.value.adapter.item !? { me.value.adapter.item.open }
		})
		.updateAction(\play, { | me |
			me.value.adapter.item !? { me.value.adapter.item.play }
		})
		.updateAction(\delete, { | me | me.value.adapter.delete(me); })
		.updateAction(\close, { | me | me.value.adapter.item.close })
		.view.font_(font)
	}

	proxyDisplay {
		var proxyDoc; // store previous ProxyDoc to remove notifier
		^this.listView(\proxies, { | me |
			me.items collect: _.name;
		}).sublistOf(\scripts, { | argProxyDoc, me |
			if (proxyDoc.notNil) { me.removeNotifier(proxyDoc.proxySpace, \list); };
			proxyDoc = argProxyDoc;
			if (proxyDoc.notNil) {
				me.addNotifier(proxyDoc.proxySpace, \list, {
					me.value.changed(\proxies, proxyDoc.proxyItems[1..]);
				});
				proxyDoc.proxyItems[1..];
			}{ [] };
		})
		.updateAction(\proxies, { | proxies, me | me.items = proxies; })
		.view.font_(font);
	}

	snippetButtons {
		^HLayout(
			this.button(\snippets)
			.action_({ | me |
				var proxyItem;
				proxyItem = this.getValue(\proxies).item;
				if (proxyItem.isNil) {
					"Please (create and/or) select a proxy to start editing snippets".postln;
				}{
					me.item ?? {
						me.value.adapter.append(me, this.defaultSnippet(proxyItem))
					};
					snippetViews.index = me.view.value;
					if (me.view.value == 0) { me.value.changed(\replace); }
				};
			})
			.view.font_(font).states_([["edit", nil, Color.yellow], ["save", Color.red]]),
			this.button(\snippets)
				.action_({ | me |
					var proxyItem;
					proxyItem = this.getValue(\proxies).item;
					proxyItem !? {
						proxyItem.evalSnippet(
							me.getString,
							start: false,
							addToSourceHistory: proxyItem.history.size == 0
						)
					}
				})
				.view.font_(font).states_([["eval"]]),
			this.button(\proxies).proxyWatcher
				.view.font_(font).states_([["start", nil, Color.green], ["stop", nil, Color.red]]),
			this.button(\snippets).insert({ | me | me.getString })
				.view.font_(font).states_([["insert"]]),
			this.button(\snippets).append({ | me | me.getString })
				.view.font_(font).states_([["append"]]),
			this.button(\snippets).delete.view.font_(font).states_([["delete snippet"]]),
			this.button(\proxies)
				.toggleShow.resetOn.view.font_(font).states_([["new proxy"], ["cancel"]]),
			this.button(\proxies).action_({ | me |
				me.item !? { me.item delete: this.proxySpace; };
			}).view.font_(font).states_([["delete proxy"]]),
			this.button(\proxies).action_({ | me |
				"NOT YET IMPLEMENTED".postln;
			}).view.font_(font).states_([["rename proxy"]]),
			this.button(\proxies) // switch to performance gui in StackLayout snippetViews?
				.action_({ | me |
					ProxyCodeEditor(this.getValue(\scripts).item.proxySpace, me.item);
				})
				.view.font_(font).states_([["gui"]]),
			this.button(\proxies).action_({ | me |
				me.items do: { | p | ProxyCodeEditor(this.getValue(\scripts).item.proxySpace, p); };
			}).view.font_(font).states_([["gui*"]]),
			this.button(\scripts).action_({ | me |
				me.item !? { ProxyCodeMixer3(me.item.proxySpace) }
			}).view.font_(font).states_([["mixer"]]),
		);		
	}

	proxyNamingDisplays {
		^HLayout(
			[this.staticText(\proxies).showOn(show: false).view.font_(font)
				.string_("Enter name of new proxy (type 'return' to create):"), s: 2],
			[this.textField(\proxies)
				.action_({ | me |
					this.makeProxy(me.view.string);	// make proxy
					me.show(false);				// hide myself + my static text label
					me.value.changed(\reset);		// reset button to state "new proxy"
					me.last;
				})
				.showOn(show: false).view.font_(font).string_("out"), s: 3],
		)
	}	

	bufferMenus {
		^nil		
	}

	snippetViews {
		^snippetViews = StackLayout(
			this.listView(\snippets).sublistOf(\proxies, { | me | me !? {
					if (me.history.size == 0) { me.addSnippet(this.defaultSnippet(me)) };
					me.history;
				}; })
				.view.font_(Font("Monaco", 10)),
			this.listItem(\snippets, TextView().background_(Color(0.9, 0.95, 0.95))).makeStringGetter
				.appendOn.insertOn.replaceOn.view.font_(Font("Monaco", 10)),
		)
	}

	defaultSnippet { | proxyItem |
		^format(
	"//:% [rate: [2, 20]]\n{ | rate = 5 | WhiteNoise.ar(Decay.kr(Impulse.kr(rate, 0, 0.1))) }",			if (proxyItem.isNil) { "out" } { proxyItem.name }
		)
	}

	// ========= Data methods ==========

	openScriptLists { | argArchivePath |
		var data, bufferScript, scriptLists, defaultList;
		archivePath = argArchivePath ?? { Platform.userAppSupportDir +/+ "Scripts.sctxar"; };
		data = Object.readArchive(archivePath);
		data !? { #bufferScript, scriptLists = data };
		scriptLists do: _.restoreFromArchive;
		^[bufferScript, scriptLists ?? { [this.makeList] }];
	}

	makeList { | argName |
		^ScriptList().name_(argName ?? { format("Scripts_%", Date.getDate.stamp) });
	}

	loadScript { | scriptList, path | 
		path = path.asSymbol;
		if (scriptList.detect({ | s | s.path === path }).isNil) {
			scriptList add: ProxyDoc(path, readFromDoc: true);
		}
	}

	makeScript { | widget | // TODO: Replace extra window by listAndScriptNamingDisplays
		widget.postln;
		widget.value.postln;
		widget.value.adapter.postln;
		AppModel().window({ | w, app |
			w.layout_(
				VLayout(
					StaticText().string_("Type name of new script, then 'return' key to create"),
					app.textField(\name)
					.string_("Script"++Date.getDate.stamp)
					.action_({ | me |
						widget.value.adapter add: ProxyDoc(me.view.string);
						widget.value.updateListeners;
						w.close;
					}).view,
					Button().action_({ w.close }).states_([["CANCEL"]]),
				)
			)
		});		
	}

	proxySpace { ^this.getValue(\scripts).item.proxySpace }

	makeProxy { | argName |
		var script;
		script = this.getValue(\scripts).item;
		if (script.notNil) {
			script.proxySpace.at(argName.asSymbol);
		}{
			"Please choose a script to add the new proxy in".postln;
		}
	}

	saveLists {
		[this.bufferScript, this.makeArchiveList].writeArchive(archivePath);
		postf("Script lists saved to: \n%\n", archivePath);
	}
	
	bufferScript {
		^BufferItem.makeLoadBuffersString;
	}
	
	makeArchiveList {
		^this.getValue(\scriptLists).adapter.items.collect(_.makeArchiveCopy);
	}
}