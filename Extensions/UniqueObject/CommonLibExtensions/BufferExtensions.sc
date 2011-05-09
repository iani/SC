
+ Buffer {
	
	*default { | server |
		^UniqueBuffer.default(server ? Server.default);
	}

	*current { | server |
		^UniqueBuffer.current(server ? Server.default);
	}
	
	*play { | func, target, outbus = 0, fadeTime = 0.02, addAction=\addToHead, args, name |
		(this.current(target.asTarget.server) ?? { this.default(target.asTarget.server) })
			.play(func, target, outbus, fadeTime, addAction, args, name);
	}

	*load { UniqueBuffer.load }	
	
	*saveList { UniqueBuffer.saveList }
	*loadList { UniqueBuffer.loadList }
	*list { BufferListWindow.new }
}



/*	
	*info { | server |
		server = server ? Server.default;
		if (DefaultBuffer isLoaded: server) {
			^DefaultBuffer(server).postInfo;
		}{
			postf("Default buffer for server: % is not loaded\nDefault buffer path is:\n%\n", server, 
				DefaultBuffer.defaultPath;
			);
			"Do:\n\tBuffer.loadDefault;\nto load the default buffer".postln;
			"Or do:\n\tBuffer.play;\nto load and play the default buffer".postln;
		}
	}
*/	
