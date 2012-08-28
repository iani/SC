/* IZ Wed 22 August 2012  4:05 PM EEST

Adapter classes for dealing with NodeProxies

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

5. ProxyHistory ===========================================

Keeps history of all code executed by ProxyCode for the selected proxy. 

*/

ProxySelector : ListAdapter {
	classvar <proxyNames;	// cache proxyNames from proxySpaces
	var <proxySpace, <proxy;

	*initClass { proxyNames = IdentityDictionary.new; }
	
	*updateProxyNames { | proxySpace, proxyName |
		var theNames;
		proxyNames[proxySpace] = theNames = proxyNames[proxySpace] add: proxyName;
		proxySpace.notify(\proxyNames, [theNames]);
	}

	proxySpace_ { | argProxySpace |
		proxySpace !? { this.removeNotifier(proxySpace, \proxyNames) };
		proxySpace = argProxySpace ?? { Document.prepareProxySpace };
		this.addNotifier(proxySpace, \proxyNames, { | names |
			this.items = ['-'] ++ names;
		});
		this.items = ['-'] ++ proxyNames[proxySpace];
	}

	value { 
		if (this.item === '-') {
			proxy = nil; 
		}{
			proxy = proxySpace[this.item]
		}
	}

	proxy_ { | argProxy |
		proxy = argProxy; 	// value method not called by update. Why? 
		this selectItem: proxySpace.envir.findKeyForValue(argProxy);
		adapter.updateListeners(this);
	}
}

AbstractProxyAdapter : ListAdapter {

	var <proxySelector; 	// proxy selector item that notifies me to set my proxy
	var <proxy;			// the currently set proxy
	var <>additionalNotifiers; // additional notifiers for proxy change, eg: \historyChanged

	proxySelector_ { | argSelector = \proxy |
		proxySelector !? { this.removeNotifier(proxySelector, \value); };
		proxySelector = adapter.model.getAdapter(argSelector).proxySelector;
		this.addNotifier(proxySelector, \value, { this.setProxy(proxySelector.adapter.proxy); });
	}

	setProxy { | newProxy |
		if (proxy !== newProxy) {
			proxy !? { this.removeNotifiers; };
			proxy = newProxy;
			proxy !? { this.addNotifiers; };
		};
		this.updateState;
	}

	removeNotifiers { this.subclassResponsibility(thisMethod) }
	addNotifiers { this.subclassResponsibility(thisMethod) }
	updateState { this.subclassResponsibility(thisMethod) }
//	value { this.subclassResponsibility(thisMethod) }
}

ProxySpecSelector : AbstractProxyAdapter {
	var <>specs = #[['-', nil]];

	removeNotifiers { this.removeNotifier(proxy, \proxySpecs); }
	addNotifiers {
		this.addNotifier(proxy, \proxySpecs, { | argSpecs |
			specs = argSpecs;
			this.items = specs.flop[0];
		});
	}

	updateState {
		specs = MergeSpecs.getSpecsFor(proxy);
		this.items = specs.flop[0];
	}

	getControl { ^[proxy] ++ specs[adapter.value]; }

}

ProxyControl : SpecAdapter {
	var <specSelector, <proxy, <parameter;
	
	*new { | adapter, specSelector |
		^super.new(adapter).specSelector_(specSelector ? \parameterSelector);
	}

	init { /* this.inspect */ } // tmp debug

	specSelector_ { | argSelector |
		specSelector !? { this.removeNotifier(specSelector, \value); };
		specSelector = adapter.model.getAdapter(argSelector);
		this.addNotifier(specSelector, \value, { this.setControl(specSelector.adapter.getControl) });
	}

	setControl { | controlSpecs |
		#proxy, parameter, spec = controlSpecs;
		proxy !? { this.updateValueFromProxy };
		action = switch ( parameter,
			'-', { { } },
			'vol', {{ proxy.vol = adapter.value }},
			'fadeTime', {{ proxy.fadeTime = adapter.value }},
			{{ proxy.set(parameter, adapter.value) }}
		);
	}

	updateValueFromProxy {
		switch (parameter, 
			'-', { },
			'vol', { adapter.adapter updateValue: proxy.vol ? 0 },
			'fadeTime', { adapter.adapter updateValue: proxy.fadeTime ? 0 },
			{ adapter.adapter updateValue: (proxy.get(parameter) ? 0) }
		)
	}

	value { super.value; proxy !? { action.value } }
}

ProxyState : AbstractProxyAdapter {
	var <>playFunc, <>stopFunc;

	*initClass {
		CmdPeriod add: { this.notify(\proxiesStoppedByCmdPeriod); }
	}

	init {
		this.addNotifier(this.class, \proxiesStoppedByCmdPeriod, { adapter.value = 0 });
		playFunc = playFunc ?? { this.defaultPlayFunc };
		stopFunc = stopFunc ?? { this.defaultStopFunc };
	}

	defaultPlayFunc { ^{ proxy.play } }
	defaultStopFunc { ^{ proxy.stop } }

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
			if (adapter.value > 0) { playFunc.(this) } { stopFunc.(this) };
		}
	}
}

ProxyHistory : AbstractProxyAdapter {

	removeNotifiers { this.removeNotifier(proxy, \proxyHistory); }
	addNotifiers {
		this.addNotifier(proxy, \proxyHistory, { | argHistory, changer |
			if (changer !== this) { this.items_(argHistory, changer); }
		});
	}

	items_ { | argItems, argSender |
		super.items_(argItems, argSender);
		// Do not send back to proxy history if you just received from it
		if (argSender !== proxy) {
			ProxyCode.replaceProxyHistory(this, proxy, items);
		};
	}
	
	updateState {
		if (proxy.isNil) {
			this.items_([])
		}{
			this.items_(ProxyCode.proxyHistory[proxy] ?? { [] }, proxy) 
		}
	}
}


