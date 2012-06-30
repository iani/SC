
//:1 ---

a = Pbind(\degree, Pseq([1, 2, 3]), \dur, 0.5).play;
a addDependant: { | player, what | if(what == \stopped) { "will start next pattern NOW".postln; } };

//:Test for using with Chain

Chain(Pseq([Pbind(\degree, Pseq([1, 2, 3]), \dur, 0.5).play]));
