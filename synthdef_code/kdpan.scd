/* 
KlangDom panner based on 2 levels of PanAz y -> z.
Tweaked for Klang Dom at ZKM with 43 Speakers.
As configured on 2011 03 15

See also class KDpan.

b = Bus.audio;
p = Synth("kdpan", [\in, b.index]);
r = { Out.ar(b.index, WhiteNoise.ar(0.1)) }.play;

p.set(\ele, 1.0);
p.set(\ele, 0.4);

p.set(\azi, 0.0);
p.set(\azi, 0.2);


{ { | i | p.set(\width, (i + 1).postln); 3.wait } ! 14 }.fork
{ { | i | p.set(\width, (i + 1).postln); 3.wait } ! 20 }.fork





 
*/

// kdpan and kdpanvol set the widths of the horizontal rings uniformly. For individual control 
// see kdpanw and kdpanvolw below. 

SynthDef("kdpan", { 
	| 	in = 0, azi = 0, ele = 0, 
		vertical_orientation = 1.5, 
		orientation1 = 7, orientation2 = 6.5, orientation3 = 3.5, orientation4 = 2.52, 
		vertwidth = 2, width = 2,
		vol = 1
	|
	var source;
	var rings, ring1, ring2, ring3, ring4, ring5;
	
	source = In.ar(in, 1) * vol;
	
	rings = PanAz.ar(7, source, ele, 1.5, vertwidth, vertical_orientation); // channels 0 and 5 are not used
	
	ring1 = PanAz.ar(14, rings[1], azi, 1, width, orientation1);
	ring2 = PanAz.ar(14, rings[2], azi, 1, width, orientation2);
	ring3 = PanAz.ar(8, rings[3], azi, 1, width, orientation3);
	ring4 = PanAz.ar(6, rings[4], azi, 1, width, orientation4);
	ring5 = rings[5];

	Out.ar(0, ring1);
	Out.ar(14, ring2);
	Out.ar(28, ring3);
	Out.ar(36, ring4);
	Out.ar(42, ring5);

}).store;


SynthDef("kdpanvol", {
	| 	out = 0, azi = 0, ele = 0, 
		vertical_orientation = 1.5, 
		orientation1 = 7, orientation2 = 6.5, orientation3 = 3.5, orientation4 = 2.52, 
		vertwidth = 2, width = 2,
		vol = 1
	|
	var source;
	var rings, ring1, ring2, ring3, ring4, ring5;
	
	source = vol;
	
	rings = PanAz.kr(7, source, ele, 1.5, vertwidth, vertical_orientation); // channels 0 and 5 are not used
	
	ring1 = PanAz.kr(14, rings[1], azi, 1, width, orientation1);
	ring2 = PanAz.kr(14, rings[2], azi, 1, width, orientation2);
	ring3 = PanAz.kr(8, rings[3], azi, 1, width, orientation3);
	ring4 = PanAz.kr(6, rings[4], azi, 1, width, orientation4);
	ring5 = rings[5];

	Out.kr(out, ring1);
	Out.kr(out + 14, ring2);
	Out.kr(out + 28, ring3);
	Out.kr(out + 36, ring4);
	Out.kr(out + 42, ring5);

}).store;


// kdpanw and kdpanvolw provide individual control inputs to the width of each horizontal ring

SynthDef("kdpanw", { 
	| 	in = 0, azi = 0, ele = 0, 
		vertical_orientation = 1.5, 
		orientation1 = 7, orientation2 = 6.5, orientation3 = 3.5, orientation4 = 2.52, 
		vertwidth = 2, width1 = 2, width2 = 2, width3 = 2, width4 = 2,
		vol = 1
	|
	var source;
	var rings, ring1, ring2, ring3, ring4, ring5;
	
	source = In.ar(in, 1) * vol;
	
	rings = PanAz.ar(7, source, ele, 1.5, vertwidth, vertical_orientation); // channels 0 and 5 are not used
	
	ring1 = PanAz.ar(14, rings[1], azi, 1, width1, orientation1);
	ring2 = PanAz.ar(14, rings[2], azi, 1, width2, orientation2);
	ring3 = PanAz.ar(8, rings[3], azi, 1, width3, orientation3);
	ring4 = PanAz.ar(6, rings[4], azi, 1, width4, orientation4);
	ring5 = rings[5];

	Out.ar(0, ring1);
	Out.ar(14, ring2);
	Out.ar(28, ring3);
	Out.ar(36, ring4);
	Out.ar(42, ring5);

}).store;


SynthDef("kdpanvolw", { 
	| 	out = 0, azi = 0, ele = 0, 
		vertical_orientation = 1.5, 
		orientation1 = 7, orientation2 = 6.5, orientation3 = 3.5, orientation4 = 2.52, 
		vertwidth = 2, width1 = 2, width2 = 2, width3 = 2, width4 = 2,
		vol = 1
	|
	var source;
	var rings, ring1, ring2, ring3, ring4, ring5;
	
	source = vol;
	
	rings = PanAz.kr(7, source, ele, 1.5, vertwidth, vertical_orientation); // channels 0 and 5 are not used
	
	ring1 = PanAz.kr(14, rings[1], azi, 1, width1, orientation1);
	ring2 = PanAz.kr(14, rings[2], azi, 1, width2, orientation2);
	ring3 = PanAz.kr(8, rings[3], azi, 1, width3, orientation3);
	ring4 = PanAz.kr(6, rings[4], azi, 1, width4, orientation4);
	ring5 = rings[5];

	Out.kr(out, ring1);
	Out.kr(out + 14, ring2);
	Out.kr(out + 28, ring3);
	Out.kr(out + 36, ring4);
	Out.kr(out + 42, ring5);

}).store;
