/* IZ variant of ProxyCodeStrip with 3 knobs, for UC-33e

Strip of controls for a proxy. For use with ProxyCodeMixer3

This is an early version with knobs and no numbers for the 2 upper rows. 

*/

ProxyCodeStrip3 : AppModel0 {
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
			HLayout(
				this.knob(\knob3).proxyControl(\knobSpecs3).view,
				VLayout(
					this.numberBox(\knob3).view.font_(font),
					this.popUpMenu(\knobSpecs3).proxySpecSelector(\proxySelector).view.font_(font),
				)
			),
			HLayout(
				this.knob(\knob2).proxyControl(\knobSpecs2).view,
				VLayout(
					this.numberBox(\knob2).view.font_(font),
					this.popUpMenu(\knobSpecs2).proxySpecSelector(\proxySelector).view.font_(font),
				)
			),
			HLayout(
				this.knob(\knob1).proxyControl(\knobSpecs1).view,
				VLayout(
					this.numberBox(\knob1).view.font_(font),
					this.popUpMenu(\knobSpecs1).proxySpecSelector(\proxySelector).view.font_(font),
				)
			),

			HLayout(
				this.slider(\slider).proxyControl(\sliderSpecs).view,
				VLayout(
					this.numberBox(\slider).view.font_(font),
					this.popUpMenu(\sliderSpecs).proxySpecSelector(\proxySelector)
						.view.font_(font),
					this.button(\edit).proxyState(\proxySelector,
						{  },
						{ | state | ProxyCodeEditor(ProxyCode(proxyCodeMixer.doc), state.proxy); }
						)
						.view.states_([["ed"]]).font_(font),
					this.button(\startstop).proxyState(\proxySelector)
						.view.states_([[">"], ["||", nil, proxyOnColor]]).font_(font),
				)
			),
			this.popUpMenu(\proxySelector).proxySelector
				.addAction({ | adapter, func |
					var proxyIndex, specAdapter;
					proxyIndex = 
						proxyCodeMixer.presetHandler.currentIndex 
						* proxyCodeMixer.numStrips
						+ index;
					(ProxySelector.proxyNames[proxyCodeMixer.proxySpace] ?? { [] })[proxyIndex] !? {
						adapter.adapter.selectAt(proxyIndex + 1, func);
						{ 
							specAdapter = this.getAdapter(\sliderSpecs).adapter;
							if (specAdapter.items.size > 1) { specAdapter.selectAt(1, func) }; 
							specAdapter = this.getAdapter(\knobSpecs1).adapter; // skip fadeTime
							if (specAdapter.items.size > 3) { specAdapter.selectAt(3, func) }; 
							specAdapter = this.getAdapter(\knobSpecs2).adapter;
							if (specAdapter.items.size > 4) { specAdapter.selectAt(4, func) }; 
							specAdapter = this.getAdapter(\knobSpecs3).adapter;
							if (specAdapter.items.size > 5) { specAdapter.selectAt(5, func) }; 
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
			adapter = this.getAdapter(\knobSpecs1);
			~knobSpecs1 = (adapter: adapter, parameter: adapter.adapter.item);
			adapter = this.getAdapter(\knobSpecs2);
			~knobSpecs2 = (adapter: adapter, parameter: adapter.adapter.item);
			adapter = this.getAdapter(\knobSpecs3);
			~knobSpecs3 = (adapter: adapter, parameter: adapter.adapter.item);
			adapter = this.getAdapter(\sliderSpecs);
			~sliderSpecs = (adapter: adapter, parameter: adapter.adapter.item);
		}			
	}
	
	restorePreset { | argPreset |
		var selector;
		argPreset use: {
			selector = ~proxySelector[\adapter];
			selector.adapter.selectItem(~proxySelector[\proxy], this);
			~knobSpecs1[\adapter].adapter.selectItem(~knobSpecs1[\parameter]);
			~knobSpecs2[\adapter].adapter.selectItem(~knobSpecs2[\parameter]);
			~knobSpecs3[\adapter].adapter.selectItem(~knobSpecs3[\parameter]);
			~sliderSpecs[\adapter].adapter.selectItem(~sliderSpecs[\parameter]);
		}
	}
	
}

