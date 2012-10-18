/* IZ Sat 18 August 2012 10:14 PM EEST

Strip of controls for a proxy. For use with scriptMixer

*/

ScriptMixerStrip : AppModel {
	var <>scriptMixer, <>index;
	var <>font, <>proxyOnColor;
	var <presets;	// cache the presets value for faster access in autoSetProxy
	
	*new { | scriptMixer, index |
		^super.new.init(scriptMixer, index)
	}
	
	init { | argSriptMixer, argIndex |
		scriptMixer = argSriptMixer;
		index = argIndex;
		font = font ?? { Font.default.size_(10); };
		proxyOnColor = proxyOnColor ?? { Color.red; };
	}

	gui {
		presets = scriptMixer.getValue(\presets);
		^VLayout(
			this.proxySelectMenu,
			this.popUpMenu(\knob).proxyControlList(\proxy, 2).view.font_(font).fixedWidth_(68),
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
			this.popUpMenu(\slider).proxyControlList(\proxy, 0).view.font_(font),
		)
	}

	proxySelectMenu {
		^this.popUpMenu(\proxy).proxyList(scriptMixer.proxySpace, index)
			.view.font_(font).background_(Color(0.7, 1, 0.8))
	}

	editButton {
		^this.button(\proxy).proxyWatcher(
			{},	// : 0 state - never gets triggered on a button with only one state
			{ | me | ProxyCodeEditor(scriptMixer.proxySpace, me.item); }
		)
		.view.states_([["ed"]]).font_(font)
	}
	
	startStopButton {
		^this.button(\proxy).proxyWatcher({ | me | me.item.checkEvalPlay })
			.view.states_([[">"], ["||", nil, proxyOnColor]]).font_(font)
	}
	
	// return my values to scriptMixer for fast access to get and set presets
	valueCache { ^this.valueCacheNames collect: this.getValue(_) }
	
	valueCacheNames { ^[\proxy, \slider, \knob] }
}
