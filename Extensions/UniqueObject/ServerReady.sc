/* 

Send notifications after a Server has called initTree and created its root node, but only when it boots. 

Add to ServerReady objects that want to start Synths, Groups or routines right after a server boots, but that do not want to restart them when a Server re-inits its tree after CmdPeriod (after the user types Command-. to stop all synths)

*/

ServerReady : UniqueObject {

	var <>server; 
	var <cmdPeriod = false;

	*makeKey { | server | ^this.mainKey add: (server ?? { server.asTarget.server }); }

	init {
		CmdPeriod.add(this);
		server = key[1];
		ServerTree.add(this, server);
		object = IdentityDictionary.new;
	}

	doOnCmdPeriod { cmdPeriod = true; }

	doOnServerTree {		
		if (cmdPeriod) {
			cmdPeriod = false;
		}{
			object.asKeyValuePairs pairsDo: { | obj, func | func.value(obj, server, this); };
		};
	}	

	*register { | object, function, server | ^this.new(server).register(object, function); }
	register { | argObject, action |
		object[argObject] = action;
	}

	*registerOneShot { | object, function, server | ^this.new(server).registerOneShot(object, function) }

	registerOneShot { | argObject, action |
		object[argObject] = { | obj, server, me |
			action.(obj, server, me);
			this.unregister(argObject);
		};
	}

	*unregister { | object, server | 	
		this.new(server).unregister(object);
	}

	unregister { | argObject | object.removeAt(argObject); }
}

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
