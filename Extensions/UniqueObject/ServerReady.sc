/* 

Send notifications after a Server has called initTree and created its root node, but only when it boots. 

Add to ServerReady objects that want to start Synths, Groups or routines right after a server boots, but that do not want to restart them when a Server re-inits its tree after CmdPeriod (after the user types Command-. to stop all synths)

*/

ServerReady : UniqueObject {
	classvar servers;

	var <cmdPeriod = false, <serverBoot = false, <serverTree = false;

	*mainKey { ^\serverReady }

	*makeKey { | server | ^server ? Server.default; }

	init { | makeFunc |
		ServerTree.add(this, key);	
		CmdPeriod.add(this);
	}

	*add { | object, function, server | this.new(server ? Server.default).add(object, function); }
	*remove { | object, server | this.at(server ? Server.default).remove(object); }
	
	add { | argObject, action |
		NotificationCenter.register(this, \reallyBooted, argObject, action);
	}
	
	addOneShot { | argObject, action |
		NotificationCenter.registerOneShot(this, \reallyBooted, argObject, action);
	}

	remove { | argObject | 
		NotificationCenter.unregister(this, \reallyBooted, argObject);
	}

	doOnCmdPeriod { cmdPeriod = true; }

	doOnServerTree {		
		if (cmdPeriod) {
			cmdPeriod = false;
		}{
			NotificationCenter.notify(this, \reallyBooted);
		};
	}	
}