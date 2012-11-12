/* iz Thu 08 November 2012 11:10 AM EET
Experimental

*/

Script {
	var <>name, <scriptLib, <>string;
	var <>envir;


	*new { | name, scriptLib, string |
		^this.newCopyArgs(name, scriptLib, string).init;
	}

	init {
		envir = (script: this, scriptLib: scriptLib);
	}

	start {
		postf("Starting : %\n", name);
		string.postln;
		envir[\source] = envir use: { string.compile.valueEnvir(envir); };
	}

	stop {
		var stopFunc;
		postf("Stopping : %\n", name);
		if ((stopFunc = envir[\stop]).isNil) {
			envir[\source].free.stop; // effective kludge: free synths, stop routines + patterns
		}{
			envir use: { stopFunc.valueEnvir(envir); };
		}
	}
}


// Task and Routine support:

