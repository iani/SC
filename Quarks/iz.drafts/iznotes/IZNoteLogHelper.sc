/* IZ 120309 
Provide menu item and shortcut for opening the help gui window to this quark,
*/

IZNotesHelp {
	*initClass {
		StartUp add: {
			CocoaMenuItem.add(["Open IZ notes"], { this.help })
				.setShortCut("z", true, true);
		};
	}
	
	*help { Help(this.filenameSymbol.asString.dirname).gui }
}
