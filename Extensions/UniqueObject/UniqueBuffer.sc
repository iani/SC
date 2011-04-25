/* 

Following additional class methods of Buffer provide an interface for using UniqueBuffer easily: 
	*load( (optional: func) )
	*play( (optional: func) )
	*select( (optional: func) )

Instance methods of String: 
	aString.load( (optional: func) )
		
*/

UniqueBuffer : AbstractUniqueServerObject {
	classvar <>defaultPath = "sounds/a11wlk01.wav";
	classvar >current;
	var <server, <path, <startFrame = 0, <numFrames, <numChannels = 1;

	*mainKey { ^\buffers }
	*removedMessage { ^\b_free }
	*makeKey { | key, target, numFrames, numChannels, path |
		^(target.asTarget.server.asString ++ ":" ++ (key ?? { this.keyFromPath(path) })).asSymbol;
	}

	*keyFromPath { | path |
		path = path ? defaultPath;
		^PathName(path).fileNameWithoutExtension.asSymbol;
	}	

	*read { | path, server, startFrame = 0, numFrames, play |
		^this.new(this.keyFromPath(path), server, numFrames, nil, path, startFrame, play);
	}

	// this method just defines arguments similar to Buffer-new
	// use it only to create buffers that do not read from a file
	// to read buffers from file use *read
	*new { | key, server, numFrames, numChannels = 1, path, startFrame = 0, play |
		^super.new(key, server ? Server.default, numFrames, numChannels, path, startFrame, play);
	}
	
	*play { | func, name |
		var ubuf;
		if (name.isNil) { ubuf = this.current } { ubuf = this.getObject(this.makeKey(name)); };
		if (ubuf.isNil) { ^postf("Could not find a UniqueBuffer named: %\n", name); };
		ubuf.play(func);
	}

	*current { 
		if (current.isNil) { current = this.getObject(this.makeKey(this.keyFromPath)); };
		if (current.isNil) { current = this.new(path: defaultPath); };
		^current;
	}
	
	*default { | server | ^this.new(server: server ? Server.default, path: defaultPath) }

	play { | play |
		if (this.isLoaded) {
			this.playNow(play) 
		} {
			// enable starting multiple play functions on the same buffer at server boot time:
			NotifyOnce(key, \loaded, this, { this.playNow(play) });
			if (server.serverBooting.not) { server.boot; };
		}
	}

	isLoaded { ^object.notNil }

	init { | argServer, argNumFrames, argNumChannels = 1, argPath, argStartFrame, playFunc |
		server = argServer;
		numFrames = argNumFrames;
		numChannels = argNumChannels;
		path = argPath;
		startFrame = argStartFrame;
		if (server.serverRunning) { this.makeObject(playFunc) };
		ServerReady(server).add(this.class, { this.class.loadAllBuffers(server) });
		ServerQuit.add(this.class, server);
	}
	
	*loadAllBuffers { | server |
		var buffers;
		server = server ? Server.default;
		buffers = this.onServer(server);
		buffers.inject(nil, { | a, b |
			if (a.notNil) { NotificationCenter.registerOneShot(a.key, \loaded, a, { b.makeObject }) };
			b;
		});
		buffers.first.makeObject;
//		UniqueSynthDef(server).doWhenLoaded({ buffers.first.makeObject });
	}
	
	*doOnServerQuit { | server |
		this.onServer(server) do: _.serverQuit;	
	}

	serverQuit {
		object = nil;
		NotificationCenter.notify(key, \serverQuit, this);
	}

	makeObject { | play |
		if (play.notNil) { this.playWhenLoaded(play) };
		if (path.isNil) {
			object = Buffer.alloc(server, numFrames ? 1024, numChannels, completionMessage: { | b | 
				NotificationCenter.notify(key, \loaded, this);
			});
		}{
			object = Buffer.read(server, path, startFrame, numFrames, { | b | 
				NotificationCenter.notify(key, \loaded, this);
			});
		};
		postf("loaded (makeObject): %\n", this);
	}

	playNow { | playFunc |		
		if (	playFunc isKindOf: Function ) {
			playFunc.(object, this);	
		}{
			object.play;
		}
	}

	free {
		if (this.isLoaded) { object.free };
		this.remove;	
		object = nil;
	}
}

Ubuf : UniqueBuffer {} // synonym for UniqueBuffer
