/* 100318
This the Abstract class for TUIO, different varients. It has 2 Subclasses: the GESTUIO_2Dobj for tracking fiducials and the GESTUIO_2Dcur for blob detection.It shows how to use TUIO from Reactivision for a basic type of installation. It works solely from data received from Reactivision (v1.4) and Community Core Vision (v2.4). Run on MAC OS Snow Leopard. 

It detects 3 types of events:

objectBorn = A new object is born
objectDied = A object has died
objectMoved = A object has moved

*/
 
// --------------------------------------- Last Updated: Cygnus Thu, 18 March 2010, 15:37PM

/* Abstract_GESTUIO

a = GESTUIO_2Dobj.new;
a.start;
a.stop;

b = GESTUIO_2Dcur.new;
b.start;
b.stop;

p = Parametric_GESTUIO.new;


*/

Abstract_GESTUIO {
	classvar default;
	// classvar default_gestuio2Dcur, default_gestuio2Dobj;
		
	var <responder;
	var current_frame;
	var previous_frame;
	var mainSound, allIDs;
	

	*default {
	
	// UNDER CONSTRUCTION
	
	/* 	default_gestuio2Dobj = 	GESTUIO_2Dobj.new;
		if (default_gestuio2Dobj.isNil) { default_gestuio2Dobj = GESTUIO_2Dobj.new };
		^default_gestuio2Dobj; */
	
	/* 	default_gestuio2Dcur = GESTUIO_2Dcur.new;
		if (default_gestuio2Dcur.isNil) { default_gestuio2Dcur = GESTUIO_2Dcur.new };
		^default_gestuio2Dcur */
	
	}
	
	*new {
		^super.new.init;		
	}
	
	init {
		
		// CODE IN COMMENTS HAS BEEN MOVED TO EACH OF THE SUBCLASSES DUE TO DIFFERENT BEHAVIOURS
		
		/* responder = this.makeResponder;
		Server.default.waitForBoot {

			 SynthDef("objectBorn", {
				Out.ar(0, LFSaw.ar(Rand(600, 3000), 0, 0.1))
			}).send(Server.default);
			
			
			
		}; */	
	}
	
	makeResponder {
	
		// CODE IN COMMENTS HAS BEEN MOVED TO EACH OF THE SUBCLASSES DUE TO DIFFERENT BEHAVIOURS

		/* this cannot be used in the abstract class. it is the one that is defined by the 
		concrete subclasses to change the behavior.
		
		Following is the official "graceful" way for expressing this fact. 
		
		It is not actually necessary, it just generates an error message that informs the 
		developer about the rationale of this class and what went wrong. 
		
		Alternatively, you could just omit the definition of the present method. */
		
		^this.shouldNotImplement(thisMethod);
	}
	
	respondToOSC { | messageType, data |
		this.perform(messageType.asSymbol, data );
	}
	
/* Note: The messages received as first data argument in the OSC message from TUIO are: 
	fseq, set, alive.
	
	The responses to these are implemented by the three methods with the same names below. 
*/


	fseq { | data |
	/* fseq message received from TUIO. Do nothing. */
//		format("this was the fseq message, and the data was: %", data).postln;
	}
 
 
// 			------------- HERE STARTS THE CRUCIAL PART OF THE ALGORITHM ------------ 

// 1. ---- set 
	
	set { |data, sessionID, i, x, y, a, dX, dY, dA, m, r|
		 
		[sessionID, i, x, y, a, dX, dY, dA, m, r].postln;
		/* set message received from TUIO. Find if a new object is created
		or if an existing object has moved. */

//		format("this was the set message, and the data was: %", data).postln;
		format("set message. current frame is: %, data is: %", current_frame, data).postln;
		format("ClassID: %", i).postln;
		format("Position: %,%,%", x, y, a).postln;
		format("Velocity: %,%,%", dX, dY,dA).postln;
		format("Acceleration: %,%",m,r).postln;
		
		
		
		current_frame.do({ | item, i | 
//			[i, item].postln; 
			if (previous_frame.includes(item)) {
				this.objectMoved(item)
			}{
				format("set message detected new object. ID is: %, previous_frame should not include it: %", 
					item, previous_frame).postln;
				this.objectBorn(item)
			}
		});

	}
	
	// CODE IN COMMENTS HAS BEEN MOVED TO EACH OF THE SUBCLASSES DUE TO DIFFERENT BEHAVIOURS

	/* objectBorn { | objectID |
			
		allIDs.put(objectID, Synth("objectBorn"));

		format("previous frame before adding: %", previous_frame).postln;
		previous_frame = previous_frame add: objectID;
		format("previous frame AFTER adding: %", previous_frame).postln;
		
		format("an object is born, and its name is: %", objectID).postln;
		
		["object born, dict of currently active: ", allIDs].postln;
		
	} */

// 2. ---- alive 
	alive { | data |
	/* alive message received from TUIO. 
	Check all id's received to see which objects died. */
	
		if (allIDs.isNil) { allIDs = IdentityDictionary.new };
		
//		format("this was the alive message, and the data was: %", data).postln;
		previous_frame = current_frame;
		current_frame = data;

//	check if all objects in this frame exist in the previous one.


// 	check if all objects in the previous frame exist in the current one.			
		previous_frame do: { | item, i |
//			["previous_frame checking item iterating. Index of iteration:", i, item].postln;
			if (current_frame.includes(item).not){
				this.objectDied(item)
			}		
		}
	}
	
	objectDied { | diedID |
	
		allIDs.at(diedID).free;
		allIDs.removeAt(diedID);		
		format("a object is dead, and its name is: %", diedID).postln;
		["object died", allIDs].postln;
			
	}
	
	*start { this.default.start; }

	start { responder.add; }

	*stop { this.default.stop; }

	stop { responder.remove; }
	
	objectMoved { | movedID, freq |
					
	}

	
}
