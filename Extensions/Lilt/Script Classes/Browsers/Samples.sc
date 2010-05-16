/* (IZ 2005-09-02) {
Managing loading of audio buffers and assignment of bufnums (index numbers of loaded buffers) by drag-and-drop to Scripts. Adapted from BufSynth. 
2005-10-17: reworking load and update mechanism. Removing reload-if-present. 
} */

Samples : Model {
	// {
	classvar <all;	// dictionary: 1 Samples instance per server only
	var <>server;	// to make variable-servers possible in later versions:  
	var <>buffers;	// envir of buffers
	// gui:
	var <selection;
	var <playingBuffers;	// synths playing test buffers: for stopping them.
	var <window;			// window of GUI for accessing all synthdefs
	var <infoview, <pathview, <listview; // views to update when buffers change
	// }
	*initClass {
		all = ();
	}
	*new { | server |
		// one instance per server!
		var instance;
		server = server ?? { Server.default; };
		^all.atFail(server, {
			all[server] = instance = this.newCopyArgs(nil, server).init;
			instance;
		});
	}
	*default {
		^all.atFail(Server.default, { this.new(Server.default) });
	}
	add { | bufname |
	// Enable access of bufnums of buffers on default Samples by Samples[bufname]
	// 0 is used as default bufnum of empty buffer
		^(this.class.default.buffers[bufname] ?? { Buffer(server, nil, nil, 0) }).bufnum;
	}
	at { | bufname |
		^buffers[bufname]
	}
	init {
		buffers = (' [NO BUFFER] ': Buffer(nil, nil, nil, 0));
	}
	load { | path |
		// Load all files matching path
		var bufs;
		bufs = path.pathMatch.collect { | p |
			if (p.last != $/) { this.loadIfNotPresent(p) }
		};
//		thisMethod.report(bufs);
		this.changed(\bufList, buffers.keys.asArray.sort);
		^bufs;
	}
	loadIfNotPresent { | path, notifierAction |
		var bufname, buffer, action, notifier;
		// DO NOT load if a buffer already exists with the same name.
		// derive file name part from path:
		action = \loadFromFile;
		if (path.first == $[ ) { // ]
			#bufname ... path = path.interpret;
			action = \alloc;
		}{
			bufname = path.fileSymbol;
		};
		buffer = buffers[bufname];
		notifier = { | buf, what |
			if (what === 'info') {
//				thisMethod.report(buf, "inside the notifier check the bufnum", buf.bufnum);
				notifierAction.(buf);	// 081025 are we using this?
				this.changed(\bufferLoaded, buf);
//				buf.removeDependant(notifier);
			}
		};
		if (buffer.isNil) { 
			buffer = this.perform(action, bufname, *path);
			buffer.addDependant(notifier);			
			buffers[bufname] = buffer;
			if (buffer.server.serverRunning) { buffer.changed('info') };
//			thisMethod.report("buffer was nil");
		}{
			// updates scripts bufnum parameter menus!
			buffer.addDependant(notifier);
			if (buffer.server.serverRunning) { buffer.changed('info') };			
//			thisMethod.report("buffer was  NOTTTT nil");
		};
		{ buffer.waitForBufInfo }.defer(0.5);
		^buffer;
	}
	loadFromFile { | bufname, path |
		var buffer;
		if (this.fileIsAudio(path).not) {
			Post << "NOT A SOUND FILE: " << path << "\n";
			^buffers[' [NO BUFFER] '];
		};
		buffer = Buffer(server, nil, nil, 0).path_(path);
		buffer.onBoot({
			buffer.reRead(path, { this.updateBufferInfo(buffer, bufname) });
		}, server, true);
		^buffer;		
	}
	alloc { | bufname, frames = 44100, chans = 1 |
		// allocate, do not load from file. For RecordBuf type scripts.
		var buffer;
		buffer = Buffer(server, frames, chans, 0)
			.path_([bufname.asSymbol, frames, chans].asCompileString);
		buffer.onBoot({
			//  arg server, numFrames, numChannels = 1, completionMessage, bufnum
			buffer.reAlloc({ this.updateBufferInfo(buffer, bufname) });
		}, server, true)
		^buffer;
	}
	fileIsAudio { | path |
		var file;
		file = SoundFile.openRead(path);
//		Post << "OPENED THIS SOUND FILE: " << path << "\n";
		if (file.isNil) {
//			Post << "NOT A SOUND FILE: " << path << "\n";
			^false;
		};
		file.close;
		^true;
	}
	updateBufferInfo { | buffer, bufname |
		if (buffer === selection) {
			// this Sample's gui items update info displays here: 
			{ this.displayBufferInfo(bufname, buffer) }.defer;
		};
		// other objects that depend on this particular buffer update here:
		{
			buffer.changed(\info);
			this.changed(\bufList, buffers.keys.asArray.sort);
		}.defer;
	}
	update { | buffer |
		// this is how a samples reacts to update messages in the 
		// observer pattern. This method is a stub and has not found
		// further use as of 080515
		[\updating, this, buffer].postln;
	}
	require { | path |
		// load each file matching path only if it is not already loaded
		if (path.pathMatch.size < 1) {
			^Warn("No files found at path:\n" ++ path);
		}
		^this.load(path, false);
	}
	*require { | path |
		^this.default.require(path)
	}
	*makeGui {
		^this.default.makeGui;
	}
	*closeGui {
		^this.default.closeGui;
	}
	makeGui {
		var playbutton, load1button, loadFolderButton, adapter;
		if (window.isNil) {
			window = GUI.window.new("Samples on " ++ server.name,
				Rect(0, 0, 280, 400).fromRight);
//			window.view.decorator = FlowLayout(window.view.bounds, 3@3, 2@2);
			pathview = SCDragSource(window, Rect(3, 3, 274, 17))
				.background_(Color.white(0.5))		// Color(0.93, 0.97, 0.7, 0.3)
				.font_(Font("Helvetica", 10))
				.string_("(path)");
			listview = GUI.listView.new(window, Rect(3, 23, 154, 372));
			loadFolderButton = GUI.button.new(window, Rect(158, 23, 120, 20))
				.states_([["load folder"]])
				.action_ { this.openFolderDialog };
			load1button = GUI.button.new(window, Rect(158, 45, 120, 20))
				.states_([["load file(s)"]])
				.action_ { this.openDialog };
			playbutton = GUI.button.new(window, Rect(158, 67, 120, 20))
				.states_([["play"]])
				.action_ { this.play(selection) };
			infoview = GUI.textView.new(window, Rect(158, 89, 120, 305))
//				.background_(Color(0.5, 0.95, 0.99, 0.3))
//				.stringColor_(Color.yellow)
				.font_(Font("Helvetica", 10));
			listview.action =  { | me |
				var bufname, buf;
				bufname = me.items[me.value];
				buf = buffers[bufname.asSymbol];
//				thisMethod.report(me, bufname, me.value, buf);
				selection = nil;
				if (buf.notNil) {
					selection = buf;
//					pathview.string = buf.path;
					pathview.object = buf;
					this.displayBufferInfo(bufname, buf, infoview, pathview);
				};
				me.focus(true);
 			};
			listview.keyDownAction = { | v, char, mod, unic, key |
 				switch (char,
					// delete: delete buffer
					127.asAscii,{ this.deleteBuffer },
					// return: play selection
					13.asAscii, { this.play(selection) },
					// escape: stop any playing samples
					27.asAscii, { this.stop },
					// enter: post buffer path
					3.asAscii, { if (selection.notNil) {selection.path.postln}},
					{ v.defaultKeyDownAction(char, mod, unic, key) }
				);
			};
			adapter = { | who, how, what1, what2 |
				switch (how,
					\bufList, {
						listview.items = what1 ? [];
						// select buffer name from listview. Needed when deleting items:
						what1 = (listview.items[listview.value] ?? { listview.items.first }).asSymbol;
						// get and display new buffer from buffer name
						what2 = buffers[what1];
						if (what2.notNil) {
							selection = what2;
							this.displayBufferInfo(what1, what2, infoview, pathview);
						}
					}, 
					\bufInfo, {
						thisMethod.report("args:", who, how, what1, what2, "selection:", selection, "is what received?", what1 === selection);
						if (what1 === selection) {
							this.displayBufferInfo(what1, what2, infoview, pathview);
						}
					}
				);
			};
			this.addDependant(adapter);
			window.onClose = {
				Samples.changed(\windowClosed); 
				window = nil;
				this.removeDependant(adapter);
			};
			this.changed(\bufList, buffers.keys.asArray.sort);
			Samples.changed(\windowOpened); 
		};
		window.front;
	}
	closeGui { if (window.notNil) { window.close; } }
	displayBufferInfo { | bufname, buf |
		{
		pathview.object = buf;		// also update the path view
		infoview.editable_(true)
			.setString("", 0, 1000)
			.string_(
				String.streamContents { | stream |
					stream << "\"" << bufname << "\"\nbufnum: " << buf.bufnum
						<< "\nduration: "
						<< (buf.numFrames ? 0 / (buf.sampleRate ? 44100)).round(0.001)
						<< "\nsize: " << buf.numFrames
						<< "\nnumber of channels: " << buf.numChannels
						<< "\nsample rate: " << buf.sampleRate;
				}
			).editable_(false);
		}.defer;	// defer needed because done via osc: buffer info update
	}
	play { | buf | // play buffer for auditing purposes
		var synth;
		if (buf.isNil) { ^nil };
		playingBuffers = playingBuffers.add(synth = Synth("playbuf", [\bufnum, buf.bufnum]));
		synth.onEnd { playingBuffers.remove(synth) };
	}
	stop { // stop all playing buffers
		playingBuffers do: _.free;
		playingBuffers = [];
	}
	openDialog {
		CocoaDialog.getPaths({ | paths |
			var last;
			paths.do { |p| last = this.load(p) };
			last = last.asArray.first;
			if (last.isKindOf(Buffer)) {
				this.select(last);
			};
		}, {
			"load cancelled".postln;
		})
	}
	select { | buffer |
		var name, found;
		found = buffers[name = buffer.path.fileSymbol];
		if (found.notNil) {
//			listview.items..postln;
			listview.value = listview.items.indexOf(name);
			listview.doAction;
		}
	}
	openFolderDialog {
		CocoaDialog.getPaths({ | paths |
			this.load(paths.first.dirname ++ "/*");
		}, {
			"load cancelled".postln;
		})
	}
	deleteBuffer {
		if (selection.isNil) { ^this };
		this.delete(selection);
	}
	delete { | buffer |
		var bufname;
		bufname = buffer.path.fileSymbol;
		Post << "Deleting buffer: " << bufname << "\n";
		buffer.free;
		ServerWatcher.remove(buffer, buffer.server);
		buffers.removeAt(bufname);
		buffer.changed(\deleted);
		this.changed(\bufList, buffers.keys.asArray.sort);
//		this.changed(\samples);
	}
}

