/* IZ 120309 

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
