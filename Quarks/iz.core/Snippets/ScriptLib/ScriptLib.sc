/* iz Mon 08 October 2012  4:48 PM EEST

Code management + performance tool.

Read/Display/Edit/Interact/Store code organized in files holding snippets of code.

- Folders containing folders with can be imported.
- If importing a folder of folders, the folders of the top folder are added to the existing folder list
- Names of items in the list are created from the name of the file or folder.
- If there is a conflict (the new file or folder imported has the same name as an existing element),
  then the name of the newly imported item is changed

- Saving is done in archive form. Load from archive is equally possible.

- Both import from folder and export into folder of the whole tree or part of the tree is possible.


Real paths must be stored by the ScriptLibApp (subclass of AppModel) - not the ScriptLib, because they should not be stored when archiving / exporting, because they do not make sense when importing to different systems:

	var <archive_path;	// path of the top folder or the file from which the library
	var <folder_path;	// NOT! Multiple data can be imported to same instance from many paths

a = ScriptLib();

a.gui;


*/

ScriptLib {
	classvar <all; // dict of currently open instances by path: Avoid opening the same path twice.
	var <lib;		// MultiLevelIdentityDictionary of folders, files and scripts by name
		// lib has 4 levels: [folder name, file name, snippet name, snippet]

	*initClass {
		all = IdentityDictionary();
//		CocoaMenuItem.add(["New ScriptLib"], { this.new.addDefaults.gui });
// 		CocoaMenuItem.add(["Open ScriptLib"], { this.open });
	}

	*new { ^this.newCopyArgs(MultiLevelIdentityDictionary()); }

	addDefaults {
		this.addSnippet("-DefaultFolder", "-Defaults", "//:-defaultsnippet\n{ WhiteNoise.ar(0.1) }");
	}

	*open {
		RecentPaths.open(this.asSymbol, { | path |
			var instance;
			path = path.asSymbol;
			instance = all[path];
			instance ?? {
				instance = Object readArchive: path.asString;
				all[path] = instance;
			};
			instance.gui;
		})
	}

	save {
		var path;
		path = this.path;
		if (path.isNil) { this.saveDialog } { this saveToPath: path };
	}

	path { ^all findKeyForValue: this }

	saveDialog { RecentPaths.save(this.class.asSymbol, { | path | this saveToPath: path }); }

	saveToPath { | path |
		this.path = path;
		this writeArchive: path.asString;
		format("% saved to:\n%\n", this.class, path).postln;
	}

	path_ { | path |
		var previousPath;
		previousPath = this.path;
		previousPath ?? { all[previousPath] = nil };
		all[path.asSymbol] = this;
	}

	gui { ScriptLibGui(this).gui; }

	import { | path |
		PathName(PathName(PathName(path).parentPath).parentPath).folders do: this.importFolder(_);
		lib.dictionary.keys.asArray.sort.postln;
		lib.changed(\dict);
	}

	importFolder { | folderPath |
		var folderName;
		folderName = this.makeUniqueName(nil, folderPath.folderName.postln);
		(folderPath.fullPath ++ "*.scd").pathMatch do: { | filePath |
			this.importSnippets(folderName, filePath);
		};
	}

	makeUniqueName { | path, name, index |
		var newName;
		newName = format("%%", name, index ? "").asSymbol;
		if (lib.atPath(path.asArray.copy add: newName).isNil) {
			^newName;
		}{
			if (index.isNil) { index = 0 } { index = index + 1 };
			^this.makeUniqueName(path, name, index);
		}
	}

	importSnippets { | folderName, filePath |
		var fileName, file, string, positions;
		fileName = this.makeUniqueName(folderName, PathName(filePath).fileNameWithoutExtension);
		file = File(filePath, "r");
		string = file.readAllString;
		file.close;
		positions = string.findRegexp("^//:").flop.first;
		positions.collect({ | pos, i |
			string[pos .. (positions[i + 1] ?? { string.size }) - 1]
		}) do: this.addSnippet(folderName, fileName, _);
	}

	addSnippet { | folderName, fileName, snippet, uniqueName = true |
		var snippetName;
		snippetName = this.getSnippetName(snippet);
		if (uniqueName) { snippetName = this.makeUniqueName([folderName, fileName], snippetName) };
		lib.put(folderName.asSymbol, fileName.asSymbol, snippetName.asSymbol, snippet);
		lib.changed(\dict);
	}

	getSnippetName { | snippet |
		^(snippet.findRegexp("//:([A-Za-z\\-_][A-Za-z0-9\\-_]*)").flop[1] ?? { [nil, "_"] })[1];
	}

	deleteSnippet { | folderName, fileName, snippetName |
		lib.removeAt(folderName.asSymbol, fileName.asSymbol, snippetName.asSymbol);
		lib.changed(\dict);
	}

	export { | path |
		var fileName, file, snippet;
		path.postln;
		{
			format("mkdir %", path.asCompileString).postln.unixCmd;
			0.2.wait;
			lib.dictionary.keys do: { | folderName |
				format("mkdir %", (path +/+ folderName.asString).asCompileString).postln.unixCmd;
				0.2.wait;
				lib.at(folderName).keys do: { | fileName |
					file = File(path +/+ folderName +/+ fileName ++ ".scd", "w");
					fileName.postln;
					lib.at(folderName, fileName).keys.asArray.sort do: { | snippetName |
						file.putString(snippet = lib.at(folderName, fileName, snippetName));
						if (snippet.last !== Char.nl) {
							file.putString("\n");
						};
						0.01.wait;
					};
					file.close;
					0.1.wait;
				};
			};
			"EXPORT DONE".postln;

		}.fork(AppClock);
	}

	folders {

	}
}

