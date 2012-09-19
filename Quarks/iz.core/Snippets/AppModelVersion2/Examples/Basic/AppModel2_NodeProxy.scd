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
a = AppModel().window({ | window, app |
	window.layout = VLayout(
		app.listView(\proxies) // list of proxies
		.proxyList(Document.prepareProxySpace(Document.current)).view, 
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

//:MIDI control of proxies
c = a.getValue(\proxyControls);
c.adapter.item.adapter.inspect;

c.item.adapter.standardizedValue_(nil, 0.5);

//:Testing access of proxy from button for diverse purposes
a = AppModel().window({ | window, app |
	window.layout = VLayout(
		app.listView(\proxies) // list of proxies
		.proxyList(Document.prepareProxySpace(Document.current)).view, 
		app.button(\proxies).proxyWatcher(
			{},	// this never gets triggerred on a button with one state only
			{  | me | ProxyCodeEditor(nil, me.item.item); }
		)
		.view.states_([["testing"]]), // start stop button
	);
	// create some node proxies in this Document's proxy space for testing: 
	~out1 = { | freq = 400 | SinOsc.ar(freq, 0, 0.1) };
	~out2 = { | amp = 0.1 | PinkNoise.ar(amp) };
});

//:Setting proxies of a proxy selector menu: Select the most recently created proxy
a = AppModel().window({ | window, app |
	var proxySpace;
	proxySpace = Document.prepareProxySpace(Document.current);
	window.layout = VLayout(
		// make this list always select the most recently created proxy
		app.listView(\proxies).proxyList(proxySpace)
		.addUpdateAction(\list, { | me | me.last })
		.do({ | me | me.last })  // initialize
		.view,
		app.listView(\specs).proxyControlList(\proxies).view,
	);
	// create some node proxies in this Document's proxy space for testing: 
	~out1 = { | freq = 400 | SinOsc.ar(freq, 0, 0.1) };
	~out2 = { | amp = 0.1 | PinkNoise.ar(amp) };
	~out1.play;
	~out2.play;
});

//:TMP: Test order independence of proxy widgets
a = AppModel().window({ | window, app |
	var proxySpace;
	proxySpace = Document.prepareProxySpace(Document.current);
	window.layout = VLayout(
		app.listView(\specs).proxyControlList(\proxies).view,
		// make this list always select the most recently created proxy
		app.listView(\proxies).proxyList(proxySpace).view,
//		app.listView(\specs).proxyControlList(\proxies).view,
	);
	// create some node proxies in this Document's proxy space for testing: 
	~out1 = { | freq = 400 | SinOsc.ar(freq, 0, 0.1) };
	~out2 = { | amp = 0.1 | PinkNoise.ar(amp) };
	~out1.play;
	~out2.play;
});



//:
{ WhiteNoise.ar(0.1) };

//:
ProxyCodeMixer();



