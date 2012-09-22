/* IZ variant of ProxyCodeStrip with 3 knobs, for UC-33e

Strip of controls for a proxy. For use with ProxyCodeMixer3

This is an early version with knobs and no numbers for the 2 upper rows. 

*/

ProxyCodeStrip3 : ProxyCodeStrip {

	gui {
		presets = proxyCodeMixer.getValue(\presets);
		^VLayout(
			this.proxySelectMenu,
			this.knobGui(\knob3, 5),
			this.knobGui(\knob2, 4),
			this.knobGui(\knob1, 3),
			HLayout(
				this.slider(\slider).proxyControl.view,
				VLayout(
					this.numberBox(\slider).proxyControl.view.font_(font),
					this.popUpMenu(\slider).proxyControlList(\proxy, 1).view.font_(font),
					this.editButton,
					this.startStopButton,
				)
			),
		)
	}

	knobGui { | knobName, autoSelectNum |
		^HLayout(
			this.knob(knobName).proxyControl.view,
			VLayout(
				this.numberBox(knobName).proxyControl.view.font_(font),
				this.popUpMenu(knobName).proxyControlList(\proxy, autoSelectNum).view.font_(font),
			)
		)
	}

	valueCacheNames { ^[\proxy, \slider, \knob1, \knob2, \knob3] }
	
}

