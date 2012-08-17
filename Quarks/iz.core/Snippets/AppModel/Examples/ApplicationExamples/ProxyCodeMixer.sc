/* IZ Thu 16 August 2012  6:28 PM EEST

Redoing the NanoKontrol2b idea as a subclass of AppModel.

ProxyCodeMixer.new;

ProxyCode(Document.current);
*/


ProxyCodeMixer : AppModel {
	var <doc, <proxyCode, <proxySpace;
	
	*new { | doc | ^super.new.init; }

	init {
		doc = doc ?? { Document.current };
		proxyCode = ProxyCode(doc);
		proxySpace = proxyCode.proxySpace;
		this.makeWindow;
		this.initValues;
	}

	makeWindow {
		this.window({ | w, app |
			w.layout = HLayout(
				ProxyCodeStrip(this).gui,
				ProxyCodeStrip(this).gui
			)
		})
	}

	initValues {
		this.addNotifier(proxySpace, \newProxy, {
			{ this.updateProxyNames }.defer(0.1); // wait for ProxySpace to register new Proxy
		});
		this.updateProxyNames;	// update list with any already existing proxies
	}
	
	updateProxyNames {
		var pn;
		pn = proxySpace.envir.keys.asArray.sort;
		this.putValue(\proxyNames, pn);
		this.notify(\proxyNames, [pn add: '-']);
	}
}

ProxyCodeStrip : AppModel {
	var <>proxyCodeMixer;
	
	*new { | proxyCodeMixer |
		^super.new.init(proxyCodeMixer)
	}
	
	init { | argProxyCodeMixer |
		proxyCodeMixer = argProxyCodeMixer;

	}

	gui {
		^VLayout(
			this.popUpMenu(\proxyMenu)
				.adapterAction({ | adapter |
					 
				})
				.addNotifierWithSelf(proxyCodeMixer, \proxyNames, { | self, pnames | 
					self.updateItemsAndValue(pnames)
				}).view,
			this.knob(\knob).view,
			this.slider(\slider).view
		)
	}
}