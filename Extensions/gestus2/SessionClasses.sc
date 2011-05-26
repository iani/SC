TouchSession {
	var sessionManager, id, data;
	*new { | sessionManager, id, data |
		^this.newCopyArgs(sessionManager, id, data).init;
	}
	
	init {
		postf("%, started. ID: %, data: %\n", this.class.name, id, data);
	}

	sessionChanged  { | argData |
		postf("%, ENDED. ID: %, data: %\n", this.class.name, id, argData);
	}
	
	sessionEnded  {
		postf("%, ENDED. ID: %, data: %\n", this.class.name, id, data);
	}
	
}

FiducialSession : TouchSession {
}


