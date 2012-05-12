


TouchSession {
	var <sessionManager, <id, <data;
	var <behavior;
	var <>displayProcesses;

	*new { | sessionManager, id, data |
		^this.newCopyArgs(sessionManager, id, data).init;
	}
	
	init {
//		behavior = sessionManager.sessionBehavior(this);
		if (sessionManager.verbose) { postf("blob % created: %\n", id, data.round(0.00001)); };
		sessionManager.sessionCreatedAction.(this);
	}

	sessionChanged  { | argData |
		// Ignore change if both x-speed and y-speed are 0
		if (sessionManager.verbose) { postf("blob % moved: %\n", id, argData.round(0.00001)); };
		sessionManager.sessionChangedAction.(this, argData);
		data = argData;
	}
	
	sessionEnded  {
		if (sessionManager.verbose) { postf("blob % ended.\n", id); };
		sessionManager.sessionEndedAction.(this);
	}
}

FiducialSession : TouchSession {
} 


