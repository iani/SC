/*
Added these modifications to remove a harmless but obnoxious error message in Docks when trying to open the definition file of a class by typing Command-J on its name. 

*/


+ Class {
	openCodeFile {
		var openDoc, filename, newDoc;
		// use the base name for comparison because quarks installed through links to Extensions
		// gives a different path than the Class
		filename = this.filenameSymbol.asString.basename;
		openDoc = Document.allDocuments detect: { | d | (d.path ? "-").basename == filename };
		if (openDoc.isNil) {
			newDoc = this.filenameSymbol.asString.openTextFileWithReturn(this.charPos, -1);
			Document.current = newDoc;
			newDoc.front;
			newDoc.toFrontAction.value;

		}{
			Document.current = openDoc;
			openDoc.front;
			openDoc.toFrontAction.value;
		}
	}
	openHelpFileLocally {
		var classBasePath, classHelpPath, match, doc;
		// use the base name for comparison because quarks installed through links to Extensions
		// gives a different path than the Class
		classBasePath = this.filenameSymbol.asString.dirname;
		classHelpPath = classBasePath ++ "/Help";
		if (classHelpPath.pathMatch.size == 0) {
			format("mkdir %", classHelpPath.asCompileString).unixCmd;
		};
		match = (classHelpPath ++ "/" ++ this.name ++ ".*").pathMatch;
		if (match.size == 0) {
			match = (classHelpPath = classHelpPath ++ "/" ++ this.name ++ ".html").pathMatch;
		}{
			match do: Document.open(_);
		};
		Library.at(\classdocgen).(this); // special stuff removed out of git
	}

}

+ Process {
	openCodeFile {
		var string, class, method, words;
		string = interpreter.cmdLine;
		if (string.includes($:), {
			words = string.delimit({ arg c; c == $: });
			class = words.at(0).asSymbol.asClass;
			if (class.notNil, {
				method = class.findMethod(words.at(1).asSymbol);
				if (method.notNil, {
					method.filenameSymbol.asString.openTextFile(method.charPos, -1);
				});
			});
		},{
			class = string.asSymbol.asClass;
			if (class.notNil, {
				class = class.classRedirect;
				class.openCodeFile;
			});
		});
	}
}