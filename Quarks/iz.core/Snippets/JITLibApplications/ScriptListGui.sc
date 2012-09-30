/* iz Fri 28 September 2012  9:18 PM EEST

Store lists of ProxyCode-style script files. 
The lists are stored in Platform.userAppSupportDir +/+ "Scripts.sctxar", together with the scripts themselves. Scripts edited through ProxyCodeEditor are thus automatically saved there. 

*/
ScriptListGui : AppModel {
	classvar <>font;

	var <>archivePath;
	var <files; // Value holding current files list;

	*initClass {
		StartUp add: {
			CocoaMenuItem.add(["Scripts"], { this.new });
			font = Font.default.size_(10);
		};
	}

	*new { | archivePath |
		^super.new.init(archivePath).makeWindow;
	}

	init { | argArchivePath |
		var scriptLists;
		scriptLists = this.getValue(\scriptLists, ListAdapter());
		scriptLists.items_(nil, this.openScriptLists(argArchivePath));
		files = this.getValue(\scripts, ListAdapter());
		files.items_(scriptLists.adapter.item);
		files.sublistOf(scriptLists);
	}

	openScriptLists { | argArchivePath |
		var scriptLists, defaultList;
		archivePath = argArchivePath ?? { Platform.userAppSupportDir +/+ "Scripts.sctxar"; };
		scriptLists = Object.readArchive(archivePath);
		scriptLists do: _.restoreFromArchive;
		^scriptLists ?? { [this.makeList] };
	}

	makeList { | argName |
		^ScriptList().name_(argName ?? { format("Scripts_%", Date.getDate.stamp) });
	}

	addScript { | scriptList, path | 
		path = path.asSymbol;
		if (scriptList.detect({ | s | s.path === path }).isNil) {
			scriptList add: ProxyDoc(path, readFromDoc: true);
		}
	}

	makeWindow { | argArchivePath |
		this.stickyWindow(this.class, \scriptListGui, { | w, app |
//			w.bounds = Rect(400, 400, 1040, 650);
			w.layout = VLayout(
				HLayout(this.listButtonRow, this.fileButtonRow, this.scriptButtonRow),
				HLayout(
					VLayout(this.selectedListDisplay.fixedWidth_(200), 
						this.listListDisplay.fixedWidth_(200)
					),
					[VLayout(this.scriptListDisplay), s: 5],
					[this.proxyDisplay, s: 1],
				),
				this.scripItemsRow,
				this.scriptDisplay,				
			);
			// open current file when re-opening
			files.item !? { files.notify(\index, files); };
			this.windowClosed(w, { this.saveLists });
			ShutDown add: { this.saveLists };
		})
	}

	listButtonRow {
		^HLayout(
			StaticText().string_("Lists:").font_(font),
			this.button(\scriptLists).notifyAction(\append).view.states_([["append"]]).font_(font),
			this.button(\scriptLists).notifyAction(\insert).view.states_([["insert"]]).font_(font),
			this.button(\scriptLists).notifyAction(\rename).view.states_([["rename"]]).font_(font),
			this.button(\scriptLists).action_({ | me | me.value.adapter.delete })
				.view.states_([["delete"]]).font_(font),
			Button().action_({ 
				this.init(archivePath);
				this.updateListeners;
			}).states_([["revert"]]).font_(font),
			Button().action_({ this.saveLists }).states_([["save"]]).font_(font),
		)
	}

	selectedListDisplay {
		^this.listItem(\scriptLists, TextField(), { | me |
			me.value.adapter.item !? { me.value.adapter.item.name; }
		})
		.updateAction(\rename, { | sender, me | 
			me.value.adapter.item.name = me.view.string;
			me.value.updateListeners;
		})
		.append({ this.makeList })
		.appendOn({ this.makeList })
		.insertOn({ this.makeList }).view.font_(font)
	}

	listListDisplay {
		^this.listView(\scriptLists, { | me |
			me.value.adapter.items collect: _.name
		})
		.view.font_(font)
	}

	fileButtonRow {
		^HLayout(
			StaticText().string_("Scripts:").font_(font),
			this.button(\scripts).notifyAction(\readNew).view.states_([["load ..."]]).font_(font),
			this.button(\scripts)
				.action_({ | me | me.item !? { me.item.proxySpace.openHistoryInDoc; } })
				.view.states_([["make doc"]]).font_(font),
			this.button(\scripts)
//			.notifyAction(\makeDoc)
			.action_({ | me | me.item !? { Document.open(me.item.path.asString); } })
			.view.states_([["open doc"]]).font_(font),
			this.button(\scripts).notifyAction(\delete).view.states_([["delete"]]).font_(font),
		)
	}

	scriptButtonRow {
		^HLayout(
			StaticText().string_("Proxies:").font_(font),
			this.button(\proxies).action_({ | me |
				me.item !? {
					ProxyCodeEditor(this.getValue(\scripts).item.proxySpace, me.item); 
				}
			}).view.font_(font).states_([["edit selected"]]),
			this.button(\proxies).action_({ | me |
				me.items do: { | p | ProxyCodeEditor(this.getValue(\scripts).item.proxySpace, p); };
			}).view.font_(font).states_([["edit all"]]),
			this.button(\scripts).action_({ | me |
				me.item !? { ProxyCodeMixer3(me.item.proxySpace) }
			}).view.font_(font).states_([["mixer"]])
		)
	}

	scriptListDisplay {
		^this.listView(\scripts, { | me | me.items collect: _.path })
		.updateAction(\readNew, { | me |
			Dialog.getPaths({ | paths |
				paths do: { | p | this.addScript(me.value.adapter, p) };
				me.value.updateListeners;
			});
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
		^this.listView(\proxies, { | me | me.items collect: _.name }).sublistOf(\scripts, { | me | 
			me !? { me.proxyItems /* collect: _.name */ }
		}).view.font_(font);
	}

	scripItemsRow { ^nil }
	scriptDisplay { ^nil }
	
	saveLists {
		this.getValue(\scriptLists).adapter.items.collect(_.makeArchiveCopy).writeArchive(archivePath);
		postf("Script lists saved to: \n%\n", archivePath);
	}
	
}