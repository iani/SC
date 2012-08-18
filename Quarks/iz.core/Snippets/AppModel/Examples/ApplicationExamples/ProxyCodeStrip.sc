/* IZ Sat 18 August 2012 10:14 PM EEST

Strip of controls for a proxy. For use with ProxyCodeMixer

*/

ProxyCodeStrip : AppModel {
	var <>proxyCodeMixer;
	var <>font, <>proxyOnColor;
	
	*new { | proxyCodeMixer |
		^super.new.init(proxyCodeMixer)
	}
	
	init { | argProxyCodeMixer |
		proxyCodeMixer = argProxyCodeMixer;
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
					this.button(\edit).view.states_([["ed"]]).font_(font),
					this.button(\startstop).proxyState(\proxySelector)
						.view.states_([[">"], ["||", nil, proxyOnColor]]).font_(font),
				)
			),
			this.popUpMenu(\sliderSpecs).proxySpecSelector(\proxySelector).view.font_(font),
			this.popUpMenu(\proxySelector).proxySelector.view.font_(font),
		)
	}
	
	makePreset {
		var adapter;
		^Event make: {
			adapter = this.getAdapter(\proxySelector);
			~proxySelector = (adapter: adapter, value: adapter.value);
			adapter = this.getAdapter(\knobSpecs);
			~knobSpecs = (adapter: adapter, value: adapter.value);
			adapter = this.getAdapter(\sliderSpecs);
			~sliderSpecs = (adapter: adapter, value: adapter.value);
		}			
	}
	
	restorePreset { | argPreset |
		argPreset use: {
			~proxySelector[\adapter].valueAction = ~proxySelector[\value];
			~knobSpecs[\adapter].valueAction = ~knobSpecs[\value];
			~sliderSpecs[\adapter].valueAction = ~sliderSpecs[\value];
		}
	}
	
}

