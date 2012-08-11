/* IZ Fri 10 August 2012  7:33 PM EEST

Version 2: Refactoring using Widget and ProxyWatchers

A GUI emulating the controls of NanoKontrol2, with features: 

Means to map the controls of any NodeProxy in a ProxySpace to a slider or knob. 
Etc. 

n = NanoKontrol2a(ProxySpace.push);

n = NanoKontrol2b(ProxySpace.push);
n.window.bounds;
*/

NanoKontrol2b {
	classvar <all; // all open windows 

	var <proxySpace, <docName; 
	var <proxyCode; // ProxyCode instance that created me. Holds history of NodeProxy source code
	var <proxies; // array of proxy names used for proxy selection menus
	var <window;	// the main gui window;
	var <presets, <currentPreset;	// store proxy, control, and spec configurations

	var font;
	
	*new { | proxySpace, docName, proxyCode |
		^this.newCopyArgs(proxySpace, docName, proxyCode).init;
	}

	init {
		font = Font.default.size_(10);
		this.addNotifier(proxySpace, \newProxy, { | proxy |
			{ this addProxy: proxy; }.defer(0.1); // wait to get source
		});
		presets = (0..9) collect: NanoK2Preset(_, this);
		currentPreset = presets.first;
		this.makeWindow;	
		this.getProxies;
		all = all add: this;
	}

	getProxies {
		proxySpace.envir.keys do: { | key |
			this.addProxy(proxySpace.envir[key], key);
		}
	}

	addProxy { | proxy, proxyName |
		var strip, stripProxyList;
		proxyName = proxyName ?? { proxySpace.envir.findKeyForValue(proxy); };
		proxies = proxies add: proxyName;
		stripProxyList = [' '] ++ proxies;
	}

	makeWindow {
		window = Window("NanoK2 " ++ (docName ? "ps"), 
			Rect(Window.screenBounds.width - 630, 0, 630, 330)
		);
		WindowHandler(this, window, 
			{ all remove: this; },
			enableAction: { 
				if (window.isClosed.not) { 
					window.view.background = Color(*[0.9, 0.8, 0.7].scramble);
				};
			},
			disableAction: { 
				if (window.isClosed.not) {
					window.view.background = Color(0.9, 0.9, 0.9, 0.5);
				}; 
			}
		);
		window.layout = HLayout(
			VLayout(
				*(presets collect: _.button)),
				*({ | i |
					var 	knob, knobNodemenu, knobCtlMenu, knobVal, knobMin, knobMax, knobEd, 
					slider, sliderNodemenu, sliderCtlMenu, sliderVal, sliderMin, sliderMax,
					sliderEd, sliderPlay;
					
					#knob, knobNodemenu, knobCtlMenu, knobVal, knobMin, knobMax, knobEd, 
					slider, sliderNodemenu, sliderCtlMenu, sliderVal, sliderMin, sliderMax,
					sliderEd, sliderPlay = [
					"knob", "knobNodemenu", "knobCtlMenu", "knobVal", "knobMin", "knobMax", 
					"knobEd", "slider", "sliderNodemenu", "sliderCtlMenu", "sliderVal", 
					"sliderMin", "sliderMax", "sliderEd", "sliderPlay"
					] collect: { | s | format("%%", s, i).asSymbol };
					VLayout(
						PopUpMenu().font_(font),
						PopUpMenu().font_(font),
						HLayout(
							VLayout(
								Button().states_([["ed"]]).font_(font),
								Knob(),
							),
							VLayout(
								NumberBox().font_(font),
								NumberBox().font_(font),
								NumberBox().font_(font),
							),
						),
						HLayout(
							Slider(),
							VLayout(
								NumberBox().font_(font),
								NumberBox().font_(font),
								NumberBox().font_(font),
								Button().states_([["ed"]]).font_(font),
								Button().states_([[">"], ["||"]]).font_(font),
							)
						),
						PopUpMenu().font_(font),
						PopUpMenu().font_(font),
					)
				} ! 8)
//			*(strips collect: _.widgets)
		);
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

NanoK2Presetb {
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

