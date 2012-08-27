//:list view of history
a = AppModel().window({ | w, app |
	w.layout = VLayout(
		app.numberBox(\control).view,
		app.knob(\control).proxyControl(\specSetter).view,
		app.popUpMenu(\specSetter).proxySpecSelector(\proxySelector).view,
		app.popUpMenu(\proxySelector).proxySelector.view,
		app.button(\startstop).proxyState(\proxySelector).view.states_([["start"],["stop"]]),
		app.listView(\history).proxyHistory(\proxySelector).view,
	)
})

//:text view with buttons for navigating between history items

a = AppModel().window({ | w, app |
	w.layout = VLayout(
		app.numberBox(\control).proxyControl(\specSetter).view,
		app.knob(\control).view,
		app.popUpMenu(\specSetter).proxySpecSelector(\proxySelector).view,
		app.popUpMenu(\proxySelector).proxySelector.view,
		app.button(\startstop).proxyState(\proxySelector).view.states_([["start"],["stop"]]),
		app.textView(\history).listItem.proxyHistory(\proxySelector).view,
		HLayout(
			app.button(\history).firstItem.view.states_([["<<"]]),
			app.button(\history).previousItem.view.states_([["<"]]),
			app.button(\history).nextItem.view.states_([[">"]]),
			app.button(\history).lastItem.view.states_([[">>"]]),
			[StaticText().string_("current index:"), s:3],
			[app.numberBox(\history).listIndex.view, s:1],
			[StaticText().string_("list size:"), s:2],
			[app.numberBox(\history).listSize.view, s: 1],
		),
	)
})

//:button for adding to history, and button with custom action

a = AppModel().window({ | w, app |
	w.layout = VLayout(
		HLayout(
			[app.popUpMenu(\specSetter).proxySpecSelector(\proxySelector).view, s:2],
			[app.numberBox(\control).proxyControl(\specSetter).view, s: 1],
			app.slider(\control).view.orientation_(\horizontal),
		),
		HLayout(
			app.popUpMenu(\proxySelector).proxySelector.view,
			app.button(\startstop).proxyState(\proxySelector).view.states_([["start"],["stop"]]),
			app.button(\history).getContents(\editor, \append).view.states_([["append"]]),
			app.button(\history).getContents(\editor, { | widget |
				widget.adapter.adapter.item.postln; }).view.states_([["post"]]),
		),
		app.listView(\history).proxyHistory(\proxySelector).view,
		app.textView(\history).listItem.proxyHistory(\proxySelector).name_(\editor).view,
		HLayout(
			app.button(\history).firstItem.view.states_([["<<"]]),
			app.button(\history).previousItem.view.states_([["<"]]),
			app.button(\history).nextItem.view.states_([[">"]]),
			app.button(\history).lastItem.view.states_([[">>"]]),
			[StaticText().string_("current index:"), s:3],
			[app.numberBox(\history).listIndex.view, s:1],
			[StaticText().string_("list size:"), s:2],
			[app.numberBox(\history).listSize.view, s: 1],
		),
	)
})


//:debug

a = AppModel().window({ | w, app |
	w.layout = VLayout(
		app.textView(\history).proxyHistory(\proxySelector).name_(\editor).view,
	)
})

//:
{ | freq = 400 | LFDNoise3.ar(freq, 0.1); }
//:
{ WhiteNoise.ar(0.01); }
//:
a.getAdapter(\proxySelector).inspect

//:

a.getAdapter(\specSetter).inspect

//:

currentEnvironment;
