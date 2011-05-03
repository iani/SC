/*
Added these modifications to remove a harmless but obnoxious error message in Docks when trying to open the definition file of a class by typing Command-J on its name. 

*/


+ Class {
	openCodeFile {
		var openDoc, filename;
		// use the base name for comparison because quarks installed through links to Extensions
		// gives a different path than the Class
		filename = this.filenameSymbol.asString.basename;
		openDoc = Document.allDocuments detect: { | d | (d.path ? "-").basename == filename };
		if (openDoc.isNil) {
			this.filenameSymbol.asString.openTextFile(this.charPos, -1);
		}{
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
		(classHelpPath ++ "/" ++ this.name ++ ".*").pathMatch do: Document.open(_);		match = (classHelpPath = classHelpPath ++ "/" ++ this.name ++ ".html").pathMatch;
		if (match.size == 0) {
			{
				format("touch %", classHelpPath.asCompileString).unixCmd;
				0.2.wait;
				Document.open(classHelpPath);
				0.2.wait;
				doc = Document.current;
				if (doc.string.size.postln == 0) {
					doc.string_(format("%\n\n\Inherits from: %\n \n \n    ",
						this.name.asString,
						"".strcatList(this.superclasses collect: _.name);
					));
					{
						0.2.wait;
						doc.selectLine(1);	 0.2.wait;
						doc.font_(Font("Helvetica-Bold", 18), 
							doc.selectionStart.postln, doc.selectionSize.postln);
						0.2.wait;
						doc.selectLine(2);		0.2.wait;
						doc.font_(Font("Helvetica-Bold", 18), 
							doc.selectionStart, doc.selectionSize); 0.2.wait;
						doc.selectLine(3); 0.2.wait;
						doc.font_(Font("Helvetica-Bold", 12), 
							doc.selectionStart, doc.selectionSize); 
						doc.selectLine(4); 0.2.wait;
						doc.font_(Font("Helvetica", 12), 
							doc.selectionStart, doc.selectionSize); 
						doc.selectLine(5); 0.2.wait;
						doc.selectLine(5); 0.2.wait;
						doc.font_(Font("Helvetica", 12), 
							doc.selectionStart, doc.selectionSize); 0.2.wait; 
						doc.font_(Font("Helvetica", 12), 
							doc.selectionStart, doc.string.size - doc.selectionStart); 
					}.fork(AppClock);
				};
			}.fork(AppClock);			
		};
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