
ServerPrep {
	classvar <all;
	var <server;
	var <bufs, <defs, <synths, <routines, <actions;
	
	var cmdPeriod = false; 			// distinguish server boot from cmd period;
	var serverBootedResponder;		// Wait for server notification on boot: ['/done', '/notify']

	*initClass { all = IdentityDictionary.new; }

	*new { | server |
		var new;
		server = server ? Server.default;
		new = all[server];
		if (new.isNil) { all[server] = new = this.newCopyArgs(server).init };
		^new; 
	}

	/* ===== Internal stuff. See "Use these methods" section below for usage ===== */

	init {
		serverBootedResponder = OSCpathResponder(server.addr, ['/done', '/notify'], {
			defs.addAllUdefs;
			bufs.addAllBufferResources;
			this.loadAllObjects;
			this.notifyTree;
		}).add;
		bufs = BufLoader(this, server);			
		defs = DefLoader(this, server);			
		synths = SynthLoader(this, server);
		routines = RoutineLoader(this, server);
		actions = ServerActionLoader(this, server);
		CmdPeriod.add(this);
		// on Server *boot*: load all registered Udefs and BufferResources
		ServerTree.add(this, server);
	}

	cmdPeriod { cmdPeriod = true }

	doOnServerTree {
		if (cmdPeriod) {			// do not reload SynthDefs + Buffers on Server init tree
			cmdPeriod = false;
			this.loadAllObjects;	// load all objects added to the tree, in order
			this.notifyTree;	/* add any functions from addToServerTree to actions
			ensuring that their SynthDefs etc. will be started in the right order. */
		}
	/* Load of objects and tree notification at boot time triggered by serverBootedResponder */
	}

	// Allow any object to add any action to initTree using addToServerTree below.
	notifyTree { this.notify(this.serverTreeMessage); }

	serverTreeMessage { ^\serverTree }

	// bufs load defs when done, defs load synths when done etc
	loadAllObjects { bufs.loadAllObjects; }

	/* loadDefs is received from bufs (BufLoader) when done.
	proceed with loading defs (SynthDefs in DefLoader), then synths, routines, actions */
	loadDefs { defs.loadAllObjects; }
	loadSynths { synths.loadAllObjects; }
	loadRoutines { routines.loadAllObjects; }
	loadActions { actions.loadAllObjects; }

	/* ===== Use these methods to add objects to ServerPrep: ===== */

	addToServerTree { | object, action |
/* 	Use NotificationCenter to attach any action to any object to perform on serverTree.
	This action is performed after the previous load object chain has finished
	this ensures that any SynthDefs, Buffers, Synths, Routines added by the action
	will load in the correct order in a new loadAllObjects chain. */
		this.addListener(object, this.serverTreeMessage, { this addAction: action; });
		// Execute immediately if the server is already running:
		if (server.serverRunning) { this.notifyTree; };
/*	Very rarely, buffers could not be allocated on time. One may add a delay to the above 
	addActions by using the followig istead of { this addAction: action; }: 
			this addAction: { action.defer(0.000001); };  
*/
	}
	
	removeFromServerTree { | object |
		this.removeListener(object, this.serverTreeMessage);
	}

	*addBuffer { | ubuf | this.new addBuffer: ubuf }
	addBuffer { | ubuf | bufs add: ubuf }
	*addDef { | udef | this.new addDef: udef }
	addDef { | udef | defs add: udef }
	*addSynth { | func | this.new addSynth: func }
	addSynth { | func | synths add: func }
	*addRoutine { | func | this.new addRoutine: func }
	addRoutine { | func | routines add: func }
	*addAction { | func | this.new addAction: func }
	addAction { | func | actions add: func }

}
