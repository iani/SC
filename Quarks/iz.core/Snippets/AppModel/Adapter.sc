/* IZ Thu 16 August 2012  8:46 PM EEST

Adapter for AppModel and AppWidgets, plus "real" Adapter classes that vary its behavior. 

The adapter instance variable inside the Adapter instance is used 
to vary the behavior of Adapber and store more state. 
The adapter variable can be a function, but it can be some other class that also stores state. 
Therefore we do not define any subclasses of the adapter.
The action of the adapter can ba a "real" adapter that encapsulates data. Is is stored in the adapter variable of the Adapter, so that it can be accessed by other elements of the application. 

In addition to the Adapter class, which is actually an "adapter-value container", here are four adapter element classes: 

1. ProxySelector ============================================

Used by item selection elements such as ListView or PopUpMenu to select a node by name from the existing nodes in a ProxySpace.

Listens to message \newProxy from an instance of ProxySpace. When received, it sends the list of node names from the ProxySpace to its container, so that it updates its value, and sends the updated list to any gui or other elements attached, by notifying \setProxy, proxy. 

2. ProxyState ============================================

Used by proxy on-off buttons or similar elements, to play or stop a node, or to update the state of the watching elements when the node starts or stops. 

Attaches itself to an adapter containing a ProxySelector. When that adapter \setProxy, proxy, the ProxyState sets its node and updates its own state. 

When the ProxyState sets its proxy, it starts listending to \play and \stop notification from that proxy, so that it can update the state of the containing adapter to 0 or 1. The \play and \stop notifications are issued from the proxy from method calls BusPlug:play and BusPlug:stop. 

Also, when its value is set to 0 or 1 via this.value = 0, this.value = 1, it stops or plays the selected proxy. 

3. ProxySpecSelector ============================================

Used by item selection elements such as ListView or PopUpMenu to select a control parameter by name from the existing parameters of a proxy. Also used by ProxyControl to set the parameter and its specs for any elements that want to set the parameter currently chosen by the item selection element. 

Like ProxyState, this adapter attaches itself to an adapter containing a ProxySpaceWatcher. When that adapter sends an update (in the form of this.notify(\value, items), the ProxySpecs sets its node and updates its own state. 

When the ProxyState sets its node, it gets the specs of the node by calling: MergeSpecs.getSpecsFor(proxy). 

It also starts listening to \proxySpecs notifications from that proxy, so that it may update the specs of the proxy if they change in the meanwhile. \proxySpecs notifications are issued from the proxy in method Meta_MergeSpecs:parseArguments, which is called in places such as: [ProxyCode:evalInProxySpace], [ProxySourceEditor:init], [ProxySourceEditor:resetSpecs]. This enables the user modify the specifications provided by the proxy itself in order to add information not available in the proxy. 

An element that wants to set the parameter of the proxy that is selected by the adapter that ProxySpecs is 

4. ProxyControl ============================================

Attaches its containing adapter to proxy spec selection adapter that contains a ProxySpecSelector. When the proxy 
*/

Adapter {
	var <model, <>adapter, <value;

	*new { | model | ^this.newCopyArgs(model) }

	// value_ valueAction_, action_ are defined in analogy to View:value_, View:valueAction_ etc.
	value_ { | argValue, sender |		 // only set value and notify
		value = argValue;
		this.updateListeners(sender);
	}

	updateListeners { | sender |
		this.notify(\value, [sender, this]);
	}

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

	// list handling methods. NOTE: TODO: maybe use ListAdapter for ListView, popUpMenu instead / also?

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

	// methods for creating specialized adapters in my adapter instance variable

	mapper { | spec | // adapter for mapping values with specs, for knobs and sliders etc.
		(adapter ?? { adapter = SpecAdapter(this) }).initSpec(spec);
	}
	proxySelector { | proxySpace | adapter = ProxySelector(this, proxySpace); }
	proxyState { | proxySelector | adapter = ProxyState(this, proxySelector); }
	proxySpecSelector { | proxySelector | adapter = ProxySpecSelector(this, proxySelector); }
	proxyControl { | proxySpecSelector | adapter.action = ProxyControl(this, proxySpecSelector); }

	// Operate directly on AppNamedWidget methods ?????? NOT
	proxyControl2 { | proxySpecSelector |
		adapter = ProxyControl2(this, proxySpecSelector);
	}
//	proxySpecSelector2 { | proxySelector | adapter ?? { adapter = ProxySpecSelector2(proxySelector); } }
//	proxyState2 { | proxySelector | adapter ?? { adapter = ProxyState2(proxySelector); } }
//	proxySelector2 { | proxySpace | adapter ?? { adapter = ProxySelector2(proxySpecSelector); } }

	list { | items |
		if (adapter.isKindOf(ListAdapter).not) { adapter = ListAdapter(this) };
		items !? { adapter.items = items }
	}
}

AbstractAdapterElement {
	var <>adapter;		// the adapter that contains me
	*new { | adapter ... args | ^this.newCopyArgs(adapter, *args).init; }
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
}

ListAdapter : AbstractAdapterElement {
	var <items;
	init { items = [] }
	items_ { | argItems, argSender |
		items = argItems;
		this.value; 	// check index
		adapter.updateListeners(argSender);
	}
	
	value { adapter setValue: (adapter.value ? 0).clip(0, items.size - 1); }

	item { ^items[adapter.value] }

	next { | setter | adapter.valueAction_(adapter.value + 1, setter) }
	previous { | setter | adapter.valueAction_(adapter.value - 1, setter) }
	first { | setter | adapter.valueAction_(0, setter) }
	last { | setter | adapter.valueAction_(items.size - 1, setter) }
	
	add { | item, setter | // analogous to Collection:add
		this.items_(items add: item, setter);
	}

	replace { | item, setter | this.put(item, adapter.value, setter); }

	put { | item, index, setter |	// analogous to Collection:put
		if (items.size == 0) { ^"Cannot replace item in empty list - try adding first".postcln; };
		index = (index ?? { index = adapter.value }) max: 0 min: (items.size - 1);
		this.items_(items[index] = item, setter);
	}

	insert { | item, index, setter |
		this.items_(items.insert(index ?? { adapter.value }, item), setter);
	}

	delete { | setter | this.removeAt(nil, setter) }

	removeAt { | index, setter | // analogous to Collection:removeAt
		if (items.size == 0) { ^"Cannot remove item from an empty list".postcln; };
		items.removeAt(index ?? { index = adapter.value });
		this.items_(items, setter);	// update
	}

	remove { | item, setter | // analogous to Collection:remove
		items remove: item;
		this.items_(items, setter);	// update
	}
}
