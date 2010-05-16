/* iz 080817
Scan a directory and create some useful lists or dictionaries. 
var files: flat list of all files found, as strings containing the entire pathname of each file.

sortFilesByNumbering gives a utility for sorting jpg export files from videos. ...

asTree creates a Tree reflecting the directory / file structure of the file collection.

*/

DirectoryScanner {
	var <files; // flat list of all files found, as strings containing the entire pathname of each file.
	*openDialog { | endFunc |
		GUI.dialog.getPaths { | paths |
			endFunc.(this.new.addDirectory(paths.first.dirname ++ "/"));
		};
	}
	
	addDirectory { | argPath |
		(argPath ++ "*").pathMatch do: { | i |
			if (i.last === $/) { this.addDirectory(i) } { this.addFile(i) }
		}
	}

	addFile { | argPath |
		files = files.add(argPath);
	}

	// sort files that were badly numbered by some wonderful windows export program
	sortFilesByNumbering { | regex = "([0-9]*).jpg" |
		var file_dict;
		var dir, fname, num, list;
		file_dict = IdentityDictionary.new;
		files do: { | p |
			dir = p.dirname.asSymbol;
			fname = p.basename;
			num = fname.findRegexp(regex);
			if (num.notNil) {
				list = file_dict[dir];
				if (list.isNil) { file_dict[dir] = list = SortedList(0, { | a, b | a.key < b.key }) };
				list.add(num.last.last.asInteger -> fname);
			}
		};
		^file_dict;
	}
	asTree {
		// create a Tree reflecting the directory / file structure of your files
		var tree, filename;
		tree = Tree.new;
		files do: { | f |
			f = f.split($/);
			filename = f.pop;			
			f = f collect: _.asSymbol;
			if (filename.size > 0) {
				tree.putPath(f, filename);
			}{
				tree.branchAt(f);
			}
		};
		^tree;
	}
}


