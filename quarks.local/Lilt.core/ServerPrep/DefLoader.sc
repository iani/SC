
DefLoader : ServerActionLoader {

	init {
		super.init;
		this.makeResponders;		// only subclasses that need responders make them
	}

	makeResponders { this.responderPaths do: this.makeResponder(_); }
	responderPaths { ^[['/done', '/d_recv']] }	// subclasses that need responders override this

	makeResponder { | path |
		OSCpathResponder(server.addr, path, { this.next; }).add;
	}

	next {
		var next;
		next = objects detect: true;
		if (next.isNil) {
			this.done;
		}{
			this.load(next);
		};
	}

	loadAllObjects {
		var first;
		if (isLoading) { ^this };	// may be still waiting for the last def to load
		if ( objects.size == 0) {
			this.done
		}{
			if (verbose and: { objects.size > 0 }) {
				postf("Loading % % to %\n", objects.size, this.objectKind, server);
			};
			isLoading = true;
			first = objects detect: true;
			this.load(first);
		}
	}

	objectKind { ^"SynthDefs" }

	done {
		super.done;
		this.loadNextObjectGroup;
	}

	loadNextObjectGroup { 
		serverPrep.loadSynths; 
	}
		
	load { | object |
		objects.remove(object);
		object.sendTo(server); // object decides appropriate action
	}
	
	addAllUdefs {
		/* Received from ServerPrep on Boot time. Done before loading process starts */
		objects addAll: Udef.onServer(server);
	}
}

BufLoader : DefLoader {

	objectKind { ^"Buffers" }

	responderPaths { ^[['/done', '/b_allocRead'], ['/done', '/b_alloc']] }

	loadNextObjectGroup { serverPrep.loadDefs; }

	addAllBufferResources {
		/* Received from ServerPrep on Boot time. Done before loading process starts */
		objects addAll: BufferResource.onServer(server);
	}
}
