/* IZ Sun 23 September 2012  3:53 PM EEST

Manage lists of sound file paths, saving these in user app support dir.
Selectively load files into buffers. 
Display sound file contents in SoundFileView. 
Add functionality for analysis and manipulation. 

Based on BufferListGui, but with compact list views to allow space for sound file display and other functionality. 

*/


SoundFileGui : AppModel {

	classvar <>font;

	var <>archivePath;
	var <buffers; // Value holding current Buffers list;

	*initClass {
		StartUp add: {
			CocoaMenuItem.add(["Sound File Manager"], { this.new });
			font = Font.default.size_(10);
		};
	}

	*new { | archivePath |
		^super.new.init(archivePath).makeWindow;
	}

	init { | argArchivePath |
		var bufferLists;
		bufferLists = this.getValue(\bufferLists, ListAdapter());
		bufferLists.items_(nil, this.loadBufferLists(argArchivePath));
		buffers = this.getValue(\buffers, ListAdapter());
		buffers.items_(bufferLists.adapter.item);
		buffers.sublistOf(bufferLists);
	}

	loadBufferLists { | argArchivePath |
		var bufferLists, defaultList;
		archivePath = argArchivePath ?? { Platform.userAppSupportDir +/+ "BufferLists.sctxar"; };
		bufferLists = Object.readArchive(archivePath);
		bufferLists !? { bufferLists do: { | bl | bl do: _.rebuild; }};
		^bufferLists ?? { [this.makeList] };
	}

	makeList { ^NamedList().name_(Date.getDate.format("Buffer List %c")) }

	readDefaults { | bufferList |
		(Platform.resourceDir +/+ "sounds/*").pathMatch do: { | path |
			this.addBuffer(bufferList, BufferItem(path));
		};
		bufferList.container.updateListeners;	
	}

	addBuffer { | bufferList, buffer | if (bufferList.includes(buffer).not) { bufferList add: buffer } }

	makeWindow { | argArchivePath |
		this.stickyWindow(this.class, \bufferListGui, { | w, app |
			w.bounds = Rect(0, 400, 1200, 650);
			w.layout = VLayout(
				HLayout(
					VLayout(this.listButtonRow, this.selectedListDisplay, this.listListDisplay),
					[VLayout(this.fileButtonRow, this.fileListDisplay), s: 2],
					[this.bufferDisplay, s: 1],
				),
				// Here will follow the file display and functionality items
				
			);
			this.windowClosed(w, { this.saveLists });
			ShutDown add: { this.saveLists };
		})
	}

	listButtonRow {
		^HLayout(
			StaticText().string_("Lists:").font_(font),
			this.button(\bufferLists).notifyAction(\append).view.states_([["append"]]).font_(font),
			this.button(\bufferLists).notifyAction(\insert).view.states_([["insert"]]).font_(font),
			this.button(\bufferLists).notifyAction(\rename).view.states_([["rename"]]).font_(font),
			this.button(\bufferLists).action_({ | me | me.value.adapter.delete })
				.view.states_([["delete"]]).font_(font),
			Button().action_({ 
				this.init(archivePath);
				this.updateListeners;
			}).states_([["revert"]]).font_(font),
			Button().action_({ this.saveLists }).states_([["save"]]).font_(font),
		)
	}

	selectedListDisplay {
		^this.listItem(\bufferLists, TextField(), { | me | me.value.adapter.item.name; })
			.updateAction(\rename, { | sender, me | 
				me.value.adapter.item.name = me.view.string;
				me.value.updateListeners;
			})
			.append({ this.makeList })
			.appendOn({ this.makeList })
			.insertOn({ this.makeList }).view.font_(font)
	}

	listListDisplay {
		^this.listView(\bufferLists, { | me | me.value.adapter.items collect: _.name })
		.view.font_(font)
	}

	fileButtonRow {
		^HLayout(
			StaticText().string_("Sound Files:").font_(font),
			this.button(\buffers).notifyAction(\readNew).view.states_([["read new"]]).font_(font),
			this.button(\buffers).notifyAction(\readDefaults)
			.view.states_([["read defaults"]]).font_(font),
			this.button(\buffers).notifyAction(\loadSelected)
			.view.states_([["load selected"]]).font_(font),
			this.button(\buffers).notifyAction(\loadAll).view.states_([["load all"]]).font_(font),
			this.button(\buffers).notifyAction(\play).view.states_([["play"]]).font_(font),
			this.button(\buffers).notifyAction(\delete).view.states_([["delete"]]).font_(font),
		)
	}

	fileListDisplay {
		^this.listView(\buffers, { | me | me.value.adapter.items collect: _.name })
			.updateAction(\readNew, { | me |
				Dialog.getPaths({ | paths |
					paths do: { | p | this.addBuffer(me.value.adapter, BufferItem(p)) };
					me.value.updateListeners;
				});
			})
			.updateAction(\readDefaults, { | me | this.readDefaults(me.value.adapter) })
			.updateAction(\loadAll, { | me |
				me.value.adapter.items do: _.load;
			})
			.updateAction(\loadSelected, { | me |
				me.value.adapter.item !? { me.value.adapter.item.load }
			})
			.updateAction(\play, { | me |
				me.value.adapter.item !? { me.value.adapter.item.play }
			})
			.updateAction(\delete, { | me | me.value.adapter.delete(me); })
			.updateAction(\free, { | me | me.value.adapter.item.free })
			.view.font_(font)
	}

	bufferDisplay {
		^GridLayout.rows(
			[this.bufferListHeader, StaticText().string_("Buffer actions:").font_(font)],
			[this.loadedBuffersList, this.bufferActionList]
		)
	}

	bufferListHeader {
		^HLayout(
			StaticText().string_("Loaded buffers:").font_(font),
			this.button(\loadedBuffers).notifyAction(\free).view.states_([["free"]]).font_(font),
		)
	}

	loadedBuffersList {
		^this.listView(\loadedBuffers).updater(BufferItem, \bufferList, { | me, names |
			me.value.adapter.items_(nil, names);
		})
		.items_(Library.at['Buffers'].keys.asArray.sort)
		.updateAction(\free, { | me |
			Library.at('Buffers', me.value.adapter.item).free;
		})
		.view.font_(font)
	}

	bufferActionList {
		^this.listView(\bufferActions).items_(["dummy 1", "dummy 2", "dummy 3"]).view.font_(font)
	}
/*	
	playBufferTextView {
		^this.textView(\loadedBuffers).listItem({ | me |
			format(this.bufferPlayCode, *me.value.adapter.item.dup(2))
		})
			.updateAction(\play, { | me, widget | widget.view.string.interpret })
			.view.font_(Font("Monaco", 10))
	}
*/
	saveLists {
		this.getValue(\bufferLists).adapter.items.writeArchive(archivePath);
		postf("Buffer lists saved to: \n%\n", archivePath);
	}

	bufferPlayCode {
		^
"var buffer;
buffer = '%'.b;
Ndef('%', {
	PlayBuf.ar(buffer.numChannels, buffer)
}).play;"
	}
}
