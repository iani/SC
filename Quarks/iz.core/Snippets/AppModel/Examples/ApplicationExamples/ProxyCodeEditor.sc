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
		all = IdentityDictionary.new;
		windowRects = [
			Rect(0, 600, 720, 267), Rect(720, 600, 720, 267), Rect(0, 300, 720, 267),
			Rect(720, 300, 720, 267), Rect(0, 0, 720, 267), Rect(720, 0, 720, 267)
		];
	}
	
	*new { | proxyCode, proxy |
		var existingEditor;
		existingEditor = all detect: { | pce | pce.proxy === proxy };
		existingEditor !? { ^existingEditor.front };
		^super.new(proxyCode).init(proxy);
	}

	front { this.notify(\windowToFront); }

	init { | proxy |
		all[proxy] = this; // TODO: Review this
		proxySpace = proxyCode.proxySpace;
		font = Font.default.size_(10);
		this.makeWindow;
		this.proxy = proxy;
	}

	makeWindow {
		this.window({ | window |
			this.addWindowHandler(window);
			this.addViews(window);
			// add auto-allocation of controls from parsed NodeProxy parameters: 
			controlMenus = [
				{ | i | this.getAdapter(format("slidermenu%", i).asSymbol) } ! 8,
				{ | i | this.getAdapter(format("knobmenu%", i).asSymbol) } ! 8,
			].flop.flat;
			controlMenus[0].addAction({ | adapter, func |
				{	adapter.adapter.items[1..16] do: { | item, index |
						controlMenus[index].adapter.selectAt(index + 1, func);
					};
				}.defer(0.2); // leave time for views to update first
			});
		});
	}

	addWindowHandler { | window |
		var bounds;
		bounds = windowRects@@(all.size - 1);
		window.bounds = bounds;
		WindowHandler(this, window, 
			{ all remove: this; this.objectClosed; }, 
			enableAction: { 
				if (window.isClosed.not) { 
					window.view.background = Color(*[0.9, 0.8, 0.7].scramble);
				};
			},
			disableAction: { 
				if (window.isClosed.not) {
					window.view.background = Color(0.9, 0.9, 0.9, 0.5);
				};
			}
		).addAction(\windowToFront, { | window | window.front })
		.addAction(\setName, { | window, name | window.name = name })
		.addAction(\toggleWindowSize, { | window, mode |
			if (mode == 1) {
				window.bounds = bounds.copy.height_(850).top_(0);
			}{
				window.bounds = bounds;
			}
		});
	}	

	addViews { | window |
		window.layout = VLayout(
			[this.textView(\history).proxyHistory(\proxy).name_(\editor)
				.view.font_(Font("Monaco", 11)), s:10],
			[HLayout(
				this.popUpMenu(\proxy).proxySelector
					.addAction({ this.notify(\setName, this.proxyName) })
					.view.font_(font),
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
				this.numberBox(\history).listIndex.view.font_(font),
				StaticText().font_(font).string_("all:"),
				this.numberBox(\history).listSize.view.font_(font),
				this.button(\resizeWindow)
					.action_({ | me | this.notify(\toggleWindowSize, me.value) })
					.view.font_(font).states_([["maximize window"], ["minimize window"]]),
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
						this.knob(knob).proxyControl(knobmenu).view,
						HLayout(
							this.numberBox(slider).view.font_(font),
							this.numberBox(knob).view.font_(font),
						),
						this.slider(slider).proxyControl(slidermenu).view.orientation_(\horizontal),
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
	
/*	resizeWindow { | size |
		if (size == 0) {
			window.bounds = bounds;
		}{
			window.bounds = bounds.copy.height_(850).top_(0);
		}
	}
*/
}

