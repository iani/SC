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
	
	*help {
		var isQT;
		Platform.case(
			\osx, {
				isQT = GUI.current == QtGUI;
				GUI.cocoa;
				Help(this.filenameSymbol.asString.dirname).gui;
				if (isQT) { GUI.qt };
			},
			{
				Help(this.filenameSymbol.asString.dirname).gui;
			}
		);
	}
}
