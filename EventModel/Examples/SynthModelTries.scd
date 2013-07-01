
(
a = SynthModel({ WhiteNoise.ar(0.1) });
a.gui;
)

a.start;

a.release(5);
a.release;
a.stop;
a.free;

a.gui([\amp]);
a.start;
a.stop;

a.set(\amp, 0.15)
a.release(5);

{ XFade(WhiteNoise.ar(0.1)) }.play;

a.eventModel;
a.eventModel.event;
a.makeEvent;


a.release;
a.release(9.1);

objectClo


nil.asTarget.server.serverRunning;

a = { XFade(WhiteNoise.ar(0.1)) }.asSynthDef;

a.add;

a.inspect;

Notification.all;


a.synth.release

View

Synth('temp__2');


SynthDescLib.global.browse;

(
{
	var a;
	12 do: { | i |
		a = Synth(format("temp__%", i + 12)).postln;
		5.wait;
		a.release(1);
	}
}.fork
)