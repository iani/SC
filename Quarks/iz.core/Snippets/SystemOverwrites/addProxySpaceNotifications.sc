
+ ProxySpace {
	makeProxy {
		var proxy = NodeProxy.new;
		this.initProxy(proxy);
		this.notify(\newProxy, proxy);
		^proxy
	}
}