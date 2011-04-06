/* 100318

This is the GESTUIO_2Dobj subclass. It enables the /tuio/2Dcur set profile of the TUIO protocol.
b = Buffer.cueSoundFile(s, "sounds/Drop.aiff", 0, 2, 2000000);
b.play;

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
				Out.ar(0, LFPulse.ar(Rand(400, 1000), 0, 0.5 * EnvGen.kr(Env([0, 0.5, 0], [0.2, 0.2]),doneAction: 2)))
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


// --------- Buffer Example ------------



// Buffer.read(s, "sounds/Ultrabeat.aif");
		
		
	/*	(
		x = SynthDef(\drumloop, { arg out = 0, bufnum;
			Out.ar( out,
				PlayBuf.ar(1, bufnum, BufRateScale.kr(bufnum))
			)
		}).play(s,[\bufnum, b]);
		)
 */
