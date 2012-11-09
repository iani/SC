/* iz Sun 28 October 2012  4:58 PM EET
Provide a default proxy space with pre-initialized proxies corresponding to 32 keys. The layout is that of the english computer keyboard.
(8 keys x 4 rows, starting with the numeric keys row and moving downwards: "12345678qwertyuiasdfghjkzxcvbnm,").
Can be expanded in the future to accommodate multiple ProxySpaces.
*/

ProxyCentral {

	classvar default;

	var <proxySpace, <proxyItems, currentProxy;

	*initClass {
		StartUp.add({ ServerBoot.add({ this.reset }) });
	}

	*reset { if (default.notNil) { default.reset } }

	reset { proxyItems do: _.reset; }

	*currentProxy { ^this.default.currentProxy }

	*default {
		default ?? { default = this.new };
		^default;
	}

	*new { ^super.new.init; }

	init {
		proxySpace = ProxySpace();
		this.initProxies;
	}

	initProxies {
		// Initialize proxies
		"12345678qwertyuiasdfghjkzxcvbnm," do: { | char | proxySpace.at(char.asSymbol) };
		proxyItems = IdentityDictionary();
		proxySpace.proxies do: { | p | proxyItems[p.name.asSymbol] = p; };
		this selectProxyItem: $1;
	}

	selectProxyItem { | key | this.currentProxy = proxyItems[key]; }

	currentProxy {
		currentProxy ?? { currentProxy = proxyItems['1'] };
		^currentProxy;
	}

	currentProxy_ { | proxyItem |
		currentProxy = proxyItem;
		this.changed(\setProxy, currentProxy);
	}

}