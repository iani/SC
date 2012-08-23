/* IZ Wed 22 August 2012  4:05 PM EEST

Attempt to redo ProxySelector, ProxyState, ProxySpecSelector, ProxyControl on the basis of ListAdapter. 
Some of these may become subclasses of ListAdapter. 

EXPERIMENTAL

When these are ready, they will replace the old ProxySelection etc. classes
*/

ProxySelector2 : ListAdapter {
	classvar <proxyNames;	// cache proxyNames from proxySpaces
	var <proxySpace, <proxy;

	*initClass { proxyNames = IdentityDictionary.new; }

	proxySpace_ { | argProxySpace |
		proxySpace !? { this.removeNotifier(proxySpace, \newProxy) };
		proxySpace = argProxySpace ?? { Document.prepareProxySpace };
		this.addNotifier(proxySpace, \newProxy, {
			{ this.items = ['-'] ++ proxySpace.envir.keys.asArray.sort; }.defer(0.2);
		});
		proxySpace.notify(\newProxy);	// perform an update at init time
	}

	value { 
		if (this.item === '-') {
			proxy = nil; 
		}{
			proxy = proxySpace[this.item]
		}
	}
	
}

AbstractProxyAdapter : ListAdapter {

	var <proxySelector; 	// proxy selector item that notifies me to set my proxy
	var <proxy;			// the currently set proxy
	var <>additionalNotifiers; // additional notifiers for proxy change, eg: \historyChanged

	proxySelector_ { | argSelector = \proxy |
		proxySelector !? { this.removeNotifier(proxySelector, \value); };
		proxySelector = adapter.model.getAdapter(argSelector);
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

ProxySpecSelector2 : AbstractProxyAdapter {
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

ProxyControl2 : SpecAdapter {
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

ProxyState2 : AbstractProxyAdapter {
	*initClass {
		CmdPeriod add: { this.notify(\proxiesStoppedByCmdPeriod); }
	}

	init {
		this.addNotifier(this.class, \proxiesStoppedByCmdPeriod, { adapter.value = 0 });
	}

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
}


