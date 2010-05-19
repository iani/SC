/* 100318

This is the GESTUIO_2Dobj subclass. It enables the /tuio/2Dcur set profile of the TUIO protocol.

*/

GESTUIO_2Dcur : Abstract_GESTUIO {
		
	makeResponder {
 		^OSCresponder(nil, '/tuio/2Dcur', { | time, resp, msg |
			this.respondToOSC(msg[1], msg[2..])
		});		
	}
	
	init {
		responder = this.makeResponder;
		Server.default.waitForBoot {

			SynthDef("BlobBorn", {
				Out.ar(0, SinOsc.ar(Rand(400, 4000), 0, 1))
			}).send(Server.default);
		};
	}
	
	objectBorn { | objectID |
			
		allIDs.put(objectID, Synth("BlobBorn"));

		format("previous frame before adding: %", previous_frame).postln;
		previous_frame = previous_frame add: objectID;
		format("previous frame AFTER adding: %", previous_frame).postln;
		
		format("an object is born, and its name is: %", objectID).postln;
		
		["object born, dict of currently active: ", allIDs].postln;
		
	}
	
}