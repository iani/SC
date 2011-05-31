
//:alpha



// { "alhpa ena".postln; } ! 10;


a = { SinOsc.ar(500 + 1000.rand, 0, 0.1) }.play;

~msg.postln;

a.postln;

\default.play;

//:beta

{ "beta 2".postln; } ! 10;