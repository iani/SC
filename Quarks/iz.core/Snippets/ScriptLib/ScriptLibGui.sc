/* iz Mon 08 October 2012  4:48 PM EEST

Code management + performance tool. 

Read/Display/Edit/Interact/Store code organized in files holding snippets of code. 


- New files and folders can be imported. 
- If importing a folder of folders, the folders of the top folder are added to the existing folder list
- Names of items in the list are created from the name of the file or folder. 
- If there is a conflict (the new file or folder imported has the same name as an existing element), 
  then the name of the newly imported item is changed 
  
- Saving is done in archive form. Load from archive is equally possible.

- Both import from folder and export into folder of the whole tree or part of the tree is possible. 

- The instance auto-saves its data every time that its window is closed or supercollider 
  shuts down / re-compiles. 
  If no path is defined for saving the data, then a file save dialog opens.

Menus: 

- Main Menu
- Folders
- Files
- Snippets



*/


ScriptLibGui : AppModel {
	classvar <>font;
	var <scriptLib;

	*initClass {
		StartUp add: {
			font = Font.default.size_(10);
		}
	}

	gui {
		this.stickyWindow(scriptLib, windowInitFunc: { | window |
			window.name = scriptLib.path ? "ScriptLib";
			window.bounds = Rect(200, 200, 1000, 700);
			window.layout = VLayout(
				this.topMenuRow,
				HLayout(
					this.folderNameList,
					this.fileNameList,
					this.snippetNameList,
				),
				this.snippetButtonRow,
				this.snippetList,
			);
			this.windowClosed(window, {
				scriptLib.save;
				this.objectClosed;
			})
		});
	}

	topMenuRow {
		^HLayout(
			this.popUpMenu(\topMenu, 
			{ ["Main Menu", "New", "Open", "Save", "Save as", "Import", "Export"] }
			).view.font_(font).action_({ | me |
				this.mainMenuAction(me.value);
				me.value = 0
			}),
			this.popUpMenu(\folders, { ["Folders:", "New", "Rename", "Delete"] })
			.view.font_(font),
			this.popUpMenu(\files, { ["Files:", "New", "Rename", "Delete"] })
			.view.font_(font),
			this.popUpMenu(\snippets, { ["Snippets:", "New", "Rename", "Delete"] })
			.view.font_(font),

/*
			Button().states_([["Import"]]).action_({
				Dialog.openPanel({ | path | scriptLib import: path });
			}),
			Button().states_([["Export"]]).action_({
				Dialog.savePanel({ | path | scriptLib export: path });
			}),
			Button().states_([["Save"]]).action_({ scriptLib.save }),
			Button().states_([["Save as"]]).action_({ scriptLib.saveDialog; }),
*/
		);
	}

	mainMenuAction { | item = 0 |
		item.postln;
	}

	folderNameList {
		^nil
	}
	fileNameList {
		^nil
	}
	snippetNameList {
		^nil
	}
	snippetButtonRow {
		^nil
	}
	snippetList {
		^nil
	}
}



