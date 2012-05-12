/* A Resource for receiving and processing TUIO OSC message input */

TUIO : Resource {
	// Note: The array of current session IDs is stored in supeclass Resource instance var. 'object'

	var <responder;
	var <sessionDict;		// IdentityDictionary with all alive sessions by ID
	var <>verbose = false;
	var <>ignoreZeroXY = true;	// ignore blobs whose x and y are zero
		// CCV sends a lot of these blobs, and it is not clear if those data can be used. 
							
	
	var <>sessionCreatedAction, <>sessionChangedAction, <>sessionEndedAction;

	// For more complex, dynamic behaviors:
	var <>behavior;	/* says what sessions do: which sounds, changes etc. 		may change during a performance. Can be a function, an instance or anything, 
		since it can be used ad libitum from any of the three custom session action functions
		(sessionCreatedAction, sessionChangedAction, sessionEndedAction */ 

	init {
		this.makeResponder;
		sessionDict = IdentityDictionary.new;
		object = [];
	}

	makeResponder {
		responder	= OSCresponder(nil, this.oscMessage, { | time, addr, msg |
			this.perform(msg[1], msg[2..]);
		}).add;
	}

	oscMessage { ^'/tuio/2Dcur' }

	// activating, deactivating, posting events.

	*activate { ^this.new.activate; }
	activate { responder.add; }
	*deactivate { ^this.new.deactivate; }
	deactivate { responder.remove }
	
	*clear { ^this.new.clear }
	clear { this alive: [] }

	*verbose { this.new.verbose = true }
	*silent { this.new.verbose = false }

	// ====== set message: check which sessions were born or moved =====
	set { | data |
		var session; 		// session object, if already alive.
		var sessionID;
//		postf("set: %\n", data);
		if (ignoreZeroXY) {
			if (data[1] == 0 and: { data[2] == 0 }) { ^this; }
		};
		sessionID = data[0];
		session = sessionDict[sessionID];
		if (session.isNil) {
			this.sessionStarted(sessionID, data);
		}{
			this.sessionChanged(session, data);
		}
	}

	sessionStarted { | sessionID, data |
//		postf("%, NEW. ID: %, data: %\n", this.class.name, sessionID, data); // temporary... debug
		sessionDict[sessionID] = this.sessionClass.new(this, sessionID, data);
	}

	sessionClass { ^TouchSession }

	sessionChanged { | session, data |
		// note: TouchSession ignores sessionChanged messages if both x-speed and y-speed are 0 
		session.sessionChanged(data);
	}

	// ====== alive message: check which sessions have ended =====
	alive { | newSessionIDs |
		/* here we compare the activeSessionIDs received with those of the previous frame.
		If any IDs of the previous frame are not present in this frame, then these have died. */
//		object do: this.findOutIfSessionStillAlive(_, activeSessionIDs);
//		object = activeSessionIDs;
		/* New method: 
	Problem: 
		Find which session ids from the previous frame no longer exist in the list 
		of sessions received for the present frame. Those ids that no longer exist
		correspond to blobs that just "died", that is, disappeared from the picture. 
		
	Example: 
		If the previous frame contains the ids [10, 12, 15, 100, 200] and the frame data
		just received with alive are [100, 200, 201], this means that the session ids
		[10, 12, 15] have ceased to exist. Each of the session objects corresponding to those
		ids should be sent the message "sessionEnded". 

	Data: 
		object contains the active session IDs from previous frame
		newSessionIDs contains the active session IDs from the present (new) frame. 
		
	Algorithm / Implementation
		Use method 'difference', available to Collection classes. 
	
	Example: 
		[10, 12, 15, 100, 200] difference: [100, 200, 201]
		gives as result: 
		[10, 12, 15]			
	Conclusion: 
		Applying the 'difference' data to compare the previous frame to the new frame, 
		we have: 
		object difference: newSessionIDs
		*/
		if (verbose) { postf("alive: %\n", newSessionIDs) };
		object.difference(newSessionIDs) do: this.sessionEnded(_);
		object = newSessionIDs;		// store for next frame

	}

	sessionEnded { | sessionID |
		var session;
		session = sessionDict[sessionID];
		if (session.notNil) {	// skip ids that were ignored because of x y being 0
			session.sessionEnded;
			sessionDict.removeAt(sessionID);
		};			
	}

	// ====== fseq message: ... =====

	fseq { | frameID |
		/* we dont need to do something at this stage, just post some useful data */
//		if (verbose) { 
//			postf("% frame: %, sessions alive: %\n", this.class.name, frameID, object);
//		}
	}	
}

Fiducials : TUIO {

	oscMessage { ^'/tuio/2Dobj' }

	sessionClass { ^FiducialSession }

}