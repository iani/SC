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
		loadChain = FunctionChain(
			[{ NetAddr.localAddr.sendMsg('/done', '/serverReady') }], 
			{
				this.notifyObjects;  // Start synths
				this.startUserActions;
			}
		);
		Udef.all.values do: _.prepareToLoad(this);
		UniqueBuffer.onServer(server) do: _.prepareToLoad(this);
	}

	loadSynthDefsAndBuffersAndStartSynths { loadChain.start }

	notifyObjects {
		object.asKeyValuePairs pairsDo: { | obj, func | func.value(obj, server, this); };
	}

	startUserActions {
		// without the defer start actions are skipped exactly every second time. WHY?
		{ this.notify(this.startedMessage, this); }.defer;
	}

	startedMessage { ^\started }

	makeResponder { 
		responder = OSCresponderNode(nil, '/done', { | time, resp, msg |
			switch (msg[1],
				// sent by ServerReady load Chain: last in chain.
				'/serverReady', { postf("% ready\n", server); loadChain.next }, 
				'/b_allocRead', { loadChain.next; },
				'/d_recv', { loadChain.next; },
	/* Introduced delay because starting Spectrograph occasionally reported Error Buffer not initialized: */
				'/b_alloc', { { loadChain.next; }.defer(0.1); } // give buffer time to initialize
			);
		}).add;
		this.onClose({ responder.remove });
	}


	// ============= General User Interface ============= 

	*addAction { | action, server |
		^this.new(server).addAction(action);
	}

	addAction { | action |
		/* 
		Perform an action for object when the server is ready 
		(When all synthdefs and buffers have been loaded)
		Clean up notification connection when ServerReady closes. 
		*/
		this.addObjectAction(UniqueID.next, action);
	}

	*addObjectAction { | object, action, server |
		^this.new(server).addObjectAction(object, action);
	}

	addObjectAction { | object, action |
		/* 
		Perform an action for object when the server is ready 
		(When all synthdefs and buffers have been loaded)
		Clean up notification connection when either ServerReady or the 
		object receive the onClosed message (see Object:onClosed)
		*/
		this.addListener(object, this.startedMessage, action);
		if (this.hasStarted) { this.startUserActions };
	}

	hasStarted {
		^server.serverRunning and: { loadChain.size == 0 }
	}

	// ========== Interface for SynthDefs, Synths and Buffers ============
	// Methods for ensuring synthdefs and buffers are loaded before synths start
	// They should not be used by the end user. 
	// They are called by Udef, UniqueBuffer, UniqueSynth when they start or load
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
