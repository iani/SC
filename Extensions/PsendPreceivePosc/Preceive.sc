/* IZ 2010 11 21
Define phrases to listen to (receive), in the form of osc messages send by Psend. 

2011 01 17 Rewriting using OSCpathResponder
*/

Preceive {
	var <actions, <responders;
	
	*new { | ... actions |
		^this.newCopyArgs(actions).init;
	}

	init {
		actions do: { | a | this.addAction(a.key, a.value) };
	}

	addAction { | key, action |
		if (key isKindOf: Integer) {
			this.addBeatAction(key, action);
		}{
			if (key.isKindOf(Symbol) or: { key.isKindOf(String) }) {
				this.addMessageAction(key, action);	
			}{
				this.addPathAction(key, action);
			}
		}
	}

	play { responders do: _.add; }
	
	stop { responders do: _.remove; }
	
	addResponder { | responder |
		responders = responders add: responder;	
	}

	addBeatAction { | beat, action |
		this addResponder: OSCpathResponder(nil, ['beat', beat], action);
	}

	addMessageAction { | message, action |
		this addResponder: OSCresponderNode(nil, message.asSymbol, action);
	}


	addPathAction { | path, action |
		this addResponder: OSCpathResponder(nil, path, action);
	}

	*postOSC { thisProcess.recvOSCfunc = { | ... args | args.postln } }
	
	*stopOSC { thisProcess.recvOSCfunc = nil }

}


