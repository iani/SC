/* IZ 2010 11 21

Define phrases to listen to (receive), in the form of osc messages send by Psend. 

// 1. Define the structure of the piece to listen to
~score = Preceive(				// Top phrase. All other phrases are contained here. 
	\play->{ ... }, 			// at play of phrase do ...
	\end->{ ... }, 			// at end of phrase do ...
	1->{ ... }, 				// at beat 1 of phrase do ...
	'1'->Preceive( ... ), 		// at subphrase 1 do ...
	'bridge'->Preceive( ... ), 	// at subphrase named 'bridge' do ...
	'crash'->{ ... }, 			// at ID (tag) named 'crash' do ...
);

Note: The name of the class suggests that this should be a subclass of Pattern. 
This could be done in the future. 

Preceive.play;
Preceive.stop;
Preceive.verbose = true;
Preceive.verbose = false;

Preceive.addAction('status.reply', { "Hello".postln; });
Preceive.removeAction('status.reply');


Preceive.event;

Preceive[
	\a->1
]

Preceive.addAction(1, { "hello".postln; });

NetAddr.localAddr.sendMsg(\beat, 1);

Preceive.play("something");

Preceive.setEvent(\bla);



NetAddr.localAddr.sendMsg(\alpha);

Preceive.event;

Preceive.postOSC;

~pack1 = Preceive(
	\start->{ "playing".postln; },
	\a -> { "this was alpha".postln; },
	1 -> { "beat 1".postln; },
	5 -> { "beat 5".postln; },
	\end -> { "end".postln; }
);

~pack2 = Preceive(
	\start->{ "playing pack 2".postln; },
	\a -> { "this was pack 2 alpha".postln; },
	1 -> { "beat 1 pack 2".postln; },
	5 -> { "beat 5 pack 2".postln; },
	\xtra -> { "extra action pack 2".postln; },
	\end -> { "end".postln; }
);


p = Posc(\msg, Pseq([\a, \xtra], inf)).play;


~pack1.play;

~pack2.play;

~pack1.stop;

~pack2.stop;


*/

Preceive : Event {
	classvar <>verbose = false;
	classvar <>event;
	*new { | ... actions |
		^super.new.init(actions)
	}

	init { | actions |
//		if (event.isNil) { event = Event.new };
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
		if (piece.notNil) { this.addEvent(piece) };
	}
		
	*getEvent { 
		if (event.isNil) { this.clear };
		^event;
	}

	*clear { event = this.new }
	
	*addEvent { | piece |
		piece.postln;
		if (piece.isNil) { ^this.getEvent };
		event = this.getEvent; // [piece] ? piece;
		piece keysValuesDo: { | key, value |
			event[key] = value;	
		};
		[this, thisMethod.name, event].postln;
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

	stop {
		this.getEvent;
		this keysDo: { | key |
			event[key] = nil;
		}	
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
			this['/beats'] = beats = IdentityDictionary.new;
		};
		beats[beat] = action;
	}

	removeTagAction { | tag, action |
		// later some more work here to deal with play, stop and phrases
		this[tag] = action;
	}

}


