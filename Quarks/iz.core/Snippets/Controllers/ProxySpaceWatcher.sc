/* IZ Tue 14 August 2012  1:36 AM EEST

Only this is kept from the other Proxy Watchers. 

Used by ProxySourceEditor. 

Simple enough to be adaptable in other contexts.

*/
ProxySpaceWatcher {
	var <widget, <proxySpace, <>action;

	*new { | widget, proxySpace, action |
		^this.newCopyArgs(widget, proxySpace, action).init;
	}

	init {
		proxySpace = proxySpace ?? {
			Document.current.envir ?? { (Document.current.envir = ProxySpace.push).envir }
		};
		action = action ?? {{
			// Wait for proxyspace to install the new proxy. 
			{ this.updateNodeProxyList; }.defer(0.1); // 0.1 was not sufficient?
		}};
		widget.addNotifier(proxySpace, \newProxy, { | proxy, space | action.(proxy, space, this) });
		this.updateNodeProxyList; // register any already existing nodes immediately
	}
	
	updateNodeProxyList {
		var currentItem, items, index;
		widget.view.items !? { currentItem = widget.view.item; };
		items = proxySpace.envir.keys.asArray.sort add: '-';
		widget.view.items = items;
		widget.value = items.indexOf(currentItem) ?? { items.size - 1 };
	}
}
