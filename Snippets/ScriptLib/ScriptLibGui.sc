/* iz Mon 08 October 2012  4:48 PM EEST
Code management + performance tool.
Read/Display/Edit/Interact/Store code organized in files holding snippets of code.

The code is organized in a tree with 3 levels:

1. Folders
3. Files
4. Snippets (Self-sufficient Units of code)

The code is stored in a single file as SC text archive. It can also be exported as a hierarchy of folders and files, and reimported from files and folders.

- If importing a folder of folders, the folders of the top folder are added to the existing folder list
- Names of items in the list are created from the name of the file or folder.
- If there is a conflict (the new file or folder imported has the same name as an existing element),
  then the name of the newly imported item is changed

- The entire library instance is auto-saved every time that its window is closed or supercollider
  shuts down / re-compiles.
  If no path is defined for saving the data, then a file save dialog opens.

Menus:

- File Menu
- Folders
- Files
- Snippets

TODO:
- Scripts button in Lilt2DefaultMenu should open last opened ScriptLib instead of selection menu
- Open menu (RecentPaths) should have delete button.

*/

ScriptLibGui : AppModel {
	classvar <>font, <windowShift = 0;
	var <scriptLib;
	var <snippetViews;
	var <buffers;		// Dictionary of buffers selected by menus, inserted in code as variables

	*initClass {
		StartUp add: {
			font = Font.default.size_(10);
		}
	}

	gui {
		buffers = IdentityDictionary();
		this.stickyWindow(scriptLib, windowInitFunc: { | window |
			window.name = scriptLib.path ? "ScriptLib";
			window.bounds = Rect(
				windowShift.neg + 700,
				windowShift.neg + 280, 700, 580);
			windowShift = windowShift + 20 % 200;
			window.layout = VLayout(
				this.topMenuRow,
				this.itemEditor.hLayout(font),
				HLayout(
					this.listView('Folder')
					.dict(scriptLib.lib)
					.updater(scriptLib, \dict, { | me | me.value.dict(scriptLib.lib) })
					.view.font = font,
					this.listView('File').branchOf('Folder').view.font = font,
					this.listView('Snippet').branchOf('File', { | adapter, name |
						format("//:%\n{ WhiteNoise.ar(0.1) }", name)
					})
					.view.font_(font).keyDownAction_({ | view, char, mod, ascii |
						switch (ascii,
							27, { this.proxySelectWindow },
							13, { this.evalSnippet(mod) }, // return key,
							32, { this.toggleProxy }, // space key
						);
					}),
				),
				this.snippetButtonRow,
				this.bufferRow,
				[this.snippetCodeList, s: 3]
			);
			this.windowClosed(window, {
				scriptLib.save;
				this.objectClosed;
			});
			this.windowToFront(window, { ScriptLib.current = scriptLib; });
			this.addNotifier(scriptLib, \path, { | path | window.name = path });
		});
		scriptLib.loadConfig;
	}

	topMenuRow {
		^HLayout(
			this.popUpMenu(\topMenu,
				{ ["File Menu", "New", "Open", "Save", "Save as", "Revert (Reload)",
					"Make this Lib Default", "Import", "Export", "Reload Config"] }
			).view.font_(font).action_({ | me |
				this.mainMenuAction(me.value);
				me.value = 0
			}),
			this.popUpMenu(\topMenu,
			{ ["Server Menu", "Boot", "Quit All", "Add loaded buffers to config", "Edit Sound Files",
					"Mixer", "Scope", "Freqscope", "Scope + Freqscope"] }
			).view.font_(font).action_({ | me |
				this.serverMenuAction(me.value);
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
			{ ScriptLib.new.addDefaults.gui }, 		// New
			{ ScriptLib.open; },      // Open
			{ scriptLib.save; },      // Save
			{ scriptLib.saveDialog }, // Save as
			{ scriptLib.revert; },    // Revert
			{ RecentPaths(scriptLib.class.asSymbol).default = ScriptLib.all.findKeyForValue(scriptLib).asString; },
			{ Dialog.openPanel({ | path | scriptLib.import(path) }) }, // Import
			{ Dialog.savePanel({ | path | scriptLib.export(path) }) }, // Export
			{ scriptLib.loadConfig; }, // Reload Config
		][actionIndex].value;
	}

	serverMenuAction { | actionIndex = 0 |
		[nil,	// MainMenu item. Just header. No action.
			{ Server.default.boot },
			{ Server.killAll; },
			{ scriptLib.addLoadedBuffersToConfig },
			{ SoundFileGui(); },
			{ ScriptMixer() },
			{ Server.default.scope.window.bounds = Rect(0, 360, 200, 200); },
			{ Server.default.freqscope },
			{
				Server.default.scope.window.bounds = Rect(0, 360, 200, 200);
				Server.default.freqscope;
			},

		][actionIndex].value;
	}

	snippetButtonRow {
		^HLayout(
			Button().font_(font).states_([["list"], ["edit"]]).action_({ | me |
				snippetViews.index = me.value
			}),
			this.button('Snippet').action_({ | me |
				"EXPERIMENTAL - for development use only".postln;
				[{ this.getScript.stop}, { this.getScript.start}][me.view.value].value;
			}).view.font_(font).states_([["script>", nil, Color.green], ["script||", nil, Color.red]]),
			this.button('Snippet').action_({ | me |
				me.getString.interpret.postln;
			}).view.font_(font).states_([["eval", Color.red]]),
			this.button('Proxy')
			.proxyWatcher({ | me |
				me.checkProxy(me.value.adapter.item.checkEvalPlay(this.getValue('Snippet').getString))
			})
			.view.font_(font).states_([[">", nil, Color.green], ["||", nil, Color.red]]).fixedWidth_(30),
			this.button('Snippet').action_({ | me |
				this.getValue(\Proxy).item.evalSnippet(me.getString, start: false, addToSourceHistory: false);
			}).view.font_(font).states_([["set proxy source"]]),
			this.popUpMenu('Proxy').proxyList(ProxyCentral.default.proxySpace)
			.view.fixedWidth_(30).font_(font).background_(Color.yellow),
			this.button('Snippet').action_({ | me |
				scriptLib.addSnippetNamed(*(me.value.adapter.path ++ [me.value.item, me.getString]));
				me.getString.postln;
				"=============== SNIPPET SAVED ===============".postln;
				// Following can be removed when SC3.6 stops crashing at recompile with ScriptLibGui open.
				scriptLib.save;
			}).view.font_(font).states_([["save"]]),
			this.button('Snippet').action_({ | me |
				scriptLib.addSnippet(*(me.value.adapter.path ++ [me.getString, true]));
			}).view.font_(font).states_([["new"]]),
			this.button('Snippet').action_({ | me |
				scriptLib.deleteSnippet(*(me.value.adapter.path ++ [me.item]));
			}).view.font_(font).states_([["delete"]]),
			Button().states_([["mixer"]]).action_({ ScriptMixer.activeMixer }).font_(font),
//			this.button('Snippet').action_({ | me |
//				ProxyCodeEditor(ProxyCentral.default.proxySpace, this.getValue('Proxy').adapter.postln);
//			}).view.font_(font).states_([["proxy editor"]]),
			Button().action_({ SoundFileGui(); }).font_(font).states_([["samples"]]),
			Button().font_(font).states_([["set buffers"]]).action_({ this.updateBuffers }),
		)
	}

		// Experimental: Adding Script class
	getSnippet { // return the string of the current snippet / script
		var snippetVal;
		snippetVal = this.getValue('Snippet');
		^snippetVal.adapter.dict.atPath(snippetVal.adapter.path ++ [snippetVal.item]);
	}

	getScript {
		var snippetVal, path, name, snippet, script;
		snippetVal = this.getValue('Snippet');
		path = snippetVal.adapter.path ++ [name = snippetVal.item];
		snippet = snippetVal.getString;
		path = ['Scripts', scriptLib] ++ path;
		script = Library.at(*path);
		script ?? { Library.put(*(path ++ [script = Script(name)])); };
		script.string = snippet;
		^script;
	}

	bufferRow { // Buffer menus:
		var menuFont;
		menuFont = font.copy.size_(9);
		^[HLayout(
			*({ | i |
				var valName;
				valName = format("b%", i).asSymbol;
				this.popUpMenu(valName)
				.updater(BufferItem, \bufferList, { | me, names | me.items_(['-'] ++ names); })
				.do({ | me |
					me.items = ['-'] ++ Library.at['Buffers'].keys.asArray.sort
				})
				.addValueListener(this, \index, { | val |
					this.update1buffer(valName, val.adapter.item) })
				.view.font_(menuFont) //.fixedWidth_(82)
			} ! 8)
		), s: 1]
	}

	update1buffer { | valName, bufName | // insert variable declaration for chosen buffers to code
		buffers[valName] = if (bufName === '-') { nil } { bufName };
		this.updateBuffers;
	}

	updateBuffers {
		var bufStrings, varString, editor, source, lines;
		bufStrings = buffers.keys.asArray.sort collect: { | bname |
			format("% = '%'.b", bname, buffers[bname]);
		};
		varString = bufStrings[1..].inject(bufStrings[0], { | vars, s | vars prCat: ", " ++ s });
		if (varString.notNil) { varString = "var " ++ varString ++ ";" };
		editor = this.getValue('Snippet');
		source = editor.getString;
		lines = source.split($\n);
		if (lines[0][0] !== $/) { lines = ["//:"] ++ lines; };
		if (lines[1][..2] == "var") { lines[1] = varString; } { lines = lines.insert(1, varString); };
		lines remove: nil; // if no buffers chosen, then remove var declaration line
		scriptLib.addSnippetNamed(*(editor.adapter.path ++ [editor.item,
			lines[1..].inject(lines[0], { | code, line | code prCat: "\n" ++ line })
		]));
	}

	snippetCodeList {
		^snippetViews = StackLayout(
			this.textView('Snippet').listItem({ | me |
				me.value.adapter.dict.atPath(me.value.adapter.path ++ [me.item])
			}).makeStringGetter.view.font_(Font("Monaco", 9)).tabWidth_(25),
			this.snippetListView
		)
	}

	snippetListView {
		var listView, proxyIndex, colors;
		colors = [Color(0.95, 0.95, 0.96), Color(1, 1, 0.99)].dup(30).flat;
		listView = this.listView('Snippet', { | me |
			var snippets;
			snippets = me.value.adapter.dict.atPath(me.value.adapter.path);
			// Must do this here, because resetting the items of the list also resets the colors:
			{ me.view.colors = colors }.defer(0.03);
			if (snippets.isNil) { [] } { snippets.asSortedArray.flop[1] };
		}).view.font_(Font("Monaco", 9));
		listView.keyDownAction = { | view, char, mod, ascii |
			switch (ascii,
				13, { this.evalSnippet(mod) }, // return key,
				32, { this.toggleProxy }, // space key
				{
					proxyIndex = "12345678qwertyuiasdfghjkzxcvbnm," indexOf: char;
					proxyIndex !? { this.getValue('Proxy').index_(nil, proxyIndex) }
				}
			);
		};
		^listView;
	}

	proxySelectWindow {
		var w;
		w = Window(bounds: Rect(400, 100, 100, 600)).front;
		w.layout = VLayout(ListView(w)
			.items_(Array.new.addAll("12345678qwertyuiasdfghjkzxcvbnm,"))
			.keyDownAction_({ | me, char, mod, ascii |
				if (ascii == 13) { w.close }
			})
			.action_({ | me | this.getValue('Proxy').index_(nil, me.value) })
		)
	}

	evalSnippet { | mod = 0 |
		if (mod == 0) {
			this.getValue(\Proxy).item.evalSnippet(
				this.getValue('Snippet').getString, start: false, addToSourceHistory: false
			);
		}{
			this.getValue('Snippet').getString.postln.interpret;
		}
	}

	toggleProxy {
		this.getValue('Proxy').changed(\toggle);
	}
}



