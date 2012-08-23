/* IZ Wed 22 August 2012  4:05 PM EEST

Attempt to redo ProxySelector, ProxyState, ProxySpecSelector, ProxyControl on the basis of ListAdapter. 
Some of these may become subclasses of ListAdapter. 

EXPERIMENTAL

When these are ready, they will replace the old ProxySelection etc. classes
*/



ProxyControl2 : SpecAdapter {
	var <specSelector, <proxy, <parameter;
	
	*new { | adapter, specSelector |
		^super.new(adapter).specSelector_(specSelector);
	}

	init { this.inspect } // tmp debug

	specSelector_ { | argSelector |
		specSelector = adapter.model.getAdapter(argSelector);
		this.addNotifier(specSelector, \value, { this.setControl(specSelector.getControl) });
	}

	setControl { | controlSpecs |
		#proxy, parameter, spec = controlSpecs;
		proxy !? { this.updateValueFromProxy };
		action = switch ( parameter,
			'-', { { } },
			'vol', {{ | val | proxy.vol = adapter.value }},
			'fadeTime', {{ | val | proxy.fadeTime = adapter.value }},
			{{ proxy.set(parameter, adapter.value) }}
		);
	}

	updateValueFromProxy {
		switch (parameter, 
			'-', { },
			'vol', { adapter.adapter updateValue: proxy.vol ? 0 },
			'fadeTime', { adapter.adapter updateValue: proxy.fadeTime ? 0 },
			{ adapter.adapter updateValue: proxy.get(parameter) ? 0 }
		)
	}

	value { proxy !? { action.(adapter.value) } }
}


ProxySelector2 : ListAdapter {
	classvar <proxyNames;	// cache proxyNames from proxySpaces
	var <proxySpace, <proxy;

	*initClass { proxyNames = IdentityDictionary.new; }
	
	proxySpace_ { | argProxySpace |
		[this, thisMethod.name, argProxySpace].postln;
		proxySpace = argProxySpace ?? { Document.prepareProxySpace };
		
	}

	getControl {
		
	}

}

