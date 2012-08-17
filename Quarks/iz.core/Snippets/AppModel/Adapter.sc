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

	// value_ and valueAction_ are defined in analogy to View:value_, View:valueAction_
	value_ { | argValue |		 // only set value and notify
		value = argValue;
		this.notify(\value, value);
	}

	valueAction_ { | argValue | // Also perform adapters action
		value = argValue;
		adapter.(this);
		this.notify(\value, value);
	}

	// list handling methods
	
	item { /* When my value has the form: [index, array], return array[index]
			For returning the selected item from a ListView or PopUpMenu */
		if (value[0].isNil) { ^nil } { ^value[1][value[0]] };
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

	proxySelector { | proxySpace | adapter = ProxySelector(this, proxySpace); }
	proxyState { | proxySelector | adapter = ProxyState(this, proxySelector); }
	proxySpecSelector { | proxySelector | adapter = ProxySpecSelector(this, proxySelector); }
	proxyControl { | proxySpecSelector | adapter = ProxyControl(this, proxySpecSelector); }
}

AbstractAdapterElement {
	var <>adapter;		// the adapter that contains me
	*new { | adapter ... args | ^this.newCopyArgs(adapter, *args).init; }
}

ProxySelector : AbstractAdapterElement {
	classvar <proxyNames;	// cache proxyNames from proxySpaces
	var <proxySpace, <proxy;
	
	*initClass { proxyNames = IdentityDictionary.new; }
	
	*addProxySelector { | proxySpace, selector |
		var pNames;
		this.addNotifier(proxySpace, \newProxy, {
			{  // only build the names list once
				pNames = ['-'] ++ proxySpace.envir.keys.asArray.sort; 
				proxyNames[proxySpace] = pNames;
				this.notify(\proxyNames, [pNames]);
			}.defer(0.1); // wait for ProxySpace to register the new proxy
		});
		selector.addNotifier(this, \proxyNames, { | names | selector updateState: names });
	}
	
	init {
		proxySpace = proxySpace ?? { Document.prepareProxySpace };
		this.class.addProxySelector(proxySpace, this);
		this.updateState(proxyNames[proxySpace] ?? { [this.nilProxyName] });
	}

	updateState { | names | adapter.updateItemsAndValue(names); }

	nilProxyName { ^'-' }

	value { // notify related items when a new proxy is chosen
		var newProxy, proxyName;
		proxyName = adapter.item;
		if (proxyName.notNil and: { proxyName !== this.nilProxyName }) {
			newProxy = proxySpace[proxyName]
		};
		if (newProxy !== proxy) {
			proxy = newProxy;
			adapter.notify(\selection, newProxy);
		};
	}

}

ProxyState : AbstractAdapterElement {
	var <proxySelector; 	// proxy selector item that notifies me to set my proxy
	var <proxy;			// the currently set proxy
	
	init {
		if (proxySelector isKindOf: Symbol) {
			proxySelector = adapter.model.getAdapter(proxySelector).proxySelector;
		};
		this.addNotifier(proxySelector, \selection, { | proxy, args |
			
			this.setProxy(proxy);
			this.setArgs(args);
		});
	}

	setProxy { | newProxy |
		if (proxy !== newProxy) {
			proxy !? { this.removeNotifiers; };
			proxy = newProxy;
			proxy !? { this.addNotifiers; };
		};
		this.updateState;
	}

	setArgs { /* used by ProxyControl */ }

	removeNotifiers {
		this.removeNotifier(proxy, \play);
		this.removeNotifier(proxy, \stop);
	}

	addNotifiers {
		this.addNotifier(proxy, \play, { adapter.value = 1 });
		this.addNotifier(proxy, \stop, { adapter.value = 0 });
	}

	updateState {
		if (proxy.isNil) {
			adapter.value = 0;
		}{
			if (proxy.isMonitoring) {  adapter.value = 1 } { adapter.value = 0 };
		}
	}

	value {
		if (proxy.isNil) {
			adapter.value = 0;
		}{
			if (adapter.value > 0) { proxy.play } { proxy.stop };
		}
	}
}

ProxySpecSelector : ProxyState {
	var <>specs = #[['-', nil]];
	removeNotifiers {
		this.removeNotifier(proxy, \proxySpecs);
	}

	addNotifiers {
		this.addNotifier(proxy, \proxySpecs, { | ... args | args.postln; });
	}

	updateState {
		specs = MergeSpecs(proxy);
		adapter.updateItemsAndValue(specs.flop[0]);
	}
	// notify listening ProxyControls of new specs
	value { 
		this.notify(\selection, [proxy, specs[adapter.value[0]]]); }
}

ProxyControl : ProxyState {
	var <spec;			// the currently set spec

	setArgs { | argSpec |
		[this, thisMethod.name, proxy, argSpec].postln;
//		this.updateState;
	}

	value {
	}
	
	updateState { 
//		[this,
	}
	
	
}
