/* IZ Sun 19 August 2012  3:57 AM EEST
Remaking ProxySourceEditor as AppModel. 

Edit Code of a proxy from ProxyCode snippets. Provide history of edited versions, and navigation amongst history and amongst different proxies. 

*/

ProxyCodeEditor : AppModel {
	classvar <>all;	// all current instances of ProxyCodeEditor; 
	classvar <>windowRects;
	var <proxyCode, <proxySpace, <>font;
	var <history;

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
	}

	makeWindow {
		this.window({ | window |
//			window.name_("Code Editor for : " ++ proxyName);
			window.bounds_(windowRects@@(all.size - 1));
			this.addWindowHandler(window);
			this.addViews(window);
		});
	}

	addWindowHandler { | window |	
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
		.addAction(\setName, { | window, name | window.name = name });
	}	

	addViews { | window |
		window.layout = VLayout(
			[this.textView(\editor).view.font_(Font("Monaco", 11)), s:10],
			[HLayout(
				this.popUpMenu(\proxyMenu).proxySelector.view.font_(font),
				this.button(\proxyMainControl).proxyState(\proxyMenu)
					.view.font_(font)
					.states_([["start", Color.black, Color.green], 
						["stop", Color.black, Color.red]]),
				this.button(\history)
					.view.states_([["<<"]]).font_(font),
				this.button(\history)
					.view.states_([["<"]]).font_(font),
				Button().states_([["eval"]]).font_(font).action_({ this.evalSnippet(false) }),
				this.button(\history)
					.view.states_([[">"]]).font_(font),
				this.button(\history)
					.view.states_([[">>"]]).font_(font),
				this.button(\history)
					.view.states_([["add"]]).font_(font),
				Button().states_([["add"]]).font_(font).action_({ this.evalSnippet }),
				Button().states_([["delete"]]).font_(font).action_({ this.deleteSnippet }),
				Button().states_([["reset specs"]]).font_(font).action_({ this.resetSpecs }),
				Button().states_([["history"]]).font_(font)
					.action_({ proxyCode.openHistoryInDoc(this.proxy) }),
				Button().states_([["all"]]).font_(font)
					.action_({ proxyCode.openHistoryInDoc(nil) }),
				StaticText().font_(font).string_("current:"),
				this.numberBox(\history).list.view.font_(font),
				StaticText().font_(font).string_("all:"),
				this.numberBox(\history).listSize.view.font_(font),
				this.button(\resizeWindow)
					.action_({ | me | this.resizeWindow(me.value) })
					.view.font_(font).states_([["maximize window"], ["minimize window"]])
			), s:1],
		)
	}
	
	proxy_ { | argProxy | this.getAdapter(\proxyMenu).adapter.setProxy(argProxy) }
	
	proxy { ^this.getAdapter(\proxyMenu).adapter.proxy }
}

