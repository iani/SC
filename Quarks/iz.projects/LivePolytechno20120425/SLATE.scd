/* 

This is the clean slate where sounds will be made afresh from blank 0 

The idea is to use SynthDefs: martianwinter and burblekaze2. 

martianwinter will be controlled in real time with routines changing its paramdeters. Possibly also binding its parameters to busses. 



*/


~m = Synth("martianwinter");

~m.set(\freq, 500);


~q = Synth("martianwinter");

~m.set(\freq, 600);



~m.free;


//:--
r = {
	loop {
		(0.1 rrand: 5).wait;			
		~m.set(\freq, [200, 300, 400, 500, 600, 700].choose);
	}		
}.fork

//:----


q = {
	loop {
		(0.1 rrand: 5).wait;			
		~m.set(\freq, ([200, 300, 400, 500, 600, 700] * 1.2).choose);
	}		
}.fork


r.free;

q.free;

//:---

~q.free;



~m release: 1

//:--
r.stop;


//:----


~dust = { { Dust.ar(20, 0, 0.1) }.play } ! 10

a = { Klang.ar(`[Array.rand(40, 100, 1000)], 0, 0.1) }.play;

Synth("burblekaze2", [\dur, 100]);




//:---

z = { Synth("martianwinter", [\freq, 7000 rrand: 15000.0]); } ! 10;

