/* IZ 2012 07 30
A GUI emulating the controls of NanoKontrol2, with features: 

Means to map the controls of any NodeProxy in a ProxySpace to a slider or knob. 
Etc. 

n = NanoKontrol2(ProxySpace.push);
n.window.bounds;
*/

NanoKontrol2 {

	var <proxySpace, <docname;
	var <window;
	var <presets, <currentPreset;
	var <strips;
	
	*new { | proxySpace, docname |
		^this.newCopyArgs(proxySpace, docname).init;
	}
	
	init {
//		this.addNotifier(proxySpace, \newProxy, { | ... args | postf("new proxy! %\n", args) });
//		proxySpace.postln;
		presets = (0..9) collect: NanoK2Preset(_, this);
		currentPreset = presets.first;
		strips = { NanoK2Strip(this) } ! 8;
		this.makeWindow;	
	}
	
	makeWindow {
		window = Window("NanoK2 " ++ (docname ? "ps"), 
			Rect(Window.screenBounds.width - 800, 0, 800, 350));
		window.layout = HLayout(
			VLayout(*(presets collect: _.button)),
			*(strips collect: _.widgets)
		);
		window.onClose { this.objectClosed; };
		window.front;		
	}
	
	loadPreset { | preset |
		currentPreset = preset ? currentPreset;
		
	}
	
	savePreset { | preset |
		currentPreset = preset ? currentPreset;
		
	}
}

NanoK2Preset {
	var <number, <kontrol;
	var data;
	
	*new { | number, kontrol | ^this.newCopyArgs(number, kontrol) }
	
	button { 
		^Button()
			.states_([[number.asString]])
			.action_({ this.loadPreset })
			.keyDownAction_({ | view, key |
				switch (key,
					$s, { this.storePreset }
				)
			});
	}
	
	loadPreset {
		postf("loading preset %\n", number);
		kontrol.loadPreset(this);
	}
	
	storePreset {
		postf("storing preset %\n", number);
		kontrol.savePreset(this);
	}
	
}

NanoK2Strip {
	var <kontrol, <>knobProxy, <>sliderProxy;
	var knobProxyButton, knobControlButton, knob, knobMin, knobVal, knobMax, knobControlSpec;
	var sliderProxyButton, sliderControlButton;
	var slider, sliderMin, sliderVal, sliderMax, sliderControlSpec;
	
	*new { | kontrol | ^this.newCopyArgs(kontrol) }

	widgets {
		^VLayout(
			knobProxyButton = Button()
				.action_({ this.chooseProxy(knobProxyButton, 'knobProxy_') }),
			knobControlButton = Button()
				.action_({ this.chooseControl(knobControlButton) }),
			HLayout(
				knob = Knob(),
				VLayout(
					knobMin = NumberBox(),
					knobVal = NumberBox(),
					knobMax = NumberBox()
				)
			),
			HLayout(
				slider = Slider()
					.action_({ | s | 
						s.value.postln;
					}),
				VLayout(
					sliderMin = NumberBox(),
					sliderVal = NumberBox(),
					sliderMax = NumberBox(),
					Button().states_([["start"], ["stop"]])
					.action_({ | me |
						"NanoK2Strip start stop button action".postln;
						this.perform([\stopProxy, \startProxy][me.value])
					})
				)
			),
			sliderProxyButton = Button()
				.action_({ this.chooseProxy(sliderProxyButton, 'sliderProxy_') }),
			sliderControlButton = Button()
		)
	}

	chooseProxy { | widget, setter |
		var proxyName, proxyIndex, proxyList, proxy, controlList, controlIndex = 0;
		var controlListView;


		kontrol.proxySpace.postln;
		kontrol.proxySpace.envir.postln;
		proxyList = kontrol.proxySpace.envir.keys.asArray.sort;
//		postf("NanoK2Strip, proxylist is: %\n", proxyList);
		
		if ((proxyName = widget.states).isNil) {
			proxyIndex = 0;
			controlList = [];
		}{
			proxyName = proxyName.first.first.asSymbol;
			proxyIndex = proxyList indexOf: proxyName;
			proxy = kontrol.proxySpace[proxyName];
//			[setter, proxy].postln;
//			this.perform(setter, proxy);
//			proxy.postln;

			controlList = proxy.controlKeys ++ [\vol, \fadeTime];
		};
		controlListView = DoubleChoiceList(
			"choose proxy + control", 
			proxyList, 
			{ | view |
				if (view.items.size > 0) {
//					view.items[view.value].postln;
					widget.states = [[view.items[view.value]]];
					proxy = kontrol.proxySpace[view.items[view.value].asSymbol];
					this.perform(setter, proxy);      	
				}
			}, 
			proxyIndex, controlList,
			{
			},
			controlIndex
		).listView2;
	}

	chooseControl { | widget |
		postf("choosing proxy: %\n", widget);	
	}

	startProxy { 
		"starting proxy ".post; sliderProxy.postln;
		if (sliderProxy.notNil) { sliderProxy.play } }

	stopProxy { if (sliderProxy.notNil) { sliderProxy.stop } }

	getPresetData {
		// return data array in form that can be used to load the data back to a preset 
		^[	knobProxyButton.states[0], knobControlButton.states[0], 
			knob.value, knobMin.value, knobVal.value, knobMax.value,
			sliderProxyButton.states[0], sliderControlButton.states[0],
			slider.value, sliderMin.value, sliderVal.value, sliderMax.value
		]
	}
	
	setPresetData { | data |
		// set preset data from an array created by getPresetData		
	}
}

ChoiceList {
	var name, items, action, initItem = 0;
	*new { | name, items, action, initItem = 0 |
		^this.newCopyArgs(name, items, action, initItem).init;
	}

	init {
		var window;
		window = Window(name, Rect(400, 400, 400, 400));
		ListView(window, window.view.bounds)
			.action_(action)
			.items_(items)
			.value_(initItem)
			.keyDownAction_({ | view, c, m, u, k | 
				if (c.ascii == 13) {
//					view.value.postln; 
					view.doAction(view);
					window.close;
				};
			});
		window.front;
	}
}

DoubleChoiceList {
	var name, items1, action1, initItem1, items2, action2, initItem2;
	var <listView1, <listView2;
	*new { | name, items1, action1, initItem1, items2, action2, initItem2 |
		^this.newCopyArgs(name, items1, action1, initItem1, items2, action2, initItem2).init;
	}

	init {
		var window;
		window = Window(name, Rect(400, 400, 400, 400));
		window.layout = HLayout(
			listView1 = ListView(window, window.view.bounds)
				.action_(action1)
				.items_(items1)
				.keyDownAction_({ | view, c, m, u, k | 
				.value_(initItem1)
					if (c.ascii == 13) { view.doAction(view); };
				}),
			listView2 = ListView(window, window.view.bounds)
				.action_(action2)
				.items_(items2)
				.value_(initItem2)
				.keyDownAction_({ | view, c, m, u, k | 
					if (c.ascii == 13) {
						view.doAction(view);
						window.close;
					};
				})
		);
		window.front;
	}
}

