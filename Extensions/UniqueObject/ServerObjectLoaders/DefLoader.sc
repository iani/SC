
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
		"loadAllObjects DEFLOADER starts".postln;
		postf("% isLoading: %, numObjects: %\n", this, isLoading, objects.size).postln;
		if (isLoading) { ^this };	// may be still waiting for the last def to load
		if ( objects.size == 0) {
		"loadAllObjects DEFLOADER DONE".postln;
			this.done
		}{
		"loadAllObjects starting do load synthdef chain".postln;
			isLoading = true;
			first = objects detect: true;
			this.load(first);
		}
	}

	done {
		super.done;
		this.loadNextObjectGroup;
	}

	loadNextObjectGroup { serverPrep.loadActions; }
		
	load { | object |
		objects.remove(object);
		object.sendTo(server); // object decides appropriate action
	}
}

BufLoader : DefLoader {

	responderPaths { ^[
			['/done', '/b_allocRead'],
			['/done', '/b_alloc'],			
		]
	}	
	loadNextObjectGroup {
//		{	// leave time for alloc buffers to initialize?
			serverPrep.loadDefs;
//		}.defer(0.5);
	}
	
}
