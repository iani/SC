/* IZ 120309 
Provide menu item and shortcut for opening the help gui window to this quark,


Document.current.path;
*/

IZNotesHelp {
	*initClass {
		StartUp add: {
			CocoaMenuItem.add(["Open IZ notes"], { this.help })
				.setShortCut("z", true, true);
		};
		StartUp add: {
			CocoaMenuItem.add(["Save to IZ notes"], { this.saveNotes })
//				.setShortCut("z", true, true);
		};
	}
	
	*help { HelpQtCompatible(this.filenameSymbol.asString.dirname).gui }
	
	*saveNotes {
		var doc, name, path, string;
		string = Document.current.selectedString;
		name = format("notes_%.scd", Date.localtime.stamp);
		path = Platform.userAppSupportDir +/+ "Notes" +/+ name;
		{
			format("touch %", path.asCompileString).unixCmd;
			0.1.wait;
			doc = Document.open(path);
			0.1.wait;
			doc.string = string;
		}.fork(AppClock);
	}
}

