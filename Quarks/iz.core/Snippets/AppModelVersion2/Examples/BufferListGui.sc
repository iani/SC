/* IZ Sat 01 September 2012  6:47 PM EEST

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
		buffers = this.getValue(\buffers);
		buffers.sublistOf(\bufferLists);
		this.getValue(\bufferLists)
			.adapter_(
				ListAdapter2().items_(nil, this.loadBufferLists(argArchivePath))
			);
	}
	
	loadBufferLists { | argArchivePath |
		var bufferLists, defaultList;
		archivePath = argArchivePath ?? { Platform.userAppSupportDir +/+ "BufferLists.sctxar"; };
		bufferLists = Object.readArchive(archivePath);
		bufferLists !? { bufferLists do: { | bl | bl do: _.rebuild; }};
		if (bufferLists.size == 0) {
			defaultList = NamedListAdapter(buffers).name_(Date.getDate.format("Buffer List %c"));
			bufferLists = bufferLists add: defaultList;
			this.readDefaults(defaultList);
		};
		^bufferLists;
	}

	readDefaults { | bufferList |
		(Platform.resourceDir +/+ "sounds/*").pathMatch do: { | path |
			this.addBuffer(bufferList, (BufferItem(path)));
		};
		bufferList.container.updateListeners;	
	}

	addBuffer { | bufferList, buffer | if (bufferList.includes(buffer).not) { bufferList add: buffer } }

	makeWindow { | argArchivePath |
		var newListFunc;
		newListFunc = { | me |
			NamedListAdapter(buffers).name_(me.view.string ++ Date.getDate.format(" %c"))
		};
		this.stickyWindow(this.class, \bufferListGui, { | w, app |
			w.bounds = Rect(300, 100, 840, 650);
			w.layout = VLayout(
				HLayout(
					StaticText().string_("Lists:"),
					app.button(\bufferLists).notify(\append).view.states_([["append"]]),
					app.button(\bufferLists).notify(\insert).view.states_([["insert"]]),
					app.button(\bufferLists).notify(\rename).view.states_([["rename"]]),
					app.button(\bufferLists).action_({ | me | me.value.adapter.delete })
						.view.states_([["delete"]]),
					Button().action_({ 
						this.init(archivePath);
						this.updateListeners;
					 }).states_([["revert"]])
				),
				app.listItem(\bufferLists, TextField(), { | me | me.value.adapter.item.name; })
				.updateAction(\rename, { | sender, me | 
					me.value.adapter.item.name = me.view.string;
					me.value.updateListeners;
				})
				.append(newListFunc)
				.appendOn(newListFunc)
				.insertOn(newListFunc).view,
				app.listView(\bufferLists, { | me | me.value.adapter.items collect: _.name }).view,
				HLayout(
					StaticText().string_("Buffers:"),
					app.button(\buffers).notify(\readNew).view.states_([["read new"]]),
					app.button(\buffers).notify(\readDefaults).view.states_([["read defaults"]]),
					app.button(\buffers).notify(\loadSelected).view.states_([["load selected"]]),
					app.button(\buffers).notify(\loadAll).view.states_([["load all"]]),
					app.button(\buffers).notify(\play).view.states_([["play"]]),
					app.button(\buffers).notify(\delete).view.states_([["delete"]]),
					app.button(\buffers).notify(\free).view.states_([["free"]]),
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
					app.button(\loadedBuffers).notify(\free).view.states_([["free"]]),
					app.button(\loadedBuffers).notify(\play).view
						.states_([["run code with buffer:"]])
				),
				HLayout(
					[app.listView(\loadedBuffers).updater(BufferItem, \bufferList, { | me, names |
						me.value.adapter.items_(nil, names);
					})
						.do({ | me | 
							me.value.adapter.items = Library.at['Buffers'].keys.asArray.sort 
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
			app.windowClosed(w, {
				app.getValue(\bufferLists).adapter.items.writeArchive(archivePath);
				postf("Buffer lists saved to: \n%\n", archivePath);
			});
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
