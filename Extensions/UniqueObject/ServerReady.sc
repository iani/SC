/* 

Send notifications after a Server has called initTree and created its root node, but only when it boots. 

Add to ServerReady objects that want to start Synths, Groups or routines right after a server boots, but that do not want to restart them when a Server re-inits its tree after CmdPeriod (after the user types Command-. to stop all synths)

*/

ServerReady : UniqueObject {
	classvar servers;

	var <>server, <cmdPeriod = false;

	*mainKey { ^[\serverReady] }

	*makeKey { | server | ^this.mainKey ++ [server ? Server.default]; }

	init {
		CmdPeriod.add(this);
		server = key[1];
		ServerTree.add(this, server);
		object = IdentityDictionary.new;
	}

	doOnCmdPeriod { cmdPeriod = true; }

	doOnServerTree {		
		if (cmdPeriod) {
			cmdPeriod = false;
		}{
//			NotificationCenter.notify(this, \reallyBooted);
			object.asKeyValuePairs pairsDo: { | obj, func | func.value(obj, server, this); };
		};
	}	

	*register { | object, function, server | ^this.new(server ? Server.default).register(object, function); }
	register { | argObject, action |
//		NotificationCenter.register(this, \reallyBooted, argObject, action);
		object[argObject] = action;
	}

	*registerOneShot { | object, function, server | ^this.new(server).registerOneShot(object, function) }

	registerOneShot { | argObject, action |
//		NotificationCenter.registerOneShot(this, \reallyBooted, argObject, action);
		object[argObject] = { | obj, server, me |
			action.(obj, server, me);
			object.removeAt(obj);
		}
	}

	*unregister { | object, server | 
		
		this.new(this.makeKey(server ? Server.default)).remove(object); }
	
	

	unregister { | argObject | 
		NotificationCenter.unregister(this, \reallyBooted, argObject);
	}

}