
a = AppModel().window({ | w, app |
	w.layout = VLayout(
		app.numberBox(\control).view,
		app.knob(\control).proxyControl2(\specSetter).view,
		app.popUpMenu(\specSetter).proxySpecSelector2(\proxySelector).view,
		app.popUpMenu(\proxySelector).proxySelector2.view,
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

