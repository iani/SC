a = [[1 ,    0 ],
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
				[     0, 0.39]
		]
		
		
		
a[[]];


a = VBAPSpeakerArray.new(2, [0, 45, 90, 135, 180, -135, -90, -45]); // 8 channel ring


a = VBAPSpeakerArray.new(2, [0, 45, 90, 135, -45, -135, -90, 180]); // 8 channel ring


Server.default = s = Server.internal;
// 2D
// a = VBAPSpeakerArray.new(2, [0, 45, 90, 135, 180, -135, -90, -45]); // 8 channel ring
a = VBAPSpeakerArray.new(2, [0, 45, 90, 135, -45, -135, -90, 180]); // 8 channel ring

a.speakers[1].dump;

b = a.loadToBuffer;

(
x = { |azi = 0, ele = 0, spr = 0|
VBAP.ar(42, PinkNoise.ar(0.2), b.bufnum, azi, ele, spr);
}.scope;
)

x.set(\azi, 180);