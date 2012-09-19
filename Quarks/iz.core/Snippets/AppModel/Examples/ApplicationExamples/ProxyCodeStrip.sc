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
		[this, thisMethod.name, "index:", index].postln;
		font = font ?? { Font.default.size_(10); };
		proxyOnColor = proxyOnColor ?? { Color.red; };
	}

	gui {
		^VLayout(
			this.popUpMenu(\knob).proxyControlList(\proxy).view.font_(font),
			this.knob(\knob).proxyControl.view,
			HLayout(
				this.slider(\slider).proxyControl.view,
				VLayout(
					this.numberBox(\knob).proxyControl.view.font_(font),
					this.numberBox(\slider).proxyControl.view.font_(font),
					this.button(\proxy).proxyWatcher(
						{},	// this never gets triggered on a button with only one state
						{ | me | ProxyCodeEditor(ProxyCode(proxyCodeMixer.doc), me.item.item); }
					)
					.view.states_([["ed"]]).font_(font),
					this.button(\proxy).proxyWatcher
					.view.states_([[">"], ["||", nil, proxyOnColor]]).font_(font),
				)
			),
			this.popUpMenu(\slider).proxyControlList(\proxy).view.font_(font),
			this.popUpMenu(\proxy).proxyList(proxyCodeMixer.proxySpace)
				.addUpdateAction(\list, { | me | this.autoSetProxy(me) })
				.view.font_(font),
		)
	}

	autoSetProxy { | widget |
		var proxyIndex, proxies;
		proxyIndex = proxyCodeMixer.presetHandler.currentIndex * proxyCodeMixer.numStrips + index;
		[this, thisMethod.name, proxyCodeMixer.presetHandler.currentIndex, proxyCodeMixer.numStrips,
			"index:", index, proxyIndex, widget.items.size].postln;
		if (widget.items.size - 1 == index) { widget.index = index }
	}

	makePreset {
		var value;
		^Event make: {
			value = this.getValue(\proxy);
			~proxy = (value: value, item: value.item);
			value = this.getValue(\knob);
			~knobControl = (value: value, item: value.item);
			value = this.getValue(\slider);
			~sliderControl = (value: value, item: value.item);
		}
	}
	
	restorePreset { | argPreset |
		var value;
		argPreset use: {
			~proxy[\value].item_(nil, ~proxy[\item]);
			~knobControl[\value].item_(nil, ~knobControl[\item]);
			~sliderControl[\value].item_(nil, ~sliderControl[\item]);
		}
	}

}
