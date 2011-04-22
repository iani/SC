
+ Buffer {
	
	*default { | server |
		^DefaultBuffer(server ? Server.default);
	}
	
	*play { | func, server, path |
		server = server ? Server.default;
		^DefaultBuffer(
			server ? Server.default, 
			path ? DefaultBuffer.defaultPath, 
			func ?? { DefaultBuffer.defaultPlayFunc }
		);
	}
	
	*loadDefault { | server, play |
		^this.load(DefaultBuffer.defaultPath, server, play ?? {{}});
	}

	*load { | path, server, play |
		server = server ? Server.default;
		if (path.isNil) {
			^DefaultBuffer(server).loadDialog(play);
		}{
			^DefaultBuffer(server, path, play);
		}
	}
	
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
	
	
}
