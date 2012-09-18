/* IZ Sat 01 September 2012  6:47 PM EEST

Revision: Wed 05 September 2012  1:55 PM BST

BufferListGui();

*/


BufferListGui : AppModel {

	var <>archivePath;
	var <buffers; // Value holding current Buffers list;

	*initClass {
		StartUp add: { CocoaMenuItem.add(["Buffers"], { this.new }); };
	}

	*new { | archivePath |
		^super.new.init(archivePath).makeWindow;
	}

	init { | argArchivePath |
		var bufferLists;
		bufferLists = this.getValue(\bufferLists, ListAdapter2());
		bufferLists.items_(nil, this.loadBufferLists(argArchivePath));
		buffers = this.getValue(\buffers, ListAdapter2());
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
			w.bounds = Rect(300, 100, 840, 650);
			w.layout = VLayout(
				HLayout(
					StaticText().string_("Lists:"),
					app.button(\bufferLists).notifyAction(\append).view.states_([["append"]]),
					app.button(\bufferLists).notifyAction(\insert).view.states_([["insert"]]),
					app.button(\bufferLists).notifyAction(\rename).view.states_([["rename"]]),
					app.button(\bufferLists).action_({ | me | me.value.adapter.delete })
						.view.states_([["delete"]]),
					Button().action_({ 
						this.init(archivePath);
						this.updateListeners;
					}).states_([["revert"]]),
					Button().action_({ this.saveLists }).states_([["save"]]),
				),
				app.listItem(\bufferLists, TextField(), { | me | me.value.adapter.item.name; })
				.updateAction(\rename, { | sender, me | 
					me.value.adapter.item.name = me.view.string;
					me.value.updateListeners;
				})
				.append({ this.makeList })
				.appendOn({ this.makeList })
				.insertOn({ this.makeList }).view,
				app.listView(\bufferLists, { | me | me.value.adapter.items collect: _.name }).view,
				HLayout(
					StaticText().string_("Buffers:"),
					app.button(\buffers).notifyAction(\readNew).view.states_([["read new"]]),
					app.button(\buffers).notifyAction(\readDefaults)
					.view.states_([["read defaults"]]),
					app.button(\buffers).notifyAction(\loadSelected)
					.view.states_([["load selected"]]),
					app.button(\buffers).notifyAction(\loadAll).view.states_([["load all"]]),
					app.button(\buffers).notifyAction(\play).view.states_([["play"]]),
					app.button(\buffers).notifyAction(\delete).view.states_([["delete"]]),
					app.button(\buffers).notifyAction(\free).view.states_([["free"]]),
				),
				app.listView(\buffers, { | me | me.value.adapter.items collect: _.name })
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
					.view,
				HLayout(
					StaticText().string_("Loaded buffers:"),
					app.button(\loadedBuffers).notifyAction(\free).view.states_([["free"]]),
					app.button(\loadedBuffers).notifyAction(\play).view
						.states_([["run code with buffer:"]])
				),
				HLayout(
					[app.listView(\loadedBuffers).updater(BufferItem, \bufferList, { | me, names |
						me.value.adapter.items_(nil, names);
					})
						.do({ | me | 
							me.value.adapter.items_(nil, Library.at['Buffers'].keys.asArray.sort);
						})
						.updateAction(\free, { | me |
							Library.at('Buffers', me.value.adapter.item).free;
						})
						.view, s: 1],
					[app.textView(\loadedBuffers).listItem({ | me |
						format(this.bufferPlayCode, *me.value.adapter.item.dup(2))
					})
						.updateAction(\play, { | me, widget | widget.view.string.interpret })
						.view.font_(Font("Monaco", 10)), 
					s: 3]
				)
			);
			app.windowClosed(w, { this.saveLists });
			ShutDown add: { this.saveLists };
		})
	}

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
