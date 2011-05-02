/* 
Redo of UniqueBuffer to work with new version of ServerReady, so that buffers are always loaded before any synths are started. 
*/
UniqueBuffer : AbstractUniqueServerObject {
	classvar <>defaultPath = "sounds/a11wlk01.wav";
	classvar >current;
	var <server, <path, <startFrame = 0, <numFrames, <numChannels = 1;

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

	*default { | server | ^this.new(server: server ? Server.default, path: defaultPath) }
	
	*current { | server |
		var cur;
		server = server ? Server.default;
		cur = current[server];
		if (cur.isNil) { ^this.default } { ^cur };
	}

	*read { | path, server, startFrame = 0, numFrames, play |
		if (path.pathMatch.size == 0) {
			^postf("Could not find Buffer at path: %\n", path);
		};
		^this.new(this.keyFromPath(path), server, numFrames, nil, path, startFrame, play);
	}
	
	*load {
		Dialog.getPaths({ | paths | paths do: this.read(_); });
	}
	
	*saveList {
		Dialog.savePanel({ | path |
			var file;
			file = File.open(path, "w");
			file.putString(this.onServer.collect(_.path).asCompileString);
			file.close;
		});	
	}
	
	*loadList {
		Dialog.getPaths({ | paths |
			var path, file, soundPaths;
			path = paths.first;
			file = File.open(path, "r");
			soundPaths = file.readAllString;
			file.close;
			soundPaths = soundPaths.interpret;
			soundPaths do: this.read(_);
		});			
	}
	*list { BufferListWindow.new }

	*play { | func, name |
		var ubuf;
		if (name.isNil) { ubuf = this.current } { ubuf = this.getObject(this.makeKey(name)); };
		if (ubuf.isNil) { ^postf("Could not find a UniqueBuffer named: %\n", name); };
		ubuf.play(func);
	}

	// this method just defines arguments similar to Buffer-new
	// use it only to create buffers that do not read from a file
	// to read buffers from file use *read
	*new { | key, server, numFrames, numChannels = 1, path, startFrame = 0 |
		^super.new(key, server ? Server.default, numFrames, numChannels, path, startFrame);
	}

	init { | argServer, argNumFrames, argNumChannels = 1, argPath, argStartFrame |
		server = argServer;
		numFrames = argNumFrames;
		numChannels = argNumChannels;
		path = argPath;
		startFrame = argStartFrame;
		ServerQuit.add(this, server);
		this.prepareToLoad(ServerReady(server));
		NotificationCenter.notify(UniqueBuffer, \created, this);
		current[server] = this;
	}

	prepareToLoad { | serverReady |
		serverReady addFuncToLoadChain: { this.makeObject; };
	}

	play { | func, target, outbus = 0, fadeTime = 0.02, addAction=\addToHead, args |
		this.makePlayFunc(func).mplay(target, outbus = 0, fadeTime, addAction, args ++ [\bufnum, object]);
		current[server] = this;
	}

	makePlayFunc { | func |
		if (func.notNil) { ^func };
		^{ | bufnum | PlayBuf.ar(bufnum.numChannels, bufnum, 1, 0, 0, 0, 2); };
	}

	doOnServerQuit { | server | this.freed; }

	makeObject { | play |
		if (path.isNil) {
			object = Buffer.alloc(server, numFrames ? 1024, numChannels, completionMessage: { | b | 
				this.loaded(b);
			});
		}{
			object = Buffer.read(server, path, startFrame, numFrames, { | b | 
				this.loaded(b);
			});
		};
//		postf("loaded (makeObject): %\n", this);
	}
	
	loaded { | b |
		numFrames = b.numFrames;
		numChannels = b.numChannels;
		NotificationCenter.notify(UniqueBuffer, \loaded, this);
	}

	free {
		if (this.isLoaded) { object.free };
		this.freed;
		this.remove;
	}

	freed {
		object = nil;
		NotificationCenter.notify(UniqueBuffer, \free, this);
		if (current[server] === this) { current[server] = nil; /* this.class.default */ };
	}

	isLoaded { ^object.notNil }
	
	bufnum { if (object.notNil) { ^object.bufnum } { ^nil } }

	*menuItems {
		^[
			CocoaMenuItem.addToMenu("Buffers", "Buffer list", [], {
				this.list;
			}),
			CocoaMenuItem.addToMenu("Buffers", "Load buffers", [], {
				{ this.load; this.list; }.defer(0.1);
			}),
			CocoaMenuItem.addToMenu("Buffers", "Load buffer list", [], {
				{ this.loadList; this.list; }.defer(0.1);
			}),
			CocoaMenuItem.addToMenu("Buffers", "Save buffer list", [], {
				{ this.saveList; }.defer(0.1);
			}),
		];
	}

}
