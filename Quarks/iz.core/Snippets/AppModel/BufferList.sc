/* IZ Wed 29 August 2012 12:57 PM EEST


Managing lists of buffers. Save the lists to an object archive inside use app support dir. 
Add and remove buffers from a list.
Add, remove, and rename lists.

Buffers are always loaded to the default server. 
Buffers are accessible by the name of the file from which they were loaded, without extension, as a symbol, sending it message 'b'. Example:  

BufferListGui(); // create the gui

// Buffers from lists are not loaded automatically. Use buttons "load list" or "play" to do that.

// Then: 

\SinedPink.b.play


*/


BufferList : ItemList { 
	defaultItemClass { ^NamedBuffer }
	
	add { | path |
		var item;
		item = itemClass.new(path);
		if (array.detect({ | b | b == item}).isNil) { array = array add: item }
	}
}

NamedBuffer : Buffer {
	*initClass {
		StartUp add: {
			ServerBoot.add({
				Library.at('NamedBuffers') do: _.load;
			}, Server.default)
		}
	}

	name { ^path }
	asString { ^path }
	== { | item |
		if (item.isNil) {
			^true
		}{
			^path == item.path
		}
	}


	*new { | path |
		var found;
		found = Library.at('NamedBuffers', this.symbolName(path));
		if (found.notNil) {
			^found;
		}{
			^this.read(Server.default, path, action: { | buffer |
				buffer.postInfo;
				{ buffer.storeInLibrary; }.defer;
			});
		}
	}

	postInfo { postf("% : % \n", this.minSec, this.symbolName) }
	
	minSec {
		var seconds;
		seconds = numFrames / sampleRate round: 0.01;
		^format("% min, % sec", seconds / 60 round: 1, seconds % 60);
	}
	
	play {
		Library.at('NamedBuffers', this.symbolName) !? { ^super.play };
		this.allocRead(path, completionMessage: {
			super.play;
			this.storeInLibrary;
		});
	}
	
	load {
		this.allocRead(path, completionMessage: {
			this.storeInLibrary;
			this.postInfo;
//			this.updateInfo; // does this cause problems? superfluous?
		});
	}
	
	free {
		super.free;
		Library.put('NamedBuffers', this.symbolName, nil);
		this.updateLists;
	}

	updateLists { this.class.notify(\bufferList, [Library.at('NamedBuffers').keys.asArray.sort]); } 

	storeInLibrary { 
		Library.put('NamedBuffers', this.symbolName, this);
		this.updateLists;
	}
	symbolName { ^this.class.symbolName(path) }
	*symbolName { | path | ^PathName(path).fileNameWithoutExtension.asSymbol }
	*openPanel { | doneFunc |
		Dialog.openPanel({ | path | doneFunc.(this.new(path)); });
	}
	
}


BufferListGui : AppModel {
	var listArchivePath = "BufferLists.sctxar";
	var bufferLists, bufList1;

	*initClass {
		StartUp add: {
			CocoaMenuItem.add(["Buffers"], { this.new })
		}
	}

	*new {  ^super.new.makeWindow }
	
	makeWindow {
		this.stickyWindow(this.class, \bufferListGui, { | w, app |
			bufferLists = Object.readArchive(Platform.userAppSupportDir +/+ listArchivePath);
			bufferLists = bufferLists ?? { ItemList("BufferLists.sctxar").itemClass_(BufferList) };
			if (bufferLists.size == 0) {
				bufferLists.add(Date.getDate.format("Buffer List %c"));
				bufList1 = bufferLists.first;
				(Platform.resourceDir +/+ "sounds/*").pathMatch do: { | path |
					bufList1.add(path);
				};
			};
			w.bounds = Rect(400, 100, 640, 650);
			w.layout = VLayout(
				HLayout(
					StaticText().string_("Lists:"),
					app.button(\bufferLists).getContents(\itemEdit, \append)
						.view.states_([["add list"]]),
					app.button(\bufferLists).getContents(\itemEdit, \rename)
						.view.states_([["rename list"]]),
					app.button(\bufferLists).getContents(\itemEdit, \delete)
						.view.states_([["delete list"]]),
					Button().states_([["save all"]]).action_({
						app.getAdapter(\bufferLists).adapter.items.save;
					}),
				),
				app.textField(\bufferLists).list
					.initValue(Date.getDate.format("Buffer List %c"))
					.name_(\itemEdit).view,
				app.listView(\bufferLists, bufferLists)
					.addAction({ | adapter |
						app.getAdapter(\buffers).adapter.items_(adapter.adapter.item)
					}).view,
				HLayout(
					StaticText().string_("Selected list:"),
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
						.addNotifier(NamedBuffer, \bufferList, { | list |
							app.getAdapter(\currentlyLoaded).adapter.items = list
						})
						.view.font_(Font.default.size_(10)),
				)
			);
			app.windowClosed(w, {
				app.getAdapter(\bufferLists).adapter.items.save;
			})
		})
	}
}
