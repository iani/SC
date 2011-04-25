/* debugging the boot notification group not found problem */

ServerReady : UniqueObject {
	classvar servers;

	var <cmdPeriod = false, <serverBoot = false, <serverTree = false;

	*mainKey { ^\serverReady }

	*makeKey { | server | ^server ? Server.default; }

	init { | makeFunc |
		ServerTree.add(this, key);	
		CmdPeriod.add(this);
	}

	*add { | object, server | this.new(server) add: object; }
	*remove { | object, server | this.new(server) remove: object; }
	
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