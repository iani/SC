

SynthDef("minidom", { 
	| in = 0, azi = 0, ele = 0, width = 2.0, 
		orientation1 = 0, orientation2 = 0, orientation3 = 0, vol = 1 |
	var source;
	var ring1mul, ring2mul, ring3mul;
	var ring1, ring2, ring3;
	
	source = In.ar(in) * vol * 2; //  2 because the ringmul's add up to 0.5 and not 1
	
	ring1mul = 0.5 - ele max: 0;
	ring3mul = 0.5 - (1 - ele) max: 0;
	ring2mul = 0.5 - ring1mul - ring3mul;
	
	ring1 = PanAz.ar(12, source, azi, ring1mul, width, orientation1);
	ring2 = PanAz.ar(10, source, azi, ring2mul, width, orientation2);
	ring3 = Pan2.ar(source, azi, ring3mul);

	Out.ar(0, ring1);
	Out.ar(12, ring2);
	Out.ar(22, ring3);
}).send(Server.default);


SynthDef("klangdom", {
	| in = 0, azi = 0, ele = 0, width = 2.0, 
		orientation1 = 0, orientation2 = 0, orientation3 = 0, orientation4 = 0, vol = 1 |
	var source;
	var ring1mul, ring2mul, ring3mul;
	var ring1, ring2, ring3;
	
	source = In.ar(in) * vol * 2; //  2 because the ringmul's add up to 0.5 and not 1
	
	ring1mul = 0.5 - ele max: 0;
	ring3mul = 0.5 - (1 - ele) max: 0;
	ring2mul = 0.5 - ring1mul - ring3mul;
	
	ring1 = PanAz.ar(12, source, azi, ring1mul, width, orientation1);
	ring2 = PanAz.ar(10, source, azi, ring2mul, width, orientation2);
	ring3 = Pan2.ar(source, azi, ring3mul);

	Out.ar(0, ring1);
	Out.ar(12, ring2);
	Out.ar(22, ring3);
}).send(Server.default);



/*

b = Bus.audio(Server.default(1));
a = Synth("minidom", [\in, b.index]);
c = { Out.ar(b.index, PinkNoise.ar(0.1)) }.play;


Server.default.queryAllNodes;
*/


"SynthDefs loaded".postln;