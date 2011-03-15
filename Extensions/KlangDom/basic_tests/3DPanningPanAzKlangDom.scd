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

Server.default = Server.internal;

Server.default.options.numInputBusChannels = 43;
Server.default.options.numOutputBusChannels = 43;

Server.default.boot;

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
