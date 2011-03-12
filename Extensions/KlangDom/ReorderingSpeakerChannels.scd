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


Server.default = s = Server.internal;

s.boot;

// 2D
a = VBAPSpeakerArray.new(2, [
	0.25, -0.25, -0.75, 0.75, 
	0, -0.5, 1, 0.5,
	0.38, -0.38, -0.62, 0.62
	]);

b = Buffer.loadCollection(s, a.getSetsAndMatrices);

(
x = { |azi = 0, ele = 0, spr = 0|
VBAP.ar(42, PinkNoise.ar(0.2), b.bufnum, azi, ele, spr);
}.scope;
)

x.set(\azi, 0);


{ 
	inf do: { | count |
		x.set(\azi, postln(count % 360 - 180 / 180));	
		0.05.wait;
	}

}.fork;

