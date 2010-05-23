/* IZ 100517
NetSync: Synchronize processes running on different computers in a network via OSC. 

*/

/*
a = SyncSender(Pbind(\event, '/beats'));
a.start;

c = SyncAction('/beats', { | ... args | args.postln })

b = SyncResponder.new;
b.addDependant({ | ... args | args.postln; });
c = SimpleController(b, '/test', { | receiver, message, time |
	format("receiver: %, message: %, time: %", receiver, message, time).postln
});
c.remove

b.deactivate;
b.activate;

a.clock.tempo = 2;

=====


a = SyncSender.new;
a.pattern = Pbind
a.start;

b = SyncResponder.new;
b.addDependant({ | ... args | args.postln; });
c = SimpleController(b, '/test', { | receiver, message, time |
	format("receiver: %, message: %, time: %", receiver, message, time).postln
});

Pcollect(['/test', _], Pseries(1, 1, inf))


*/

SyncSender {
	classvar <defaultSyncMessage = '/sync';
	var <>pattern;	// the pattern that produces the stream
	var <>clients;	// array of NetAddr to which the messages from the pattern are broadcast
	var <clockFunc; 	// the function that makes the clock each time that we start the SyncSender
	var <protoEvent; 	// protoEvent supplied to the pattern, with the clock and the clients
	var <clock;	// clock for running the pattern
	var <stream;		// stream that schedules and broadcasts the messages

	
	*new { | pattern, clients, clockFunc, protoEvent |
		// lazy initialization of netsync event 
		if (Event.partialEvents.playerEvent.eventTypes[\netsync].isNil) {
			Event.addEventType(\netsync, {
				// we are caching these to avoid accessing ~vars in the ~clients do: loop
				var oscMessage, syncEvent, clock; 
				var tempo, beats, beatsPerBar, baseBarBeat=0.0, baseBar;
				var args;
				oscMessage = ~oscMessage ? defaultSyncMessage;
				syncEvent = ~syncEvent;
				clock = ~clock;
				tempo = clock.tempo;
				beats = clock.beats.round.asInteger;
				beatsPerBar = clock.beatsPerBar;
				baseBarBeat = clock.baseBarBeat;
				baseBar = clock.baseBarBeat;
				args = ~args.(syncEvent, clock);
				~sender.broadcast(oscMessage, syncEvent, beats, tempo, beatsPerBar, baseBarBeat, baseBar, *args);
			});		
		}
		^this.newCopyArgs(pattern, (clients ? [NetAddr.localAddr]), 
			clockFunc ?? {{ TempoClock.new }}, protoEvent).init;
	}
	
	init {
		protoEvent = protoEvent ?? { (type: \netsync, sender: this, syncEvent: 'beats') };
	}

	broadcast { | ... args |
		clients do: { | client | client.sendMsg(*args) }
	}	
	
	start {
		/* clock must be made each time we start, since clocks stopped by CMD-. cannot restart */
		clock = clockFunc.(this);
		protoEvent[\clock] = clock;
		if (stream.isNil or: { stream.isPlaying.not }) {
			stream = pattern.play(clock, protoEvent);
		}
	}
	
	stop {
		if (stream.notNil) {
			stream.stop;
			stream = nil;
		}
	}
	
	pause {
		if (stream.notNil) {
			stream.pause;
		}	
	}

	reset {
		if (stream.notNil) {
			stream.reset;		
		}
	}

	resume {
		if (stream.notNil) {
			stream.resume;
		}
	}
}

SyncResponder {
	classvar <all;	// stores each responder under its syncMessage, because we should not have duplicate messages
	var <responder;
	var <actions;		// dictionary of lists of actions (FunctionList)
	var <dependants;	// dependants are necessary to update things that depend on the osc message itself

	*new { | syncMessage, activate = true |
		var new;
		syncMessage = syncMessage ? SyncSender.defaultSyncMessage;		if (all.isNil) { all = IdentityDictionary.new };
		new = all[syncMessage];
		if (new.notNil) {
			^new
		}{
			new = super.new.init(syncMessage, activate);
			all[syncMessage] = new;
			^new;	
		};
	}

	init { | syncMessage, activate |
		actions = IdentityDictionary.new;
		responder = OSCresponder(nil, syncMessage ? SyncSender.defaultSyncMessage, { | time, resp, msg |
			dependants do: { | d | d.update(this, *msg); };
			actions[msg[1]].(*msg[2..]);
		});
		if (activate) { this.activate };
	}
	
	addDependant { | dependant |
		if (dependants.isNil) {
			dependants = Set.new add: dependant
		}{
			dependants.add(dependant)
		}
	}

	removeDependant { | dependant |
		if (dependants.notNil) {
			dependants remove: dependant;
			if (dependants.size == 0) { dependants = nil }
		}
	}
	
	addAction { | syncEvent, func |
		actions[syncEvent] = actions[syncEvent] addFunc: func;
	}

	removeAction { | syncEvent, func |
		var flist;
		flist = actions[syncEvent];
		if (flist.isNil) { ^this };
		if (func === flist) {
			actions.removeAt(syncEvent)
		}{
			flist.removeFunc(func)		
		} 
	}

	activate { responder.add }

	deactivate { responder.remove }
}

/* add individual actions that respond to a syncEvent tag received by a SyncResponder
Any number of actions can be added to listen to the same syncEvent.
An action can be activated or deactivated by sending it the corresponding messages. 
*/
SyncAction {
	var <syncEvent;
	var <action;
	var <responder;
	*new { | syncEvent, action, syncMessage |
		^this.newCopyArgs(syncEvent, action, SyncResponder(syncMessage ? SyncSender.defaultSyncMessage)).activate;	}
	activate {
		responder.addAction(syncEvent, action);
	}
	deactivate { responder.removeAction(syncEvent, action) }	
}

