/* IZ 2012 07 30
A GUI emulating the controls of NanoKontrol2, with features: 

Means to map the controls of any NodeProxy in a ProxySpace to a slider or knob. 
Etc. 

n = NanoKontrol2(ProxySpace.push);
n.window.bounds;
*/

NanoKontrol2 {
	classvar <all; // all open windows 
	classvar <>current; // current instance = top window instance
	// Midi controller always addresses current instance

	var <proxySpace, <docName; 
	var <proxyCode; // ProxyCode instance that created me. Holds history of NodeProxy source code
	var <proxies; // array of proxy names used for proxy selection menus
	var <window;	// the main gui window;
	var <presets, <currentPreset;	// store proxy, control, and spec configurations
	var <strips;	// 8 control strips emulating those of the Korg NanoKontrol2 
	
	*new { | proxySpace, docName, proxyCode |
		^this.newCopyArgs(proxySpace, docName, proxyCode).init;
	}

	init {
		this.addNotifier(proxySpace, \newProxy, { | proxy |
			{ this addProxy: proxy; }.defer(0.1); // wait to get source
		});
		presets = (0..9) collect: NanoK2Preset(_, this);
		currentPreset = presets.first;
		strips = { | i | NanoK2Strip(this, i + 1) } ! 8;
		this.makeWindow;	
		this.getProxies;
		all = all add: this;
	}

	getProxies {
		proxySpace.envir.values do: { | proxy |
			this addProxy: proxy;
		};
	}
	
	addProxy { | proxy |
		var pxName, strip, stripProxyList;
		pxName = proxySpace.envir.findKeyForValue(proxy);
		this.addNotifiersForProxy(proxy, pxName);
		proxies = proxies add: pxName;
		stripProxyList = [' '] ++ proxies;
		strips do: _.setProxies(stripProxyList, proxy);
	}

	addNotifiersForProxy { | proxy, pxName |
		this.addNotifier(proxy, \play, { this.proxyStarted(proxy, pxName) });
		this.addNotifier(proxy, \clear, { this.proxyStopped(proxy, pxName) });
		this.addNotifier(proxy, \end, { this.proxyStopped(proxy, pxName) });
		this.addNotifier(proxy, \free, { this.proxyStopped(proxy, pxName) });
		this.addNotifier(proxy, \stop, { this.proxyStopped(proxy, pxName) });
	}
	
	proxyStarted { | proxy, name |
		strips do: _.proxyStarted(name);
	}

	proxyStopped { | proxy, name |
		strips do: _.proxyStopped(name);
	}


	makeWindow {
		window = Window("NanoK2 " ++ (docName ? "ps"), 
			Rect(Window.screenBounds.width - 800, 0, 800, 350));
		window.layout = HLayout(
			VLayout(*(presets collect: _.button)),
			*(strips collect: _.widgets)
		);
		window.onClose = {
			this.objectClosed;
			all remove: this;
			if (current === this and: { all.size > 0 }) {
				all.first.window.toFrontAction.value;
			};
		};
		window.toFrontAction = {
			if (current === this) { } {
				current = this;
				window.view.background = Color(*([0.9, 0.8, 0.7].scramble));
				all do: { | k |
					if (k !== this and: { k.window.isClosed.not }) {
						k.window.view.background = Color.grey(0.8)
					}
				};
			}
		};
		window.front
	}

	loadPreset { | preset |
		currentPreset = preset ? currentPreset;
		
	}

	savePreset { | preset |
		currentPreset = preset ? currentPreset;

	}
	
	editNodeProxySource { | proxyName |
		// received from NanoK2Strip. Edit the source code of the proxy
		// (and replace source). 
		proxyCode editNodeProxySource: proxyName;
	}
}

NanoK2Preset {
	var <number, <kontrol;
	var data;

	*new { | number, kontrol | ^this.newCopyArgs(number, kontrol) }

	button {
		^Button()
			.states_([[number.asString]])
			.action_({ this.loadPreset })
			.keyDownAction_({ | view, key |
				switch (key,
					$s, { this.storePreset }
				)
			});
	}

	loadPreset {
		postf("loading preset %\n", number);
		kontrol.loadPreset(this);
	}

	storePreset {
		postf("storing preset %\n", number);
		kontrol.savePreset(this);
	}

}

