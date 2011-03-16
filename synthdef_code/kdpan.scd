/* 
KlangDom panner based on 2 levels of PanAz y -> z.
Tweaked for Klang Dom at ZKM with 43 Speakers.
As configured on 2011 03 15

See also class KDpan.

b = Bus.audio;
p = Synth("kdpan", [\in, b.index]);
r = { Out.ar(b.index, WhiteNoise.ar(0.1)) }.play;

p.set(\ele, 1.0);

p.set(\azi, 0.2);

*/

SynthDef("kdpan",{ | in = 0, azi = 0, ele = 0, width = 2.0, vertical_orientation = 1.5, 
		orientation1 = 6.8, orientation2 = 6.8, orientation3 = 6.8, orientation4 = 6.8, 
		vol = 1 |
	var source;
	var rings, ring1, ring2, ring3, ring4, ring5;
	
	source = In.ar(in, 1) * vol;
	
	rings = PanAz.ar(7, source, ele, 1.5, width, vertical_orientation); // channels 0 and 5 are not used
	
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

}).load(Server.local);
