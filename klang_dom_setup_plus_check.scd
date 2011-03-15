

~rate = 1;

~numchannels = 42;

Server.default = s = Server.local;


Server.default.options.numOutputBusChannels = ~numchannels;
Server.default.options.numInputBusChannels = ~numchannels;

s.waitForBoot({
	~r = {
		~rate.wait;
		~a = { | out = 0 |
		Ê Ê Ê ÊOut.ar(out, PinkNoise.ar(Decay2.kr(Impulse.kr(4), 0.01, 0.15, 0.05)));
		Ê Ê Ê ÊOut.ar(out, SinOsc.ar(880, 0, Decay2.kr(Impulse.kr(4, 2 * pi), 0.1, 0.15, 0.2)));
		}.play;
	Ê Ê inf do: { | i |
			~rate.wait;
			~a.set(\out, i % ~numchannels);
	Ê Ê };
	}.fork;
});

(
~l = EZSlider(label: " channel ", controlSpec: ControlSpec(0, 43, \linear, 1, 0))
	.action = { | me | ~a.set(\out, me.value.asInteger) };
	
~l.sliderView.window.postln;
)
// Channel test.

/*
a.set(\out, 23);

~rate = 1;

~r.stop;

(Platform.userAppSupportDir ++ "/mini_dom_setup_plus_check.scd").load;

*/

