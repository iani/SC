/* IZ Wed 29 August 2012 12:57 PM EEST

BufferItems are accessible by the name of the file from which they were loaded, without extension, as a symbol, sending it message 'b'. 

Once a BufferItem is loaded, it will reload when the default server reboots.

// To access or play a loaded buffer item: 

\SinedPink.b; // accesses the buffer

\SinedPink.b.play // accesses and plays the buffer

BufferItems can be added, loaded, deleted, free'd through the BufferListGui. 

BufferListGui();

*/

BufferItem : NamedItem {
	// name -> path. item -> Buffer
	// Buffer allocated only and always when server boots or is booted.
	classvar loadingBuffers; // Load buffers only one at a time. See method load.
	classvar <>all;	// IdentityDictionary with one buffer per symbol. 
					// prevent creating duplicate buffers with same path.
	
	var <>nameSymbol;
	*initClass {
		loadingBuffers = IdentityDictionary.new;
		all = IdentityDictionary.new;
		StartUp add: {
			Library.put('Buffers', IdentityDictionary.new);
			ServerBoot.add({
				Library.at('Buffers') do: _.load;
			}, Server.default);
			ServerQuit.add({
				Library.at('Buffers') do: _.serverQuit;
			}, Server.default);
		}
	}

	*new { | name |
		var nameSymbol, existing;
		nameSymbol = PathName(name).fileNameWithoutExtension.asSymbol;
		(existing = all[nameSymbol]) !? { ^existing };
		^super.new(name).nameSymbol_(nameSymbol).register;
	}

	register { all[nameSymbol] = this }

	rebuild {
		var existing;
		item = nil;
		existing = all[nameSymbol];
		if (existing.notNil) {
			^existing;
		}{
			all[nameSymbol] = this;
			^this;  // (;-)
		}
	}

	load { | extraAction | // mechanism for loading next buffer after this one is loaded
		item !? { ^this };
		if (Server.default.serverRunning) {
			loadingBuffers[this] = { this.prLoad(extraAction); };
			if (loadingBuffers.size == 1) { this.prLoad(extraAction); }
		}{
			this.storeInLibrary;
			if (Server.default.serverBooting.not) { Server.default.boot };
		};
	}

	prLoad { | extraAction | // called from loadingBuffers when previous buffer is loaded
		Buffer.read(Server.default, name, action: { | buffer |
			item = buffer;
			this.postInfo;
			this.storeInLibrary;
			extraAction.(buffer);
			loadingBuffers[this] = nil;
			loadingBuffers.detect(true).value;
		})	
	}

	serverQuit { item = nil; }

	play {
		item !? { ^item.play };
		this.load({ item.play })
	}

	postInfo { postf("% : % \n", this.minSec, nameSymbol) }
	
	minSec {
		var seconds;
		item ?? { ^"?? min, ?? sec" };
		^minSec(item.numFrames / item.sampleRate);
	}

	free {
		var registeredItem;
		registeredItem = Library.at('Buffers', nameSymbol);
		registeredItem !? { if (registeredItem !== this) { ^registeredItem.free }; };
		item !? { item.free; };
		item = nil;
		Library.put('Buffers', this.nameSymbol, nil);
		this.updateLists;
	}

	updateLists { this.class.updateLists; }

	*updateLists {
		var buffers;
		(buffers = Library.at('Buffers')) !? {
			{ this.notify(\bufferList, [Library.at('Buffers').keys.asArray.sort]); }.defer;
		}
	}

	storeInLibrary { 
		Library.put('Buffers', this.nameSymbol, this);
		this.updateLists;
	}

	*openPanel { | doneFunc |
		Dialog.openPanel({ | path | doneFunc.(this.new(path)); });
	}

	*makeLoadBuffersString {
		var buffers;
		buffers = Library.at('Buffers').asArray;
		if (buffers.size == 0) { ^"" };
		^buffers.inject("\n// ====== BUFFERS ====== \n\n", { | str, b |
			str ++ format("BufferItem(%).load;\n", b.name.asCompileString);
		});
	}

}
