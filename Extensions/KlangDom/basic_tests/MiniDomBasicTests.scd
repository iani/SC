







Server.default = s = Server.local;


Server.default.options.numOutputBusChannels = 24;
Server.default.options.numInputBusChannels = 24;

s.boot;


// Channel test.
a = { | out = 0 |
Ê Ê Ê ÊOut.ar(out, WhiteNoise.ar(0.1));
}.play;

a.set(\out, 23);

r = {
Ê Ê Ê Êinf do: { | i |
Ê Ê Ê Ê Ê Ê Ê Êa.set(\out, i % 24);
Ê Ê Ê Ê Ê Ê Ê Ê0.5.wait;
Ê Ê Ê Ê}
}.fork;

r.stop;

// Channel io copy

a = { Out.ar(0, In.ar(24, 24)) }.scope;


// 2D

f = {

a = VBAPSpeakerArray.new(2, [0, 45, 90, 135, 180, -135, -90, -45]); //
8 channel ring

a.speakers[1].dump;

b = a.loadToBuffer;

(
x = { |azi = 0, ele = 0, spr = 0|
VBAP.ar(8, PinkNoise.ar(0.2), b.bufnum, azi, ele, spr);
}.scope;
)

// test them out
{[45, 90, 135, 180, -135, -90, -45, 0].do({|ang| x.set(\azi, ang);
1.wait; }) }.fork;





a = VBAPSpeaker(1.0, 0);
a.inspect;

(
~azel = [[1 , Ê Ê0 ],
[ Ê0.75 , Ê Ê0 ],
[ Ê0.62 , Ê Ê0 ],
[ Ê 0.5 , Ê Ê0 ],
[ Ê0.38 , Ê Ê0 ],
[ Ê0.25 , Ê Ê0 ],
[ Ê Ê 0 , Ê Ê0 ],
[ -0.25 , Ê Ê0 ],
[ -0.38 , Ê Ê0 ],
[ Ê-0.5 , Ê Ê0 ],
[ -0.62 , Ê Ê0 ],
[ -0.75 , Ê Ê0 ],
[ Ê Ê 1 , Ê0.2 ],
[ Ê0.76 , 0.16 ],
[ Ê0.64 , Ê0.2 ],
[ Ê0.36 , Ê0.2 ],
[ Ê0.23 , 0.16 ],
[ Ê Ê 0 , Ê0.2 ],
[ -0.24 , 0.16 ],
[ -0.36 , Ê0.2 ],
[ -0.64 , Ê0.2 ],
[ -0.76 , 0.16 ],
[ Ê Ê 1 , 0.39 ],
[ Ê Ê 0, 0.39]]

)

~kubus = VBAPSpeakerArray.new(3, ~azel);

~kubus.speakers do: { | speaker | postf("az: %, el: %, x: %, y: %, z:
%\n", speaker.azi.round(0.01), speaker.ele.round(0.01),
speaker.x.round(0.01), speaker.y.round(0.01), speaker.z.round(0.01))
};

Ê Ê Ê Ê~kubus.speakers.first.inspect;


VBAPSpeaker


Server.default = s = Server.internal;
// 2D
a = VBAPSpeakerArray.new(2, [0, 45, 90, 135, 180, -135, -90, -45]); //
8 channel ring

a.speakers[1].dump;

b = a.loadToBuffer;

(
x = { |azi = 0, ele = 0, spr = 0|
VBAP.ar(8, PinkNoise.ar(0.2), b.bufnum, azi, ele, spr);
}.scope;
)

// test them out
{[45, 90, 135, 180, -135, -90, -45, 0].do({|ang| x.set(\azi, ang);
1.wait; }) }.fork;
