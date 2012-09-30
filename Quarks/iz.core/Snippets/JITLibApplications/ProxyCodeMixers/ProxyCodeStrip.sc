/* IZ Sat 18 August 2012 10:14 PM EEST

Strip of controls for a proxy. For use with ProxyCodeMixer

*/

ProxyCodeStrip : AppModel {
	var <>proxyCodeMixer, <>index;
	var <>font, <>proxyOnColor;
	var <presets;	// cache the presets value for faster access in autoSetProxy
	
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
		presets = proxyCodeMixer.getValue(\presets);
		^VLayout(
			this.proxySelectMenu,
			this.popUpMenu(\knob).proxyControlList(\proxy, 3).view.font_(font),
			this.knob(\knob).proxyControl.view,
			HLayout(
				this.slider(\slider).proxyControl.view,
				VLayout(
					this.numberBox(\knob).proxyControl.view.font_(font),
					this.numberBox(\slider).proxyControl.view.font_(font),
					this.editButton,
					this.startStopButton
				)
			),
			this.popUpMenu(\slider).proxyControlList(\proxy, 1).view.font_(font),
		)
	}

	editButton {
		^this.button(\proxy).proxyWatcher(
			{},	// : 0 state - never gets triggered on a button with only one state
			{ | me | ProxyCodeEditor(proxyCodeMixer.proxySpace, me.item); }
		)
		.view.states_([["ed"]]).font_(font)
	}
	
	startStopButton {
		^this.button(\proxy).proxyWatcher({ | me | me.item.checkEvalPlay })
			.view.states_([[">"], ["||", nil, proxyOnColor]]).font_(font)
	}

	proxySelectMenu {
		^this.popUpMenu(\proxy).proxyList(proxyCodeMixer.proxySpace)
			.addUpdateAction(\list, { | me | this.autoSetProxy(me) })
			.updater(proxyCodeMixer, \autoSetProxy, { | me | this.autoSetProxy(me) })
			.view.font_(font).background_(Color(0.7, 1, 0.8))
	}

	autoSetProxy { | proxyWidget |
		var proxyIndex, proxies;
		if (proxyWidget.index == 0) {
			proxyIndex = presets.index * proxyCodeMixer.numStrips + index;
			if (proxyWidget.items.size - 1 > proxyIndex) { proxyWidget.index = proxyIndex + 1; }
		}
	}
	
	// return my values to proxyCodeMixer for fast access to get and set presets
	valueCache { ^this.valueCacheNames collect: this.getValue(_) }
	
	valueCacheNames { ^[\proxy, \slider, \knob] }
}
