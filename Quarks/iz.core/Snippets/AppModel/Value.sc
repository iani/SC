/* IZ Fri 31 August 2012  2:57 AM EEST

Redo of Adapter idea from scratch, with radical simplification of principle 

See Value.org file for discussion. 

*/

Value {
	var <>model;		// AppModel instance that contains me
	var <adapter;	 	// My value (object) + adapter interfacing to it
	var <inputs;		// Array of MIDIFunc and/or OSCFunc that send me input

	*new { | model, adapter | ^this.newCopyArgs(model).adapter_(adapter) }

	updateListeners { adapter !? { this.changed(adapter.updateMessage); } }

	adapter_ { | argAdapter | 
		argAdapter !? { adapter = argAdapter.container_(this) };
		this.updateListeners;
	}

	// === Text utilities ===
	getString { | message = \getString | // Get the string of a TextView prepared with makeStringGetter
		var string;
		string = `"";
		this.changed(message, string);
		^string.value;
	}
	string_ { | string | adapter.string_(this, string) }

	// === List utilities ===
	list { | items | this.adapter = ListAdapter(this, items) }
	items_ { | changer, items | adapter.items_(changer, items); }
	items { ^adapter.items }
	item_ { | changer, item | adapter.item_(changer, item); }
//	item { ^adapter.item } // does not work with multiple values on same List.
	item { ^adapter.items[adapter.index] }	// works with independent indices on same List
	index_ { | changer, index | adapter.index_(changer, index); }
	index { ^adapter.index }
	append { | item | adapter.append(this, item) }
	insert { | item, index | adapter.insert(this, item, index) }
	replace { | item, index | adapter.replace(this, item, index) }

	// make me get my list from the item of another list
	sublistOf { | superList, getListFunction |
		this.adapter = ListAdapter();
		 // vary this to get different parts of the item
		getListFunction ?? { getListFunction = { | sublist | sublist }; };
		if (superList isKindOf: Symbol) { superList = model.getValue(superList); };
		this.addNotifier(superList, \list, {
			this.adapter.items_(this, getListFunction.(superList.adapter.item, this));
		});
		this.addNotifier(superList, \index, {
			this.adapter.items_(this, getListFunction.(superList.adapter.item, this));
		});
	}
	// === Dictionary utilities ===
	dict { | dict, itemCreationFunc |
		this.adapter = MultiLevelIdentityDictionaryAdapter(this, dict, itemCreationFunc);
		this.replaceNotifier(dict, \dict, { adapter.getItems });
	}

	branchOf { | superBranch, itemCreationFunc |
		superBranch = model.getValue(superBranch);
		this.adapter = MultiLevelIdentityDictionaryAdapter(this, nil, itemCreationFunc);
		if (superBranch.adapter.isKindOf(MultiLevelIdentityDictionaryAdapter)) {
			this.adapter.getBranch(superBranch);
		};
		this.addNotifier(superBranch, \list, {
			this.adapter.getBranch(superBranch);
		});
		this.addNotifier(superBranch, \index, {
			this.adapter.getBranch(superBranch);
		});
	}

	// === MIDI and OSC ===
	enable { inputs do: _.enable }
	disable { inputs do: _.disable }

	setMIDI { |  createMsg = \cc, func ... args |
		// remove any previous MIDIFuncs before adding this one
		this.removeMIDI;	// remove all previous MIDIFuncs from inputs
		this.addMIDI(createMsg, func, *args);
	}

	removeMIDI {	// remove all previous MIDIFuncs from inputs
		inputs.copy do: { | i | if (i isKindOf: MIDIFunc) { inputs remove: i } };
	}

	addMIDI { | createMsg = \cc, func ... args |
		// Create and add a MIDIFunc to my inputs
		inputs = inputs add: MIDIFunc.performList(createMsg, this.makeMIDIAction(func), args);
	}

	makeMIDIAction { | argFunc |
		if (argFunc.isNil) { ^this.defaultMIDIAction } { ^{ | ... args | argFunc.(this, *args) } }
	}

	defaultMIDIAction { // default is for setting value of proxyControl. Other defaults?
		^{ | ... args | this.setProxyParameter(args[0] / 127) }
	}
	
	setProxyParameter { | argValue |
		this.item.adapter.standardizedValue_(this, argValue)
	}

	addOSC { /* not yet implemented */ }

	objectClosed {
		super.objectClosed;
		inputs do: _.free;
		inputs = nil;
	}
}
