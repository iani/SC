
+ Buffer {
	
	*default { | server |
		^UniqueBuffer.default(server ? Server.default);
	}

	*current { | server |
		^UniqueBuffer.current(server ? Server.default);
	}
	
	*play { | func, server |
		this.current(server).play(func);
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
