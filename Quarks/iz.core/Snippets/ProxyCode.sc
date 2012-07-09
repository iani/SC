/* IZ 2012 07 01
Each Snippet plays in its own proxy. 
Cooperates with Code. 


Forked from ProxyDoc (which will be deprecated). 

To replace ProxyDoc. 


a = ProxyCode.new;
a.inspect;

ProxyCode.all

*/


ProxyCode {
	classvar all;
 
 	var <doc;
	var <proxySpace, proxy, proxyName, snippet, index;

	*new { | doc |
		var new;
		doc = doc ?? { Document.current };
		new = this.all[doc];
		if (new.isNil) { new = this.newCopyArgs(doc).init };
		^new;
	}
	
	*all {
		if (all.isNil) { all = IdentityDictionary.new };
		^all;
	}

	init {
		all[doc] = this;
		proxySpace = ProxySpace.new;
		doc.envir = proxySpace;
	}

	getProxy {
		var code, string;
		code = Code(doc);
		index = code.findIndexOfSnippet;
		snippet = code.getSnippetStringAt(index);
		proxyName = snippet.findRegexp("^//:([a-z][a-zA-Z0-9_]+)")[1];
		proxyName = (proxyName ?? { [0, format("out%", index)] })[1].asSymbol;
		proxy = proxySpace[proxyName];
	}

	evalInProxySpace {
		this.getProxy;
		postf("snippet: %\nproxy (%): %\n", snippet, proxyName, proxy);
		proxy.source = snippet.interpret;
		proxy.play;
	}

	playCurrentDocProxy {
		this.getProxy;
		proxy.play;
		postf("proxy % started: % \n", proxyName, proxy);
	}
	
	stopCurrentDocProxy {
		this.getProxy;
		proxy.stop;
		postf("proxy % stopped: %\n", proxyName, proxy);
	}
}

