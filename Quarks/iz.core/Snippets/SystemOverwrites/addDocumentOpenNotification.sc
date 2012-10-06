/* IZ Sun 02 September 2012 12:56 PM EEST

Add notification when a Document opens so that document management guis such as Dock may update 

*/

+ Document {
	*open { | path, selectionStart=0, selectionLength=0, envir |
		var doc, env;
		env = currentEnvironment;
		if(this.current.notNil) { this.current.restoreCurrentEnvironment };
		doc = Document.implementationClass.prBasicNew.initFromPath(
			path, selectionStart, selectionLength
		);
		if (doc.notNil) {
			doc.envir_(envir)
		} {
			currentEnvironment = env
		};
		this.changed(\docOpened, doc);
		^doc
	}
}