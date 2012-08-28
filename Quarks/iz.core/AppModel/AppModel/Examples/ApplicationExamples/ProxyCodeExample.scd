
//:


a = ProxyCodeMixer(nil, 8);

~out1 = { BrownNoise.ar(0.1) };

~out2 = { PinkNoise.ar(0.1) };

~out3 = { WhiteNoise.ar(0.1) };

~out4 = { | freq = 400 | SinOsc.ar(freq, 0, 0.1) };

~out5 = { WhiteNoise.ar(0.01) };

~out6 = { | freq = 400 | SinOsc.ar(freq, 0, 0.1) };

~out7 = { | freq = 400 | SinOsc.ar(freq, 0, 0.1) };

~out8 = { | freq = 400 | SinOsc.ar(freq, 0, 0.1) };

~out9 = { | freq = 400 | SinOsc.ar(freq, 0, 0.1) };

~out10 = { | freq = 400 | SinOsc.ar(freq, 0, 0.1) };

//: Connecting a proxy-selection menu to a button. NOT READY.

AppModel().window({ | w, app |
	w.layout = VLayout(
		app.popUpMenu(\selectProxy).proxySelector.view,
		app.button(\echo).proxyState(\selectProxy).action_({ | adapter | 
			adapter.inspect;
			{ 
				5 do: { | i |
					~out1.play.vol = (i + 1).reciprocal;
					0.3.wait;
					~out1.stop;
					0.2.wait;
				}	
			}.fork(AppClock)
		}).view.states_([["test"]])
	);
})

