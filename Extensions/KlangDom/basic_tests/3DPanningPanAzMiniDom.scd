/* IZ 2011 03 11 
An alternative to VBAP, panning with several PanAz over horizontal rings of speakers

PanAz.ar(numChans, in, pos, level, width, orientation)

width: 2 -> panning between 2 speakers. < 1: gaps between speakers. > 2: spread over more than 2 speakers;
pos: from -1 to 1 is full circle. 

=============== 3 rings
ele:
	0 -> ring 1
	0.5 -> ring 2
	1 -> ring 3
Interpolation between rings: 
ring1mul = 1 - ele;

a = (0, 0.1 .. 1);
b = 0.5 - a max: 0;
c = 0.5 - (1 - a) max: 0;
d = 0.5 - b - c;
d + c + b;

============= 4 rings


a = (0, 0.05..1.0);

((a - 0.75).fold(-10, 0.25) max: 0).postln.plot;
(a - 0.5).fold(-10, 0.25) max: 0;
(a - 0.25).fold(-10, 0.25) max: 0;
(1 - a - 0.75).fold(-10, 0.25) max: 0;





// Not so good: 

b = (0.5 - a max: 0).pow(3) * 0.125.reciprocal; 
c = (0.5 - (1 - a) max: 0).pow(3) * 0.125.reciprocal;

d = 0.5 - b - c;

d + b
d + c + b

b.plot;
c.plot;
d.plot;


0.5 - b

(0.5 - (1 - a) max: 0).pow(3) * 0.125.reciprocal; // !!!!!!!!

(1 - ((0.5 - a max: 0).pow(3) * 0.125.reciprocal));

(1 - ((0.5 - a max: 0).pow(3) * 0.125.reciprocal)) + 

1 - a

(a max: 0).pow(3) // * 0.125.reciprocal;


0.5 / (a + 1)

*/

// ============= 1: 24 channel mini dom, linear vertical panning

Server.default = Server.internal;

Server.default.options.numInputBusChannels = 24;
Server.default.options.numOutputBusChannels = 24;

Server.internal.boot;

(
a = { | azi = 0, ele = 0, width = 2.0, orientation1 = 0, orientation2 = 0, orientation3 = 0, vol = 1 |
	var source;
	var ring1mul, ring2mul, ring3mul;
	var ring1, ring2, ring3;
	
	source = PinkNoise.ar(0.1) * vol;
	
	ring1mul = 0.5 - ele max: 0;
	ring3mul = 0.5 - (1 - ele) max: 0;
	ring2mul = 0.5 - ring1mul - ring3mul;
	
	ring1 = PanAz.ar(12, source, azi, ring1mul, width, orientation1);
	ring2 = PanAz.ar(10, source, azi, ring2mul, width, orientation2);
	ring3 = Pan2.ar(source, azi, ring3mul);

	Out.ar(0, ring1);
	Out.ar(12, ring2);
	Out.ar(22, ring3);

}.scope;
)

a.set(\ele, 1.0);
a.set(\ele, 0.75);
a.set(\ele, 0.5);
a.set(\ele, 0.25);
a.set(\ele, 0.1);
a.set(\ele, 0.0);

a.set(\azi, -0.5);
a.set(\azi, 0);
a.set(\azi, 1);

a.set(\azi, -1);


// ============= 2: 24 channel mini dom, PanAz vertical panning

Server.default = Server.internal;

Server.default.options.numInputBusChannels = 24;
Server.default.options.numOutputBusChannels = 24;

Server.internal.boot;

// PanAz.ar(numChans, in, pos, level, width, orientation)

(
a = { | azi = 0, ele = 0, width = 2.0, orientation1 = 0, orientation2 = 0, orientation3 = 0, vol = 1 |
	var source;
	var rings, ring1, ring2, ring3;
	
	source = PinkNoise.ar(0.1) * vol;
	
	rings = PanAz.ar(5, source, ele, 1.5, width, 0); // channels 0 and 4 are not used
	
	ring1 = PanAz.ar(12, rings[1], azi, 1, width, orientation1);
	ring2 = PanAz.ar(10, rings[2], azi, 1, width, orientation2);
	ring3 = Pan2.ar(rings[3], azi, 1, width);

	Out.ar(0, ring1);
	Out.ar(12, ring2);
	Out.ar(22, ring3);

}.scope;
)

a.set(\ele, 1.0);
a.set(\ele, 0.75);
a.set(\ele, 0.5);
a.set(\ele, 0.25);
a.set(\ele, 0.1);
a.set(\ele, 0.0);

a.set(\azi, -0.5);
a.set(\azi, 0);
a.set(\azi, 1);

a.set(\azi, -1);



/// Other ...


(
f = {
	var window, azi, ele, speaker_selector;
	window = GUI.window.new("MiniDom test", Rect(1100, 200, 400, 100));
	window.addFlowLayout( 10@10, 20@5 );
	azi = EZSlider(window.view, label: " azi ", controlSpec: ControlSpec(-1, 1, \linear, 0, 0));
	ele = EZSlider(window.view, label: " ele ", controlSpec: ControlSpec(0, 1, \linear, 0, 0));
	speaker_selector = EZSlider(window.view, label: "goto speaker", controlSpec: ControlSpec(1, 24, \linear, 1, 1));
	azi.action = { | me | me.value.postln; a.set(\azi, me.value) };
	ele.action = { | me | me.value.postln; a.set(\ele, me.value) };
	window.front;
};
f.value;
)
(
~incr = 0.02;
r = {
	var ele = 0;
	loop {
		a.set(\ele, (ele = ele + ~incr).fold(0, 1));
		0.1.wait;	
	};
}.fork;
)

a.set(\ele, 0);
a.set(\ele, 0.5);
a.set(\ele, 1.0);

a.set(\ele, 0.5);

r.stop;

(
~incr = 0.02;
r = {
	var azi = 0;
	loop {
		a.set(\azi, azi = azi + ~incr);
		0.1.wait;	
	};
}.fork;
)



Pan2

(
a = { | azi = 0, ele = 0, width = 2.0, orientation1 = 0, orientation2 = 0, orientation3 = 0, vol = 1 |
	var source;
	var ring1mul, ring2mul, ring3mul;
	var ring1, ring2, ring3;
	
	source = PinkNoise.ar(0.1) * vol;
	
	ring1mul = 0.5 - ele max: 0;
	ring2mul = 0.5 - (1 - ele) max: 0;
	
	ring1 = PanAz.ar(12, source, azi, ring1mul, width, orientation1);
	ring2 = PanAz.ar(10, source, azi, ring2mul, width, orientation2);
	Out.ar(0, ring1);
	Out.ar(0, ring2);

}.play;
)

a.set(\azi, 0);
a.set(\azi, 1);
a.set(\azi, -1);

a.set(\ele, 0.5);


(
~incr = 0.1;
r = {
	var azi = 0;
	loop {
		a.set(\azi, azi = azi + ~incr);
		0.1.wait;	
	};
}.fork;
)

~incr = 0.01;

