
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

//:

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

//:

a.getAdapter(\proxySelector).inspect

//:

a.getAdapter(\specSetter).inspect

//:

currentEnvironment;

//:

{ | freq = 400 | LFNoise2.ar(freq, 0.1); }

//:


['-'] ++ currentEnvironment.envir.keys.asArray.sort


//:

{ WhiteNoise.ar(0.01); }

