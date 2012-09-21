/* IZ Fri 31 August 2012  8:45 PM EEST
See Value.org file for discussion. 
*/

NumberAdapter {
	var <>container, <spec, <value = 0, <standardizedValue = 0;

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
	var <>container, <string;

	*new { | container, string |
		^this.newCopyArgs(container, string ? "<empty>");
	}

	string_ { | changer, argString |
		string = argString ? "";
		container.notify(\text, changer);
	}	

	updateMessage { ^\text }
}



NamedList : List {		// used for BufferListGui
	var <>name;
	
	*new { | name, items |
		^super.new.name_(name).array_(if (items.isKindOf(List)) { items } { List.newUsing(items) });
	}
	
	items { ^array }
}

ListAdapter {
	/* Actual list is contained in items variable. It should be a kind of List. 
	Different views can hold different ListAdapter2 instances with the same list as items.
	That way, the different views can operate on different items of the list, and update themselves
	when the list changes. */
	var <>container, <items, <index = 0, <item;

	*new { | container, items | ^super.new.init(container, items) }

	init { | argContainer, argItems |
		container = argContainer;	// TODO: probably remove this part? 
		if (argItems isKindOf: List) {
			items = argItems;
		}{
			items = List newUsing: argItems;
		};
	}

	updateMessage { ^\list }

	items_ { | changer, argItems |
		/* 	Remove connection to previous list, and create connection to new list.
			When the list changes, it notifies me, so that I can update my listeners. */
		items !? { this.removeNotifier(items, \list); };
		if (argItems isKindOf: Array) { argItems = List.newUsing(argItems) };
		items = argItems ?? { List.new };
		this.addNotifier(items, \list, { | changer | container.notify(\list, changer) });
		index = items.indexOf(item) ? 0;
		item = items[index];
		container !? { container.notify(\list, changer) };
	}

	item_ { | changer, argItem |
		this.index_(changer, items.indexOf(argItem) ? 0);
	}

	index_ { | changer, argIndex |
		index = argIndex max: 0 min: (items.size - 1);
		item = items[index];
		container.notify(\index, changer);
	}

	replace { | changer, argItem, argIndex |
		if (items.size == 0) { ^this.append(changer, argItem) };
		items.put(argIndex ? index, argItem);
		item = argItem;
		items.notify(\list, changer);
	}

	append { | changer, argItem |
		items add: argItem;
		this.index_(changer, items.size - 1);
		items.notify(\list, changer);
	}

	insert { | changer, argItem, argIndex |
		items.insert(argIndex ? index, argItem);
		this.index_(changer, argIndex ? index);
		items.notify(\list, changer);
	}

	delete { | changer, argItem |
		items.remove(argItem ? item);
		this.index_(changer, index);
		items.notify(\list, changer);
	}
	
	previous { this.index_(this, index - 1 max: 0) }
	next { this.index_(this, index + 1 min: (items.size - 1)) }
	first { this.index_(this, 0); }
	last { this.index_(this, items.size - 1) }

	at { | argIndex | ^items[argIndex] }
	add { | argItem | 
		items.add(argItem);
		item ?? { this.last };
		container.notify(\list, this);
	}
	put { | argIndex, argItem | 
		items.put(argIndex, item);
		container.notify(\list, this);
	}

	includes { | item | ^items includes: item }
	indexOf { | item | ^items indexOf: item }
	collect { | func | ^items collect: func }
	detect { | func | ^items detect: func }
	select { | func | ^items select: func }
	size { ^items.size }
}
