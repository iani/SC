
a = AppModel().window({ | w, app |
	w.layout = VLayout(
		app.knob(\proxy).proxyControl2.view,
		app.numberBox(\proxy).view,
		app.knob(\proxy, \freq).view,
	)
})

