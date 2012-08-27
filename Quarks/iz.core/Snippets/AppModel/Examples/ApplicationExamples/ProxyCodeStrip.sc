/* IZ Sat 18 August 2012 10:14 PM EEST

Strip of controls for a proxy. For use with ProxyCodeMixer

*/

ProxyCodeStrip : AppModel {
	var <>proxyCodeMixer, <>index;
	var <>font, <>proxyOnColor;
	
	*new { | proxyCodeMixer, index |
		^super.new.init(proxyCodeMixer, index)
	}
	
	init { | argProxyCodeMixer, argIndex |
		proxyCodeMixer = argProxyCodeMixer;
		index = argIndex;
		font = font ?? { Font.default.size_(10); };
		proxyOnColor = proxyOnColor ?? { Color.red; };
	}

	gui {
		^VLayout(
			this.popUpMenu(\knobSpecs).proxySpecSelector(\proxySelector).view.font_(font),
			this.knob(\knob).proxyControl(\knobSpecs).view,
			HLayout(
				this.slider(\slider).proxyControl(\sliderSpecs).view,
				VLayout(
					this.numberBox(\knob).view.font_(font),
					this.numberBox(\slider).view.font_(font),
					this.button(\edit).proxyState(\proxySelector,
						{  },
						{ | state | ProxyCodeEditor(ProxyCode(proxyCodeMixer.doc), state.proxy); }
						)
						.view.states_([["ed"]]).font_(font),
					this.button(\startstop).proxyState(\proxySelector)
						.view.states_([[">"], ["||", nil, proxyOnColor]]).font_(font),
				)
			),
			this.popUpMenu(\sliderSpecs).proxySpecSelector(\proxySelector)
				.view.font_(font),
			this.popUpMenu(\proxySelector).proxySelector
				.addAction({ | adapter, func |
					var proxyIndex, specAdapter;
					proxyIndex = 
						proxyCodeMixer.presetHandler.currentIndex 
						* proxyCodeMixer.numStrips
						+ index;
					(ProxySelector.proxyNames[proxyCodeMixer.proxySpace] ?? { [] })[proxyIndex] !? {
						adapter.adapter.selectAt(proxyIndex + 1, func);
						specAdapter = this.getAdapter(\sliderSpecs).adapter;
						{ 
							if (specAdapter.items.size > 1) { specAdapter.selectAt(1, func) }; 
						}.defer(0.1);
					};
				})
				.view.font_(font),
		)
	}
	
	makePreset {
		var adapter;
		^Event make: {
			adapter = this.getAdapter(\proxySelector);
			~proxySelector = (adapter: adapter, proxy: adapter.adapter.item);
			adapter = this.getAdapter(\knobSpecs);
			~knobSpecs = (adapter: adapter, parameter: adapter.adapter.item);
			adapter = this.getAdapter(\sliderSpecs);
			~sliderSpecs = (adapter: adapter, parameter: adapter.adapter.item);
		}			
	}
	
	restorePreset { | argPreset |
		var selector;
		argPreset use: {
			selector = ~proxySelector[\adapter];
			selector.adapter.selectItem(~proxySelector[\proxy], this);
			~knobSpecs[\adapter].adapter.selectItem(~knobSpecs[\parameter]);
			~sliderSpecs[\adapter].adapter.selectItem(~sliderSpecs[\parameter]);
		}
	}
	
}

