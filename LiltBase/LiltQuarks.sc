LiltQuarks : GitQuarks {
	/*
	Inheriting from GitRoot and specifying the classvars below
	amounts to the least common convention for a generalised quark extension mechanism:
	
	local quark dirs (typically in a git repositories) are searched in 'localQuarksDir'
	relative to 'thisGitBaseExtensionFileName'.
		
	The GitQuarks class should be in any SC git repository
	but put in SC's Platform.userExtensionDir only once (of cause ;-)
	*/
	classvar thisGitBaseExtensionFileName= "LiltBase";
	classvar localQuarksDir= "quarks.local";
	
	*initClass{ this.addLocalQuarks(thisGitBaseExtensionFileName, localQuarksDir) }
}