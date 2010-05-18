/*

Parametrize the following properties using instance variables: 
- OSC message to respond to.
- Actions to perform on blob born, blob moved, blob died. 


p = Parametric_GESTUIO.new;

p.blobBorn = { | objectID |
	switch (objectID, 
		5, { Synth("blobBorn1") },
		10, { Synth("blobBorn2") }
	)
};

p.synthDefs = [
	SynthDef("blobBorn1", {
		Out.ar(0, LFSaw.ar(Rand(600, 3000), 0, 0.1))
	}),
	SynthDef("blobBorn2", {
		Out.ar(Rand(0, 1), SinOsc.ar(Rand(400, 1600), 0, 0.1 * EnvGen.kr(Env.perc, doneAction: 2)))
	})
]; 
	

p.start; 
p.stop;
*/

Parametric_GESTUIO : Abstract_GESTUIO {
	var <oscMessage;	// osc message to respond to. Creates OSCresponder;
	var <>blobBorn, <>blobMoved, <>blobDied; // actions to perform on blob events. 
	var <synthDefs;	// synthdefs to load on server boot, on init.
	var <>server;		// server to work with. 

	*new { | oscMessage, synthDefs |
		^super.new.init(oscMessage, synthDefs);
	}
	
	init { | argOscMessage, argSynthDefs |
		// provide some defaults. 
		oscMessage = argOscMessage ? '/tuio/2Dobj';
		server = server ? Server.default;
		this.synthDefs(*argSynthDefs);
		// create responder, parametrically ...
		responder = this.makeResponder;
	}
	
	synthDefs_ { | argSynthDefs |
		synthDefs = synthDefs addAll: argSynthDefs.asArray;
		this.loadSynthDefs;
	}

	loadSynthDefs {
		server.waitForBoot {
			synthDefs do: { | synthDef | synthDef.send(server) };
		};	
	}

	makeResponder {
 		^OSCresponder(nil, oscMessage, { | time, resp, msg |
			this.respondToOSC(msg[1], msg[2..])
		});		
	}

	objectBorn { | objectID |
			
		allIDs.put(objectID, blobBorn.(objectID, this));

		format("previous frame before adding: %", previous_frame).postln;
		previous_frame = previous_frame add: objectID;
		format("previous frame AFTER adding: %", previous_frame).postln;
		
		format("an object is born, and its name is: %", objectID).postln;
		
		["object born, dict of currently active: ", allIDs].postln;	
	}

	objectMoved { | movedID |
		blobMoved.(allIDs.at(movedID), this);
	}

	objectDied { | diedID |

		blobDied.(allIDs.at(diedID),this);
		allIDs.removeAt(diedID).free;		
		format("a object is dead, and its name is: %", diedID).postln;
		["object died", allIDs].postln;
		
	}

}