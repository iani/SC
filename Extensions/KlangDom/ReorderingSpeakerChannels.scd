/* 

Principle for reordering 

1->7
2->4
etc exactly as in studio loudspeaker diagram. 



*/
Server.default = s = Server.internal;

s.boot;

// 2D
y = [
	[ 0.25, 0 ], [ -0.25, 0 ], [ -0.75, 0 ], [ 0.75, 0 ], [ 0, 0 ], 
	[ -0.5, 0 ], [ 1, 0 ], [ 0.5, 0 ], [ 0.38, 0 ], 
	[ -0.38, 0 ], [ -0.62, 0 ], [ 0.62, 0 ], // level 1 ends here 
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
	
	
	] * 180;


x = [ 0.25, -0.25, -0.75, 0.75, 0, -0.5, 1, 0.5, 0.38, -0.38, -0.62, 0.62 ] collect: { | i | [i, 0] }

z = y;

z = y[[6, 3, 11, 7, 8, 0, 4, 1, 9, 5, 10, 2, 18, 19, 20, 21, 12, 13, 14, 15, 16, 17, 22, 23]]; 

z.size;

a = VBAPSpeakerArray.new(3, z);

b = Buffer.loadCollection(s, a.getSetsAndMatrices);

(
x = { |azi = 0, ele = 0, spr = 0|
	var output;
	output = VBAP.ar(42, PinkNoise.ar(0.2), b.bufnum, azi, ele, spr);
	Out.ar(0, output);
}.scope;
)

x.set(\azi, 0);
x.set(\azi, 75);


x.set(\azi, 0);
x.set(\azi, 15);
x.set(\azi, 30);
x.set(\azi, 45);
x.set(\azi, 75);
x.set(\azi, 90);
x.set(\azi, 115);


x.set(\azi, 90);
x.set(\azi, 115);
x.set(\azi, 180);
x.set(\azi, -180);
x.set(\azi, -10);

x.set(\azi, -90);



(
{ 
	loop {
	(-180, -165 .. 180) do: { | azi_degrees |
		x.set(\azi, postln(azi_degrees));
		1.wait;
	};
	};
}.fork;
)



//

f = {
	var window, azi, ele, speaker_selector;
	window = GUI.window.new("MiniDom test", Rect(1100, 200, 400, 100));
	window.addFlowLayout( 10@10, 20@5 );
	azi = EZSlider(window.view, label: " azi ", controlSpec: ControlSpec(-180, 180, \linear, 0, 0));
	ele = EZSlider(window.view, label: " ele ", controlSpec: ControlSpec(0, 90, \linear, 0, 0));
	speaker_selector = EZSlider(window.view, label: "goto speaker", controlSpec: ControlSpec(1, 24, \linear, 1, 1));
	azi.action = { | me | me.value.postln; x.set(\azi, me.value) };
	ele.action = { | me | me.value.postln; x.set(\ele, me.value) };
	speaker_selector.action = { | me |
		var azi;
		azi = (z[me.value - 1]).at(0);
		ele = (z[me.value - 1]).at(1);
		postf("index: %, azi: %, ele: %\n", me.value, azi, ele);
		x.set(\azi, azi);
		x.set(\ele, ele);
//		postf("speaker nr: %, azimuth: %, elevation: %", me.value, theEle, theAzi);
//		azi.valueAction = theAzi;
//		ele.valueAction = theEle;
	};
	window.front;
};

f.value;


// 
)






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


