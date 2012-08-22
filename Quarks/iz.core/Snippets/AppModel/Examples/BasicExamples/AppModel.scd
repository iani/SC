/* IZ Thu 16 August 2012  9:10 AM EEST
tests for AppModel
*/

//:NumberBox, Knob, Slider, Button
AppModel().window({ | w, app |
	w.layout = VLayout(
		app.numberBox(\value).view,
		app.knob(\value).view, 
		app.slider(\value, \freq).view.orientation_(\horizontal),
		app.button(\value).valueAction_({ 20 rrand: 2000 }).view.states_([["randomize"]]),
	);
});

//:TextView, TextField, StaticTextView
AppModel().window({ | w, app |
	w.layout = VLayout(
		app.staticText(\text).view.string_("will be updated by others"), 
		app.textField(\text).view.string_("change my string and press the return key"),
		app.textView(\text, 'AnyNameGoesHere')
			.view.string_("change my string and push the accept text button"),
		app.button(\text).getContents('AnyNameGoesHere')
			.view.states_([["accept text from TextView above"]]),
	);
});


//:PopUpMenu, ListView
AppModel().window({ | w, app |
	w.layout = VLayout(
		app.popUpMenu(\list, ["a", "b", "c"]).view,
		app.listView(\list).view.items_(["one", "two", "three", "four", "five"]),
	);
});


//:PopUpMenu, ListView with item append, insert, replace, delete

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