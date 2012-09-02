			w.layout = VLayout(
				HLayout(
					StaticText().string_("Lists:"),
					app.button(\bufferLists).getContents(\itemEdit, \append,
						{ | string | string ++ Date.getDate.format(" %c") }
					).view.states_([["add list"]]),
					app.button(\bufferLists).getContents(\itemEdit, \rename)
						.view.states_([["rename list"]]),
					app.button(\bufferLists).getContents(\itemEdit, \delete)
						.view.states_([["delete list"]]),
					Button().states_([["save all"]]).action_({
						app.getAdapter(\bufferLists).adapter.items.save;
					}),
				),
				app.textField(\bufferLists).list
					.name_(\itemEdit).view,
				app.listView(\bufferLists, bufferLists)
					.addAction({ | adapter |
						app.getAdapter(\buffers).adapter.items_(adapter.adapter.item)
					}).view,
				HLayout(
					StaticText().string_("Buffers:"),
					app.button(\buffers, { | widget |
						Dialog.openPanel({ | path |
							widget.adapter.adapter.items add: path;
							widget.adapter.updateListeners;
						});
					}).view.states_([["load new"]]),
					Button().states_([["load all"]]).action_({
						var list;
						list = app.getAdapter(\bufferLists).adapter.item;
						if (list.isNil) {
							"Please choose a buffer list to load first".postln;
						}{
							if (list.size == 0) { "This list contains no buffers".postln; };
							list do: _.load
						}
					}),
					app.button(\buffers, { | widget |
						widget.adapter.adapter.item.load;
					}).view.states_([["load selected"]]),
					app.button(\buffers, { | widget |
						widget.adapter.adapter.item.play;
					}).view.states_([["play"]]),
					app.button(\buffers, { | widget |
						var buffer;
						buffer = widget.adapter.adapter.item;
						buffer.free;
						widget.adapter.adapter remove: buffer;
					}).view.states_([["delete"]]),
					app.button(\buffers, { | widget |
						widget.adapter.adapter.item.free;
					}).view.states_([["free"]]),
				),
				app.listView(\buffers).view.font_(Font.default.size_(10)),
				HLayout(
					VLayout(
						StaticText().string_("Currently loaded buffers:"),
						Button().states_([["load defaults"]]).action_({
							var list;
							list = app.getAdapter(\bufferLists).adapter.item;
							(Platform.resourceDir +/+ "sounds/*").pathMatch do: { | path |
								list.add(path);
							};
							this.updateListeners;
						})
					),
					app.listView(\currentlyLoaded)
						.updateAction_({ | view, sender, adapter |
							view.items = adapter.adapter.items collect: { | b | 
								format("% : %", b, Library.at('Buffers', b).minSec)
							};
						})
						.addNotifier(BufferItem, \bufferList, { | list |
							app.getAdapter(\currentlyLoaded).adapter.items = list;
						})
						.view.font_(Font.default.size_(10)),
				)
			);
