
ServerPrep : UniqueObject {
	var <server;
	var <bufs, <defs, <actions;
	var <cmdPeriod = false;

	init {
		server = key[1];
		bufs = BufLoader(this, server);			
		defs = DefLoader(this, server);			
		actions = ServerActionLoader(this, server);
		CmdPeriod.add(this);
		ServerTree.add(this, server);
	}

	doOnCmdPeriod { cmdPeriod = true; }

	doOnServerTree {
		if (cmdPeriod) {
			cmdPeriod = false;
		}{
			this.loadAllObjects;
		};
	}

	addBuffer { | buffer | bufs add: buffer }
	addDef { | def | postf("def received: %\n", def.def.name); defs add: def }
	addAction { | action | actions add: action }

	loadAllObjects { 
		bufs.loadAllObjects; // bufs load defs when done, defs load actions when done
//		defs.loadAllObjects;	// tested OK
	}

	loadDefs {
		// received from bufs (BufLoader) when done.
		// proceed with loading defs (SynthDefs in DefLoader)
		defs.loadAllObjects;
	}

	loadActions {
		// received from defs (DefLoader) when done.
		// proceed with loading actions (synths and other functions in ServerActionLoader)
		actions.loadAllObjects;
	}

}