/* IZ 2012 08 02
Pop up a window to edit the most recent source code for a NodeProxy generated through ProxyCode. 

Created interactively from GUI of NanoKontrol2. See NanoKontrol2, NanoK2Strip, ProxyCode.

Note: all should actually not be an IdentityDictionary. There should be one such dictionary for each instance of ProxyCode, if we intend to use multiple ProxyCode instances. But currently there are no practical reasons for doing that. In the future, this may be solved transparently by making all into a MultiLevelIdentityDictionary, storing a separate Node-Code dictionary for each instance of ProxyCode. The only methods affected would be initClass and new. 

????? TODO: Always use global history from ProxyCode. Remove proxyHistory variable.

*/

ProxySourceEditor {
	classvar <all; // Dictionary with all instances: Prevents opening multiple windows for same Proxy.
				// (Yet see note above: Conflicts may arise if using multiple ProxyCode 
				// instances concurrently)
	classvar <>windowRects;		// positions and sizes for windows for tiling entire laptop screen  
	classvar <>extraSpecs;		// specs for vol and fadeTime, for all NodeProxies


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
	}

	setProxy { | argProxyName |
		proxyName = proxyName.asSymbol;
		proxy !? {
			this.removeNotifier(proxy, \play);
			this.removeNotifier(proxy, \stop);
			this.removeNotifier(proxy, \updateSpecs);
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
		window.layout = VLayout(
			[TextView().font_(Font("Monaco", 11)).addModel(this, \editor).w, s:10],
			[HLayout(
				Button().states_([["start"], ["stop"]])
					.addModel(this, \startStopButton, action: { | me |
						this.perform([\stopProxy, \startProxy][me.value])
					}).w,
				Button().states_([["<<"]]).action_({
					this.notify(\currentSnippet, 1);
				}),
				Button().states_([["<"]]).action_({
					var widget;
					widget = this.widget(\currentSnippet);
					widget.valueAction = widget.value - 1;
				}),
				Button().states_([["eval"]]).action_({ this.evalSnippet(false) }),
				Button().states_([[">"]]).action_({
					var widget;
					widget = this.widget(\currentSnippet);
					widget.valueAction = widget.value + 1;
				}),
				Button().states_([[">>"]]).action_({
					this.notify(\currentSnippet, proxyHistory.size);
				}),
				Button().states_([["add"]]).action_({ this.evalSnippet }),
				Button().states_([["delete"]]).action_({ this.deleteSnippet }),
				Button().states_([["reset specs"]]).action_({ this.resetSpecs; }),
				StaticText().string_("current:"),
				NumberBox().value_(proxyHistory.size)
					.addModel(this, \currentSnippet, action: { | val, widget |
						val = val.clip(1, proxyHistory.size);
						widget.value = val;
						this.widget(\editor).widget.string = proxyHistory[val - 1];
					}, updateFunc: { | val, me |
						me.valueAction = val;
					}).w,
				StaticText().string_("all:"),
				NumberBox().value_(proxyHistory.size)
					.addModel(this, \numSnippets).w,
				Button()
					.states_([["maximize window"], ["minimize window"]])
					.action_({ | me | this.resizeWindow(me.value); })
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
							PopUpMenu().items_(["-", "vol", "fadeTime", "freq"])
								.addModel(this, knobmenu,
									action: { | val, menuWidget |
										this.setControlParameter(
											this.widget(knobnum),
											menuWidget.widget,
											knob
										);
									}
								).w; knobMenus.last,
						Knob().addModel(this, knob, knobnum).w,
						HLayout(
							NumberBox().font_(Font.sansSerif(10)) 
								.addModel(this, slidernum, slider).w,
							NumberBox().font_(Font.sansSerif(10))
								.addModel(this, knobnum, knob).w
						),
						Slider().orientation_(\horizontal)
							.addModel(this, slider, slidernum).w,
						sliderMenus = sliderMenus add: 
							PopUpMenu().items_(['vol', 'fadeTime', '-'])
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
		this.front;
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
							proxyName, this.widgetValue(\currentSnippet)
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
}
