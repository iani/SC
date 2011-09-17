/* 100318
This the Abstract class for TUIO, different varients. It has 2 Subclasses: the GESTUIO_2Dobj for tracking fiducials and the GESTUIO_2Dcur for blob detection.It shows how to use TUIO from Reactivision for a basic type of installation. It works solely from data received from Reactivision (v1.4) and Community Core Vision (v2.4). Run on MAC OS Snow Leopard. 

It detects 3 types of events:

objectBorn = An new object is born
objectDied = An object has died
objectMoved = An object has moved
objectRotated = An object has rotated 

*/
 
// --------------------------------------- Last Updated: Cygnus Fri, 12 November 2010, 05:56AM
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
	var rotation_angleZ;
	var movement_X, movement_Y;
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
	
	set { | data |
		//data.postln;	
		//data[1].postln;
		var itemID;
		itemID = data[0];
		rotation_angleZ = data[4];
		//		movement_X = data[5];
//		movement_Y = data[6];
//		current_frame.do({ | item | 
			if (previous_frame.includes(itemID)) {
				this.objectMoved(*data)
			}{
				postf("previous frame: %, current frame: %, new id: %, data: %\n", previous_frame, current_frame, itemID, data);
				this.objectBorn(*data)
			}
	
			
//		});
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
//				postf("alive: object died: %\n", item);
				this.objectDied(item)
			}		
		}
	}
	
	objectDied { | diedID |
		NotificationCenter.notify(this, \objectDied, diedID);
		allIDs.at(diedID).free;
		allIDs.removeAt(diedID);	
	//	format("a object is dead, and its name is: %", diedID).postln;
	//	["object died", allIDs].postln;
			
	}
	
	*start { this.default.start; }

	start { responder.add; }

	*stop { this.default.stop; }

	stop { responder.remove; }
	
	objectMoved { | movedID, freq |
	
	}
				

	
}