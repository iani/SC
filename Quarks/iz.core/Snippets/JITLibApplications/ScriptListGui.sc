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
		files = this.getValue(\files, ListAdapter());
		files.items_(scriptLists.adapter.item);
		files.sublistOf(scriptLists);
	}

	openScriptLists { | argArchivePath |
		var scriptLists, defaultList;
		archivePath = argArchivePath ?? { Platform.userAppSupportDir +/+ "Scripts.sctxar"; };
		scriptLists = Object.readArchive(archivePath);
		^scriptLists ?? { [this.makeList] };
	}

	makeList { | argName |
		^NamedList().name_(argName ?? { format("Scripts_%", Date.getDate.stamp) });
	}

	addScript { | scriptList, script | if (scriptList.includes(script).not) { scriptList add: script } }

	makeWindow { | argArchivePath |
		this.stickyWindow(this.class, \scriptListGui, { | w, app |
//			w.bounds = Rect(400, 400, 1040, 650);
			w.layout = VLayout(
				HLayout(
					VLayout(this.listButtonRow, this.selectedListDisplay, this.listListDisplay),
					[VLayout(this.fileButtonRow, this.fileListDisplay), s: 2],
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
			this.button(\files).notifyAction(\readNew).view.states_([["add ..."]]).font_(font),
			this.button(\files).notifyAction(\openSelected)
			.view.states_([["open selected"]]).font_(font),
			this.button(\files).notifyAction(\openAll).view.states_([["open all"]]).font_(font),
			this.button(\files).notifyAction(\delete).view.states_([["delete"]]).font_(font),
		)
	}

	fileListDisplay {
		^this.listView(\files, { | me |
			me.value.adapter.items collect: _.name
		})
			.updateAction(\readNew, { | me |
				Dialog.getPaths({ | paths |
					paths do: { | p | this.addScript(me.value.adapter, ScriptItem(p)) };
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

	scripItemsRow { ^nil }
	scriptDisplay { ^nil }
	
	
}