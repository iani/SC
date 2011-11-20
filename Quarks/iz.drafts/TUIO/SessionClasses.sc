


TouchSession {
	var <sessionManager, <id, <data;
	var <behavior;
	var <>displayProcesses;

	*new { | sessionManager, id, data |
		^this.newCopyArgs(sessionManager, id, data).init;
	}
	
	init {
//		postf("%, NEW. ID: %, data: %\n", this.class.name, id, data); // temporary... debug
//		behavior = sessionManager.sessionBehavior(this);
		if (sessionManager.verbose) { postf("blob % created: %\n", id, data); };
		sessionManager.sessionCreatedAction.(this);
	}

	sessionChanged  { | argData |
//		postf("%, CHANGED. ID: %, data: %\n", this.class.name, id, argData);
		if (sessionManager.verbose) { postf("blob % moved: %\n", id, argData); };
		sessionManager.sessionChangedAction.(this);
	}
	
	sessionEnded  {
//		postf("%, ENDED. ID: %, data: %\n", this.class.name, id, data);
		sessionManager.sessionEndedAction.(this);
	}
}

FiducialSession : TouchSession {
} 


