
/* 

store synthdefs in a global pool and make them available to synths on any server by sending them to the server at boot time, before starting any synth.

Does not store separate synthdef copies for different servers, just one global pool. 

SynthDefs created while the server is running are sent immediately. ServerReady ensures that they will be sent before starting any further instance of SynthResource. 

*/

Udef {
	classvar <all;
	var <def, <name;
	
	*initClass {
		all = MultiLevelIdentityDictionary.new;	
	}
	
	*new { | name, ugenGraphFunc, rates, prependArgs, variants, metadata, server |
		^super.new.init(name, ugenGraphFunc, rates, prependArgs, variants, metadata, server);
	}

	init { | argName, ugenGraphFunc, rates, prependArgs, variants, metadata, server |
		this.addDef(SynthDef(argName, ugenGraphFunc, rates, prependArgs, variants, metadata), server);
		name = argName.asSymbol;
	}

	addDef { | argDef, server |
		def = argDef;
		server = server.asTarget.server;
		all.putAtPath([server, argDef.name.asSymbol], this);
		ServerPrep(server).addDef(this);
	}
	
	play { | args, target, addAction = \addToHead |
		^name.play(args, target, addAction); // (name, args, target, addAction);
	}

	// using generic different name to be also used by BufferResource
	sendTo { | server | def.send(server) }
	
	*at { | server | ^this.onServer(server) }
	*onServer { | server |
		server = server ? Server.default;
		if (all.atPath(server).isNil) { ^[] };
		^all.leaves(server);
	}
}
