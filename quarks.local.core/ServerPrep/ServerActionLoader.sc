
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
		if (isLoading) { ^this };	// leave if still in previous loading loop
		isLoading = true;
		if (verbose and: { objects.size > 0 }) {
			postf("Loading % % to %\n", objects.size, this.objectKind, server);
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

	objectKind { ^"actions" }

	done { isLoading = false; }
}

SynthLoader : ServerActionLoader {

	done { 
		super.done;
		serverPrep.loadRoutines;
	}

	objectKind { ^"Synths" }
	
}

RoutineLoader : ServerActionLoader {

	done { 
		super.done;
		serverPrep.loadActions;
	}

	objectKind { ^"Routines" }
	
}