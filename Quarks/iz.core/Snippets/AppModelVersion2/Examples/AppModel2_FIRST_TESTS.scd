/* IZ Sat 01 September 2012  1:05 AM EEST
FIrst tests with AppModel2.
*/

//:General tests for numeric views, text views and lists.
AppModel2().window({ | window, app |
	window.layout = VLayout(
		app.numberBox(\number).view,
		app.button(\number, { | me | me.value.adapter.value_(me, 20 rrand: 20000) })
			.view.states_([["randomize"]]),
		app.knob(\number, \freq).view,
		app.slider(\number).view.orientation_(\horizontal),
		app.listView(\list).do({ | me | me.value.adapter.items_(me, ["a", "b", "c"]) })
			.view,
		app.popUpMenu(\list).view,
		app.listSize(\list).view,
		app.listIndex(\list).view,
		app.listItem(\list).replace.view,
		app.textField(\list).append.view,
		app.textField(\list).insert.view,
		app.textView(\list).updateAppend.view,
		app.button(\list).notify(\append).view.states_([["append"]]),
		app.textField(\text).view,
		app.textView(\text).view,
		app.staticText(\text).view,
		app.button(\text).notify(\getText).view.states_([["get text from TextView"]]),
	)
});

