/* IZ Tue 04 September 2012 10:18 PM BST

Fourth version of ProxyCodeEditor, doing away with direct Document coupling. 

Edit Code of a proxy from snippets. Provide history of edited versions, and navigation amongst history and amongst different proxies. 

*/

ProxyCodeEditor : AppModel {
	classvar <all;
	classvar <>windowRects;
	var <proxySpace, <>font;
	var <buffers;		// Dictionary of buffers selected by menus, inserted in code as variables

	*initClass {
		windowRects = [
			Rect(0, 600, 720, 267), Rect(720, 600, 720, 267), Rect(0, 300, 720, 267),
			Rect(720, 300, 720, 267), Rect(0, 0, 720, 267), Rect(720, 0, 720, 267)
		];
		Class.initClassTree(MIDISpecs);
		MIDISpecs.put(this, this.uc33eSpecs);
		all = List.new;
	}
	
	*new { | proxySpace, proxyItem, rect |
		var new;
		new = all detect: { | ed | ed.proxyItem === proxyItem };
		new !? { ^new.front };
		^super.new(proxySpace).init(proxyItem, rect);
	}

	front { this.changed(\windowToFront); } // bring window to front if it exists

	init { | proxyItem, rect |
		proxyItem = proxyItem ?? { proxySpace.proxies.first };
		all add: this;
		font = Font.default.size_(10);
		this.makeWindow(rect);
		this.proxyItem = proxyItem; 
		this addMIDI: this.midiSpecs;
		buffers = IdentityDictionary.new;
	}

	makeWindow { | bounds |
		this.window({ | window |
			this.addWindowActions(window, bounds ?? { windowRects@@(all.size - 1) });
			this.addViews(window);
		});
	}

	addWindowActions { | window, bounds |
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
		this.changed(\colorEnabled);
	}

	disable { | window |
		super.disable;
		this.changed(\colorDisabled);
	}

	addViews { | window |
		window.layout = VLayout(
			[this.textView(\editor).makeStringGetter
			.listItem({ | me |
				if (me.item.size == 0) {
					"//:out\n{ | freq = 440 | SinOsc.ar(freq, 0, 0.1) }"
				}{
					me.item;
				}
			})
			.appendOn
			.replaceOn
			.updateAction(\restore, { | string, me | me.view.string = string })
			.sublistOf(\proxy, { | item, widget |
				if (item.isNil) { "<empty>" } { item.history; }
			}).view.font_(Font("Monaco", 10)), s: 10],
			[
			HLayout(
				[this.popUpMenu(\proxy).proxyList(proxySpace)
				.addValueListener(window, \index, { | value | window.name = value.adapter.item.name })
				.view.font_(font), s:4],
				this.button(\proxy).proxyWatcher({ | me |
					var addp = true, editor, snippet;
					me.item.item !? { addp = false };
					editor = this.getValue(\editor);
					snippet = editor.getString;
					if (me.item.item.isNil or: { me.item.item.source.isNil }) {
						this.evalSnippet(snippet, start: true, addToSourceHistory: false
						);
					}{
						me.item.play;
					};
					me.checkProxy(me.item);
					editor.changed(\restore, snippet);
				}).view.states_(
					[["start", Color.black, Color.green], ["stop", Color.black, Color.red]]
				).font_(font),
				this.button(\editor).firstItem.view.states_([["<<"]]).font_(font),
				this.button(\editor).previousItem.view.states_([["<"]]).font_(font),
				this.button(\editor).action_({ | widget |
					var snippet;
					snippet = widget.value.getString;
					this.evalSnippet(snippet, start: false, addToSourceHistory: false);
					// eval button must re-send current string to editor
					widget.value.changed(\restore, snippet); // to restore from history update
				}).view.states_([["eval"]]).font_(font),
				this.button(\editor).nextItem.view.states_([[">"]]).font_(font),
				this.button(\editor).lastItem.view.states_([[">>"]]).font_(font),
				this.button(\editor).changedAction(\append).view.states_([["add"]]).font_(font),
				this.button(\editor).changedAction(\replace).view.states_([["replace"]]).font_(font),
				this.button(\editor).delete.view.states_([["delete"]]).font_(font),
				this.button(\editor).action_({ | widget |
					var proxy = this.proxyItem.item;
					proxy !? { MergeSpecs.parseArguments(proxy, widget.getString) };
				}).view.states_([["reset specs"]]).font_(font),
				Button().states_([["doc"]]).font_(font)
					.action_({ proxySpace.openHistoryInDoc }),
				StaticText().font_(font).string_("current:"),
				[this.numberBox(\editor).listIndex.view.font_(font), s: 2],
				StaticText().font_(font).string_("all:"),
				[this.numberBox(\editor).listSize.view.font_(font), s: 2],
				this.button(\resizeWindow)
					.action_({ | me | this.changed(\toggleWindowSize, me.view.value) })
					.view.font_(font).states_([["maximize"], ["minimize"]]),
			), 
			s:1],
			// Buffer menus: 
			[HLayout(
				*({ | i |
					var valName;
					valName = format("b%", i).asSymbol;
					this.popUpMenu(valName)
					.updater(BufferItem, \bufferList, { | me, names | me.items_(['-'] ++ names); })
					.do({ | me |
						me.items = ['-'] ++ Library.at['Buffers'].keys.asArray.sort 
					})
					.addValueListener(this, \index, { | val | 
						this.updateBuffers(valName, val.adapter.item) })
					.view.font_(font).fixedWidth_(82)
				} ! 8), 
			), s: 1],
			[HLayout(
				*({ | i |
					var knobmenu, slidermenu;
					knobmenu = format("knobmenu%", i).asSymbol;
					slidermenu = format("slidermenu%", i).asSymbol;
					VLayout(
						this.popUpMenu(knobmenu).proxyControlList(\proxy, i * 2 + 2)
						.view.font_(font),
						HLayout(
							this.slider(slidermenu).proxyControl
									.view.orientation_(\vertical),
							VLayout(
								this.knob(knobmenu).proxyControl.view,
								this.numberBox(knobmenu).proxyControl.view.font_(font),
								this.numberBox(slidermenu).proxyControl.view.font_(font),
							)
						),
						this.popUpMenu(slidermenu).proxyControlList(\proxy, i * 2 + 1)
						.view.font_(font),
					)

				} ! 8)
			), s:2]
			
		);
	}

	evalSnippet { | snippet, start = true, addToSourceHistory = false |
		var myProxyName, myProxyItem;
		myProxyName = snippet.findRegexp("^//:([a-z][a-zA-Z0-9_]+)")[1];
		if (myProxyName.notNil) {
			myProxyItem = proxySpace proxyItem: proxySpace.at(myProxyName[1].asSymbol);
		}{
			myProxyItem = this.proxyItem;
		};
		this.proxyItem = myProxyItem;
		if (myProxyItem.history.size == 0) { addToSourceHistory = true };
		myProxyItem.evalSnippet(snippet, start: start, addToSourceHistory: addToSourceHistory);
	}					

	proxyItem_ { | proxyItem | this.getValue(\proxy).item_(this, proxyItem); }
	proxyName { ^this.proxyItem.name }
	proxyItem { ^this.getValue(\proxy).adapter.item }

	updateBuffers { | valName, bufName | // insert variable declaration for chosen buffers to code
		var bufStrings, varString, editor, source, lines;
		buffers[valName] = if (bufName === '-') { nil } { bufName };
		bufStrings = buffers.keys.asArray.sort collect: { | bname |
			format("% = '%'.b", bname, buffers[bname]);
		};
		varString = bufStrings[1..].inject(bufStrings[0], { | vars, s | vars prCat: ", " ++ s });
		if (varString.notNil) { varString = "var " ++ varString ++ ";" };
		editor = this.getValue(\editor);
		source = editor.getString;
		lines = source.split($\n);
		if (lines[0][0] !== $/) { lines = ["//:"] ++ lines; };
		if (lines[1][..2] == "var") { lines[1] = varString; } { lines = lines.insert(1, varString); };
		lines remove: nil; // if no buffers chosen, then remove var declaration line
		editor.adapter.replace(this, 
			lines[1..].inject(lines[0], { | code, line | code prCat: "\n" ++ line })
		);
	}

	resizeWindow { this.changed(\toggleWindowSize) }

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

