//:a OK!
ServerReady.addFuncToLoadChain({ | nextfunc | { nextfunc.value }.defer(5) });
UniqueSynth2(\default);
//:c
UniqueSynth2(\default);
//:s
ServerReady
//:x 
a = FunctionChain([{ | ch | { ch.postln; ch.next; }.defer(1) }], { "ended".postln; });
a.start;
//:y
//:x 
a = FunctionChain(nil, { "ended".postln; });
a.start;
//:a 
a = FunctionChain(({ | i | { i.postln; }} ! 10), { "ended".postln; });
a.start;
//:b 
a = FunctionChain(nil, { "ended".postln; });
\default.play;
\test.play;
UniqueSynth2(\test);


List.new.addAll((1..5))