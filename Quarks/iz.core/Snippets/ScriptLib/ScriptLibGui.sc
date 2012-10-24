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
	classvar <>font, <windowShift = 0;
	var <scriptLib;
	var <snippetViews;

	*initClass {
		StartUp add: {
			font = Font.default.size_(10);
		}
	}

	gui {
		this.stickyWindow(scriptLib, windowInitFunc: { | window |
			window.name = scriptLib.path ? "ScriptLib";
			window.bounds = Rect(
				windowShift + 100,
				windowShift.neg + 50, 800, 800);
			windowShift = windowShift + 20 % 200;
			window.layout = VLayout(
				this.topMenuRow,
				this.itemEditor.hLayout(font),
				HLayout(
					this.listView('Folder').dict(scriptLib.lib).view.font = font,
					this.listView('File').branchOf('Folder').view.font = font,
					this.listView('Snippet').branchOf('File', { | adapter, name |
						format("//:%\n{ WhiteNoise.ar(0.1) }", name)
					})
						.view.font = font,
				),
				this.snippetButtonRow,
				[this.snippetCodeList, s: 3]
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
			this.itemEditMenu('Folder')
			.view.font_(font),
			this.itemEditMenu('File')
			.view.font_(font),
			this.itemEditMenu('Snippet')
			.view.font_(font),
		);
	}

	mainMenuAction { | actionIndex = 0 |
		[nil,	// MainMenu item. Just header. No action.
		{ scriptLib.new.addDefaults }, 		// New
		{ scriptLib.class.open; },		// Open
		{ scriptLib.save; },			// Save
		{ scriptLib.saveDialog },		// Save as
		{ Dialog.openPanel({ | path | scriptLib.import(path) }) }, // Import
		{ Dialog.savePanel({ | path | scriptLib.export(path) }) }, // Export
		][actionIndex].value;
	}

	snippetButtonRow {
		^HLayout(
			Button().font_(font).states_([["show list"], ["show snippet"]]).action_({ | me |
				snippetViews.index = me.value
			}),
			this.button('Snippet').action_({ | me |
				scriptLib.addSnippet(*(me.value.adapter.path ++ [me.getString, false]));
				me.getString.postln;
				"=============== SNIPPET SAVED ===============".postln;
			}).view.font_(font).states_([["save snippet"]]),
			this.button('Snippet').action_({ | me |
				me.getString.interpret;
			}).view.font_(font).states_([["eval"]]),
			this.button('Snippet').action_({ | me |
				ScriptMixer.currentProxy.postln;
			}).view.font_(font).states_([["play"], ["stop"]]),
			this.button('Snippet').action_({ | me |

			}).view.font_(font).states_([["eval proxy source"]]),
			this.button('Snippet').action_({ | me |
				scriptLib.addSnippet(*(me.value.adapter.path ++ [me.getString, true]));
			}).view.font_(font).states_([["new"]]),
			this.button('Snippet').action_({ | me |
				scriptLib.deleteSnippet(*(me.value.adapter.path ++ [me.item]));
			}).view.font_(font).states_([["delete"]]),
			this.button('Snippet').action_({ | me |

			}).view.font_(font).states_([["show buffers"], ["hide buffers"]]),
		)
	}

	snippetCodeList {
		^snippetViews = StackLayout(
			this.textView('Snippet').listItem({ | me |
				me.value.adapter.dict.atPath(me.value.adapter.path ++ [me.item])
			}).makeStringGetter.view.font_(Font("Monaco", 10)).tabWidth_(25),
			this.listView('Snippet', { | me |
				var snippets;
				snippets = me.value.adapter.dict.atPath(me.value.adapter.path);
				if (snippets.isNil) { [] } { snippets.asSortedArray.flop[1] };
			}).view.font_(Font("Monaco", 10)).background_(Color(1, 1, 0.9)),
		)
	}
}



