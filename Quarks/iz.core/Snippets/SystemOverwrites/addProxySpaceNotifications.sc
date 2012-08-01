
+ ProxySpace {
	makeProxy {
		var proxy = NodeProxy.new;
		this.initProxy(proxy);
		this.notify(\newProxy, proxy);
		^proxy
	}
/*
	allProxyNames {
		var names;
		names = SortedList(8, { |a,b| a < b });
		this.keysValuesDo({ arg key, px;
			if (px.rate === rate)  {
				if (func.value(px, key), { pxs.add(key) })
			}
		});
		^pxs;
*/

}