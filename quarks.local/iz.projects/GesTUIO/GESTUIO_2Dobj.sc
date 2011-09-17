/* 100318

This is the GESTUIO_2Dobj subclass. It enables the /tuio/2Dobj set profile of the TUIO protocol.

*/

GESTUIO_2Dobj : Abstract_GESTUIO {
		
	makeResponder {
 		^OSCresponder(nil, '/tuio/2Dobj', { | time, resp, msg |
			this.respondToOSC(msg[1], msg[2..])
		});		
	}
	
	init {
		responder = this.makeResponder;
		Server.default.waitForBoot {

			SynthDef("ObjectBorn", {
				Out.ar(0, LFSaw.ar(Rand(400, 4000), 0, 2))
			}).send(Server.default);
		};
	}
	
	objectBorn { | objectID |
			
		allIDs.put(objectID, Synth("objectBorn"));

		format("previous frame before adding: %", previous_frame).postln;
		previous_frame = previous_frame add: objectID;
		format("previous frame AFTER adding: %", previous_frame).postln;
		
		format("an object is born, and its name is: %", objectID).postln;
		
		["object born, dict of currently active: ", allIDs].postln;
		
	}
}