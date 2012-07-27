//:---
~pock1 = { | i | 
	Synth(\playbuf, [\buf, \weddellpock.buffer, \out, i, \rate, 1 - (i * 0.05) ]);
} ! 21;

//:---
~pock1 do: _.set(\amp, 0.013);

//:---
~pock1 do: _.free;

//:---
~wed2 = { | i | 
	Synth(\playbuf, [\buf, \weddell1.buffer, \out, i, \rate, 1 - (i * 0.05) ]);
} ! 21;

//:---
~wed2 do: _.set(\amp, 0.013);

//:---
~wed2 do: _.free;

