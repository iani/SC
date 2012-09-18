/* 

Basic example of connecting proxies to views. 

*/

//:A proxy selector list and a button for starting / stopping the selected proxy
AppModel().window({ | window, app |
	var proxySpace;
	proxySpace = Document.prepareProxySpace(Document.current);
	window.layout = VLayout(
		// List of existing proxies. Other views can access the selected proxy from this view:
		app.listView(\proxies).items_(proxySpace.proxies).view,
		// Button for starting or stopping the selected proxy: 
		app.button(\proxies).proxyWatcher.view.states_([["start"], ["stop"]]),
	);
	// create some node proxies in this Document's proxy space for testing: 
	~out1 = { | freq = 400 | SinOsc.ar(freq, 0, 0.1) };
	~out2 = { | amp = 0.1 | PinkNoise.ar(amp) };
});


//:Add parameter list and slider, knob, number box for setting any selected parameter of the proxy
AppModel().window({ | window, app |
	var proxySpace;
	proxySpace = Document.prepareProxySpace(Document.current);
	window.layout = VLayout(
		app.listView(\proxies).items_(proxySpace.proxies).view, // list of proxies
		app.button(\proxies).proxyWatcher.view.states_([["start"], ["stop"]]), // start stop button
		// list of control parameters of selected button: 
		app.listView(\proxyControls).proxyControlList(\proxies).view,
		// Following views set the parameter selected above:
		app.knob(\proxyControls).proxyControl.view,
		app.slider(\proxyControls).proxyControl.view.orientation_(\horizontal),
		app.numberBox(\proxyControls).proxyControl.view,
	);
	// create some node proxies in this Document's proxy space for testing: 
	~out1 = { | freq = 400 | SinOsc.ar(freq, 0, 0.1) };
	~out2 = { | amp = 0.1 | PinkNoise.ar(amp) };
});

//:Testing the autoSelect parameter of proxyControlList
AppModel().window({ | window, app |
	var proxySpace;
	proxySpace = Document.prepareProxySpace(Document.current);
	window.layout = VLayout(
		app.listView(\proxies).items_(proxySpace.proxies).view, // list of proxies
		app.button(\proxies).proxyWatcher.view.states_([["start"], ["stop"]]), // start stop button
		// list of control parameters of selected button: 
		app.listView(\proxyControls).proxyControlList(\proxies, 3).view,
		// Following views set the parameter selected above:
		app.knob(\proxyControls).proxyControl.view,
		app.numberBox(\proxyControls).proxyControl.view,
	);
	// create some node proxies in this Document's proxy space for testing: 
	~out1 = { | freq = 400 | SinOsc.ar(freq, 0, 0.1) };
	~out2 = { | amp = 0.1 | PinkNoise.ar(amp) };
});
