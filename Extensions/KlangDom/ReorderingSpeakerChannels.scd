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
y = [
	0.25, -0.25, -0.75, 0.75, 
	0, -0.5, 1, 0.5,
	0.38, -0.38, -0.62, 0.62
] * 180;

y;

z = y[[6, 3, 11, 7, 8, 0, 4, 1, 9, 5, 10, 2]]; 
z = y[[3, 6, 11, 7, 8, 0, 4, 1, 9, 5, 10, 2]]; 


// z = z.scramble;
z = [0, 45, 90, 135, 180, -135, -90, -45];

z = z.scramble;

a = VBAPSpeakerArray.new(2, z);

b = Buffer.loadCollection(s, a.getSetsAndMatrices);

(
x = { |azi = 0, ele = 0, spr = 0|
VBAP.ar(42, PinkNoise.ar(0.2), b.bufnum, azi, ele, spr);
}.scope;
)

x.set(\azi, 0);

//

f = {
	var window, azi, ele, speaker_selector;
	window = GUI.window.new("MiniDom test", Rect(1100, 200, 400, 100));
	window.addFlowLayout( 10@10, 20@5 );
	azi = EZSlider(window.view, label: " azi ", controlSpec: ControlSpec(-1, 1, \linear, 0, 0));
	ele = EZSlider(window.view, label: " ele ", controlSpec: ControlSpec(0, 1, \linear, 0, 0));
	speaker_selector = EZSlider(window.view, label: "goto speaker", controlSpec: ControlSpec(1, 24, \linear, 1, 1));
	azi.action = { | me | me.value.postln; x.set(\azi, me.value * 180) };
//	ele.action = { | me | me.value.postln; synth.set(\ele, me.value * 180) };
	speaker_selector.action = { | me |
		var azi;
		azi = z[me.value - 1];
		postf("index: %, winkel: %\n", me.value, azi);
		x.set(\azi, azi);
//		postf("speaker nr: %, azimuth: %, elevation: %", me.value, theEle, theAzi);
//		azi.valueAction = theAzi;
//		ele.valueAction = theEle;
	};
	window.front;
};

f.value;


// 
(
{ 
	inf do: { | count |
		x.set(\azi, postln(count % 360 - 180 / 180));
		0.05.wait;
	}

}.fork;
)
