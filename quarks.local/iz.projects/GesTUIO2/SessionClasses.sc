TouchSession {
	var <sessionManager, <id, <data;
	var <behavior;
	var <>displayProcesses;

	*new { | sessionManager, id, data |
		^this.newCopyArgs(sessionManager, id, data).init;
	}
	
	init {
		postf("%, NEW. ID: %, data: %\n", this.class.name, id, data); // temporary... debug
		behavior = sessionManager.sessionBehavior;
		sessionManager.sessionCreatedAction.(this);
	}

	sessionChanged  { | argData |
		postf("%, CHANGED. ID: %, data: %\n", this.class.name, id, argData);
		sessionManager.sessionChangedAction.(this);
	}
	
	sessionEnded  {
		postf("%, ENDED. ID: %, data: %\n", this.class.name, id, data);
		sessionManager.sessionEndedAction.(this);
	}
}

FiducialSession : TouchSession {
} 


