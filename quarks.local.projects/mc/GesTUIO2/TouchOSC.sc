/* A Resource for receiving and processing TouchOSC input */

TouchOSC : Resource {
	var <responder;
	var <sessionDict;		// IdentityDictionary with all alive sessions by ID
	var <>verbose = false;
	var <>sessionBehavior;	// says what sessions do: which sounds, changes etc. 						// may change during a performance
	// Note: The array of current session IDs is stored in object
	
	var <>sessionCreatedAction, <>sessionChangedAction, <>sessionEndedAction;

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
	
	activate {
		responder.add;	
	}

	oscMessage { ^'/tuio/2Dcur' }

	// ====== set message: check which sessions were born or moved =====
	set { | data |
		var session; 		// session object, if already alive.
		var sessionID;
		postf("set: %\n", data);
		sessionID = data[0];
		session = sessionDict[sessionID];
		if (session.isNil) {
			this.sessionStarted(sessionID, data);
		}{
			this.sessionChanged(session, data);
		}
	}

	sessionStarted { | sessionID, data |
		postf("%, NEW. ID: %, data: %\n", this.class.name, sessionID, data); // temporary... debug
		sessionDict[sessionID] = this.sessionClass.new(this, sessionID, data);
	}

	sessionClass { ^TouchSession }

	sessionChanged { | session, data |
		session.sessionChanged(data);
	}

	// ====== alive message: check which sessions have ended =====
	alive { | activeSessionIDs |
		/* here we compare the activeSessionIDs received with those of the previous frame.
		If any IDs of the previous frame are not present in this frame, then these have died. */
//		object do: this.findOutIfSessionStillAlive(_, activeSessionIDs);
//		object = activeSessionIDs;
	}

	findOutIfSessionStillAlive { | sessionID, activeSessionIDs |
		if ((activeSessionIDs includes: sessionID).not) {
			this.sessionEnded(sessionID);
		}
	}

	sessionEnded { | sessionID |
		var session;
		session = sessionDict[sessionID];
		session.sessionEnded;
		sessionDict.removeAt(sessionID);	
	}

	// ====== fseq message: ... =====

	fseq { | frameID |
		/* we dont need to do something at this stage, just post some useful data */
//		if (verbose) { 
//			postf("% frame: %, sessions alive: %\n", this.class.name, frameID, object);
//		}
	}

	// deactivating
	*deactivate {
		^this.new.deactivate;	
	}
	
	deactivate { responder.remove }
	
}

FiducialOSC : TouchOSC {

	oscMessage { ^'/tuio/2Dobj' }

	sessionClass { ^FiducialSession }

}