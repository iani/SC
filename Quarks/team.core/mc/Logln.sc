Logln {
	classvar <>logLevelGlobal= 1, <tab=28;
	// support 5 logLevels Ñ 0 Level always posts ...
	// !! needs to extend class Object !!
}

+ Object {
//	*logln { arg anything; if(isLog > 0,{(" log> ").post; anything.postln;});}
	
	// Logln.logLevelGlobal = -1 ; prevents all logging
	// a classvar logLevel = - 1 ; prevents individual class and its instances from logging

	logln { arg logMsg, level=0, who, lfB, lfE; // linefeed Begin and End 
		var logHead, spacer="", isClass, logLevelPerClass, intance="";
		if (who.isNil) {who = ""} {"*"++who++"*"};
		if (level <= Logln.logLevelGlobal, {	// total global filter ... is cheep
			isClass = this.class.isMetaClass;
			if (isClass) { logLevelPerClass = this.tryPerform(\logLevel) ? Logln.logLevelGlobal}
				{logLevelPerClass = this.class.tryPerform(\logLevel) ? Logln.logLevelGlobal};
			if (level <= logLevelPerClass) { // total global filter ... still cheep
				if (isClass.not) {
					intance = this.tryPerform(\name);
					if (intance.isNil) {intance = ""}{
						intance = " | " ++ intance};
				};
				lfB.do{ "\n".post};
				logHead = "*" ++ who ++ "<" 
					++ this.class.name ++ intance ? "" ++ ">";
				(Logln.tab - logHead.size).do{spacer = spacer ++ " "};
				(logHead ++ spacer ++ "-> ").post;
				if (logMsg.isNil,{"--".post},{logMsg.post});
				" |  ".postln;
				lfE.do{ "\n".post};
			}
		});
	}
		
	isLog {|level=0|
		var isClass, logLevelPerClass;
		if (level > Logln.logLevelGlobal) { 
			^false 
		}{
			isClass = this.class.isMetaClass;
			if (isClass) { logLevelPerClass = this.tryPerform(\logLevel) ? Logln.logLevelGlobal}
				{logLevelPerClass = this.class.tryPerform(\logLevel) ? Logln.logLevelGlobal};
			if (level > logLevelPerClass) { ^false } { ^true }
		}
	}
/*


*/
}