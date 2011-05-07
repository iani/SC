ServerReady : UniqueObject {

	var <>server; 
	var <cmdPeriod = false;
	var <loadChain;			// scheme for loading SynthDefs and Buffers
	var <responder;			// notifies when synthdefs or buffers are loaded
	
	// Creating an alternative scheme for addAction and addObjectAction: 
//	var actions;
//	var objectActions;
	

	*makeKey { | server | ^this.mainKey add: (server ?? { server.asTarget.server }); }

	init {
		CmdPeriod.add(this);
		server = key[1];
		ServerTree.add(this, server);
		object = IdentityDictionary.new;
		this.initLoadChain;
		this.makeResponder;
//		actions = Set.new;
//		objectActions = IdentityDictionary.new;
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
			[{
				this.startSynthsAndUserActions;	
				NetAddr.localAddr.sendMsg('/done', '/serverReady')
			}]
		);
		Udef.all.values do: _.prepareToLoad(this);
		UniqueBuffer.onServer(server) do: _.prepareToLoad(this);
	}

	loadSynthDefsAndBuffersAndStartSynths {
		if (loadChain.size == 0) {
			"loadchain EMPTY. WHAT TO DO?".postln;
			object.postln;
			NotificationCenter.registrations.atPath([this]).postln;
//			this.startSynthsAndUserActions;
		}{
			"loadchain EXISTS. Will start".postln;
			loadChain.start;
		}
	}

	startSynthsAndUserActions {
		this.notifyObjects;  		// Start synths
		this.startUserActions;		// Start actions added by user
	}

	notifyObjects {
		object.asKeyValuePairs pairsDo: { | obj, func | func.value(obj, server, this); };
	}

	startUserActions { "sending notifications startuseractions".postln; this.notify(this.startedMessage, this); }

	startedMessage { ^\started }

	makeResponder { 
		responder = OSCresponderNode(nil, '/done', { | time, resp, msg |
			switch (msg[1],
				// /serverReady is sent by ServerReady load Chain as last element run in chain.
				'/serverReady', { postf("% ready\n", server); /* { loadChain.next }.defer(3); */ }, 
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
		Perform an action ONCE when the server is ready 
		(When all synthdefs and buffers have been loaded).
		Perform the action ONCE ONLY when the server boots, or immediately if it is running.
		*/
		NotificationCenter.registerOneShot(this, this.startedMessage, UniqueID.next, action);
//		actions.add(action);
	}

	*addObjectAction { | object, action, server |
		^this.new(server).addObjectAction(object, action);
	}

	addObjectAction { | object, action |
		/* 
		Perform an action for object ALWAYS when the server is ready
		(When all synthdefs and buffers have been loaded).
		Perform immediately if the server is already booted, and each new boot, 
		until the object performs either objectClosed, or remove, or until
		the ServerReady performs this.remove or this.objectClosed.
		*/
		object.addNotifier(this, this.startedMessage, 
		{ "addObjectActionnotificationreeived".postln; action.value});
//		objectActions[object] = action;
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
		"ServerReady addSynth debug start".postln;
		if (server.serverRunning) {
			postf("addSynth: SERVER RUNNING, loadChain size is:%\n", loadChain.size);
			if (loadChain.size == 0) {
				postf("loadChain size is:%, running makingFunc only\n", loadChain.size);
				makeFunc.value;
//				uSynth.synthStarted;
			}{ 
				postf("loadChain size is:%, registeroneshot+loadDefsBufsetc\n", loadChain.size);
				this.registerOneShot(uSynth, makeFunc);
				this.loadSynthDefsAndBuffersAndStartSynths;
			};
		}{
			postf("addSynth: SERVER NOT RUNNING, registeroneshot makefunc+usynthstarted + BOOT\n");
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
