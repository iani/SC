
/*
KlankReceive.load;
RastMakam.load;
*/


KlankReceive {
	*load {
///////////////////////////////////////
var s;
s = Server.default;

SynthDef("klkOver", {|out = 0, 
	att = 5, sus = 8, rls = 5, lvl = 0.8, pan = 0,
	freqs = #[0,0,0,0,0,0,0,0,0,0,0,0],
	rings = #[0,0,0,0,0,0,0,0,0,0,0,0]|
	
	var e = EnvGen.kr(Env.linen(att, sus, rls, 1, 4), doneAction:2);
	var i = PinkNoise.ar(0.0002);
	//var i = SoundIn.ar(0, 2);
	var z = DynKlank.ar(
		`[freqs, nil, rings],	// specs
		i					// input
	);
	Out.ar(out, Pan2.ar(z*e, pan));
}).send(s);



//	Tags
~klankA01 = Preceive(
	
	\klk1 -> {
		~klk = Synth.head(~piges, "klkOver", [
			[\att,\sus,\rls], [0.8, 1, 4],
			\lvl, 0.8,
			\pan, 0.2,
			
			\freqs,  {~rastA7}.dup(12),
			\rings, {0.1.rrand(3)}.dup(12),
			
			\out, 		~mainBus
		]);
		},
	\klk2 -> {
		~klk = Synth.head(~piges, "klkOver", [
			[\att,\sus,\rls], [0.8, 1, 4],
			\lvl, 0.8,
			\pan, 1.0.rand2,
			
			\freqs,  {~rastB4}.dup(12),
			\rings, {0.1.rrand(3)}.dup(12),
			
			\out, 		~mainBus
		]);
		},
	\klk3 -> {
		~klk = Synth.head(~piges, "klkOver", [
			[\att,\sus,\rls], [0.8, 1, 4],
			\lvl, 0.6,
			\pan, 1.0.rand2,
			
			\freqs,  {~rastC1}.dup(12),
			\rings, {0.1.rrand(3)}.dup(12),
			
			\out, 		~mainBus
		]);
		},
	\klk4 -> {
		~klk = Synth.head(~piges, "klkOver", [
			[\att,\sus,\rls], [0.8, 1, 4],
			\lvl, 0.8,
			\pan, -0.2,
			
			\freqs,  {~rastD2}.dup(12),
			\rings, {0.1.rrand(3)}.dup(12),
			
			\out, 		~mainBus
		]);
		},
	\klk5 -> {
		~klk = Synth.head(~piges, "klkOver", [
			[\att,\sus,\rls], [0.8, 1, 4],
			\lvl, 0.8,
			\pan, 1.0.rand2,
			
			\freqs,  {~rastB1}.dup(12),
			\rings, {0.1.rrand(3)}.dup(12),
			\out, 		~mainBus
		]);
		}
).play;

//////////////////////////////////////
	}
	*unLoad { 
	}
}

/*
NcircSDefs.load;

KlankReceive.load;
RastMakam.load;
NcircGroups.load;
NcircFX.load;
NcircBusses.load;
NcircFXSynths.load;
JODOsc.load;
Pdef(\klkA01).play;
(
Pdef(\klkA01, Posc(
	[\msg, \dur ], 
	Pseq([ 
		[\klk1, 16], [\klk2, 2], [\klk3, 1], [\klk4, 1], [\klk5, 2]
	], inf)
	)
);
)

Pdef(\klkA01).stop;


s.queryAllNodes;

*/