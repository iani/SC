
/* 

store synthdefs in a global pool and make them available to synths on any server by sending them to the server at boot time, before starting any synth.

Does not store separate synthdef copies for different servers, just one global pool. 

SynthDefs created while the server is running are sent immediately. ServerReady ensures that they will be sent before starting any further instance of UniqueSynth. 

*/

Udef {
	classvar <all;
	var <def;
	
	*initClass {
		all = IdentityDictionary.new;	
	}
	
	*new { | name, ugenGraphFunc, rates, prependArgs, variants, metadata, server |
		^super.new.init(name, ugenGraphFunc, rates, prependArgs, variants, metadata, server.asTarget.server);
	}

	init { | name, ugenGraphFunc, rates, prependArgs, variants, metadata, server |
		def = SynthDef(name, ugenGraphFunc, rates, prependArgs, variants, metadata);
		all[name.asSymbol] = this;
		this.prepareToLoad(ServerReady(server));
	}

	prepareToLoad { | serverReady |
//		postf("% preparing to load\n", this);
		serverReady addFuncToLoadChain: { def.send(serverReady.server); 
			
//					postf("%  SENT TO SERVER\n", this);

			}
	}

}
	