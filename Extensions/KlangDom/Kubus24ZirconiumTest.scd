

a = VBAPSpeaker(1.0, 0);
a.inspect;

(
~azel = [[1 ,    0 ],
[  0.75 ,    0 ],
[  0.62 ,    0 ],
[   0.5 ,    0 ],
[  0.38 ,    0 ],
[  0.25 ,    0 ],
[     0 ,    0 ],
[ -0.25 ,    0 ],
[ -0.38 ,    0 ],
[  -0.5 ,    0 ],
[ -0.62 ,    0 ],
[ -0.75 ,    0 ],
[     1 ,  0.2 ],
[  0.76 , 0.16 ],
[  0.64 ,  0.2 ],
[  0.36 ,  0.2 ],
[  0.23 , 0.16 ],
[     0 ,  0.2 ],
[ -0.24 , 0.16 ],
[ -0.36 ,  0.2 ],
[ -0.64 ,  0.2 ],
[ -0.76 , 0.16 ],
[     1 , 0.39 ],
[     0, 0.39]]

)

~kubus = VBAPSpeakerArray.new(3, ~azel);

~kubus.speakers do: { | speaker | postf("az: %, el: %, x: %, y: %, z: %\n", speaker.azi.round(0.01), speaker.ele.round(0.01), speaker.x.round(0.01), speaker.y.round(0.01), speaker.z.round(0.01)) };
	
	~kubus.speakers.first.inspect;
	
	
VBAPSpeaker


Server.default = s = Server.internal;
// 2D
a = VBAPSpeakerArray.new(2, [0, 45, 90, 135, 180, -135, -90, -45]); // 8 channel ring

a.speakers[1].dump;

b = a.loadToBuffer;

(
x = { |azi = 0, ele = 0, spr = 0|
VBAP.ar(8, PinkNoise.ar(0.2), b.bufnum, azi, ele, spr);
}.scope;
)

// test them out
{[45, 90, 135, 180, -135, -90, -45, 0].do({|ang| x.set(\azi, ang); 1.wait; }) }.fork;
