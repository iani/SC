//:opening <
{
	var dur;
	loop {
		dur = 0.1 exprand: 30;
		{
			Pan2.ar(
				Mix.ar(
					SinOsc.ar(
						Line.kr(50 rrand: 4000, 
							Array.rand(50, 50 rrand: 4000, 50 rrand: 4000), dur)
						, 0
						, Array.rand(50, 0.01, 0.99)
				)) * EnvGen.kr(Env.sine(dur), 1, 0.003, doneAction: 2)
				, -1.0 rrand: 1.0
			)
		}.play;
		dur.wait;
	};
}.fork;

//:closing >
{
	var dur;
	loop {
		dur = 0.1 exprand: 30;
		{
			Pan2.ar(
				Mix.ar(
					SinOsc.ar(
						Line.kr(
							Array.rand(50, 50 rrand: 4000, 50 rrand: 4000), 
							50 rrand: 4000, dur)
						, 0
						, Array.rand(50, 0.01, 0.99)
				)) * EnvGen.kr(Env.sine(dur), 1, 0.003, doneAction: 2)
				, -1.0 rrand: 1.0
			)
		}.play;
		dur.wait;
	};
}.fork;


//:Wavy

SynthDef("test3", { 
	var source, dur;
	dur = Rand(0.1, 30);	
	source = Pan2.ar(
		Mix.ar(
			SinOsc.ar(
				{ LFNoise1.kr(0.05 rrand: 2).range(50 rrand: 4000, 50 rrand: 4000) } ! 100
				, 0
				, Array.rand(100, 0.01, 0.99)
		)) * EnvGen.kr(Env.sine(dur), 1, 0.001, doneAction: 2)
		, -1.0 rrand: 1.0
	);
	Out.ar(0, source);
}).send(Server.default);


//:----
c = Synth("test3");

