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
			{},	// this never gets triggered on a button with only one state
			{ | me | ProxyCodeEditor(ProxyCode(proxyCodeMixer.doc), me.item.item); }
		)
		.view.states_([["ed"]]).font_(font)
	}
	
	startStopButton {
		^this.button(\proxy).proxyWatcher
			.view.states_([[">"], ["||", nil, proxyOnColor]]).font_(font)
	}

	proxySelectMenu {
		^this.popUpMenu(\proxy).proxyList(proxyCodeMixer.proxySpace)
			.addUpdateAction(\list, { | me | this.autoSetProxy(me) })
			.view.font_(font).background_(Color(0.7, 1, 0.8))
	}

	autoSetProxy { | proxyWidget |
		var proxyIndex, proxies;
		proxyIndex = proxyCodeMixer.getValue(\presets).index * proxyCodeMixer.numStrips + index;
		if (proxyWidget.items.size - 1 > index) { proxyWidget.index = index + 1; }
	}
	
	// return my values to proxyCodeMixer for fast access to get and set presets
	valueCache { ^this.valueCacheNames collect: this.getValue(_) }
	
	valueCacheNames { ^[\proxy, \slider, \knob] }
}
