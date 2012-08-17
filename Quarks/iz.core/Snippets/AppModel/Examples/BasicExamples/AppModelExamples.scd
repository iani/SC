/* IZ Thu 16 August 2012  9:10 AM EEST
tests for AppModel
*/

//:

a = AppModel().window({ | w, app |
	w.layout = HLayout(
		app.knob(\test).spec_(\freq).view,
		app.slider(\test).spec_(\freq).view,
		app.numberBox(\test).view,
		app.view(Button()).view
			.states_([["randomize"]])
			.action_({ app.put(\test, 20.0 rrand: 20000) })
	);

});

//:
a = AppModel().window({ | w, app |
	w.layout = VLayout(
		app.textField(\staticTextExample).view.string_("else?"),
		app.staticText(\staticTextExample).view.string_("static?"),
		app.textView(\staticTextExample).view.string_("something?"),
		app.button(\acceptText)
			.makeViewValueGetter(\staticTextExample)
			.view
			.states_([["accept my text"]]),
		app.popUpMenu(\menu).view.items_(["one", "two", "three"]),
		app.popUpMenu(\menu).view.items_(["10", "11", "12"]),
		app.listView(\menu).view.items_(["seven", "eight", "nine"]),
		app.listView(\list).view.items_(["four", "five", "six"]),
		app.listView(\list).view.items_(["seven", "eight", "nine"]),
	);
});

//: Getting fluent with one widget influenceing another aspect of another widget. 

AppModel().window({ | w, app |
	app.putValue(\synth, { | amp = 0.02 | WhiteNoise.ar(amp) }.play);
	w.layout = VLayout(
		app.knob(\vol)
			.adapterAction({ app.getValue(\synth).postln; })
			.view;
	)
})