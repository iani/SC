/* IZ 2012 08 02
Pop up a window to edit the most recent source code for a NodeProxy generated through ProxyCode. 

Created interactively from GUI of NanoKontrol2. See NanoKontrol2, NanoK2Strip, ProxyCode.

Note: all should actually not be an IdentityDictionary. There should be one such dictionary for each instance of ProxyCode, if we intend to use multiple ProxyCode instances. But currently there are no practical reasons for doing that. In the future, this may be solved transparently by making all into a MultiLevelIdentityDictionary, storing a separate Node-Code dictionary for each instance of ProxyCode. The only methods affected would be initClass and new. 

????? TODO: Always use global history from ProxyCode. Remove proxyHistory variable.

============================= MIDI ===========================

The MIDI settings of this version are for the U-Control UC-33e, using standard preset 1, as activated by button labeled "1" at the right part of the device. Different configurations can be installed easily by setting the midiSpecs class variable of ProxySourceEditor. For an example of the midiSpecs format see class method midiSpecs. 

Configuration of action for the numeric buttons of UC-33e:

| 1: previous snippet	| 2:	 eval				| 3: next snippet |
| 1: first snippet		| 2:	 add					| 3: last snippet |
| 1: reset specs		| 2:	 toggle window size 	| 3: delete       |
| - 					| 0: start/stop			| -               |

*/

ProxySourceEditor {
	classvar <all; // Dictionary with all instances: Prevents opening multiple windows for same Proxy.
				// (Yet see note above: Conflicts may arise if using multiple ProxyCode 
				// instances concurrently)
	classvar <>windowRects;		// positions and sizes for windows for tiling entire laptop screen  
	classvar <>extraSpecs;		// specs for vol and fadeTime, for all NodeProxies
	classvar <>font;			// The font used for the first line of GUI items
	classvar >midiSpecs;		/* Dictionary (Event) holding one MIDI spec for each GUI item 
		
	*/

	var <proxyCode;	// ProxyCode instance that holds the ProxySpace, NodeProxies and History.
	var <proxyName;	// Name of the current NodeProxy, whose code is being edited here
	var <proxy;		// The current NodeProxy
	var <proxyHistory;	// array of source code strings in order of execution
	var <specs;		/* Holds specs for controlling the parameters of the current NodeProxy, 
		in the form of an array of pairs: [symbol, spec], where 
		symbol is the name of the parameter,
		spec is the ControlSpec for mapping GUI or MIDI input to the appropriate range,
		 */

	
	// ======= GUI ITEMS AND DATA =======
	var <window;
	var <controlMenus;	// array of 16 menus of sliders and knobs, used for fast access 
					// when updating the parameter lists from the NodeProxy's controls
	var <>bounds; 	// saves original window bounds for reset after maximizing

	*initClass {
		Class.initClassTree(Spec);
		Class.initClassTree(ControlSpec);
		extraSpecs = [[\vol, ControlSpec(0, 2)], [\fadeTime, ControlSpec(0, 60)]];
		StartUp add: {
			all = IdentityDictionary.new;
			windowRects = [
				Rect(0, 600, 720, 267),
				Rect(720, 600, 720, 267),
				Rect(0, 300, 720, 267),
				Rect(720, 300, 720, 267),
				Rect(0, 0, 720, 267),
				Rect(720, 0, 720, 267)
			];
		}	
	}

	*new { | proxyCode, proxyName |
		var existingEditor;
		existingEditor = all[proxyName];
		existingEditor !? { ^existingEditor.front };
		font = Font.default.size_(10);
		^this.newCopyArgs(proxyCode, proxyName).init;
	}

	front { window.front }

	init {
		this.addNotifier(proxyCode, \proxySource, { | argProxyName, argHistory |
			this.updateHistory(argProxyName, argHistory);
		});
		this.makeWindow;
		this setProxy: proxyName;
		all[proxyName] = this;
		this.updateSpecs([]); // TODO: use resetSpecs instead ??? !!!
		if (midiSpecs.isNil) { this.initMIDIspecs };
	}

	setProxy { | argProxyName |
		proxyName = proxyName.asSymbol;
		proxy !? {
			this.removeNotifier(proxy, \play);
			this.removeNotifier(proxy, \stop);
			this.removeNotifier(proxy, \proxySpecs);
		};
		proxy = proxyCode.proxySpace[proxyName];
		this.addNotifier(proxy, \play, { this.setValue(\startStopButton, 1) });
		this.addNotifier(proxy, \stop, { this.setValue(\startStopButton, 0) });
		this.addNotifier(proxy, \proxySpecs, { | ... specs | this.updateSpecs(specs) });
		if (proxy.isMonitoring) {
			proxy.notify(\play);
		}{
			proxy.notify(\stop);
		};
		proxyHistory = proxyCode.proxyHistory[proxyName];
		this.notify(\numSnippets, proxyHistory.size);
		this.notify(\currentSnippet, proxyHistory.size);
	}

	makeWindow {
		var knobMenus, sliderMenus;
		window = Window(proxyName, bounds = windowRects@@(all.size));
		window.onClose = { this.closed };
		window.toFrontAction = { this.enable; };
		window.layout = VLayout(
			[TextView().font_(Font("Monaco", 11)).addModel(this, \editor).w, s:10],
			[HLayout(
				Button().states_([["start"], ["stop"]])
					.font_(font)
					.addModel(this, \startStopButton, action: { | me |
						this.perform([\stopProxy, \startProxy][me.value])
					}).w,
				Button().states_([["<<"]]).font_(font)
					.addModel(this, \firstSnippet, action: {
						this.notify(\currentSnippet, 1);
					}).w,
				Button().states_([["<"]]).font_(font)
					.addModel(this, \prevSnippet, action: {
						this.widget(\currentSnippet).decrement(1, 1);
					}).w,
				Button().states_([["eval"]]).font_(font).action_({ this.evalSnippet(false) }),
				Button().states_([[">"]]).font_(font)
					.addModel(this, \nextSnippet, action: {
						this.widget(\currentSnippet).increment(1, proxyHistory.size);
					}).w,
				Button().states_([[">>"]]).font_(font)
					.addModel(this, \lastSnippet, action: {
						this.notify(\currentSnippet, proxyHistory.size);
					}).w,
				Button().states_([["add"]]).font_(font)
					.addModel(this, \add, action: { this.evalSnippet }).w,
				Button().states_([["delete"]]).font_(font)
					.addModel(this, \delete, action: { this.deleteSnippet }).w,
				Button().states_([["reset specs"]]).font_(font)
					.addModel(this, \resetSpecs, action: { this.resetSpecs; }).w,
				StaticText().font_(font).string_("current:"),
				NumberBox().font_(font).value_(proxyHistory.size)
					.addModel(this, \currentSnippet, action: { | val, widget |
						widget.widget.value = val = val max: 1 min: proxyHistory.size;
						this.widget(\editor).widget.string = proxyHistory[val - 1];
					}).w,
				StaticText().font_(font).string_("all:"),
				NumberBox().font_(font).value_(proxyHistory.size).enabled_(false)
					.addModel(this, \numSnippets).w,
				Button().font_(font)
					.states_([["maximize window"], ["minimize window"]])
					.addModel(this, \toggleWindowSize, action: { | me |
						this.resizeWindow(me.value);
					}).w
			), s:1],
			[HLayout(
				*({ | i |
					var knob, slider, knobmenu, slidermenu, knobnum, slidernum;
					knobmenu = format("knobmenu%", i).asSymbol;
					knob = format("knob%", i).asSymbol;
					slidermenu = format("slidermenu%", i).asSymbol;
					slider = format("slider%", i).asSymbol;
					knobnum = format("knobnum%", i).asSymbol;
					slidernum = format("slidernum%", i).asSymbol;
					VLayout(
						knobMenus = knobMenus add: 
							PopUpMenu().font_(font).items_(["-", "vol", "fadeTime", "freq"])
								.addModel(this, knobmenu,
									action: { | val, menuWidget |
										this.setControlParameter(
											this.widget(knobnum),
											menuWidget.widget,
											knob
										);
									}
								).w; knobMenus.last,
						Knob().addModel(this, knob, knobnum)
							.w,
						HLayout(
							NumberBox().font_(Font.sansSerif(10)) 
								.addModel(this, slidernum, slider).w,
							NumberBox().font_(Font.sansSerif(10))
								.addModel(this, knobnum, knob).w
						),
						Slider().orientation_(\horizontal)
							.addModel(this, slider, slidernum)
							.w,
						sliderMenus = sliderMenus add: 
							PopUpMenu().font_(font).items_(['vol', 'fadeTime', '-'])
								.value_(2)
								.addModel(this, slidermenu,
									action: { | val, menuWidget |
										this.setControlParameter(
											this.widget(slidernum),
											menuWidget.widget,
											slider
										);
									}
								).w; sliderMenus.last
					)
				} ! 8)
			), s:2]
		);
		controlMenus = [sliderMenus, knobMenus].flop.flat;
		this.addMIDI(this.midiSpecs);
		this.front;
	}

	enable {
		var view;
		super.enable;
		window.view.background = Color(*[0.9, 0.8, 0.7].scramble);
	}

	disable {
		super.disable;
		window.view.background = Color(0.9, 0.9, 0.9, 0.5);
	}


	updateSpecs { | argSpecs |
		var paramNames, menuItems, size;
		specs = extraSpecs;
		argSpecs[0] !? { specs = specs ++ argSpecs };
		paramNames = specs.flop.first;
		menuItems = paramNames.copy add: '-';
		paramNames do: { | m, i |
			controlMenus[i].items_(menuItems).valueAction = i;
		}
	}

	setControlParameter { | controlWidget, menuWidget, specWidget |
		var parameter;
		parameter = menuWidget.item.asSymbol;
		controlWidget.action = switch ( parameter,
			'-', { {} },
			\vol, { { | val | proxy.vol = val; } },
			\fadeTime, { { | val | proxy.fadeTime = val; } },
			{ { | val | proxy.set(parameter, val); } }
		);
		this.widget(specWidget).spec = specs[menuWidget.value][1];
	}

	resetSpecs {
		var snippet;
		snippet = this.widget(\editor).widget.string;
		if (	snippet[0] === $/) {
			proxyCode.parseArguments(proxy, snippet);
		}{
			proxyCode.parseArguments(proxy)
		};
	}

	closed {
		all[proxyName] = nil;
		this.objectClosed;
	}

	evalSnippet { | addToSourceHistory = true |
		proxyCode.evalInProxySpace(
			this.widget(\editor).widget.string, 
			proxyCode.proxySpace[proxyName], proxyName, false, addToSourceHistory
		)
	}

	startProxy {
		if (proxy.source.isNil) {
			proxyCode.evalInProxySpace(
				this.widget(\editor).widget.string, 
				proxyCode.proxySpace[proxyName], proxyName, true, false
			);
		}{
			proxyCode.startProxy(proxy, proxyName);
		}
	}
	stopProxy { proxyCode.stopProxy(proxy, proxyName) }

	deleteSnippet {
		var dialog;
		if (proxyHistory.size <= 1) { ^"Cannot delete the last snippet from history".postcln };
		dialog = Window.new.layout_(
			VLayout(
				StaticText().string_("Do you really want to delete this snippet?"),
				HLayout(
					Button().states_([["OK"]]).action_({
						proxyCode.deleteNodeSourceCodeFromHistory(
							proxyName, this.widget(\currentSnippet).widget.value
						);
						dialog.close;
					}),
					Button().states_([["CANCEL"]]).action_({
						"Cancelled".postcln;
						dialog.close;
					})
				)
			)
		).front;
	}

	updateHistory { | argProxyName, argHistory |
		if (argProxyName === proxyName) {
			proxyHistory = argHistory;
			this.notify(\currentSnippet, proxyHistory.size);
			this.notify(\numSnippets, proxyHistory.size);
			this.widget(\editor).widget.string = proxyHistory.last;
		}
	}
	
	resizeWindow { | type |
		if (type == 0) {
			window.bounds = bounds;
		}{
			window.bounds = bounds.copy.height_(850).top_(0);
		}
	}

	midiSpecs {
		if (midiSpecs.isNil) {
			midiSpecs = [
				knob0: [\cc, 10, 0],
				knob1: [\cc, 10, 1],
				knob2: [\cc, 10, 2],
				knob3: [\cc, 10, 3],
				knob4: [\cc, 10, 4],
				knob5: [\cc, 10, 5],
				knob6: [\cc, 10, 6],
				knob7: [\cc, 10, 7],
				slider0: [\cc, 7, 0],
				slider1: [\cc, 7, 1],
				slider2: [\cc, 7, 2],
				slider3: [\cc, 7, 3],
				slider4: [\cc, 7, 4],
				slider5: [\cc, 7, 5],
				slider6: [\cc, 7, 6],
				slider7: [\cc, 7, 7],
				startStopButton: [\cc, 18, 0, nil, { | me | me.toggle }],
				prevSnippet: [\cc, 19, 0, nil, { | me | me.action.value }],
				eval: [\cc, 20, 0, nil, { | me | me.action.value }],
				nextSnippet: [\cc, 21, 0, nil, { | me | me.action.value }],
				firstSnippet: [\cc, 22, 0, nil, { | me | me.action.value }],
				add: [\cc, 23, 0, nil, { | me | me.action.value }],
				lastSnippet: [\cc, 24, 0, nil, { | me | me.action.value }],
				resetSpecs: [\cc, 25, 0, nil, { | me | me.action.value }],
				toggleWindowSize: [\cc, 26, 0, nil, { | me | me.toggle }],
				delete: [\cc, 27, 0, nil, { | me | me.action.value }],
			]
		};
		^midiSpecs;
	}
}
