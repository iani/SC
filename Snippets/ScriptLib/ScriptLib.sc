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

a = ScriptLib().gui;
a.gui;
*/

ScriptLib {
	classvar <configPath = '---Config---'; // scripts in this folder are loaded when this lib opens
	classvar <pathID = 'ScriptLib';   // for saving and retrieving paths with RecentPaths
	var <lib;		// MultiLevelIdentityDictionary of folders, files and scripts by name
	// lib has 4 levels: [folder name, file name, snippet name, snippet]

	*current {
		var current;
		current = Library.at('selectedLib');
		current ?? {
			current = this.new;
			this.current = current;
		};
		^current;
	}

	*current_ { | argLib |
		Library.put('selectedLib', argLib = argLib ?? { this.new });
		Library.changed('selectedLib', argLib);
	}

	*new { ^this.newCopyArgs(MultiLevelIdentityDictionary()); }

	*openDefault {
		var recentPaths, path;
		recentPaths = RecentPaths(this.asSymbol);
		path = recentPaths.default;
		if (path.isNil) { this.open; } { this.loadFromArchive(path).gui; }
	}

	*open {
		RecentPaths.open(this.asSymbol, { | path |
			this.loadFromArchive(path).gui;
			},{
			this.new.addDefaults.gui;
		})
	}

	*loadFromArchive { | path |
		^RecentPaths(pathID).selectExistingOrOpen(path, { Object readArchive: path.asString });
	}

	path { ^RecentPaths.getPathFor(pathID, this) }

	addDefaults {
		this.addSnippet("-DefaultFolder", "-Defaults", "//:-defaultsnippet\n{ WhiteNoise.ar(0.1) }");
	}

	loadConfig {
		var autoLoadBuffers;
		// load all scripts in Folder "---Config---"
		lib.leafDoFrom(configPath, { | path, script | script.interpret });
		// Load buffers in auto-load file:
		(autoLoadBuffers = lib.at('---Config---', 'Buffers-Autoload')) !? {
			autoLoadBuffers.values do: { | script | script.interpret.loadIfNeeded; };
		};
		// Currently not used:
		// scripts that access other scripts must wait for load to finish:
//		this.changed(\loadedConfig);
	}

	// Currently not used:
	// scripts that access other scripts must wait for load to finish:
//	doWhenLoaded { | action |
//		this.addNotifierAction(this, \loadedConfig, action.(this));
//	}

	// Sound files and buffers
	buffers { ^lib.atPath(this.bufferConfigPath) ?? { IdentityDictionary() } }
	bufferConfigPath { ^[configPath, 'Buffers-Autoload'] }
	soundFileConfigPath { ^[configPath, 'SoundFiles'] }

	addLoadedBuffersToConfig { Library.at('Buffers') do: this.addBuffer(_); }

	addSoundFile { | bufferItem |
		if (bufferItem.isNil) { ^"Cannot add nil Buffer item".postln };
		this.addSnippetNamed(*(this.soundFileConfigPath ++ [bufferItem.nameSymbol,
				format("//:%\nBufferItem(\n%\n).loadIfNeeded", bufferItem.nameSymbol, bufferItem.name.asCompileString)
			])
		);
//		if (this.isCurrent) { bufferItem.loadIfNeeded };
	}

	addBuffer { | bufferItem |
		if (bufferItem.isNil) { ^"Cannot add nil Buffer item".postln };
		this.addSnippetNamed(*(this.bufferConfigPath ++ [bufferItem.nameSymbol,
				format("//:%\nBufferItem(\n%\n).loadIfNeeded", bufferItem.nameSymbol, bufferItem.name.asCompileString)
			])
		);
		if (this.isCurrent) { bufferItem.loadIfNeeded };
	}

	isCurrent { ^this.class.current === this }
	removeSoundFile { | scriptName |
		if (scriptName.isNil) { ^"Cannot remove nil Buffer item".postln };
		this.deleteSnippet(*(this.bufferConfigPath ++ [scriptName.asSymbol]));
		if (this.isCurrent) { BufferItem.free(scriptName) };
	}

	revert {
		var path;
		(path = ScriptLib.all.findKeyForValue(this).asString) ?? {
			^"This ScriptLib has not been saved yet. Cannot revert".postln;
		};
		lib = Object.readArchive(path).lib;
		this.changed(\dict);
	}

	save {
		RecentPaths.save(pathID, { | path | this writeArchive: path.asString }, this);
	}

	savePanel {
		RecentPaths.savePanel(pathID, { | path | this saveToPath: path }, this);
	}

	saveToPath { | path |
		RecentPaths.saveToPath(pathID, { this writeArchive: path.asString }, this, path );
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
		this.addSnippetNamed(folderName, fileName, snippetName, snippet);
	}

	getSnippetName { | snippet |
		^(snippet.findRegexp(this.snippetNameRegexp).flop[1] ?? { [nil, "_"] })[1];
	}

	snippetNameRegexp { ^"\\A//:([A-Z0-9a-z\\-_][A-Za-z0-9\\-_]*)" }

	replaceSnippetName { | snippet, newName |
		^"//:" ++ newName ++ snippet[(snippet.findRegexp(this.snippetNameRegexp) ?? { [[0, ""]] })[0][1].size..];
	}

	addSnippetNamed { | folderName, fileName, snippetName, snippet |
		lib.put(folderName.asSymbol, fileName.asSymbol, snippetName.asSymbol, snippet);
		lib.changed(\dict);
		Library.changed(\selectedLib); // update SoundFileGui if open
	}

	deleteSnippet { | folderName, fileName, snippetName |
		lib.removeAt(folderName.asSymbol, fileName.asSymbol, snippetName.asSymbol);
		lib.changed(\dict);
		Library.changed(\selectedLib); // update SoundFileGui if open
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

	// ============ UNDER DEVELOPMENT: ============

	 // evaluate a snippet and return the result
	getSnippetValue { | path | ^this.getSnippet(path).interpret; }

	getSnippet { | path | ^lib.atPath(path) }

	// evaluate all snippets under path and collect the results in dictionary
	collectValues { | path |

	}
	folders { // TODO?

	}
}

