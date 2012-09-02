/* IZ Thu 16 August 2012  9:10 AM EEST
tests for AppModel
*/

//:NumberBox, Knob, Slider, Button
AppModel0().window({ | w, app |
	w.layout = VLayout(
		app.numberBox(\any_name).view,
		app.knob(\any_name).view, 
		app.slider(\any_name, \freq).view.orientation_(\horizontal),
		app.button(\any_name).valueAction_({ 20 rrand: 2000 }).view.states_([["randomize"]]),
	);
});

//:TextView, TextField, StaticTextView
AppModel0().window({ | w, app |
	w.layout = VLayout(
		app.staticText(\any_name)
			.initValue("will be updated by others")
			.view,
		app.textField(\any_name)
			.initValue("change my string and press the return key")
			.view,
		app.textView(\any_name, 'AnyNameGoesHere')
			.initValue("change my string and push the accept text button")
			.view,
		app.button(\any_name).getContents('AnyNameGoesHere')
			.view.states_([["accept text from TextView above"]]),
	);
});

//:PopUpMenu, ListView
a = AppModel0().window({ | w, app |
	w.layout = VLayout(
		app.popUpMenu(\any_name, ["SELECTED", "b", "c"]).view,
		app.listView(\any_name).view,
		StaticText().string_("update index only - do not update items:"),
		app.listView(\any_name, nil, false).view.items_(["one", "2", "3", "4", "5"]),
	);
});

//:When the items are changed, the index is adjusted to match the previously selected item:
// Note that the items change, but the previously selected item remains selected: 
a.getAdapter(\any_name).adapter.items = ["something else", "c", "b", "a", "SELECTED", "e"];
//:Checking adjustment of index again:
a.getAdapter(\any_name).adapter.items = ["not selected", "SELECTED", "something else"];


//:PopUpMenu, ListView with navigation buttons, index and display
AppModel0().window({ | w, app |
	w.layout = VLayout(
		app.popUpMenu(\any_name, ["a", "b", "c", "four", "five"]).view,
		app.listView(\any_name).view,
		HLayout(
			app.button(\any_name).firstItem.view.states_([["<<"]]),
			app.button(\any_name).previousItem.view.states_([["<"]]),
			app.button(\any_name).nextItem.view.states_([[">"]]),
			app.button(\any_name).lastItem.view.states_([[">>"]]),
			[StaticText().string_("current index:"), s:3],
			[app.numberBox(\any_name).listIndex.view, s:1],
			[StaticText().string_("list size:"), s:2],
			[app.numberBox(\any_name).listSize.view, s: 1],
		),
	);
});

//:ListView with textField showing selected item
AppModel0().window({ | w, app |
	w.layout = VLayout(
		StaticText().string_("Selecting an item updates contents of text field"),
		app.listView(\any_name).items_(["a", "b", "c", "four", "five"]).view,
		app.textField(\any_name).listItem  // default mode is append
			.initValue("type return to append my text at the end of the list")
			.view,
		StaticText().string_("field above edits + appends selected item")
	);
});

//:PopUpMenu, ListView with item append, insert, replace from TextFields
AppModel0().window({ | w, app |
	w.layout = VLayout(
		app.popUpMenu(\any_name, ["a", "b", "c", "four", "five"]).view,
		app.listView(\any_name).view,
		HLayout(
			app.button(\any_name).firstItem.view.states_([["<<"]]),
			app.button(\any_name).previousItem.view.states_([["<"]]),
			app.button(\any_name).nextItem.view.states_([[">"]]),
			app.button(\any_name).lastItem.view.states_([[">>"]]),
			[StaticText().string_("current index:"), s:3],
			[app.numberBox(\any_name).listIndex.view, s:1],
			[StaticText().string_("list size:"), s:2],
			[app.numberBox(\any_name).listSize.view, s: 1],
		),
		app.textField(\any_name).listItem  // default mode is append
			.initValue("type return to append my text at the end of the list")
			.view,
		app.textField(\any_name).listItem(\insert)
			.initValue("type return to insert my text at the current position in the list")
			.view,
		app.textField(\any_name).listItem(\replace)
			.initValue("type return to replace the current item in the list with my text")
			.view
	);
});

//:PopUpMenu, ListView with item append, insert, replace from a TextView, triggered via buttons
a = AppModel0().window({ | w, app |
	w.layout = VLayout(
		app.popUpMenu(\any_name, ["a", "b", "c", "four", "five"]).view,
		app.listView(\any_name).view,
		HLayout(
			app.button(\any_name).firstItem.view.states_([["<<"]]),
			app.button(\any_name).previousItem.view.states_([["<"]]),
			app.button(\any_name).nextItem.view.states_([[">"]]),
			app.button(\any_name).lastItem.view.states_([[">>"]]),
			[StaticText().string_("current index:"), s:3],
			[app.numberBox(\any_name).listIndex.view, s:1],
			[StaticText().string_("list size:"), s:2],
			[app.numberBox(\any_name).listSize.view, s: 1],
		),
		app.textView(\any_name).list.name_(\itemEdit)
			.view.string_("choose an action below to do with me in the list"),
		HLayout(
			app.button(\any_name).getContents(\itemEdit, \append).view.states_([["append"]]),
			app.button(\any_name).getContents(\itemEdit, \insert).view.states_([["insert"]]),
			app.button(\any_name).getContents(\itemEdit, \replace).view.states_([["replace"]]),
			app.button(\any_name).getContents(\itemEdit, \delete).view.states_([["delete"]]),
			app.button(\any_name).getContents(\itemEdit, { | ... args |
				args.postln;
			}).view.states_([["custom action"]]),
		)
	);
});

//:Many examples combined
AppModel0().window({ | w, app |
	w.layout = VLayout(
		HLayout(
			app.numberBox(\numbox).view,
			app.knob(\numbox).view, 
			app.slider(\numbox, \freq).view.orientation_(\horizontal),
		),
		app.textField(\text).view,
		app.textView(\text).view,
		app.button(\text).getContents
			.view.states_([["accept text from TextView"]]),
		app.popUpMenu(\menu, ["a", "b", "c"]).view,
		app.listView(\menu).view,
		HLayout(
			[StaticText().string_("selected item's index, starting on 0"), s: 5],
			app.numberBox(\menu).view,
		),
		HLayout(
			[StaticText().string_("selected item's index, 1-list size"), s: 5],
			app.numberBox(\menu).listIndex.view,
		),
		HLayout(
			[StaticText().string_("press return to add"), s: 5],
			app.textField(\menu).listItem.view,
		),
		HLayout(
			StaticText().string_("list size"),
			app.numberBox(\menu).listSize.view,
		),
		HLayout(
			app.button(\menu).firstItem.view.states_([["<<"]]),
			app.button(\menu).previousItem.view.states_([["<"]]),
			app.button(\menu).nextItem.view.states_([[">"]]),
			app.button(\menu).lastItem.view.states_([[">>"]]),
		),
	)
});

//:
m = AppModel0().window({ | w, app |
	w.layout = VLayout(
		app.numberBox(\index).list(["alpha", "beta", "gamma"]).view,
		app.textField(\index).list.view,
		app.numberBox(\index).viewAction_(nil)
			.adapterUpdate({ | adapter, view |
				view.value = adapter.adapter.items.size
			}).doUpdate.view,
		app.textField(\index).list(replaceItems: false).view,
		app.numberBox(\index).listSize.doUpdate.view // same as adapterUpdate above
	)
});


//:List of snippets
AppModel0().window({ | window, app |
	window.bounds = Rect(Window.screenBounds.width - 180, 0, 180, 800);
	window.layout = VLayout(
		app.listView(\snippets, Code(Document.current).getAllSnippetStrings)
			.view.keyDownAction_({ | view, char | 
				if (char == $  ) {
					app.getAdapter(\snippets).adapter.item.interpret
				};
			})
	)
});


//:List of snippets, one line per item
AppModel0().window({ | window, app |
	var doc;
	doc = Document.current;
	window.bounds = Rect(Window.screenBounds.width - 180, 0, 180, 800);
	window.layout = VLayout(
		app.listView(\snippets)
			.updateAction_({ | view, sender, adapter |
				view.items = adapter.adapter.items collect: { | s | 
					s[3..80].replace("\n", "-")
				}
			}).items_(Code(doc).getAllSnippetStrings)
			.view.keyDownAction_({ | view, char | 
				if (char.ascii == 13) { // when return key is pressed: evaluate snippet
					app.getAdapter(\snippets).adapter.item.interpret
				};
				if (char == $ ) { // when space key is pressed: select snippet in document
					Code(doc).selectSnippetAt(app.getAdapter(\snippets).value + 1);
				};
			})
	)
});

//:Snippet Buttons;

AppModel0().window({ | window, app |
	var doc, code, headers, font;
	font = Font.default.size_(10);
	doc = Document.current;
	code = Code(doc);
	headers = code.headers;
	window.name = doc.name ++ " : snippets";
	window.layout = VLayout(
		*(headers collect: { | h, i | 
			Button().states_([[h[3..]]])
			.action_({ Code(doc).performCodeAt(i + 1, \fork, AppClock) })
			.font_(font) })
	)	
})

//:AppStickyWindow - Evaluate this again to bring existing window to front
AppModel0().stickyWindow(\test, \window1, { | window, app |
	var code, headers, font;
	font = Font.default(10);
	code = Code(Document.current);
	headers = code.headers;
	window.layout = VLayout(
		*(headers collect: { | h | Button().states_([[h[3..20]]]).font_(font) })
	)	
})


//:Test osc commands from Code - run this first: 
Code.makeCodeOSC;
//:Then test it by running the following: 
NetAddr.localAddr.sendMsg("/NumberBox");


