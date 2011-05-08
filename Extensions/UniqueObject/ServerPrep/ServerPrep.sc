
ServerPrep {
	classvar <all;
	var <server;
	var <bufs, <defs, <synths, <routines, <actions;
	
	var <cmdPeriod = false;

	*initClass { all = IdentityDictionary.new; }

	*new { | server |
		var new;
		server = server ? Server.default;
		new = all[server];
		if (new.isNil) { all[server] = new = this.newCopyArgs(server).init };
		^new; 
	}

	init {
		bufs = BufLoader(this, server);			
		defs = DefLoader(this, server);			
		synths = SynthLoader(this, server);
		routines = RoutineLoader(this, server);
		actions = ServerActionLoader(this, server);
		CmdPeriod.add(this);
		ServerTree.add(this, server);	// on Server *boot*: load all registered Udefs and UniqueBuffers
	}

	doOnCmdPeriod { cmdPeriod = true; }

	doOnServerTree {
		if (cmdPeriod) {	// do not reload SynthDefs + Buffers on Server init tree
			cmdPeriod = false;			
		}{				// but always load them when the server boots
			defs.addAllUdefs;			// add all Udefs stored in this session
			bufs.addAllUniqueBuffers;	// add all UniqueBuffers stored in this session
		};
		this.loadAllObjects;			// load all objects added to the tree, in order
		this.notifyTree;	// add any functions from addToServerTree to actions
		// ensuring that their SynthDefs etc. will be started in the right order. 
	}

	notifyTree {
	// any object can add any action to initTree using addToServerTree
		this.notify(this.serverTreeMessage);
	}

	serverTreeMessage { ^\serverTree }

	addToServerTree { | object, action |
		// use NotificationCenter to attach any action to any object to perform on serverTree
		this.addListener(object, this.serverTreeMessage, {
			// perform this action after the previous load object chain has finished
			// this ensures that any SynthDefs, Buffers, Synths, Routines added by the action
			// will load in the correct order in a new loadAllObjects chain. 
//			this addAction: { action.defer(0.000001) };  // this is safest so far
// Now experimenting with new scheme where actions examines if the chain needs restarting
			this addAction: action;
		});
	}

	addBuffer { | buffer | bufs add: buffer }
	addDef { | def | defs add: def }
	addSynth { | action | synths add: action }
	addRoutine { | action | routines add: action }
	addAction { | action | actions add: action }

	loadAllObjects { 
		bufs.loadAllObjects; // bufs load defs when done, defs load synths when done etc.
	}

	loadDefs {
		// received from bufs (BufLoader) when done.
		// proceed with loading defs (SynthDefs in DefLoader), then synths, routines, actions
		defs.loadAllObjects;
	}
	loadSynths { synths.loadAllObjects; }
	loadRoutines { routines.loadAllObjects; }
	loadActions { actions.loadAllObjects; }

}