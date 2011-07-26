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
		sessionManager.newSession(this);
	}

	sessionChanged  { | argData |
		postf("%, CHANGED. ID: %, data: %\n", this.class.name, id, argData);
		sessionManager.sessionChanged(this);
	}
	
	sessionEnded  {
		postf("%, ENDED. ID: %, data: %\n", this.class.name, id, data);
		sessionManager.sessionEnded(this);
	}
	
}

FiducialSession : TouchSession {
}


