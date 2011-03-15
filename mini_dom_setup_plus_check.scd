

~rate = 1;

Server.default = s = Server.local;


Server.default.options.numOutputBusChannels = 24;
Server.default.options.numInputBusChannels = 24;

s.waitForBoot({
	{
		~rate.wait;
		a = { | out = 0 |
		Ê Ê Ê ÊOut.ar(out, PinkNoise.ar(Decay2.kr(Impulse.kr(4), 0.01, 0.15, 0.05)));
		Ê Ê Ê ÊOut.ar(out, SinOsc.ar(880, 0, Decay2.kr(Impulse.kr(4, 2 * pi), 0.1, 0.15, 0.2)));
		}.play;
	Ê Ê inf do: { | i |
	Ê Ê Ê Ê ~rate.wait;
			a.set(\out, i % 24);
	Ê Ê };
	}.fork;
});


// Channel test.

/*
a.set(\out, 23);

~rate = 1;

(Platform.userAppSupportDir ++ "/mini_dom_setup_plus_check.scd").load;


*/

