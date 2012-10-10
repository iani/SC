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
		CocoaMenuItem.add(["New ScriptLib"], { this.new.gui });
		CocoaMenuItem.add(["Open ScriptLib"], { this.open });

	}

	*new { ^this.newCopyArgs(MultiLevelIdentityDictionary()); }

	*open {
		RecentPaths.open(this, { | path |
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

	saveDialog { RecentPaths.save(this.class, { | path | this saveToPath: path }); }

	saveToPath { | path |
		this.path = path;
		this writeArchive: path.asString;
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
	}

	importFolder { | pathName |
		(pathName.fullPath ++ "*.scd").pathMatch do: { | scriptPath |
			this importSnippets: scriptPath;
		};
	}

	importSnippets { | scriptPath |
		var pName, folders;
		pName = PathName(scriptPath);
		[pName.folderName, pName.fileNameWithoutExtension.asSymbol].postln;
	}

	makeUniqueSnippetName { | folder, file, snippet, index |
		var name;
		name = format("%%", snippet, index ? "").asSymbol;
		if (lib.at(folder, file, name).isNil) {
			^name;
		}{
			if (index.isNil) { index = 0 } { index = index + 1 };
			^this.makeUniqueSnippetPath(folder, file, snippet, index);
		}
	}

	export { | path |
		path.postln;
	}
}

