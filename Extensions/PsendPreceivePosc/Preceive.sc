/* IZ 2010 11 21
Define phrases to listen to (receive), in the form of osc messages send by Psend. 
*/

Preceive : Event {
	classvar <>verbose = false;
	classvar <>event;
	*new { | ... actions |
		^super.new.init(actions)
	}

	init { | actions |
		actions do: { | a | this.addAction(a.key, a.value) };
	}

	addAction { | key, action |
		if (key isKindOf: Integer) {
			this.addBeatAction(key, action);
		}{
			this.addTagAction(key, action);
		}
	}

	play {
		this.class.play(this);
	}

	*play { | piece |
		var newPiece;
		thisProcess.recvOSCfunc = this;
		this.getEvent;
		if (piece.notNil) { this.setEvent(piece) };
	}
		
	*getEvent { 
		if (event.isNil) { this.clear };
		^event;
	}

	*clear { event = this.new }

	*setEvent { | piece |
		if (piece.isNil) { ^this.getEvent };
		event = this.getEvent[piece] ? piece;
		^event;
	}
	
	*stopOSC {
		// shortcut for Preceove.verbose = false;
		verbose = false;
	}
	
	*postOSC {
		verbose = true;
		this.play;
	}

	*stop {	
		thisProcess.recvOSCfunc = nil;	
	}
	
	*value { | time, addr, msg | 
		if (verbose) { msg.postln; };		
 		event[msg[0]].value(msg[1..]);
	}

	*addAction { | key, action |
		this.getEvent.addAction(key, action);
	}

	addBeatAction { | beat, action |
		var beats;
		beats = this['/beat'];
		if (beats.isNil) {
			this['/beat'] = beats = BeatActions.new;
		};
		beats[beat] = action;
	}

	addTagAction { | tag, action |
		// later some more work here to deal with play, stop and phrases
		tag = tag.asString;
		if (tag[0] != $/) { tag = "/" ++ tag };
		this[tag.asSymbol] = action;
	}
	
	*removeAction { | key |
		if (event.notNil) {
			event.removeAction(key);
		}
	}

	removeAction { | key |
		if (key isKindOf: Integer) {
			this.removeBeatAction(key);
		}{
			this.removeTagAction(key);
		}
	}

	removeBeatAction { | beat, action |
		var beats;
		beats = this[\beats];
		if (beats.isNil) {
			this['beats'] = beats = IdentityDictionary.new;
		};
		beats[beat] = action;
	}

	removeTagAction { | tag, action |
		// later some more work here to deal with play, stop and phrases
		this[tag] = action;
	}

}


