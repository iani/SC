/* will not be used. use SynthDef(...).add and add a mechanism that always sends the global library when a server boots 

See LoadSynthDefs Class

*/
 


SynthDefResource : AbstractServerResource {
	*mainKey { ^[SynthDefResource] /* ^[\objects] */ }

	*new { | name, ugenGraphFunc, rates, prependArgs, variants, metadata, server |
		^super.new(name.asSymbol, server.asTarget.server, 
			ugenGraphFunc, rates, prependArgs, variants, metadata, server);
	}

	init { | server, ugenGraphFunc, rates, prependArgs, variants, metadata |
			object = SynthDef(key[2], ugenGraphFunc, rates, prependArgs, variants, metadata);
			if (server.serverRunning) { this.sendToServer; };
	}

	sendToServer {
		postf("sending % % to server: %\n", object, key[2], key[1]);
		object.send(this.server) 
	}
	*initClass {	
		StartUp.add(this);
	}
	
	*doOnStartUp {
		Server.set do: ServerTree.add(this, _);
	}

	*doOnServerTree { | server | // thisMethod.name.postln; args.postln;
//		server.postln; this.onServer(server).postln;
//		this.loadAllSynthDefs(server);
	}


	*loadAllSynthDefs { | server |
		var synthDefs;
		server = server ? Server.default;
		synthDefs = this.onServer(server);
		synthDefs.postln;
		synthDefs.inject(nil, { | a, b |
			if (a.notNil) { NotificationCenter.registerOneShot(a.key, \loaded, a, {
				b.sendToServer;
				b.post; " sent to server ".postln;
			}) };
			b;
		});
		synthDefs.first.sendToServer;

/*		synthDefs.inject(nil, { | a, b |
			if (a.notNil) { NotificationCenter.registerOneShot(a.key, \loaded, a, { b.sendToServer }) };
			b;
		});
		NotificationCenter.registerOneShot(synthDefs.last.key, \loaded, synthDefs.last, {
			NotificationCenter.notify(server, \synthDefsLoaded);
		});
		
*/
//		SynthDefResource(server).doWhenLoaded({ buffers.first.makeObject });
//		synthDefs.last
	}

	doWhenLoaded { | func |
		/* 	do any actions that require synthdefs to be sent before they can be successful
			Used by BufferResource to load its buffers, because some of them may want to play right away
			using some synthdef sent here. 
			SynthDefResource.doWhenLoaded({ buffers.first.makeObject });
		*/
		NotifyOnce(this.server, \synthDefsLoaded, this, func);
		
	}	
}


Udef : SynthDefResource {} // synonym for SynthDefResource 

