/* 

Basic example of connecting proxies to views. 

*/

//:A proxy selector list and a button for starting / stopping the selected proxy
AppModel().window({ | window, app |
	var proxySpace;
	proxySpace = Document.prepareProxySpace(Document.current);
	window.layout = VLayout(
		app.listView(\proxies).items_(proxySpace.proxies).view,
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
		app.listView(\proxies).items_(proxySpace.proxies).view,
		app.button(\proxies).proxyWatcher.view.states_([["start"], ["stop"]]),
		app.listView(\proxyControls).proxyControlList(\proxies).view,
		app.knob(\proxyControls).proxyControl.view,
		app.slider(\proxyControls).proxyControl.view.orientation_(\horizontal),
		app.numberBox(\proxyControls).proxyControl.view,
	);
	// create some node proxies in this Document's proxy space for testing: 
	~out1 = { | freq = 400 | SinOsc.ar(freq, 0, 0.1) };
	~out2 = { | amp = 0.1 | PinkNoise.ar(amp) };
});

//:Testing proxyList method
AppModel().window({ | window, app |
	window.layout = VLayout(
		app.listView(\proxies).proxyList.view,
	)
})

//:Testing ProxyCodeEditor2 class

ProxyCodeEditor2();

//:

~out1;

~out1.source;
~out1.source.envirCompileString;

.asCode;