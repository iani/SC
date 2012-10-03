/* iz Wed 03 October 2012 12:29 PM EEST

Varies the proxy selection behavior of ProxyCodeStrip: 

Instead of selecting a proxy for each strip, the menu select snippets from a list. 

One adds snippets to a strip's select menu list by selecting them in the 
ScriptLibGui and then typing the key corresponding to the strip. 

Typing the key + shift adds and immediately evaluates the new snippet. 

*/

ProxyKeyStrip : ProxyCodeStrip {

	proxySelectMenu {
		/* TODO: change the code below copied from ProxyCodeStrip to implement snippet selection. 
		*/
		^this.popUpMenu(\proxy).proxyList(proxyCodeMixer.proxySpace)
			.addUpdateAction(\list, { | me | this.autoSetProxy(me) })
			.updater(proxyCodeMixer, \autoSetProxy, { | me | this.autoSetProxy(me) })
			.view.font_(font).background_(Color(0.7, 1, 0.8))
	}
}

