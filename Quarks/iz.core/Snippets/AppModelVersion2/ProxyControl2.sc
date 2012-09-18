/* iz Sat 08 September 2012 10:47 AM BST

Adapter for control parameters of a NodeProxy.

See also: ProxyItem, Widget:proxyControl.

*/

ProxyControl2 : NumberAdapter {
	var <>proxy;			// the proxy which I control.
	var <parameter;		// (a Symbol): name of parameter to set 
	var <controlAction;	// action for setting the proxy's parameter

	standardizedValue_ { | changer, mappedNumber |
		// prevent funny wiring of all nil-params? : ...
//		if (parameter === '-') { ^this }; // ... No: leave it anyway, for efficiency. No harm done.
		super.standardizedValue_(changer, mappedNumber);
		controlAction.value;
	}

	value_ { | changer, mappedNumber |
		super.value_(changer, mappedNumber);
		controlAction.value;
	}

	parameter_ { | argParameter |
		parameter = argParameter;
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