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
	var <files; // Value holding current files list;

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
		files = this.getValue(\files, ListAdapter());
		files.items_(bufferLists.adapter.item);
		files.sublistOf(bufferLists);
	}

	loadBufferLists { | argArchivePath |
		var bufferLists, defaultList;
		archivePath = argArchivePath ?? { Platform.userAppSupportDir +/+ "BufferLists.sctxar"; };
		bufferLists = Object.readArchive(archivePath);
		bufferLists !? { bufferLists do: { | bl | bl do: _.rebuild; }};
		^bufferLists ?? { [this.makeList] };
	}

	makeList { | argName |
		^NamedList().name_(argName ?? { Date.getDate.format("Buffer List %c") });
	}

	readDefaults { | bufferList |
		(Platform.resourceDir +/+ "sounds/*").pathMatch do: { | path |
			this.addBuffer(bufferList, BufferItem(path));
		};
		bufferList.container.updateListeners;	
	}

	addBuffer { | bufferList, buffer | if (bufferList.includes(buffer).not) { bufferList add: buffer } }

	makeWindow { | argArchivePath |
		this.stickyWindow(this.class, \bufferListGui, { | w, app |
			w.bounds = Rect(400, 400, 1040, 650);
			w.layout = VLayout(
				HLayout(
					VLayout(this.listButtonRow, this.selectedListDisplay, this.listListDisplay),
					[VLayout(this.fileButtonRow, this.fileListDisplay), s: 2],
					[this.bufferDisplay, s: 1],
				),
				this.soundFileItemsRow1,
				this.soundFileDisplay,
//				GridLayout.rows(
//					[Knob(), this.soundFileDisplay],
//				).setMinColumnWidth(0, 200),
				// Here will follow the functionality items
//				[nil, s: 5],
				
			);
			// load current file when re-opening
			files.item !? { files.notify(\index, files); };
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
		^this.listItem(\bufferLists, TextField(), { | me |
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
		^this.listView(\bufferLists, { | me |
			me.value.adapter.items collect: _.name
		})
		.view.font_(font)
	}

	fileButtonRow {
		^HLayout(
			StaticText().string_("Sound Files:").font_(font),
			this.button(\files).notifyAction(\readNew).view.states_([["read new"]]).font_(font),
			this.button(\files).notifyAction(\readDefaults)
			.view.states_([["read defaults"]]).font_(font),
			this.button(\files).notifyAction(\loadSelected)
			.view.states_([["load selected"]]).font_(font),
			this.button(\files).notifyAction(\loadAll).view.states_([["load all"]]).font_(font),
			this.button(\files).notifyAction(\delete).view.states_([["delete"]]).font_(font),
		)
	}

	fileListDisplay {
		^this.listView(\files, { | me |
			me.value.adapter.items collect: _.name
		})
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
		^this.listView(\bufferActions).items_(["--"]).view.font_(font)
	}
	
	soundFileDisplay {
		^this.soundFileView(\soundFileView)
		.viewGetter(\sfView)	// provide view to other widgets for extra actions
		.updater(files, \index, { | me, list |
			list.item !? { me.value.adapter.soundFile_(list.item.name); }
		}).view.timeCursorOn_(true);
	}
	
	soundFileItemsRow1 {
		^HLayout(
			[StaticText().string_("num frames:").font_(font), s: 2],
			this.numberBox(\soundFileView)
			.updateAction(\read, { | sf, me |
				sf.soundFile !? { me.view.value = sf.soundFile.numFrames }
			})
			.view.font_(font),
			[StaticText().string_("sample rate:").font_(font), s: 2],
			[this.numberBox(\soundFileView)
			.updateAction(\read, { | sf, me |
				sf.soundFile !? { me.view.value = sf.soundFile.sampleRate }
			})
			.view.font_(font), s: 1],
			StaticText().string_("duration:").font_(font),
			[this.numberBox(\soundFileView)
			.updateAction(\read, { | sf, me | 
				sf.soundFile !? { me.view.value = sf.soundFile.duration }
			})
			.view.font_(font), s: 1],
			StaticText().string_("cursor:").font_(font),
			this.numberBox(\soundFileView)
			.updateAction(\sfViewAction, { | sfv, me |
				sfv.view.soundfile !? { me.view.value = sfv.view.timeCursorPosition; }
			})
			.view.font_(font),
			StaticText().string_("time:").font_(font),
			[this.numberBox(\soundFileView)
			.updateAction(\sfViewAction, { | sfv, me | 
				sfv.view.soundfile !? { 
					me.view.value = sfv.view.timeCursorPosition / sfv.view.soundfile.sampleRate; 
				}
			})
			.view.font_(font), s: 1],
			[StaticText().string_("selected frames:").font_(font), s: 2],
			this.numberBox(\soundFileView)
			.updateAction(\sfViewAction, { | sfv, me |
				sfv.view.soundfile !? { 
					me.view.value = sfv.view.selectionSize(sfv.view.currentSelection);
				}
			})
			.view.font_(font),
			[StaticText().string_("selected dur.:").font_(font), s: 2],
			[this.numberBox(\soundFileView)
			.updateAction(\sfViewAction, { | sfv, me |
				sfv.view.soundfile !? {
					me.view.value = 
						sfv.view.selectionSize(sfv.view.currentSelection) / 
						sfv.view.soundfile.sampleRate; 
				}
			})
			.view.font_(font), s: 1],
			this.button(\files).notifyAction(\play).view.states_([["play"]]).font_(font),
			this.button(\soundFileView)
			.action_({ | me |
				var sfv, selection, firstFrame, lastFrame;
				sfv = me.getView(\sfView);
				selection = sfv.currentSelection;
				firstFrame = sfv.selectionStart(selection);
				lastFrame = sfv.selectionStart(selection) + sfv.selectionSize(selection);
				if (lastFrame <= firstFrame) { lastFrame = sfv.soundfile.numFrames };
				sfv.soundfile.cue(
				(
					firstFrame: firstFrame,
					lastFrame: lastFrame,
				), playNow: true, closeWhenDone: true)
			})
			.view.states_([["play sel"]]).font_(font),
		)
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
