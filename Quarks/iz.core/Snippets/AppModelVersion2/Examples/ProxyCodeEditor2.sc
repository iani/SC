/* IZ Tue 04 September 2012 10:18 PM BST

Third version of ProxyCodeEditor, using the new AppModel implementation

Edit Code of a proxy from ProxyCode snippets. Provide history of edited versions, and navigation amongst history and amongst different proxies. 

*/

ProxyCodeEditor2 : AppModel {
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
		this.makeWindow(proxy);
		this addMIDI: this.midiSpecs;
	}

	makeWindow { | proxy |
		this.window({ | window |
			this.addWindowActions(window);
			this.addViews(window, proxy);
			// add auto-allocation of controls from parsed NodeProxy parameters: 
/*			controlMenus = [
				{ | i | this.getValue(format("slidermenu%", i).asSymbol) } ! 8,
				{ | i | this.getValue(format("knobmenu%", i).asSymbol) } ! 8,
			].flop.flat;
			controlMenus do: { | cm, i |
				cm.addAction({ | adapter, func |
					if (adapter.adapter.items.size - 1 > i) { adapter.adapter.selectAt(i + 1, func) }
				})	
			}
*/
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
		this.windowToFront(window, { this.enable; });
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

	addViews { | window, proxy |
		window.layout = VLayout(
			[this.textView(\editor).makeStringGetter.listItem.sublistOf(\proxy, { | item | 
				if (item.isNil) { "<empty" } { item.history }
			}).view.font_(Font("Monaco", 10)), s: 10],
			HLayout(
				this.popUpMenu(\proxy, { | me | me.value.adapter.items collect: _.name })
				.addValueListener(window, \index, { | value | window.name = value.adapter.item.name })
				.items_(proxySpace.proxies)
				.item_(
					proxySpace.proxies detect: { | p | p.item === proxy }
				).view.font_(font),
				this.button(\editor).firstItem.view.states_([["<<"]]).font_(font),
				this.button(\editor).previousItem.view.states_([["<"]]).font_(font),
				this.button(\editor).action_({ | widget |
					var proxy = this.proxy;
					proxy !? { 
						proxyCode.evalInProxySpace(
							widget.getString, proxy, this.proxyName, 
							start: false, addToSourceHistory: false
						)
					};
				}).view.states_([["eval"]]).font_(font),
				this.button(\editor).nextItem.view.states_([[">"]]).font_(font),
				this.button(\editor).lastItem.view.states_([[">>"]]).font_(font),
			),
			// for testing new spec update mechanism: 
			this.popUpMenu(\specs, { | me | me.value.adapter.items collect: _[0] })
			.sublistOf(\proxy, { | item | item.specs }).view,
		);
	}
	
	proxy_ { | proxy |
		this.getValue(\proxy).item_(this, proxySpace.proxies detect: { | p | p.item === proxy });
	}

	proxy { ^this.proxyItem.item }
	proxyName { ^this.proxyItem.name }
	proxyItem { ^this.getValue(\proxy).adapter.item }

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

