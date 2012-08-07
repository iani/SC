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
	var <proxyCode;	// ProxyCode instance that holds the ProxySpace, NodeProxies and History.
	var <proxyName;	// Name of the current NodeProxy, whose code is being edited here
	var <proxy;		// The current NodeProxy
	var <proxyHistory;	// array of source code strings in order of execution
	
	// ======= GUI ITEMS: =======
	var <window;
	var <editor;		// TextView holding the code to be edited
	var <startStopButton;
	var <numSnippets, <currentSnippet; // NumberBoxes holding number of snippets and of current snippet
	var <>bounds; 	// saves original window bounds for reset after maximizing
	var <controls;	// array of 8 control strips with GUIs for controlling proxy parameters

	*initClass {
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
		this setProxy: proxyName;
		this.makeWindow;
		this.lastSnippet;
		this.addNotifier(proxyCode, \proxySource, { | argProxyName, argHistory |
			this.updateHistory(argProxyName, argHistory);
		});
		all[proxyName] = this;
	}

	setProxy { | argProxyName |
		proxyName = proxyName.asSymbol;
		proxy !? {
			this.removeNotifier(proxy, \play);
			this.removeNotifier(proxy, \stop);
		};
		proxy = proxyCode.proxySpace[proxyName];
		this.addNotifier(proxy, \play, { startStopButton.value = 1 });
		this.addNotifier(proxy, \stop, { startStopButton.value = 0 });
		proxyHistory = proxyCode.proxyHistory[proxyName];
	}

	makeWindow {
		window = Window(proxyName, bounds = windowRects@@(all.size));
		window.onClose = { this.closed };
		window.layout = VLayout(
			[editor = TextView().font_(Font("Monaco", 11)).string_(proxyHistory.last), s:10],
			[HLayout(
				Button().states_([["enter"]]).action_({ this.evalSnippet }),
				startStopButton = Button().states_([["start"], ["stop"]]).action_({ | me |
					this.perform([\stopProxy, \startProxy][me.value])
				}),
				Button().states_([["reset specs"]]).action_({ this.resetSpecs; }),
				Button().states_([["delete"]]).action_({ this.deleteSnippet }),
//				Button().states_([["<<"]]).action_({ this.firstSnippet }),
				Button().states_([["<<"]]).action_({
					this.notify(\currentSnippet, 1);
				}),
				Button().states_([["<"]]).action_({
					var widget;
					widget = this.widget(\currentSnippet);
					widget.valueAction = widget.value - 1;
				}),
//				Button().states_([[">"]]).action_({ this.nextSnippet }),
				Button().states_([[">"]]).action_({
					var widget;
					widget = this.widget(\currentSnippet);
					widget.valueAction = widget.value + 1;
				}),
				Button().states_([[">>"]]).action_({ this.lastSnippet }),
				StaticText().string_("current:"),
				currentSnippet = NumberBox().value_(proxyHistory.size)
					.addModel(this, \currentSnippet, action: { | val, widget |
						val = val.clip(1, proxyHistory.size);
						widget.value = val;
						editor.string = proxyHistory[val - 1];
					}, updateFunc: { | val, me |
//						["updateFunc arguments received:", val, me].postln;
						me.valueAction = val;
					}).w,
				StaticText().string_("all:"),
				numSnippets = NumberBox().value_(proxyHistory.size),
				Button()
					.states_([["maximize window"], ["minimize window"]])
					.action_({ | me | this.resizeWindow(me.value); })
			), s:1],
			[HLayout(
				*({
					VLayout(
						PopUpMenu(),
						Knob(),
						HLayout(
							NumberBox().font_(Font.sansSerif(10)), 
							NumberBox().font_(Font.sansSerif(10))
						),
						Slider().orientation_(\horizontal),
						PopUpMenu(),
					)
				} ! 8)
			), s:2]
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

	startProxy { proxyCode.startProxy(proxy, proxyName) }
	stopProxy { proxyCode.stopProxy(proxy, proxyName) }

	resetSpecs {
		var snippet, specString, specs;
		snippet = editor.string;
		if (	snippet[0] != $/) { ^nil }; // or reparse from proxy??? 
		specString = snippet.findRegexp("^//[^[]*([^\n]*)")[1][1];
		specs = specString.interpret;
	}

	deleteSnippet {
		var dialog;
		if (proxyHistory.size <= 1) { ^"Cannot delete the last snippet from history".postcln };
		dialog = Window.new.layout_(
			VLayout(
				StaticText().string_("Do you really want to delete this snippet?"),
				HLayout(
					Button().states_([["OK"]]).action_({
						proxyCode.deleteNodeSourceCodeFromHistory(proxyName, currentSnippet.value);
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

	updateHistory { | argProxyName, argHistory |
		if (argProxyName === proxyName) {
			proxyHistory = argHistory;
			numSnippets.value = proxyHistory.size;
			currentSnippet.value = proxyHistory.size;
			editor.string = proxyHistory.last;
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
