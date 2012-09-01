/* IZ Sat 01 September 2012  6:47 PM EEST

Redoing BufferListGui with AppModel2

UNDERWAY!

*/


BufferListGui2 : AppModel2 {

	var <>archivePath;
	var bufferLists, bufList1; // bufList1 gets initialized with defaults if new

	*initClass {
		StartUp add: {
			CocoaMenuItem.add(["Buffers"], { this.new });
			 
		} 
	}

	*new { | archivePath |
		^super.new.makeWindow(archivePath);
	}
	
	loadBufferLists { | me |
		var bufferLists;
		bufferLists = Object.readArchive(Platform.userAppSupportDir +/+ archivePath);
		bufferLists !? { bufferLists do: { | b | b do: _.rebuild } };
		bufferLists = bufferLists ?? { ListAdapter2(me.value) };
		if (bufferLists.size == 0) {
			bufferLists.add(
				NamedListAdapter(this.getValue(\buffers))
					.name_(Date.getDate.format("Buffer List %c"));
			);
			bufList1 = bufferLists.first;
			(Platform.resourceDir +/+ "sounds/*").pathMatch do: { | path |
				bufList1.add(path);
			};
		};
		
	}

	makeWindow { | argArchivePath |
		archivePath = argArchivePath ?? { Platform.userAppSupportDir +/+ "BufferLists.sctxar"; };
		this.stickyWindow(this.class, \bufferListGui, { | w, app |
			w.bounds = Rect(400, 100, 640, 650);
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
				app.listView(\bufferLists, { | me | me.value.adapter.items collect: _.name })
					.do({ | me | // load buffer list
						me.value.adapter = this.loadBufferLists(me);
					})
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
			app.windowClosed(w, {
				app.getValue(\bufferLists).adapter.items.writeArchive(archivePath);
				postf("Buffer lists saved to: \n%\n", archivePath);
			})
		})
	}
}
