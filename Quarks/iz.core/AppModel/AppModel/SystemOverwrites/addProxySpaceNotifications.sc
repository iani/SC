
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
	at { arg key;
		var proxy;
		proxy = super.at(key);
		if(proxy.isNil) {
			proxy = this.makeProxy(key);
			envir.put(key, proxy);
			ProxySelector.updateProxyNames(this, key);
		};
		^proxy

	}	
}