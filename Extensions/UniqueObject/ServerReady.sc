ServerReady : UniqueObject {

	var <>server; 
	var <cmdPeriod = false;
	var <loadChain;			// will become scheme for loading SynthDefs and Buffers
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

	makeResponder { 
		responder = OSCresponderNode(nil, '/done', { | time, resp, msg |
			if (['/b_allocRead', '/b_alloc', '/d_recv'] includes: msg[1]) {
//				{ 
					
					loadChain.next; 
				
//				}.defer(3);
			}
		}).add;	
	}

	initLoadChain {
		loadChain = FunctionChain(nil, { 
			
//			"initLoadChain for ServerReady waiting 3 seconds before starting synths".postln;
//			{
				this.notifyObjects; 
//			}.defer(3);
			
		});
		Udef.all.values do: _.prepareToLoad(this);
		UniqueBuffer.onServer(server) do: _.prepareToLoad(this);
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
	
	loadSynthDefsAndBuffersAndStartSynths { loadChain.start; }

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
