/* 
Works with ServerPrep, so that buffers are always loaded before any synths are started. 
*/
BufferResource : AbstractServerResource {
	classvar <>defaultPath = "sounds/a11wlk01.wav";
	classvar >current;
	var <path, <startFrame = 0, <numFrames, <numChannels = 1, <sampleRate, <duration;

	*initClass {
		current = IdentityDictionary.new;
	}

	*makeKey { | key, target, numFrames, numChannels, path |
		^super.makeKey(key ?? { this.keyFromPath(path) }, target)
	}

	*keyFromPath { | path |
		path = path ? defaultPath;
		^PathName(path).fileNameWithoutExtension.asSymbol;
	}

	*default { | server | ^this.new(\default, server ? Server.default, path: defaultPath) }
	
	*current { | server |
		^current[server ? Server.default];
/*		var cur;
		server = server ? Server.default;
		cur = current[server];
		if (cur.isNil) { ^this.default } { ^cur };
*/
	}

	// this method just defines arguments similar to Buffer-new
	// use it only to create buffers that do not read from a file
	// to read buffers from file use *read
	*new { | key, server, numFrames, numChannels = 1, path, startFrame = 0 |
		^super.new(key, (server ? Server.default), numFrames, numChannels, path, startFrame);
	}

	init { | argServer, argNumFrames, argNumChannels = 1, argPath, argStartFrame |
		super.init(argServer);
		numFrames = argNumFrames;
		numChannels = argNumChannels;
		path = argPath;
		startFrame = argStartFrame;
		ServerPrep(server).addBuffer(this);
		NotificationCenter.notify(BufferResource, \created, this);
		if (path.notNil) {
			current[server] = this;
		};
	}

	*saveDefaults {
		// save all current instances to Archive.global;
		Archive.global.put(this.name, (this.all.flat collect: { | b | b.key add: b.path }));
		Archive.write;
	}
	
	*loadDefaults {
		// load all Server.default instances from Archive.global;
		Archive.global.at(this.name) do: { | bspec |
			if (bspec[3].notNil) { this.read(bspec[3], Server.named[bspec[2]]) };
		};
	}

	*loadSCdefaults { this.loadPaths((this.defaultSoundsDir +/+ "a*").pathMatch;) }

	*defaultSoundsDir { /* IZ 2012 03 22 
		Return the current default path of the sounds folder belonging to the standard SuperCollider 		distribution. */
		^Platform.resourceDir +/+ "sounds";
	}


	*load {
		Dialog.getPaths({ | paths | this.loadPaths(paths) });
	}

	*loadPaths { | paths | paths do: this.read(_);	}

	*read { | path, server, startFrame = 0, numFrames, play |
		if (path.pathMatch.size == 0) {
			^postf("Could not find Buffer at path: %\n", path);
		};
		^this.new(this.keyFromPath(path), server, numFrames, nil, path, startFrame, play);
	}

	*saveListDialog {
		Dialog.savePanel({ | path |
			var file;
			file = File.open(path, "w");
			file.putString(this.onServer.collect(_.path).select(_.notNil).asCompileString);
			file.close;
		});	
	}

	*loadListDialog {
		Dialog.getPaths({ | paths |
			var path, file, soundPaths;
			this.loadList(paths.first);
		});			
	}
	
	*loadList { | path |
		var file, soundPaths;
		file = File.open(path, "r");
		soundPaths = file.readAllString;
		file.close;
		soundPaths = soundPaths.interpret;
		soundPaths do: this.read(_);
	}

	*list { BufferListWindow.new }
	
	*nameSelectionList { 
		BufferListWindow(
			action: { | b | postf("'%'.buffer.play;\n", b.name); },
			message: ": Click to post name"
		);
	}

	*postAll { this.onServer.postln }
	*postNames { this.onServer.collect(_.name).asCompileString.postln }

	*play { | func, name |
		var ubuf;
		if (name.isNil) { ubuf = this.current } { ubuf = this.getObject(this.makeKey(name)); };
		if (ubuf.isNil) { ^postf("Could not find a BufferResource named: %\n", name); };
		ubuf.play(func);
	}

	play { | func, target, outbus = 0, fadeTime = 0.02, addAction=\addToHead, args, name |
		var synthResource;
		synthResource = this.makePlayFunc(func).play(target ? server, outbus, fadeTime, 
			addAction, args ++ [\buf, object], name
		);
		current[target.asTarget.server] = this;
		^synthResource;
	}

	makePlayFunc { | func |
		if (func.notNil) { ^func };
		^{ | buf, rate = 1 |
			PlayBuf.ar(buf.numChannels, buf, rate * BufRateScale.kr(buf), 0, 0, 0, 2); 
		};
	}

	sendTo {
		if (path.isNil) {
			warn("allocating new buffer: %\n", this.key);
			object = Buffer.alloc(server, (numFrames ? 1024), numChannels, 
				completionMessage: { | b | this.loaded(b); }
			);
		}{
			object = Buffer.read(server, path, startFrame, numFrames, { | b |
				postf("Loaded buffer (%) '%' (% seconds)\n", 
					b.bufnum, b.path.basename, (b.numFrames / b.sampleRate).round(0.01));
				this.loaded(b);
			});
		};
	}

	loaded { | b |
		numChannels = b.numChannels;
		numFrames = b.numFrames;
		sampleRate = b.sampleRate;
		duration = b.numFrames / (b.sampleRate ? 44100);
		NotificationCenter.notify(BufferResource, \loaded, this);
	}

	free {
		if (this.isLoaded) { object.free };
		this.freed;
		this.remove;
	}

	freed {
		object = nil;
		NotificationCenter.notify(this.class, \free, this);
		if (current[server] === this) { current[server] = nil; /* this.class.default */ };
	}

	isLoaded { ^object.notNil }

	bufnum { if (object.notNil) { ^object.bufnum } { ^nil } }
	

	name { ^key[2] }

	*menuItems {
		^[
			CocoaMenuItem.addToMenu("Buffers", "Buffer list", [], {
				this.list;
			}),
			CocoaMenuItem.addToMenu("Buffers", "Load buffers", [], {
				{ this.load; this.list; }.defer(0.1);
			}),
			CocoaMenuItem.addToMenu("Buffers", "Load buffer list", [], {
				{ this.loadListDialog; this.list; }.defer(0.1);
			}),
			CocoaMenuItem.addToMenu("Buffers", "Save buffer list", [], {
				{ this.saveListDialog; }.defer(0.1);
			}),
			CocoaMenuItem.addToMenu("Buffers", "Save lists to Archive", [], {
				{ this.saveDefaults; }.defer(0.1);
			}),
			CocoaMenuItem.addToMenu("Buffers", "Load lists from Archive", [], {
				{ this.loadDefaults; }.defer(0.1);
			}),
			CocoaMenuItem.addToMenu("Buffers", "Buffer selector list", [], {
				{ this.nameSelectionList; }.defer(0.1);
			}),
			CocoaMenuItem.addToMenu("Buffers", "Post current buffer", [], {
				{ postf("'%'.buffer", this.current.key.last); }.defer(0.1);
			}),

		];
	}
}
