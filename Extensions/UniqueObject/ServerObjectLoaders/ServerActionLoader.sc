
ServerActionLoader {
	
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
		[this, "adding", object].postln;
		objects add: object;
		[this, "objects are now:", objects].postln;
		if (isLoading.not and: { server.serverRunning }) {
			serverPrep.loadAllObjects;
		};
	}

	loadAllObjects {
		var array;
		[this, "loadingAllObjects", objects, "isLoading?:", isLoading].postln;
		if (isLoading) { ^this };	// may be still in previous loading loop
		isLoading = true;
		while { objects.size > 0 } {
			array = objects.asArray;
			array do: { | action |
				["evaluating action", action].postln;
				objects remove: action; 
				action.value;	
			};		
		};
		this.done;
	}

	done { isLoading = false; }
}
