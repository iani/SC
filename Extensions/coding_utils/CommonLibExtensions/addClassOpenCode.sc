/*
Added these modifications to remove a harmless but obnoxious error message in DocLisWindow when trying to open the definition file of a class by typing Command-J on its name. 

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