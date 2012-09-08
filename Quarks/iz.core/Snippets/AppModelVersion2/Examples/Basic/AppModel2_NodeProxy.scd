/* IZ Fri 07 September 2012  8:19 AM BST

Working with AppModel on NodeProxies. 

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

//:Additionally display the proxy's controls
AppModel().window({ | window, app |
	var proxySpace;
	proxySpace = Document.prepareProxySpace(Document.current);
	window.layout = VLayout(
		app.listView(\proxies).items_(proxySpace.proxies).view,
		app.button(\proxies).proxyWatcher.view.states_([["start"], ["stop"]]),
		app.listView(\controls).proxyControlList(\proxies).view,
		app.slider(\controls).proxyControl(\proxies).view.orientation_(\horizontal),
	);
	// create some node proxies in this Document's proxy space for testing: 
	~out1 = { | freq = 400 | SinOsc.ar(freq, 0, 0.1) };
	~out2 = { | amp = 0.1 | PinkNoise.ar(amp) };
});

//:


MergeSpecs.parseArguments(~out1);
~out1.play;
