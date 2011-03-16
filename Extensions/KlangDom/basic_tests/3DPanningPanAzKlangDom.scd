/*
============= 4 rings

a = (0, 0.05..1.0);

((a - 0.75).fold(-10, 0.25) max: 0).postln.plot;
(a - 0.5).fold(-10, 0.25) max: 0;
(a - 0.25).fold(-10, 0.25) max: 0;

((1 - a - 0.65).fold(-10, 0.25).max(0).pow(1.15) * 1.5); // .sum //.round(0.01);

// (1 - a - 0.75).fold(-10, 0.25).max(0);

(1 - a - 0.75).fold(-10, 0.25).max(0).pow(3) * 15;




[14, 14, 8, 6].sum;

*/

~numchannels = 50;

Server.default = Server.internal;

Server.default.options.numInputBusChannels = 2;
Server.default.options.numOutputBusChannels = ~numchannels;

Server.default.boot;


// Version 2: vertical panning done by PanAz

(
a = { | azi = 0, ele = 0, width = 2.0, orientation1 = 6.8, vertical_orientation = 1.5, vol = 1 |
	var source;
	var rings, ring1, ring2, ring3, ring4, ring5;
	
	source = PinkNoise.ar(0.1) * vol;
	
	rings = PanAz.ar(7, source, ele, 1.5, width, vertical_orientation); // channels 0 and 5 are not used
	
	ring1 = PanAz.ar(14, rings[1], azi, 1, width, orientation1);
	ring2 = PanAz.ar(14, rings[2], azi, 1, width, orientation1);
	ring3 = PanAz.ar(8, rings[3], azi, 1, width, orientation1);
	ring4 = PanAz.ar(6, rings[4], azi, 1, width, orientation1);
	ring5 = rings[5];

	Out.ar(0, ring1);
	Out.ar(14, ring2);
	Out.ar(28, ring3);
	Out.ar(36, ring4);
	Out.ar(42, ring5);

}.play;
)

a.set(\ele, 1.3);
a.set(\ele, 0);

(
var vert_inc, vert_scale, vert;
var azi_inc, azi_scale, azi;
var ele_inc, ele_scale, ele;

w = GUI.window.new("calibrate dome", Rect(1200, 20, 270, 200)).front;
w.addFlowLayout( 2@2, 2@2 );
vert = EZSlider(w, 260@15, "vert", ControlSpec(-2, 2, \linear, 0, 0.5))
	.action_({ | me | a.set(\vertical_orientation, me.value); });
ele = EZSlider(w, 260@15, "ele", ControlSpec(0, 1, \linear, 0, 0))
	.action_({ | me | a.set(\ele, me.value); });
//	.action_({ | me | a.set(\ele, me.value * ele_scale.value + ele_inc.value); });
// ele_inc = EZSlider(w, 260@15, "ele_inc", ControlSpec(-2, 2, \linear, 0, 0));
// ele_scale = EZSlider(w, 260@15, "ele_scale", ControlSpec(0, 2, \linear, 0, 1));
azi = EZSlider(w, 260@15, "azi", ControlSpec(-1, 1, \linear, 0, 0))
	.action_({ | me | a.set(\azi, me.value); });
azi_inc = EZSlider(w, 260@15, "azi ori", ControlSpec(-14, 14, \linear, 0, 0))
	.action_({ | me | a.set(\orientation1, me.value); });

/*
EZSlider(w, 260@15, "azi", ControlSpec(-2, 2, \linear, 0, 0))
	.action_({ | me | a.set(\azi, me.value); });
*/
)


==== EARLIER
// Version 1: vertical panning done by linear interpolation

(
a = { | azi = 0, ele = 0, width = 2.0, orientation1 = 0, orientation2 = 0, orientation3 = 0, vol = 1 |
	var source;
	var ring1mul, ring2mul, ring3mul, ring4mul, top_centre_mul;
	var ring1, ring2, ring3, ring4;
	
	source = PinkNoise.ar(0.1) * vol * 2;
	
	ring1mul = (ele - 0.75).fold(-10, 0.25) max: 0;
	ring2mul = (ele - 0.5).fold(-10, 0.25) max: 0;
	ring3mul = (ele - 0.25).fold(-10, 0.25) max: 0;
	ring4mul = (1 - ele - 0.65).fold(-10, 0.25).max(0).pow(1.15) * 1.5;
	top_centre_mul = (1 - ele - 0.75).fold(-10, 0.25).max(0).pow(3) * 15;	
	
	ring1 = PanAz.ar(14, source, azi, ring1mul, width, orientation1);
	ring2 = PanAz.ar(14, source, azi, ring2mul, width, orientation2);
	ring3 = PanAz.ar(8, source, azi, ring3mul, width, orientation2);
	ring4 = PanAz.ar(6, source, azi, ring4mul, width, orientation2);

	Out.ar(0, ring1);
	Out.ar(14, ring2);
	Out.ar(28, ring3);
	Out.ar(36, ring4);
	Out.ar(42, source * top_centre_mul);
	source;
}.scope;
)


a.set(\ele, 1.0);
a.set(\ele, 0.75);
a.set(\ele, 0.5);
a.set(\ele, 0.25);
a.set(\ele, 0.1);
a.set(\ele, 0.0);

a.set(\ele, 1.0);
a.set(\ele, 0.5);
a.set(\ele, 0.25);
a.set(\ele, 0.1);

a.set(\azi, -0.5);
a.set(\azi, 0);
a.set(\azi, 1);

a.set(\azi, -1);


a.set(\ele, 1.0);
a.set(\ele, 0.75);
a.set(\ele, 0.5);
a.set(\ele, 0.25);
a.set(\ele, 0.1);
a.set(\ele, 0.0);

a.set(\ele, 1.0);
a.set(\ele, 0.5);
a.set(\ele, 0.25);
a.set(\ele, 0.1);

a.set(\azi, -0.5);
a.set(\azi, 0);
a.set(\azi, 1);

a.set(\azi, -1);
