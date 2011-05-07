
ServerActionLoader {
	classvar <>verbose = false;
	var <serverPrep, <server;
	var <isLoading = false;
	var <objects;

	*new { | serverPrep, server |
		^this.newCopyArgs(serverPrep, server).init;
	}

	init {
		server = serverPrep.server;
		objects = Set.new;
	}

	add { | object |
		objects add: object;
		if (isLoading.not and: { server.serverRunning }) {
			serverPrep.loadAllObjects;
		};
	}

	loadAllObjects {
		var array;
		if (isLoading) { ^this };	// may be still in previous loading loop
		isLoading = true;
		if (verbose and: { objects.size > 0 }) {
			postf("Loading % actions to %\n", objects.size, server);
		};
		while { objects.size > 0 } {
			array = objects.asArray;
			array do: { | action |
				objects remove: action; 
				action.value;	
			};		
		};
		this.done;
	}

	done { isLoading = false; }
}
