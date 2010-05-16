/* IZ 04xxxx - 050903 - 070116 - 080421f

ServerWatcher registers actions (functions) that an object may want to execute whenever the status of a Server boots, resets or quits. One of different methods can be used to register an object: onBoot, onReset, onQuit. The effect of each of these methods is to perform an action in one of the following 4 cases: 
1. onBoot: Do action when the server boots. For example to load a file or allocate some buffer every time a server boots.
2. onBootOrReset: Do action when the server boots but also when it frees all nodes (usually in response to Command-.) Can be used to start some groups or synths that need to be always present, for example for scope.  
2a: onReset: Do action only when the server resets (clears all nodes), but not when the server boots. This is used when for example the same object also registers itself under onBoot, in order to perform a different method when the server boots than when the server resets. A concrete example is found in the scope script. This script needs to behave differently when the server boots than when it resets: When the server boots, it will allocate a buffer and start a scope synth on that buffer as soon as the buffer is allocated. When the server resets, it will only restart the synth on the existing buffer. So the script will set the action of onBoot to allocate the buffer + make the synth, and onReset to only make the synth. It cannot use the onReset action when it boots because the synth must follow the buffer allocation so it must be done inside the Buffer.alloc completionMessage function.
 
3. onQuit: Do an action when the server quits. 

Wednesday, April 23, 2008 redoing using the init tree. The previous solution performed the onBoot actions before the NodeAllocator was initialized, thereby resulting in problems when starting Synths on boot. Therefore the onBoot actions will be also performed from within the init tree function. 

The ServerWatcher uses somewhat elaborate mechanisms for checking both the boot and the quit conditions. This is in order to circumvent the standard StatusWatcher which gives false notifications of server-startup and quit (\serverRunning update message) when a computer goes to sleep and wakes up. 

Check for booting: The ServerWatcher checks for the existence of a buffer that contains 4 samples with specific values. This both prevents false notification if the computer is waking up from sleep and works correctly if the server is already running when the client starts up. 

Check for quitting: Install an OSCresponderNode that watches the default Group of the server (its NodeID is 1), and then checks the value of the Server's serverRunning variable. This avoids false notifications of server quit that are sent by Server:aliveThread when the computer (and therefore the server with it also) goes to sleep. 

For responding to node-reset events, the ServerWatcher uses the Servers 'tree' variable. That means if you use the ServerWatcher for reset-watching, then you are obliged to use it for *any* actions that you want done at initTree time. This is more an extension than a limitation, as the ServerWatcher allows you to install multiple actions in the tree variable. The ServerWatcher will not touch the tree variable unless you start using its 'onReset' method. That is, it is possible to use the ServerWatcher along with any other way of occupying the tree variable, as long as the onReset method is not called on ServerWatcher. 

Examples:

Make object integer 1 respond to Server.local boot and quit:

1.onBoot({ |me| [me, "Ready for action"].postln; });
1.onQuit({ |me| [me, "bye bye"].postln; });

Same thing using ServerWatcher directly:

ServerWatcher.onBoot(1, { |me, server| [me, "my server booted"].postln; });
ServerWatcher.onQuit(1, { |me, server| [me, "my server quit"].postln; });

// Create a new Group whenever the local Server's nodes are reset: 

1.onReset({ Group(Server.local).postln; }, Server.local);

// stop responding to server boots, quits, and resets:
1.removeServer;

Also possible: 
f = { Group(Server.local).postln; };
f.onReset(Server.local);
// to remove: 
f.removeServer(Server.local);

050903 
Redo of ServerWatcher to prevent server-boot messages when the computer awakes from sleep. 
This works by allocating a buffer and filling it with an array of "magic numbers": #[1000, 2000, 314, 999]. When the StatusWatcher receives a serverRunning message and the serverRunning value of the server is true, then the StatusWachter checks first to see if the allocated buffer still has the same magic values. If yes, then the server was awaked from sleeping and no re-allocation or loading of any buffers is needed.

070116 Rewrote quit check mechanism using OSCresponderNode on default Group and added onReset mechanism. 

NOTE: Tried in vain to get around the buffer checking mechanism above, but could not find another solution! Keeping the buffer mechanism!

} */

ServerWatcher {
	classvar <all;
	var <server;	// server to watch
	var <buffer;	// buffer for checking if this is still the old server instance
	var <checkArray;	// array of 16 values to check for identity of test buffer.
	var <bootObjects, <quitObjects, <resetObjects;

	*initClass {
		all = IdentityDictionary.new;
	}

	*onBoot { | anObject, anAction, aServer, doNowIfRunning = false |
		this.for(aServer).onBoot(anObject, anAction, doNowIfRunning);
	}

	*onReset { | anObject, anAction, aServer, doNowIfRunning = false |
		this.for(aServer).onReset(anObject, anAction, doNowIfRunning);
	}
	
	*onBootOrReset { | anObject, anAction, aServer, doNowIfRunning = false |
		this.for(aServer).onBootOrReset(anObject, anAction, doNowIfRunning);
	}
	*onQuit { | anObject, anAction, aServer, doNowIfStopped = false |
		this.for(aServer).onQuit(anObject, anAction, doNowIfStopped);
	}


	onBoot { | anObject, anAction, doNowIfRunning = false |
		bootObjects[anObject] = bootObjects[anObject].add(anAction ? anObject);
		if (doNowIfRunning) {
			if (server.serverRunning) { anAction.(anObject) }
		}
	}

	onReset { | anObject, anAction, doNowIfRunning = false |
		resetObjects[anObject] = resetObjects[anObject].add(anAction ? anObject);
		if (doNowIfRunning) {
			if (server.serverRunning) { anAction.(anObject) }
		}
	}

	onBootOrReset { | anObject, anAction, doNowIfRunning = false |
		this.onBoot(anObject, anAction, doNowIfRunning);
		this.onReset(anObject, anAction, doNowIfRunning);
	}

	onQuit { | anObject, anAction, doNowIfStopped = false |
		quitObjects[anObject] = quitObjects[anObject].add(anAction ? anObject);
		if (doNowIfStopped) {
			if (server.serverRunning.not) { anAction.(anObject) }
		}
	}

	*for { | aServer |
		var instance;
		aServer = aServer ?? { Server.local };
		instance = all.at(aServer);
		if (instance.isNil) {
			instance = this.new(aServer);
			all.put(aServer, instance);
		};
		^instance;
	}

	*new { | server |
		^super.new.init(server);
	}

	init { | argServer |
		server = argServer ?? { Server.local };
		server.tree = { this.update(server, \init) };
		// create buffer for checking whether the server really started
		if (server.serverRunning) { this.makeCheckBuffer };
		// create node for watching whether the default server group ended
		// this avoids false notifications from when the server goes to sleep. 
		OSCresponderNode(server.addr, '/n_end', { | time, resp, msg |
			if (msg[1] == 1 and: { server.serverRunning.not }) {
				this.serverEnded;
			};
		}).add;
		server.addDependant(this);
		this.clear;
	}

	// remove anObject from both boot and quit list of aServers ServerWatcher
	// if aServer is nil, remove it from all servers. This way
	// if a view does not remember which server it is watching, it can still remove itself
	*remove { | anObject, aServer |
		(all.at(aServer) ?? { all.values }).asArray.do { | sw |
			sw.remove(anObject);
		};
	}

	// TODO: write individual remove methods for the 3 status-watching methods of ServerWatcher
	remove { | ... objects |
		objects do: { | ob |
			bootObjects.removeAt(ob);
			quitObjects.removeAt(ob);
			resetObjects.removeAt(ob);
		}
	}

	*clear { | server |
		var who;
		if ((who = all.at(server)).notNil) { who.clear };
	}

	clear {
		bootObjects = IdentityDictionary.new;
		quitObjects = IdentityDictionary.new;
		resetObjects = IdentityDictionary.new;
	}

	update { | theServer, statusMessage |
//		thisMethod.report(theServer, statusMessage); // please remove this statement when debugged ...
		// update is received by the server via its dependency mechanism. 
		// Here only check to see if the server really booted.
		// Server quit monitoring is done via an OSCresponderNode 
		// watching the default server group, created by init method above.
		if (statusMessage == \init and: { server.serverRunning }) {
				this.checkServer4BootOrReset;
		}
	}

	checkServer4BootOrReset {
		if (buffer.isNil) {
			this.makeCheckBuffer;
			^this.serverBooted;
		};
		buffer.updateInfo({ | buf |
			if (buf.numFrames != 4) {
				this.makeCheckBuffer;
				this.serverBooted;
			}{
				buffer.getn(0, 4, { | vals |
					if (vals != checkArray) {
						this.serverBooted
					}{
						this.serverReset
					};
				})
			}
		})
	}

	makeCheckBuffer {
		checkArray = #[1000, 2000, 314, 999];
		buffer = Buffer.sendCollection(server, checkArray, 1);
	}

	serverBooted {
		bootObjects.keysValuesDo { | object, actions |
			actions do: _.value(object);
		}
	}

	serverEnded {
		quitObjects.keysValuesDo { | object, actions |
			actions do:  _.value(object);
		};
	}

	serverReset {
		resetObjects.keysValuesDo { | object, actions |
			actions do:  _.value(object);
		};
	}
}
