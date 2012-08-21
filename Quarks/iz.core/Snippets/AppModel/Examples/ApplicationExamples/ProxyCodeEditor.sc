/* IZ Sun 19 August 2012  3:57 AM EEST
Remaking ProxySourceEditor as AppModel. 

Edit Code of a proxy from ProxyCode snippets. Provide history of edited versions, and navigation amongst history and amongst different proxies. 

*/

ProxyCodeEditor : AppModel {
	classvar <>all;	// all current instances of ProxyCodeEditor; // TODO: REVIEW THIS
	classvar <>windowRects;
	var <proxyCode, <proxyName, <proxy, <proxySpace, <>font;
	var <history;

	*initClass {
		all = IdentityDictionary.new;
		windowRects = [
			Rect(0, 600, 720, 267), Rect(720, 600, 720, 267), Rect(0, 300, 720, 267),
			Rect(720, 300, 720, 267), Rect(0, 0, 720, 267), Rect(720, 0, 720, 267)
		];
	}
	
	*new { | proxyCode, proxyName, proxy |
		var existingEditor;
		existingEditor = all[proxyName];
		existingEditor !? { ^existingEditor.front };
		^super.new(proxyCode, proxyName, proxy).init;
	}

	front {
		
	}

	init {
		all[proxyName] = this; // TODO: Review this
		proxySpace = proxyCode.proxySpace;
		font = Font.default.size_(10);
		this.makeWindow;
	}

	makeWindow {
		this.window({ | window, app |
			window
				.name_("Code Editor for : " ++ proxyName)
				.bounds_(windowRects@@(all.size));
			WindowHandler(this, window, 
				{ all[proxyName] = nil; }, // TODO: Review this
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
			).addAction(\setName, { | window, name | window.name = name });
			window.layout = VLayout(
				[this.textView(\editor).view.font_(Font("Monaco", 11)), s:10],
				[HLayout(
					this.popUpMenu(\proxyMenu).proxySelector
						.addSelectionListener(this, { | proxy, proxyName |
							this.setProxy(proxy, proxyName) 
						})
						.view.font_(font),
					this.button(\proxyMainControl).proxyState(\proxyMenu)
						.adapterDo({ | me | 
							me.adapter.additionalNotifiers = 
							[\proxyHistory, { | changer, history |
								this.updateHistory(changer, history) }];
						})
						.view.font_(font)
						.states_([["start", Color.black, Color.green], 
							["stop", Color.black, Color.red]]),
					this.button(\history).list(["a", "b", "c"])
						.viewAction_({ | view, me |
							me.adapter.adapter.first;
							me.adapter.adapter.item.postln;
						})
						.updateAction_({})
						.view.states_([["<<"]]).font_(font),
					this.button(\history)
						.viewAction_({ | view, me |
							me.adapter.adapter.previous;
							me.adapter.adapter.item.postln;
						})
						.updateAction_({})
						.view.states_([["<"]]).font_(font),
					Button().states_([["eval"]]).font_(font).action_({ this.evalSnippet(false) }),
					this.button(\history)
						.viewAction_({ | view, me |
							me.adapter.adapter.next;
							me.adapter.adapter.item.postln;
						})
						.updateAction_({})
						.view.states_([[">"]]).font_(font),
					this.button(\history)
						.viewAction_({ | view, me |
							me.adapter.adapter.last;
							me.adapter.adapter.item.postln;
						})
						.updateAction_({})
						.view.states_([[">>"]]).font_(font),
					this.button(\history)
						.viewAction_({ | view, me |
							me.adapter.adapter.last;
							me.adapter.adapter.item.postln;
						})
						.updateAction_({})
						.view.states_([["add"]]).font_(font),
//					Button().states_([["add"]]).font_(font).action_({ this.evalSnippet }),
					Button().states_([["delete"]]).font_(font).action_({ this.deleteSnippet }),
					Button().states_([["reset specs"]]).font_(font).action_({ this.resetSpecs }),
					Button().states_([["history"]]).font_(font)
						.action_({ proxyCode.openHistoryInDoc(proxy) }),
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
		});
		{
			this.getAdapter(\proxyMenu).selectMatchingItem(proxyName); 
		}.defer(0.2);
	}
	
	setProxy { | argProxy, argProxyName |
		argProxyName ?? { ^"Cannot set my proxy to nil".postcln; };
		proxy = argProxy; 
		proxyName = argProxyName;
		this.notify(\setName, proxyName); // set my window's name
	}
	
	updateHistory { | argChanger, argHistory |
		[this, thisMethod.name, argChanger, argHistory].postln;
	}
}

