/* iz Thu 08 November 2012 11:10 AM EET
Experimental

*/

Script {
	var <>name, <>string;
	var <>envir;


	*new { | name, string |
		^this.newCopyArgs(name, string).init;
	}

	init {
		envir = ();
	}

	start {
		[name, thisMethod.name].postln;
		envir[\source] = envir use: { string.compile.valueEnvir(envir); };
	}

	stop {
		var stopFunc;
		[name, thisMethod.name].postln;
		if ((stopFunc = envir[\stop]).isNil) {
			envir[\source].postln;
			envir[\source].free;
		}{
			envir use: { stopFunc.valueEnvir(envir); };
		}
	}
}


