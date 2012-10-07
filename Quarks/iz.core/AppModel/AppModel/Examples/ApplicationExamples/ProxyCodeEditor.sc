/* IZ Sun 19 August 2012  3:57 AM EEST
Remaking ProxySourceEditor as AppModel. 

Edit Code of a proxy from ProxyCode snippets. Provide history of edited versions, and navigation amongst history and amongst different proxies. 

*/

ProxyCodeEditor : AppModel {
	classvar <>all;	// all current instances of ProxyCodeEditor; 
	classvar <>windowRects;
	var <proxyCode, <proxySpace, <>font;
	var <history;
	var <controlMenus;	// control menu widgets for initializing parameters from NodeProxy

	*initClass {
		windowRects = [
			Rect(0, 600, 720, 267), Rect(720, 600, 720, 267), Rect(0, 300, 720, 267),
			Rect(720, 300, 720, 267), Rect(0, 0, 720, 267), Rect(720, 0, 720, 267)
		];
		Class.initClassTree(MIDISpecs);
		MIDISpecs.put(this, this.uc33eSpecs);
	}
	
	*new { | proxyCode, proxy |
		var existingEditor;
		existingEditor = all detect: { | pce | pce.proxy === proxy };
		existingEditor !? { ^existingEditor.front };
		proxyCode = proxyCode ?? { ProxyCode() };
		^super.new(proxyCode).init(proxy);
	}

	front { this.notify(\windowToFront); } // bring window to front if it exists

	init { | proxy |
		all = all add: this;
		proxySpace = proxyCode.proxySpace;
		font = Font.default.size_(10);
		this.makeWindow;
		this.proxy = proxy;
		this addMIDI: this.midiSpecs;
	}

	makeWindow {
		this.window({ | window |
			this.addWindowActions(window);
			this.addViews(window);
			// add auto-allocation of controls from parsed NodeProxy parameters: 
			controlMenus = [
				{ | i | this.getAdapter(format("slidermenu%", i).asSymbol) } ! 8,
				{ | i | this.getAdapter(format("knobmenu%", i).asSymbol) } ! 8,
			].flop.flat;
			controlMenus do: { | cm, i |
				cm.addAction({ | adapter, func |
					if (adapter.adapter.items.size - 1 > i) { adapter.adapter.selectAt(i + 1, func) }
				})	
			}
		});
	}

	addWindowActions { | window |
		var bounds;
		bounds = windowRects@@(all.size - 1);
		window.bounds = bounds;
		window.addNotifier(this, \windowToFront, { 
			window.front;
			window.toFrontAction.value;
		});
		window.addNotifier(this, \setName, { | name | window.name = name });
		window.addNotifier(this, \toggleWindowSize, { | mode |
			if (mode == 1) {
				window.bounds = bounds.copy.height_(850).top_(0);
			}{
				window.bounds = bounds;
			}
		});
		this.windowClosed(window, { 
			this.disable(window);
			this.objectClosed;
			all remove: this;
		});
		this.windowToFront(window, { this.enable });
		this.windowEndFront(window, { this.disable; });
		window.addNotifier(this, \colorEnabled, {
			if (window.isClosed.not) { window.view.background = Color(*[0.9, 0.8, 0.7].scramble); }
		});
		window.addNotifier(this, \colorDisabled, {
			if (window.isClosed.not) { window.view.background = Color(0.8, 0.8, 0.8, 0.5); };
		});
	}	

	enable { | window |
		super.enable(true);
		this.notify(\colorEnabled);
	}

	disable { | window |
		super.disable;
		this.notify(\colorDisabled);
	}

	addViews { | window |
		window.layout = VLayout(
			[this.textView(\history).proxyHistory(\proxy).name_(\editor)
				.view.font_(Font("Monaco", 11)), s:10],
			[HLayout(
				[this.popUpMenu(\proxy).proxySelector
					.addAction({ this.notify(\setName, this.proxyName) })
					.view.font_(font), s:10],
				this.button(\proxyMainControl).proxyState(\proxy, { | widget |
						var proxy = widget.adapter.adapter.proxy;
						proxy !? {
							if (proxy.source.isNil) {
								proxyCode.evalInProxySpace(
									this.getAdapter(\history).adapter.item,
									proxy, this.proxyName, true, false
								)
							}{
								proxyCode.startProxy(proxy, this.proxyName)
							}
						}
					})
					.view.font_(font)
					.states_([["start", Color.black, Color.green], 
						["stop", Color.black, Color.red]]),
				this.button(\history).firstItem
					.view.states_([["<<"]]).font_(font),
				this.button(\history).previousItem
					.view.states_([["<"]]).font_(font),
				this.button(\history).getContents(\editor, { | widget |
					var proxy = this.proxy;
					proxy !? {
						proxyCode.evalInProxySpace(widget.view.string, proxy, this.proxyName, 
							start: false, addToSourceHistory: false)
					};
				}).view.states_([["eval"]]).font_(font),
				this.button(\history).nextItem.view.states_([[">"]]).font_(font),
				this.button(\history).lastItem.view.states_([[">>"]]).font_(font),
				this.button(\history).getContents(\editor, \append)
					.view.states_([["add"]]).font_(font),
				this.button(\history).getContents(\editor, \delete)
					.view.states_([["delete"]]).font_(font),
				this.button(\history).getContents(\editor, { | widget |
					var proxy = this.proxy;
					proxy !? { MergeSpecs.parseArguments(proxy, widget.view.string) };
				}).view.states_([["reset specs"]]).font_(font),
				Button().states_([["history"]]).font_(font)
					.action_({ proxyCode.openHistoryInDoc(this.proxy) }),
				Button().states_([["all"]]).font_(font)
					.action_({ proxyCode.openHistoryInDoc(nil) }),
				StaticText().font_(font).string_("current:"),
				[this.numberBox(\history).listIndex.view.font_(font), s: 2],
				StaticText().font_(font).string_("all:"),
				[this.numberBox(\history).listSize.view.font_(font), s: 2],
				this.button(\resizeWindow)
					.action_({ | me | this.notify(\toggleWindowSize, me.value) })
					.view.font_(font).states_([["maximize"], ["minimize"]]),
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
						this.popUpMenu(knobmenu).proxySpecSelector(\proxy).view.font_(font),
						HLayout(
							this.slider(slider).proxyControl(slidermenu)
									.view.orientation_(\vertical),
							VLayout(
								this.knob(knob).proxyControl(knobmenu).view,
								this.numberBox(knob).view.font_(font),
								this.numberBox(slider).view.font_(font),
							)
						),
						this.popUpMenu(slidermenu).proxySpecSelector(\proxy).view.font_(font),
					)

				} ! 8)
			), s:2]

		)
	}
	
	proxy_ { | argProxy | this.getAdapter(\proxy).adapter.proxy = argProxy }
	proxy { ^this.getAdapter(\proxy).adapter.proxy }
	proxyName { ^this.getAdapter(\proxy).adapter.item }
	resizeWindow { this.notify(\toggleWindowSize) }

	*uc33eSpecs { // these specs are for M-Audio U-Control UC-33e, 1st program setting
		^[
			knob0: [\cc, nil, 10, 0],
			knob1: [\cc, nil, 10, 1],
			knob2: [\cc, nil, 10, 2],
			knob3: [\cc, nil, 10, 3],
			knob4: [\cc, nil, 10, 4],
			knob5: [\cc, nil, 10, 5],
			knob6: [\cc, nil, 10, 6],
			knob7: [\cc, nil, 10, 7],
			slider0: [\cc, nil, 7, 0],
			slider1: [\cc, nil, 7, 1],
			slider2: [\cc, nil, 7, 2],
			slider3: [\cc, nil, 7, 3],
			slider4: [\cc, nil, 7, 4],
			slider5: [\cc, nil, 7, 5],
			slider6: [\cc, nil, 7, 6],
			slider7: [\cc, nil, 7, 7],
			startStopButton: [\cc, { | me | me.toggle }, 18, 0],
			prevSnippet: [\cc, { | me | me.action.value }, 19, 0],
			eval: [\cc, { | me | me.action.value }, 20, 0],
			nextSnippet: [\cc, { | me | me.action.value }, 21, 0],
			firstSnippet: [\cc, { | me | me.action.value }, 22, 0],
			add: [\cc, { | me | me.action.value }, 23, 0],
			lastSnippet: [\cc, { | me | me.action.value }, 24, 0],
			resetSpecs: [\cc,  { | me | me.action.value }, 25, 0],
			toggleWindowSize: [\cc,  { | me | me.toggle }, 26, 0],
			delete: [\cc,  { | me | me.action.value }, 27, 0],
		]
	}

}

