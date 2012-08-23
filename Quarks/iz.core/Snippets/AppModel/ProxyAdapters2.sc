/* IZ Wed 22 August 2012  4:05 PM EEST

Attempt to redo ProxySelector, ProxyState, ProxySpecSelector, ProxyControl on the basis of ListAdapter. 
Some of these may become subclasses of ListAdapter. 

EXPERIMENTAL

When these are ready, they will replace the old ProxySelection etc. classes
*/



ProxyControl2 : SpecAdapter {
	var <specSelector, <proxy;
	
	*new { | specSelector |
//		^super.new()
	}
	init { this.inspect }


}


ProxySelector2 : ListAdapter {
	classvar <proxyNames;	// cache proxyNames from proxySpaces
	var <proxySpace, <proxy;

	*initClass { proxyNames = IdentityDictionary.new; }
	
	proxySpace_ { | argProxySpace |
		[this, thisMethod.name, argProxySpace].postln;
		proxySpace = argProxySpace ?? { Document.prepareProxySpace };
		
	}

}

