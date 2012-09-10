/* iz Sat 08 September 2012 10:47 AM BST

Adapter for control parameters of a NodeProxy.

See also: ProxyItem, Widget:proxyControl.

*/

ProxyControl2 : NumberAdapter {
	var <>proxy;			// the proxy which I control.
	var <parameter;		// (a Symbol): name of parameter to set 
	var <controlAction;	// action for setting the proxy's parameter

	standardizedValue_ { | changer, mappedNumber |
		super.standardizedValue_(changer, mappedNumber);
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

}