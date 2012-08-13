
NanoK2Stripb {
	var <kontrol;  	// NanoKontrol2 instance to which I belong
	var <proxySpace;
	var <proxyCode;
	var <font;
	
	*new { | kontrol |
		^this.newCopyArgs(kontrol, kontrol.proxySpace,  kontrol.proxyCode, Font.default.size_(10));
	}
	
	disable { this.widgets do: _.disableInput }
	enable { this.widgets do: _.enableInput }

	gui {
		^VLayout(
			PopUpMenu().font_(font)
				.addModel(this, \knobNodeMenu)
				.proxySpaceWatcher(kontrol.proxySpace)
				.proxyNodeSetter(\knobControlMenu)
				.proxyNodeSetter(\knobStartStop)
				.v,
			PopUpMenu().font_(font)
				.addModel(this, \knobControlMenu)
				.proxySpecWatcher
				.proxySpecSetter(\knob).v,
			VLayout(
				HLayout(
					NumberBox().font_(font)
						.addModel(this, \knobNumBox, \knob).v,
					Button().states_([["ed"]]).font_(font).action_({
							this.editNodeSource(\knobNodeMenu);
						}),
				),
				HLayout(
					Knob().addModel(this, \knob, \knobNumBox).v,
					Button().states_([[">"], ["||", Color.black, Color.red]]).font_(font)
						.addModel(this, \knobStartStop)
						.proxyNodeWatcher.v,
				),
			),
			HLayout(
				Slider()
					.addModel(this, \slider, \sliderNumBox)
					.proxyNodeWatcher.v,
				VLayout(
					NumberBox().font_(font)
						.addModel(this, \sliderNumBox, \slider).v,
					Button().states_([["ed"]]).font_(font).action_({
							this.editNodeSource(\sliderNodeMenu);
						}),
					Button().states_([[">"], ["||", Color.black, Color.red]]).font_(font)
						.addModel(this, \sliderStartStop)
						.proxyNodeWatcher.v,
				)
			),
			PopUpMenu().font_(font)
				.addModel(this, \sliderNodeMenu)
				.proxySpaceWatcher(kontrol.proxySpace)
				.proxyNodeSetter(\sliderControlMenu)
				.proxyNodeSetter(\sliderStartStop)
				.v,
			PopUpMenu().font_(font)
				.addModel(this, \sliderControlMenu)
				.proxySpecWatcher
				.proxySpecSetter(\slider).v,
		)
	}

	editNodeSource { | menuID |
		var widget, menu, theNode;
		widget = this.widget(menuID);
		menu = widget.view;
		theNode = widget.getNode;
		theNode !? { 
			ProxySourceEditor(proxyCode, menu.item, theNode)
		};
	}
}
