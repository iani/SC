
WaitForServer {
	var <function, <server, <waiting = true;
	*new { | server, function |
		server = server.asTarget.server;
		if (server.serverRunning) {
			^function.value;	
		}{
			^this.newCopyArgs(function, server).init;
		};
	}
	
	init {
		ServerReady.registerOneShot(
			UniqueID.next, 
			{ waiting = false; function; }, 
			server
		);
		server.boot;
		while { waiting or: { server.serverRunning.not } } { 0.01.wait };
	}
}
