/* 
IZ, 2011 08 17ff

TODO: Maybe \list notifications should be issued by the proxy list in Library, and not by ProxySpace.
	Maybe ProxyItems should also store their ProxySpace
*/

+ LazyEnvir {
	at { | key | // must be here. If in ProxySpace, then notifications do not work with super. 
		var proxy, proxyItem, proxyList;
		proxy = super.at(key);
		if(proxy.isNil) {
			proxy = this.makeProxy(key);
			envir.put(key, proxy);
			proxyItem = ProxyItem(key, proxy);
			proxyList = this.proxies.add(proxyItem);
			this.changed(\list, this, proxyList); // proxies in order of creation
		};
		^proxy
	}	
}

+ ProxySpace {
	removeNeutral {
		envir.copy.keysValuesDo { arg key, val; if(val.isNeutral) { envir.removeAt(key) } };
		this.changed(\removeNeutral, this);
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
	
	parseSnippets { | string |
		var positions, snippet;
		positions = string.findRegexp("^//:").flop[0];
		positions do: { | p, i |
			snippet = string[p..(positions[i + 1] ?? { string.size }) - 1];
			this.getProxyItemFromSnippet(snippet, i).addSnippet(snippet);
		};
	}

	getProxyItemFromSnippet { | argSnippet, argIndex |
		^this proxyItem: this.at(this.getProxyName(argSnippet, argIndex));
	}

	getProxyName { | argSnippet, argIndex = 0 |
		^(argSnippet.findRegexp("^//:([a-z][a-zA-Z0-9_]+)")[1] ?? {
			[0, format("out%", argIndex)] 
		})[1].asSymbol;
	}

	openHistoryInDoc { | proxyItem |
		var title, docString;
		if (proxyItem.isNil) {
			title = Date.getDate.format("History for all proxies on %Y-%m-%e at %Hh:%Mm:%Ss");
			docString = this.makeHistoryStringForAll;
		}{
			title = format("History for % on %",
				proxyItem.name,
				Date.getDate.format("%Y-%d-%e at %Hh:%mm:%Ss")
			);
			docString = proxyItem.makeHistoryString;
		};
		^Document(title, docString)
	}

	makeHistoryStringForAll {
		var docString, histories;
		docString = format(
			"/* *********** HISTORY FOR ALL PROXIES on % *********** */\n",
			Date.getDate.format("%Y-%m-%e at %Hh:%Mm:%Ss")
		);
		docString = docString ++ this.makeLoadBuffersString;
		histories = this.proxies collect: _.makeHistoryString;
		^histories.inject(docString, { | a, b | a ++ b });
	}

	makeLoadBuffersString { ^BufferItem.makeLoadBuffersString }

	removeProxyItem { | proxyItem |
		var proxyList;
		proxyList = this.proxies;
		this.removeAt(proxyItem.name);
		proxyList.remove(proxyItem);
		this.changed(\list, this, proxyList); // proxies in order of creation
	}	
}
