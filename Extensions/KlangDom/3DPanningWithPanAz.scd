/* IZ 2011 03 11 
An alternative to VBAP, panning with several PanAz over horizontal rings of speakers

PanAz.ar(numChans, in, pos, level, width, orientation)

width: 2 -> panning between 2 speakers. < 1: gaps between speakers. > 2: spread over more than 2 speakers;
pos: from -1 to 1 is full circle. 


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
	Out.ar(0, PinkNoise.ar(0.1) * ring1mul);
	Out.ar(6, PinkNoise.ar(0.1) * ring2mul);
	Out.ar(23, PinkNoise.ar(0.1) * ring3mul);
}.play;
)

a.set(\ele, 1.0);

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

