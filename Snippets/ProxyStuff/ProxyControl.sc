/* iz Sat 08 September 2012 10:47 AM BST

Adapter for control parameters of a NodeProxy.

See also: ProxyItem, Widget:proxyControl.

*/

ProxyControl : NumberAdapter {
	var <>proxy;			// the proxy which I control.
	var <parameter;		// (a Symbol): name of parameter to set
	var <controlAction;	// action for setting the proxy's parameter

	spec_ { | paramSpec |
		spec = paramSpec[1];
		value = spec map: standardizedValue;
		proxy = container;
		this.parameter = paramSpec[0];
	}

	standardizedValue_ { | changer, mappedNumber |
		// prevent funny wiring of all nil-params? : ...
//		if (parameter === '-') { ^this }; // ... No: leave it anyway, for efficiency. No harm done.
		super.standardizedValue_(changer, mappedNumber);
		controlAction.value;
	}

	value_ { | changer, number |
		super.value_(changer, number);
		controlAction.value;
	}

	parameter_ { | argParameter |
		if (proxy.notNil and: parameter.notNil) {
			this.removeNotifier(proxy, parameter);
		};
		parameter = argParameter;
		if (proxy.notNil and: parameter.notNil) {
			this.addNotifier(proxy, parameter, { | argValue | super.value_(proxy, argValue) });
		};
		// set action according to type of parameter:
		controlAction = switch ( parameter,
			'-', { { } },
			'vol', { { proxy.vol = value } },
			'fadeTime', { { proxy.fadeTime = value } },
			{ { proxy.set(parameter, value) } }
		);
	}

	// Update own value and standardizeValue and notify
	getValueFromProxy {
		super.value_(nil, switch ( parameter,
			'-', { 0 },
			'vol', { proxy.vol },
			'fadeTime', { proxy.fadeTime },
			{ proxy.get(parameter) }
		));
	}
}