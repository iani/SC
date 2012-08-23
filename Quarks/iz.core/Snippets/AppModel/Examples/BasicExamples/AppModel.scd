/* IZ Thu 16 August 2012  9:10 AM EEST
tests for AppModel
*/

//:NumberBox, Knob, Slider, Button
AppModel().window({ | w, app |
	w.layout = VLayout(
		app.numberBox(\any_name).view,
		app.knob(\any_name).view, 
		app.slider(\any_name, \freq).view.orientation_(\horizontal),
		app.button(\any_name).valueAction_({ 20 rrand: 2000 }).view.states_([["randomize"]]),
	);
});

//:TextView, TextField, StaticTextView
AppModel().window({ | w, app |
	w.layout = VLayout(
		app.staticText(\any_name).view.string_("will be updated by others"), 
		app.textField(\any_name).view.string_("change my string and press the return key"),
		app.textView(\any_name, 'AnyNameGoesHere')
			.view.string_("change my string and push the accept text button"),
		app.button(\any_name).getContents('AnyNameGoesHere')
			.view.states_([["accept text from TextView above"]]),
	);
});

//:PopUpMenu, ListView
a = AppModel().window({ | w, app |
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
AppModel().window({ | w, app |
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

//:PopUpMenu, ListView with item append, insert, replace from TextFields
AppModel().window({ | w, app |
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
			.view.string_("type return to append my text at the end of the list"),
		app.textField(\any_name).listItem(\insert)
			.view.string_("type return to insert my text at the current position in the list"),
		app.textField(\any_name).listItem(\replace)
			.view.string_("type return to replace the current item in the list with my text"),
	);
});

//:PopUpMenu, ListView with item append, insert, replace from a TextView, triggered via buttons
a = AppModel().window({ | w, app |
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
		),
	);
});

//:debugging:

d = a.getAdapter(\any_name);


d.notify(\getContents, \itemEdit);

//:Many examples combined


AppModel().window({ | w, app |
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
			[app.staticText(\label1, "selected item's index, starting on 0").view, s: 5],
			app.numberBox(\menu).view,
		),
		HLayout(
			[app.staticText(\label1, "selected item's index, 1-list size").view, s: 5],
			app.numberBox(\menu).listIndex.view,
		),
		HLayout(
			[app.staticText(\label1, "selected item (press return to add)").view, s: 5],
			app.textField(\menu).listItem.view,
		),
		HLayout(
			app.staticText(\label1, "list size").view,
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

//:OLD - need rechecking: 
(a = AppModel()).window({ | w, app |
	w.layout = VLayout(
		HLayout(
			app.knob(\test, \freq).view,
			app.slider(\test, \freq).view.orientation_(\horizontal),
			app.numberBox(\test).view,
			app.view(Button()).view
				.states_([["randomize"]])
				.action_({ app.put(\test, 20 rrand: 20000) }),
		),
		HLayout(
			app.view(StaticText())
				.addAction(\setLabel, { | me, string |
					me.view.string = string;
				}).view.string_("test"),
			app.button(\player).view.states_([["play"], ["stop"]]),
			app.staticText(\text).view.string_("static?"),
			app.textField(\text).view
		)
	);
	app.addListener(w, \closeTestWindow, { w.close });
}, { "HOHOHO and a barrel of rum".postln }
);


//:

a = AppModel().window({ | w, app |
	w.layout = VLayout(
		app.textField(\st).view,
		app.staticText(\st).view.string_("test")
	)
});

//:

a.setValue(\tf, "aua");


a.setValue(\test, 0.5);
a.setValue(\st, "asdf".scramble);

//:
m = AppModel().window({ | w, app |
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


//:
a = StaticText().front;
a.value;
a.value_("asdf");
//:

(a = AppModel()).window({ | w, app |
	w.layout = VLayout(
		HLayout(
			app.knob(\test).spec_(\freq).view,
			app.slider(\test).spec_(\freq).view.orientation_(\horizontal),
			app.numberBox(\test).view,
			app.view(Button()).view
				.states_([["randomize"]])
				.action_({ app.setValue(\test, 20 rrand: 20000) }),
		),
		HLayout(
			app.view(StaticText())
				.addAction(\setLabel, { | me, string |
					me.view.string = string;
				}).view.string_("test"),
			app.button(\player).view.states_([["play"], ["stop"]]),
			app.textField(\text).view.addAction(\value, { | ... args | args.postln })
		)
	);
	app.addListener(w, \closeTestWindow, { w.close });
}, { "HOHOHO and a barrel of rum".postln }
);


//:

a.getValue(\text)
a.notify(\setLabel, "asdfasdf");


//:

AppWindow(a, { | w, app |
	w.layout = VLayout(
		app.knob(\test).spec_(\freq).view,
		app.numberBox(\test).view
	)
});

//:

.window({ | w, app |
	w.view.layout = VLayout(
		app.knob(\test).spec_(\freq).view,
		app.numberBox(\test).view
	)
};