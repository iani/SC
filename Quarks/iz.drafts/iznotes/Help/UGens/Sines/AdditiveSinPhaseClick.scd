// Use randomized phase to avoid initial clicks in sine clusters: 

//:----
SynthDef("sinecluster-randphase", { | out = 0 |
	var lo, hi;
	lo = Rand(100, 1000.0);
	hi = Rand(1010, 10000.0);
	Out.ar(out,
		Mix(SinOsc.ar({ Rand(lo, hi) } ! 300, { Rand(0.0, 2pi) } ! 300, 0.001))
	)
}).send(Server.default);


//:NO CLICK AT START OF SOUND

Synth("sinecluster-randphase");

//:Adding an effect ... 
/*
Server.default.queryAllNodes;
*/

{ Pan2.ar(In.ar(0), LFNoise1.kr(1).range(-1, 1.0)); }.play(addAction: \addToTail);

//:THE SOUND STARTS WITH A CLICK 
SynthDef("sinecluster-zerophase", { | out = 0 |
	var lo, hi;
	lo = Rand(100, 1000.0);
	hi = Rand(1010, 10000.0);
	Out.ar(out,
		Mix(SinOsc.ar({ Rand(lo, hi) } ! 300, 0, 0.001))
	)
}).send(Server.default);

//:---

Synth("sinecluster-zerophase");

//:Is Klang more efficient? Yes.

SynthDef("klangcluster-randphase", { | out = 0 |
	var lo, hi;
	lo = Rand(100, 1000.0);
	hi = Rand(1010, 10000.0);
	Out.ar(out,
		Klang.ar(`[{ Rand(lo, hi) } ! 300, 0.001, { Rand(0.0, 2pi) } ! 300])
	)
}).send(Server.default);

//:---

Synth("klangcluster-randphase");

//:random panning klangclusters ...

SynthDef("klangcluster-randphase-randpan", { | out = 0 |
	var lo, hi, source;
	lo = Rand(100, 1000.0);
	hi = Rand(1010, 10000.0);
	source = Klang.ar(`[{ Rand(lo, hi) } ! 300, 0.001, { Rand(0.0, 2pi) } ! 300]);
	Out.ar(out, Pan2.ar(source, LFNoise1.kr(1).range(-1, 1)))
}).send(Server.default);

//:Twenty panning klangclusters. 6000 Sine waves. 

{ Synth("klangcluster-randphase-randpan"); } ! 20;
