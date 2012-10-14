/* iz Sat 13 October 2012  9:54 PM EEST

A ListAdapter that gets its items from a MultiLeverIdentityDictionary. 

See Value:dict, Value:branchOf, Widget:dict, Widget:branchOf.

*/

MultiLevelIdentityDictionaryAdapter : ListAdapter {
	var <dict, <>path;
	var <>newItemFunc;

	*new { | container, dict | 
		^super.new(container, dict)
	}

	init { | argContainer, argDict |
		container = argContainer;
		argDict !? { this.dict = argDict; };
		newItemFunc = newItemFunc ?? {{ IdentityDictionary() }};
	}

	dict_ { | argDict |
		dict = argDict;
		this.getItems;
	}

	getItems {
		this.items_(this, dict.atPathFail(path, IdentityDictionary()).keys.asArray.sort)
	}

	getBranch { | superBranch |
		superBranch = superBranch.adapter;
		dict = superBranch.dict; // only at init time: get the dict
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
	
	append { | widget, name |
		name = dict.makeUniqueName(path, name.asSymbol);
		dict.putAtPath(path.copy add: name, newItemFunc.(this));
		this.getItems;
	}
	
	replace {
		
	}


}

