/* IZ Thu 16 August 2012  9:10 AM EEST
tests for AppModel
*/

//:

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