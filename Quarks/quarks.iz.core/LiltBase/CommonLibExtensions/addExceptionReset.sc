/*

Reset ServerReady if an Exception occurs, to enable restarting the next time that ServerReady is needed. 

*/

+ Exception {

	*new { arg what;
		var backtrace;
	// permit to use Cmd-Shift-X for evaluating code again. See classes Code and ServerPrep
		ServerPrep.initClass;
		if (debug) { backtrace = this.getBackTrace.caller };
		^super.newCopyArgs(what ? this.name, backtrace)
	}
	
}

