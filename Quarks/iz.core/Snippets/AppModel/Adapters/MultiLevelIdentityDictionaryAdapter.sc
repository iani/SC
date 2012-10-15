/* iz Sat 13 October 2012  9:54 PM EEST

A ListAdapter that gets its items from a MultiLeverIdentityDictionary. 

See Value:dict, Value:branchOf, Widget:dict, Widget:branchOf.

*/

MultiLevelIdentityDictionaryAdapter : ListAdapter {
	var <dict, <>path;
	var <itemCreationFunc;

	*new { | container, dict, itemCreationFunc | 
		^super.new(container, dict).itemCreationFunc = itemCreationFunc;
	}

	init { | argContainer, argDict |
		container = argContainer;
		this.dict = argDict ?? { MultiLevelIdentityDictionary() };
	}

	itemCreationFunc_ { | argItemCreationFunc |
		itemCreationFunc = argItemCreationFunc ?? { this.defaultItemCreationFunc };
	}

	defaultItemCreationFunc { ^{ IdentityDictionary() } }

	dict_ { | argDict |
		dict = argDict;
		this.getItems;
	}

	getItems {
		this.items_(this, dict.atPathFail(path, IdentityDictionary()).keys.asArray.sort)
	}

	getBranch { | superBranch |
		// Get items from your superBranch. Called when superBranch notifies \list or \index.
		// Get dict every time, so be always synchronized with superBranch.
		superBranch = superBranch.adapter;
		dict = superBranch.dict;
		path = superBranch.path.copy add: superBranch.item;
		this.getItems;
	}

	put { | ... argPath |
		dict.put(*argPath);
		this.getItems;
		container.changed(\list);
	}

	delete {
		dict.removeAtPath(path ++ [item]);
		this.getItems;
	}
	
	append { | widget, name | // TODO: Remove widget arg
		name = dict.makeUniqueName(path, name.asSymbol);
		dict.putAtPath(path.copy add: name, itemCreationFunc.(this, name));
		item = name;
		this.getItems;
	}
	
	replace { | widget, name | // TODO: Remove widget arg
		var branch;
		name = dict.makeUniqueName(path, name.asSymbol);
		branch = dict.atPath(path ++ [item]);
		dict.putAtPath(path ++ [name], branch);
		dict.putAtPath(path ++ [item], nil);
		item = name;
		this.getItems;
	}


}

