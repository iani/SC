
+ ProxySpace {
/*	makeProxy {
		var proxy = NodeProxy.new;
		this.initProxy(proxy);
		{ ProxySelector.updateProxyNames(this); }.defer(0.1);
		^proxy
	}
*/
	removeNeutral {
		envir.copy.keysValuesDo { arg key, val; if(val.isNeutral) { envir.removeAt(key) } };
		this.notify(\removeNeutral, this);
	}
}

+ LazyEnvir {
	at { arg key, proxies, proxyItem;
		var proxy;
		proxy = super.at(key);
		if(proxy.isNil) {
			proxy = this.makeProxy(key);
			envir.put(key, proxy);
			ProxySelector.updateProxyNames(this, key); // to be replaced by Library version below
			// new version: 
			proxyItem = ProxyItem(key, proxy);
			this.proxies.add(proxyItem).notify(\list, this); // proxies in order of creation
			// Below not needed. Proxy spec update via \spec notification to ProxyItem
//			Library.put('ProxyItems', proxy, proxyItem); // Access proxies for spec updates
			this.notify(\proxies);
		};
		^proxy
	}
	
	proxies {
		var proxies;
		proxies = Library.at('Proxies', this);
		if (proxies.isNil) { 
			proxies = List.new.add(ProxyItem('-', nil)); 
			Library.put('Proxies', this, proxies);
		};
		^proxies;
	}
	
	proxyItem { | proxy |
		var proxies, item;
		proxies = this.proxies;
		item = proxies.detect({ | p | p.item === proxy });
		if (item.isNil) { proxies.add(item = ProxyItem(this.findKeyForValue(proxy), proxy)) };
		^item;
	}
}