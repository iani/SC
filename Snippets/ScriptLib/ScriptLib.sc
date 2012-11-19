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

a = ScriptLib().gui;
a.gui;
*/

ScriptLib {
	classvar <configPath = '---Config---'; // scripts in this folder are loaded when this lib opens
	classvar <pathID = 'ScriptLib';   // for saving and retrieving paths with RecentPaths
	var <lib;		// MultiLevelIdentityDictionary of folders, files and scripts by name
	var <soundFilePath;
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

	isCurrent { ^this.class.current === this }

	*new { ^this.newCopyArgs(MultiLevelIdentityDictionary()).init; }

	init { soundFilePath = ['SoundFiles', this] }

	*openDefault {
		RecentPaths.openDefault(pathID,
			{ | path | this.loadFromArchive(path).gui },
			{ this.new.addDefaults.gui }
		)
	}

	*open {
		RecentPaths.openDialog(pathID, { | path |
			this.loadFromArchive(path).gui;
			},{
			this.new.addDefaults.gui;
		})
	}

	*loadFromArchive { | path |
		^Object readArchive: path.asString;
	}

	path { ^RecentPaths.getPathFor(pathID, this) }

	proxySpace { ^ProxyCentral.default.proxySpace } // may change at some later point

	addDefaults {
		this.addSnippet("-DefaultFolder", "-Defaults", "//:-defaultsnippet\n{ WhiteNoise.ar(0.1) }");
	}

	loadConfig {
		var autoLoadBuffers, autoLoadPath, bufferDebugPath;
		// load all scripts in Folder "---Config---"
		lib.leafDoFrom(configPath, { | path, script | this.interpretScriptSavingErrors(path, script) });
			// Load buffers in auto-load file:
		autoLoadPath = [configPath, 'Buffers-Autoload'];
		bufferDebugPath = [configPath ++ "-DEBUG", 'Buffers-Files-Not-Found', nil];
		(autoLoadBuffers = lib.at(*autoLoadPath)) !? {
			autoLoadPath = autoLoadPath add: nil;
			autoLoadBuffers.keysValuesDo({ | key, script |
				this.interpretScriptSavingErrors(autoLoadPath.put(2, key), script).loadIfNeeded({ | bufferItem |
					{
						"Moving buffer to debug folder".postln;
						autoLoadPath.postln;
						bufferDebugPath.put(2, key).postln;
						this.deleteSnippet(*autoLoadPath);
						this.addSnippetAtPath(bufferDebugPath.put(2, key), script);
					}.defer
					// this.addBufferNotFoundScript(bufferItem);
				});
			});
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

	interpretScriptSavingErrors { | path, script | // TODO
		var result;
		result = script.compile;
		if (result.isNil) {
			this.saveScriptInDebugPath(path, script);
			^nil;
		}{
			^result.value; // interpret script
		}
	}

	saveScriptInDebugPath { | path, script |
		("Saving script in debug path:").postln;
		postf("\n%\n\n", path);
		this.addSnippetAtPath(path.copy.put(2, path[2] ++ "_debug"), "/*\n\n" ++ script ++ "\n\n*/");
	}

	addSnippetAtPath { | path, snippet |
		this.addSnippetNamed(path[0], path[1], path[2], snippet);
	}

	addSnippetNamed { | folderName, fileName, snippetName, snippet |
		lib.put(folderName.asSymbol, fileName.asSymbol, snippetName.asSymbol, snippet);
		lib.changed(\dict);
		Library.changed(\selectedLib); // update SoundFileGui if open
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

	 // evaluate a snippet and return the result
	getSnippetValue { | path | ^this.getSnippet(path).interpret; }

	getSnippet { | path | ^lib.atPath(path) }

	// ============ UNDER DEVELOPMENT / TODO ============

	// Sound files and buffers

	// new version of addSoundFile - to replace the old one below
	addSoundFile2 { | category, path |
		var bufferItem;
		bufferItem = BufferItem(path); // TODO: must change the way buffer item creates instances!!!
		Library.put(this.soundFilePath ++ [category, path, bufferItem]);
		this.addSnippetNamed('SoundFiles', category, bufferItem.nameSymbol, this.makeBufferItemScript(bufferItem));
		this.changed(\soundFile, category, path);
		^bufferItem;
	}

	makeBufferItemScript { | bufferItem |
		format("//:%\nBufferItem(\n%\n).loadIfNeeded", bufferItem.nameSymbol, bufferItem.name.asCompileString)
	}

	removeSoundFile2 { | category, bufferItem |
		Library.put(this.soundFilePath ++ [category, bufferItem.name, nil]);
		this.deleteSnippet('SoundFiles', category, bufferItem.nameSymbol);
		this.changed(\soundFile, category, bufferItem.name);
	}

	// older version - still working, but under review
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

	removeSoundFile { | scriptName |
		if (scriptName.isNil) { ^"Cannot remove nil Buffer item".postln };
		this.deleteSnippet(*(this.bufferConfigPath ++ [scriptName.asSymbol]));
		if (this.isCurrent) { BufferItem.free(scriptName) };
	}

	// copying, deleting, importing, exporting branches
	importLib { | lib ... paths |
		// merge scripts from lib at paths to this lib
		// e
	}

	// evaluate all snippets under path and collect the results in dictionary
	collectValues { | path |

	}

	folders { // TODO?

	}
}

