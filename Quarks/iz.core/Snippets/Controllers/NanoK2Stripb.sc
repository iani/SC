
NanoK2Stripb {
	var <kontrol;  	// NanoKontrol2 instance to which I belong
	var <proxySpace;
	var <proxyCode;
	var <font;
	
	*new { | kontrol |
		^this.newCopyArgs(kontrol, kontrol.proxySpace,  kontrol.proxyCode, Font.default.size_(10));
	}
	
	disable { this.widgets do: _.disableInput }
	enable { this.widgets do: _.enableInput; this.notify(\update) }

	gui {
		^VLayout(
			PxMenu(this, \knobNodeMenu, kontrol.proxySpace).font_(font),
			PxControlsMenu(this, \knobControlMenu, \knobNodeMenu).font_(font),
			VLayout(
				HLayout(
					PxNumberBox(this, \knobNumBox, \knob).font_(font),
					Button().states_([["ed"]]).font_(font).action_({
							this.editNodeSource(\knobNodeMenu);
						}),
				),
				HLayout(
					PxKnob(this, \knob, \knobControlMenu, \knobNumBox),
					PxButton(this, \knobStartStop, \knobNodeMenu)
						.states_([[">"], ["||", Color.black, Color.red]])
						.font_(font),
				),
			),
			HLayout(
				PxSlider(this, \slider, \sliderControlMenu, \sliderNumBox),
				VLayout(
					PxNumberBox(this, \sliderNumBox, \slider).font_(font),
					Button().states_([["ed"]]).font_(font).action_({
							this.editNodeSource(\sliderNodeMenu);
						}),
					PxButton(this, \sliderStartStop, \sliderNodeMenu)
						.states_([[">"], ["||", Color.black, Color.red]])
						.font_(font)
				)
			),
			PxMenu(this, \sliderNodeMenu, kontrol.proxySpace).font_(font),
			PxControlsMenu(this, \sliderControlMenu, \sliderNodeMenu).font_(font)
		);
	}

	editNodeSource { | menuID |
		var widget, menu, theProxy;
		widget = this.widget(menuID);
		menu = widget.view;
		theProxy = widget.getProxy;
		theProxy !? { 
			ProxySourceEditor(proxyCode, menu.item, theProxy)
		};
	}
}
