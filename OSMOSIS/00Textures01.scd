/* Series of Textures to serve as basic building blocks of Osmosis piece. 

... Draft 1 ...

IZ 2011 03 16 

*/

// Prepare the piece with this to configure the server, boot it, and load synthdefs and buffers: 

O.startup;

// A delicate texture to start, pianissimo: 

// ~swallows1000 = { | i | Synth(\bphasor, [\out, i.postln, \bufnum, O@\swallowsa, \rate, i / 1000 + 1]); } ! 43;

// But should be prefaced by a single swallow? 

~swallows = { | i | Synth(\bphasor, [\out, i.postln, \bufnum, O@\swallowsa, \rate, 1]); } ! 43;

~swallows do: { | s, i | s.set(\rate, i / 1000 + 1) };
~swallows do: { | s, i | s.set(\rate, i / 100 + 1) };
~swallows do: { | s, i | s.set(\rate, i / 10 + 1) };
~swallows do: { | s, i | s.set(\rate, i / 9 + 1) };
~swallows do: { | s, i | s.set(\rate, i / 8 + 1) };
~swallows do: { | s, i | s.set(\rate, i / 7 + 1) };
~swallows do: { | s, i | s.set(\rate, i / 6 + 1) };
~swallows do: { | s, i | s.set(\rate, i / 5 + 1) };
~swallows do: { | s, i | s.set(\rate, i / 4 + 1) };
~swallows do: { | s, i | s.set(\rate, i / 3 + 1) };
~swallows do: { | s, i | s.set(\rate, i / 2 + 1) };
~swallows do: { | s, i | s.set(\rate, i / 1 + 1) };
~swallows do: { | s, i | s.set(\rate, i / 0.9 + 1) };
~swallows do: { | s, i | s.set(\rate, i / 0.5 + 1) };
~swallows do: { | s, i | s.set(\rate, i / 0.5 + 0.9) };
~swallows do: { | s, i | s.set(\rate, i / 0.5 + 0.8) };
~swallows do: { | s, i | s.set(\rate, i / 0.5 + 0.7) };
~swallows do: { | s, i | s.set(\rate, i / 0.5 + 0.6) };
~swallows do: { | s, i | s.set(\rate, i / 0.5 + 0.5) };
~swallows do: { | s, i | s.set(\rate, i / 0.5 + 0.4) };
~swallows do: { | s, i | s.set(\rate, i / 0.5 + 0.3) };
~swallows do: { | s, i | s.set(\rate, i / 0.5 + 0.2) };
~swallows do: { | s, i | s.set(\rate, i / 0.5 + 0.1) };
~swallows do: { | s, i | s.set(\rate, i / 0.6 + 0.1) };
~swallows do: { | s, i | s.set(\rate, i / 0.7 + 0.1) };
~swallows do: { | s, i | s.set(\rate, i / 0.8 + 0.1) };
~swallows do: { | s, i | s.set(\rate, i / 0.9 + 0.1) };
~swallows do: { | s, i | s.set(\rate, i / 1 + 0.1) };
~swallows do: { | s, i | s.set(\rate, i / 2 + 0.1) };
~swallows do: { | s, i | s.set(\rate, i / 3 + 0.1) };
~swallows do: { | s, i | s.set(\rate, i / 4 + 0.1) };
~swallows do: { | s, i | s.set(\rate, i / 5 + 0.1) };
~swallows do: { | s, i | s.set(\rate, i / 6 + 0.1) };
~swallows do: { | s, i | s.set(\rate, i / 7 + 0.1) };
~swallows do: { | s, i | s.set(\rate, i / 8 + 0.1) };
~swallows do: { | s, i | s.set(\rate, i / 9 + 0.1) };
~swallows do: { | s, i | s.set(\rate, i / 10 + 0.1) };
~swallows do: { | s, i | s.set(\rate, i / 11 + 0.1) };
~swallows do: { | s, i | s.set(\rate, i / 12 + 0.1) };
~swallows do: { | s, i | s.set(\rate, i / 13 + 0.1) };
~swallows do: { | s, i | s.set(\rate, i / 14 + 0.1) };
~swallows do: { | s, i | s.set(\rate, i / 15 + 0.1) };
~swallows do: { | s, i | s.set(\rate, i / 20 + 0.1) };
~swallows do: { | s, i | s.set(\rate, i / 30 + 0.1) };
~swallows do: { | s, i | s.set(\rate, i / 50 + 0.1) };
~swallows do: { | s, i | s.set(\rate, i / 70 + 0.1) };
~swallows do: { | s, i | s.set(\rate, i / 90 + 0.1) };
~swallows do: { | s, i | s.set(\rate, i / 100 + 0.1) };
~swallows do: { | s, i | s.set(\rate, i / 200 + 0.1) };
~swallows do: { | s, i | s.set(\rate, i / 300 + 0.1) };
~swallows do: { | s, i | s.set(\rate, i / 400 + 0.1) };
~swallows do: { | s, i | s.set(\rate, i / 1000 + 0.1) };
~swallows do: { | s, i | s.set(\rate, i / 1000 + 0.05) };
~swallows do: { | s, i | s.set(\rate, 0.05) };
~swallows do: { | s, i | s.set(\trig, 0) };
~swallows do: { | s, i | s.set(\trig, 1) };

