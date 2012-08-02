/* IZ 2012 08 02
Pop up a window to edit the most recent source code for a NodeProxy generated through ProxyCode. 

Created interactively from GUI of NanoKontrol2. See NanoKontrol2, NanoK2Strip, ProxyCode.

Note: all should actually not be an IdentityDictionary. There should be one such dictionary for each instance of ProxyCode, if we intend to use multiple ProxyCode instances. But currently there are no practical reasons for doing that. In the future, this may be solved transparently by making all into a MultiLevelIdentityDictionary, storing a separate Node-Code dictionary for each instance of ProxyCode. The only methods affected would be initClass and new. 

*/

ProxySourceEditor {
	classvar <all; // Dictionary with all instances: Prevents opening multiple windows for same Proxy.
				// (Yet see note above: Conflicts may arise if using multiple ProxyCode 
				// instances concurrently) 
	var <proxyCode; // ProxyCode instance that holds the ProxySpace, NodeProxies and History.
	var <proxyName; // Name of the current NodeProxy, whose code is being edited here
	var <proxyHistory;	// array of source code strings in order of execution
	var <window;
	var <editor;	// TextView holding the code to be edited
	var <numSnippets, <currentSnippet; // NumberBoxes holding number of snippets and of current snippet
	
	*initClass {
		StartUp add: { all = IdentityDictionary.new }	
	}
	
	*new { | proxyCode, proxyName |
		var existingEditor;
		proxyName = proxyName.asSymbol;
		existingEditor = all[proxyName];
		existingEditor !? { ^existingEditor.front };
		^this.newCopyArgs(proxyCode, proxyName).init;
	}

	front { window.front }

	init {
		all[proxyName] = this;
		proxyHistory = proxyCode.proxyHistory[proxyName] ?? { [" "] };
		this.makeWindow;
		this.addNotifier(proxyCode, \proxySource, { | argProxyName, argHistory |
			if (argProxyName === proxyName) {
				proxyHistory = argHistory;
				numSnippets.value = proxyHistory.size;
				currentSnippet.value = proxyHistory.size;
			}
		})
	}

	makeWindow {
		window = Window(proxyName, Rect(100, 450, 750, 400));
		window.onClose = { this.closed };
		window.layout = VLayout(
			editor = TextView().setFont(Font("Monaco", 9))
				.string_(proxyHistory.last),
			HLayout(
				StaticText().string_("all:"),
				numSnippets = NumberBox().value_(proxyHistory.size),
				StaticText().string_("current:"),
				currentSnippet = NumberBox().value_(proxyHistory.size),
				Button().states_([["<<"]]).action_({ this.firstSnippet }),
				Button().states_([["<"]]).action_({ this.previousSnippet }),
				Button().states_([[">"]]).action_({ this.nextSnippet }),
				Button().states_([[">>"]]).action_({ this.lastSnippet }),
				Button().states_([["enter"]]).action_({ this.evalSnippet }),
				Button().states_([["start"], ["stop"]]).action_({ | me |
					this.perform([\stopProxy, \startProxy][me.value])
				})
			),
			HLayout(
				*({ VLayout(
					HLayout(
						Slider(),
						VLayout(
							NumberBox(),
							NumberBox(),
							NumberBox()
						)
					),
					PopUpMenu()
				) } ! 8)		
			)
		);
		this.front;
	}

	closed {
		all[proxyName] = nil;
		this.objectClosed;
	}

	evalSnippet {
		proxyCode.evalInProxySpace(
			editor.string, proxyCode.proxySpace[proxyName], proxyName, false
		)
	}

	firstSnippet {
		currentSnippet.value = 1;
		editor.string = proxyHistory[0];	
	}

	previousSnippet {
		currentSnippet.value = currentSnippet.value - 1 max: 1;
		editor.string = proxyHistory[currentSnippet.value - 1];
	}

	nextSnippet {
		currentSnippet.value = currentSnippet.value + 1 min: proxyHistory.size;
		editor.string = proxyHistory[currentSnippet.value - 1];
	}

	lastSnippet {
		currentSnippet.value = proxyHistory.size;
		editor.string = proxyHistory.last;	
	}

	startProxy { proxyCode.startProxy(proxyCode.proxySpace[proxyName], proxyName) }
	stopProxy { proxyCode.stopProxy(proxyCode.proxySpace[proxyName], proxyName) }
}
