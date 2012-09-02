/* IZ Thu 16 August 2012  8:46 PM EEST

Adapter for AppModel and AppWidgets, plus "real" Adapter classes that vary its behavior. 

The adapter instance variable inside the Adapter instance is used 
to vary the behavior of Adapber and store more state. 
The adapter variable can be a function, but it can be some other class that also stores state. 
Therefore we do not define any subclasses of the adapter.
The action of the adapter can ba a "real" adapter that encapsulates data. Is is stored in the adapter variable of the Adapter, so that it can be accessed by other elements of the application. 

TODO: ListAdapter sends different update messages according to what changed:

- When the index changes, do adapter.notify(\index, [index ... ])
- When the list changes, do adapter.notify(\list, [list ...]);
- When both the list and the index change, do: adapter.notify(\listAndIndex, [list, index])

This is important for list views that want to update their lists only when the list changes. 

*/

Adapter0 {
	var <model, <>adapter, <value = 0;
	var <inputs;	// Array of MIDIFunc and/or OSCFunc that send me input

	*new { | model | ^this.newCopyArgs(model) }

	// value_ valueAction_, action_ are defined in analogy to View:value_, View:valueAction_ etc.
	value_ { | argValue, sender |		 // only set value and notify
		value = argValue;
		this.updateListeners(sender);
	}

	updateListeners { | sender | this.notify(\value, [sender, this]); }

	setValue { | argValue |
		// inner adapter may set value "silently" without notification, during valueAction_
		value = argValue;
	}

	valueAction_ { | argValue, sender | // Also perform adapters action
		argValue !? { // nil causes too many problems
			value = argValue;
			adapter.(this, sender);
			this.updateListeners(sender);
		}
	}

	// some basic utilities

	addAction { | func, key | // perform additional action when \value is notified by adapter
		/* if 'key' is an object (e.g. a symbol) that is stored somewhere for later recall,
		then this action can later be removed by this.removeAction(key); */
		(key ? func).addNotifier(this, \value, { | sender |
			/* WARNING: actions that call value_ on this adapter, must send themselves as senders
			   otherwise infinite loop results. Example of correct call: 
			   anAdapter.addAction({ | adapter, func | adapter.value_(0, func) }); 
			*/
			if (sender !== func) { func.(this, func, sender) };
		});
	}

	removeAction { | key | key.removeNotifier(this, \value) }

	// Have the following 3 been used at all? 
	// Like View:action_ : Set the adapter, since it can also function as my action.
	action_ { | argFunc | adapter = argFunc }
	addValueAction { | action | this.addValueListener(this, action) }

	addValueListener { | listener, action |
		// add listener with action to be performed when the value notification is sent:
		listener.addNotifier(this, \value, { | ... args | action.(this, *args); })  
	}
	
	// incrementing and decrementing 
	increment { | upperLimit = inf, increment = 1 |
		this.valueAction = value + increment min: upperLimit;
	}
	decrement { | lowerLimit = 0, decrement = 1 | 
		this.valueAction = value - decrement min: lowerLimit;
	}

	// methods for creating specialized adapters in my adapter instance variable

	list { | items | // operate on lists of items
		if (adapter.isKindOf(ListAdapter).not) { adapter = ListAdapter(this) };
		items !? { adapter.items = items }
	}

	mapper { | spec | // adapter for mapping values with specs, for knobs and sliders etc.
		(adapter ?? { adapter = SpecAdapter(this) }).initSpec(spec);
	}

	// Proxy related adapter creation
	proxyControl { | proxySpecSelector | adapter = ProxyControl(this, proxySpecSelector); }
	proxySpecSelector { | proxySelector |
		adapter = ProxySpecSelector(this).proxySelector_(proxySelector); 
	}
	proxyState { | proxySelector, playFunc, stopFunc |
		adapter = ProxyState(this).proxySelector_(proxySelector);
		playFunc !? { adapter.playFunc = playFunc };
		stopFunc !? { adapter.stopFunc = stopFunc };
	}
	proxySelector { | proxySpace |
		if (adapter.isKindOf(ProxySelector) and: { proxySpace.notNil }) {
			adapter.proxySpace = proxySpace;
		}{
			adapter = ProxySelector(this).proxySpace_(proxySpace); 
		}
	}
	proxyHistory { | proxySelector | adapter = ProxyHistory(this).proxySelector_(proxySelector); }

	// MIDI and OSC
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

	defaultMIDIAction {
		if (adapter.isNil) {
			^{ | ... args | this.valueAction_(args[0]) }
		}{
			^adapter.defaultMIDIAction;
		}
	}

	addOSC { /* not yet implemented */ }
	
	objectClosed {
		super.objectClosed;
		inputs do: _.free;
		inputs = nil;
	}

	// OLDER: list handling methods. 
	// These are under review for removal.

	item { /* When my value has the form: [index, array], return array[index]
			For returning the selected item from a ListView or PopUpMenu */
		if (value[0].isNil) { ^nil } { ^value[1][value[0]] };
	}
	selectItemAt { | index = 0 |
		value !? { this.valueAction = [index.clip(0, value[1].size - 1), value[1]]; }
	}
	selectMatchingItem { | item |
		var index;
		index = value[1] indexOf: item;
		index !? { this.valueAction = [index, value[1]] }
	}
	updateItemsAndValue { | newItems, defaultItem = '-' |
		value = value ?? { [nil, []] };
		if (value[0].isNil) {
			this.valueAction = [newItems indexOf: defaultItem ?? 0, newItems]
		}{
			this.valueAction = [newItems indexOf: value[1][value[0]] ?? 0, newItems]
		}
	}

}

AbstractAdapterElement {
	var <>adapter;		// the adapter that contains me
	*new { | adapter ... args | ^this.newCopyArgs(adapter, *args).init; }
	
	defaultMIDIAction { ^{ | argVal | { adapter.valueAction = argVal / 127  }.defer } }
}

SpecAdapter : AbstractAdapterElement {
	var <spec, <unmappedValue = 0;
	var <>action;	
	init { }

	initSpec { | argSpec | 		// called by Adapter:mapper at creation time.
		if (argSpec.isNil) {	// ensure update 
			if (adapter.value.isNil) { adapter.value = 0; } { adapter.value = adapter.value }
		}{
			this.spec = argSpec; 
		};
	}

	spec_ { | argSpec |
		spec = argSpec.asSpec;
		adapter.value = spec map: unmappedValue;
	}

	map { | value |
		unmappedValue = value;
		adapter.valueAction = spec map: value;
	}
	value {
		var value;
		value = adapter.value;
		unmappedValue = spec unmap: value;
		action.(value);
	}
	
	updateValue { | argValue |
		unmappedValue = spec unmap: argValue;
		adapter.value = argValue;
	}

	defaultMIDIAction { ^{ | argVal | { this.map(argVal / 127)  }.defer } }

}

ListAdapter : AbstractAdapterElement {
	var <items;
	init { items = [] }
	items_ { | argItems, argSender |
		var oldItem;	// Adjust adapter's value to match the previously selected item, if found
		oldItem = this.item;
		items = argItems ?? { [] };
		adapter.setValue(items.indexOf(items.detect(_ == oldItem)) ?? { /* adapter.value ? */ 0 });
		adapter.updateListeners(argSender);
	}

	setItems { | argItems |
		items = argItems;
	}
	
	value { adapter setValue: (adapter.value ? 0).clip(0, items.size - 1); }

	item { ^items[adapter.value ? 0] }

	next { | setter | adapter.valueAction_(adapter.value + 1, setter) }
	previous { | setter | adapter.valueAction_(adapter.value - 1, setter) }
	first { | setter | adapter.valueAction_(0, setter) }
	last { | setter | adapter.valueAction_(items.size - 1, setter) }

	add { | item, setter | // analogous to Collection:add
		this.items_(items add: item, setter);
		this.last;
	}

	replace { | item, setter | this.put(adapter.value, item, setter); }

	rename { | newName, setter | 
		this.item.name = newName;
		this.items_(items, setter);	// update
	}

	put { | index, item, setter |	// analogous to ArrayedCollection:put
		if (items.size == 0) { ^"Cannot replace item in empty list - try adding first".postcln; };
		index = (index ?? { index = adapter.value }) max: 0 min: (items.size - 1);
		this.items_(items[index] = item, setter);
	}

	insert { | index, item, setter |
		this.items_(items.insert(index ?? { adapter.value }, item), setter);
	}

	delete { | setter | this.removeAt(nil, setter) }

	removeAt { | index, setter | // analogous to ArrayedCollection:removeAt
		if (items.size == 0) { ^"Cannot remove item from an empty list".postcln; };
		items.removeAt(index ?? { index = adapter.value });
		this.items_(items, setter);	// update
	}

	remove { | item, setter | // analogous to Collection:remove
		items remove: item;
		this.items_(items, setter);	// update
	}
	
	selectItem { | item, setter | // send setter to prevent infinite loops when setting from self
		var newIndex;
		newIndex = items.indexOf(items.detect(_ == item));
		newIndex !? { adapter.valueAction_(newIndex, setter) };
	}
	
	selectAt { | index, setter | // send setter to prevent infinite loops when setting from self
		adapter.valueAction_(index max: 0 min: (items.size - 1), setter);
	}
}
