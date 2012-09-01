/* IZ Fri 31 August 2012  8:45 PM EEST

New adapters, to work with AppModel2, Value, and Widget.

Also redo of Adapter idea from scratch, on radically different and simpler principle.
Adapter2 is meant to store its additional "methods" or "variables" as environment variables. Its behavior is customizable. Not yet tested. 

See Value.org file for discussion. 

*/



NumberAdapter {
	var <container, <spec, <value = 0, <standardizedValue = 0;

	*new { | container, spec |
		^this.newCopyArgs(container).spec_(spec);
	}

	spec_ { | argSpec |
		spec = argSpec.asSpec;
		value = spec map: standardizedValue;
	}

	value_ { | changer, number |
		value = number;
		standardizedValue = spec unmap: value;
		container.notify(\number, changer);
	}

	standardizedValue_ { | changer, mappedNumber |
		value = spec map: mappedNumber;
		standardizedValue = mappedNumber;
		container.notify(\number, changer);
	}
	
	updateMessage { ^\number }
}

TextAdapter {
	var <container, <string;

	*new { | container, string |
		^this.newCopyArgs(container, string ? "<empty>");
	}

	string_ { | changer, argString |
		string = argString ? "";
		container.notify(\text, changer);
	}	

	updateMessage { ^\text }
}


ListAdapter2 : List {
	var <container, <index = 0, <item;

	*new { | container | ^super.new.init(container) }

	init { | argContainer |
		container = argContainer;
	}

	updateMessage { ^\list }

	items_ { | changer, items |
		array = items;
		index = array.indexOf(item) ? 0;
		item = array[index];
		container.notify(\list, changer);		
	}
	
	items { ^array }

	replace { | changer, argItem, argIndex |
		this.put(argIndex ? index, argItem);
		item = argItem;
		container.notify(\list, changer);
	}

	append { | changer, argItem |
		array = array add: argItem;		
		container.notify(\list, changer);
	}

	insert { | changer, argItem, argIndex |
		super.insert(argIndex ? index, argItem);
		container.notify(\list, changer);
	}

	delete { | changer, argItem |
		this.remove(argItem ? item);
		index = array.indexOf(item) ? 0;
		item = array[index];
		container.notify(\list, changer);
	}

	index_ { | changer, argIndex |
		index = argIndex max: 0 min: (this.size - 1);
		item = array[index];
		container.notify(\index, changer);
	}

}

NamedListAdapter : ListAdapter2 {
	var <>name;
}

/*
Adapter2 is meant to store its additional "methods" or "variables" as environment variables. Its behavior is customizable. Not yet tested. 
*/

Adapter2 : Event { 
	var <container, <value;

	*new { | container | ^super.new.init(container) }
	

}
