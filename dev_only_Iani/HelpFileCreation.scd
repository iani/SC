//:a

f = { | class |
	var classBasePath, classHelpPath, match, doc;
	// use the base name for comparison because quarks installed through links to Extensions
	// gives a different path than the Class
	classBasePath = class.filenameSymbol.asString.dirname;
	classHelpPath = classBasePath ++ "/Help";
	if (classHelpPath.pathMatch.size == 0) {
		format("mkdir %", classHelpPath.asCompileString).unixCmd;
	};
	(classHelpPath ++ "/" ++ class.name ++ ".*").pathMatch do: Document.open(_);		match = (classHelpPath = classHelpPath ++ "/" ++ class.name ++ ".html").pathMatch;
	if (match.size == 0) {
		{
			format("touch %", classHelpPath.asCompileString).unixCmd;
			0.2.wait;
			Document.open(classHelpPath);
		}.fork(AppClock);
	};
};

Library.global[\classdocgen] = f;

"Help file creation enabled".postln;

//:b
/*
f.(ListWindow);
*/