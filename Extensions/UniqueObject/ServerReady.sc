ServerReady : UniqueObject {

	var <>server; 
	var <cmdPeriod = false;
	var <loadChain;			// scheme for loading SynthDefs and Buffers
	var <responder;			// notifies when synthdefs or buffers are loaded

	*makeKey { | server | ^this.mainKey add: (server ?? { server.asTarget.server }); }

	init {
		CmdPeriod.add(this);
		server = key[1];
		ServerTree.add(this, server);
		object = IdentityDictionary.new;
		this.initLoadChain;
		this.makeResponder;
	}

	doOnCmdPeriod { cmdPeriod = true; }

	doOnServerTree {
		if (cmdPeriod) {
			cmdPeriod = false;
		}{
			this.initLoadChain;
			this.loadSynthDefsAndBuffersAndStartSynths;
		};
	}

	initLoadChain {
		loadChain = FunctionChain(nil, { this.notifyObjects; });
		Udef.all.values do: _.prepareToLoad(this);
		UniqueBuffer.onServer(server) do: _.prepareToLoad(this);
	}

	loadSynthDefsAndBuffersAndStartSynths { loadChain.start; }

	makeResponder { 
		responder = OSCresponderNode(nil, '/done', { | time, resp, msg |
			switch (msg[1],
				'/b_allocRead', { loadChain.next; },
				'/d_recv', { loadChain.next; },
	/* Introduced delay because starting Spectrograph occasionally reported Error Buffer not initialized: */
				'/b_alloc', { { loadChain.next; }.defer(0.1); } // give buffer time to initialize
			);
		}).add;
		this.onClose({ responder.remove });
	}

	notifyObjects {
		object.asKeyValuePairs pairsDo: { | obj, func | func.value(obj, server, this); };
	}
	
	// methods for ensuring synthdefs and buffers are loaded before synths start
	*addFuncToLoadChain { | func, server |
		^this.new(server).addFuncToLoadChain(func);
	}
	addFuncToLoadChain { | func | loadChain.add(func); }
	
	*addSynth { | uSynth, makeFunc, server |
		^this.new(server).addSynth(uSynth, makeFunc);	
	}
	addSynth { | uSynth, makeFunc |
		if (server.serverRunning) {
			this.registerOneShot(uSynth, makeFunc);
			this.loadSynthDefsAndBuffersAndStartSynths;
		}{
			this.registerOneShot(uSynth, {
				makeFunc.value;
				uSynth.synthStarted;
			});
			server.boot;
		}
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
