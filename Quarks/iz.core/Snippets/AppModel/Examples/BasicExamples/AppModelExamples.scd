/* IZ Thu 16 August 2012  9:10 AM EEST
tests for AppModel
*/

//:

a = AppModel().window({ | w, app |
	w.layout = HLayout(
		app.knob(\test, \freq).view,
		app.slider(\test).view,
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

a = AppModel().window({ | w, app |
	w.layout = VLayout(
		app.popUpMenu(\nodes).proxySelector.view,
		app.button(\startstop).proxyState(\nodes)
			.view.states_([["start"], ["stop"]]),
		(b = app.popUpMenu(\specs).proxySpecSelector(\nodes)).view, //,
		app.numberBox(\control).proxyControl(\specs).view,
//		app.knob(\control).proxyControl(\specs).view,
		app.knob(\numbox, \freq).view,
		app.numberBox(\numbox).view,
	)
})