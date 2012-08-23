/* IZ Wed 22 August 2012  4:05 PM EEST

Attempt to redo ProxySelector, ProxyState, ProxySpecSelector, ProxyControl on the basis of ListAdapter. 
Some of these may become subclasses of ListAdapter. 

EXPERIMENTAL

When these are ready, they will replace the old ProxySelection etc. classes
*/


ProxySelector : AbstractAdapterElement {
	classvar <proxyNames;	// cache proxyNames from proxySpaces
	var <proxySpace, <proxy;

	*initClass { proxyNames = IdentityDictionary.new; }
	
	*addProxySelector { | proxySpace, selector, action |
		var pNames;
		action = action ?? {{ | names | selector updateState: names }};
		this.addNotifier(proxySpace, \newProxy, { | proxy |
			{   // only build the names list once
				pNames = this.updateProxyNames(proxySpace);
				this.notify(\proxyNames, [pNames, proxy]);
			}.defer(0.1); // wait for ProxySpace to register the new proxy
		});
		proxyNames[proxySpace] ?? { this.updateProxyNames(proxySpace) };  // init the first time
		selector.addNotifier(this, \proxyNames, action);
	}

	*updateProxyNames { | proxySpace |
		var pNames;
		pNames = ['-'] ++ proxySpace.envir.keys.asArray.sort; 
		proxyNames[proxySpace] = pNames;
		^pNames;
	}

	*getProxyIndex { | proxySpace, proxy |
		^proxyNames[proxySpace] indexOf: proxySpace.envir.findKeyForValue(proxy);
	}

	*getProxyIndexForName { | proxySpace, name | ^proxyNames[proxySpace] indexOf: name }

	init {
		proxySpace = proxySpace ?? { Document.prepareProxySpace };
		this.class.addProxySelector(proxySpace, this);
		this.updateState(proxyNames[proxySpace] /* ?? { [this.nilProxyName] } */);
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
			adapter.notify(\selection, [newProxy, proxyName]);
		};
	}
}

ProxyState : AbstractAdapterElement {
	var <proxySelector; 	// proxy selector item that notifies me to set my proxy
	var <proxy;			// the currently set proxy
	var <>additionalNotifiers; // additional notifiers for proxy change, eg: \historyChanged

	*initClass {
		CmdPeriod add: { this.notify(\proxiesStoppedByCmdPeriod); }
	}
	init {
		if (proxySelector isKindOf: Symbol) { this.getProxySelector(proxySelector); };
		this.addSelectionNotifier;
		if (this.class === ProxyState) { // exclude subclasses in a primitive way ...
			this.addNotifier(ProxyState, \proxiesStoppedByCmdPeriod, { adapter.value = 0 });
		};
	}

	addSelectionNotifier {
		this.addNotifier(proxySelector, \selection, { | argProxy |
			this.setProxy(argProxy);
		});

	}
	
	getProxySelector { | selector |
		proxySelector = adapter.model.getAdapter(selector).proxySelector;
	}

	setProxy { | newProxy |
		if (proxy !== newProxy) {
			proxy !? { this.removeNotifiers; };
			proxy = newProxy;
			proxy !? { this.addNotifiers; };
		};
		this.updateState;
	}

	removeNotifiers {
		this.removeNotifier(proxy, \play);
		this.removeNotifier(proxy, \stop);
		this.removeAdditionalNotifiers;
	}

	removeAdditionalNotifiers {
		additionalNotifiers pairsDo: this.removeNotifier(proxy, _);
	}
	addNotifiers {
		this.addNotifier(proxy, \play, { adapter.value = 1 });
		this.addNotifier(proxy, \stop, { adapter.value = 0 });
		this.addAdditionalNotifiers;
	}

	addAdditionalNotifiers {
		additionalNotifiers pairsDo: { | message, action |
//			this.addNotifier(proxy, message, { | ... args | action.(this, *args) });
			this.addNotifier(proxy, message, action); // simpler, but we may need the ProxyState?
		}
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
		this.removeAdditionalNotifiers;
	}
	addNotifiers {
		this.addNotifier(proxy, \proxySpecs, { | argSpecs |
			specs = argSpecs;
			adapter.updateItemsAndValue(specs.flop[0]);
		});
		this.addAdditionalNotifiers;
	}
	updateState { adapter.updateItemsAndValue((specs = MergeSpecs.getSpecsFor(proxy)).flop[0]); }
	value { adapter.notify(\selection, [proxy, specs[adapter.value[0] ? 0]]); }
}


ProxyControl : ProxyState {
	var <parameter, <spec;
	var <action;

	getProxySelector { | selector | proxySelector = adapter.model.getAdapter(selector); }

	addSelectionNotifier {
		this.addNotifier(proxySelector, \selection, { | argProxy, argSpec |
			proxy = argProxy;
			#parameter, spec = argSpec;
			adapter.adapter.spec = spec;
			proxy !? { this.updateValueFromProxy };
			action = switch ( parameter,
				'-', { { } },
				'vol', {{ | val | proxy.vol = adapter.value }},
				'fadeTime', {{ | val | proxy.fadeTime = adapter.value }},
				{{ proxy.set(parameter, adapter.value) }}
			);
		});
	}
	updateValueFromProxy {
		var currentParameterValue;
		switch (parameter, 
			'-', { },
			'vol', { adapter.adapter updateValue: proxy.vol ? 0 },
			'fadeTime', { adapter.adapter updateValue: proxy.fadeTime ? 0 },
			{ adapter.adapter updateValue: proxy.get(parameter) ? 0 }
		)
	}
	value { proxy !? { action.(adapter.value) } }
}
