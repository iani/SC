/* Series of Textures to serve as basic building blocks of Osmosis piece. 

... Draft 1 ...

IZ 2011 03 16 

*/

// Prepare the piece with this to configure the server, boot it, and load synthdefs and buffers: 

O.startup;

// A delicate texture to start, pianissimo: 

// ~dashes1000 = { | i | Synth(\bphasor, [\out, i.postln, \bufnum, O@\swallowsa, \rate, i / 1000 + 1]); } ! 43;

// But should be prefaced by a single swallow? 

~dashes = { | i | Synth(\bphasor, [\out, i, \bufnum, O@\dashes, \rate, 1, \vol, 0]); } ! 43;

~dashes[27].set([\vollag, 1, \vol, 0.2]
~dashes[27].set([\vollag, 5, \vol, 0.6]
~dashes[27].set([\vollag, 5, \vol, 0.06]

~dashes[22].set([\vollag, 5, \vol, 0.6]

~dashes[10..14].set([\vollag, 5, \vol, 0.1]
~dashes[10..14].set([\vollag, 5, \vol, 0.0]

~dashes do: { | s, i | s.set(\ratelag, 20, \rate, i / 1000 + 1) };

~dashes do: { | s, i | s.set(\ratelag, 20, \rate, i) };

~dashes do: { | s, i | s.set(\ratelag, 20, \rate, i / 5 + 0.2) };

~dashes do: { | s, i | s.set(\vollag, 20, \vol, 0.2, \ratelag, 50, \rate, i / 100 + 1) };

~dashes do: { | s, i | s.set(\vollag, 20, \vol, 0.01, \ratelag, 17, \rate, i / 1000 + 1) };


~dashes do: { | s, i | s.set(\rate, i / 1000 + 1) };
~dashes do: { | s, i | s.set(\rate, i / 100 + 1) };
~dashes do: { | s, i | s.set(\rate, i / 10 + 1) };
~dashes do: { | s, i | s.set(\rate, i / 9 + 1) };
~dashes do: { | s, i | s.set(\rate, i / 8 + 1) };
~dashes do: { | s, i | s.set(\rate, i / 7 + 1) };
~dashes do: { | s, i | s.set(\rate, i / 6 + 1) };
~dashes do: { | s, i | s.set(\rate, i / 5 + 1) };
~dashes do: { | s, i | s.set(\rate, i / 4 + 1) };
~dashes do: { | s, i | s.set(\rate, i / 3 + 1) };
~dashes do: { | s, i | s.set(\rate, i / 2 + 1) };
~dashes do: { | s, i | s.set(\rate, i / 1 + 1) };
~dashes do: { | s, i | s.set(\rate, i / 0.9 + 1) };
~dashes do: { | s, i | s.set(\rate, i / 0.5 + 1) };
~dashes do: { | s, i | s.set(\rate, i / 0.5 + 0.9) };
~dashes do: { | s, i | s.set(\rate, i / 0.5 + 0.8) };
~dashes do: { | s, i | s.set(\rate, i / 0.5 + 0.7) };
~dashes do: { | s, i | s.set(\rate, i / 0.5 + 0.6) };
~dashes do: { | s, i | s.set(\rate, i / 0.5 + 0.5) };
~dashes do: { | s, i | s.set(\rate, i / 0.5 + 0.4) };
~dashes do: { | s, i | s.set(\rate, i / 0.5 + 0.3) };
~dashes do: { | s, i | s.set(\rate, i / 0.5 + 0.2) };
~dashes do: { | s, i | s.set(\rate, i / 0.5 + 0.1) };
~dashes do: { | s, i | s.set(\rate, i / 0.6 + 0.1) };
~dashes do: { | s, i | s.set(\rate, i / 0.7 + 0.1) };
~dashes do: { | s, i | s.set(\rate, i / 0.8 + 0.1) };
~dashes do: { | s, i | s.set(\rate, i / 0.9 + 0.1) };
~dashes do: { | s, i | s.set(\rate, i / 1 + 0.1) };
~dashes do: { | s, i | s.set(\rate, i / 2 + 0.1) };
~dashes do: { | s, i | s.set(\rate, i / 3 + 0.1) };
~dashes do: { | s, i | s.set(\rate, i / 4 + 0.1) };
~dashes do: { | s, i | s.set(\rate, i / 5 + 0.1) };
~dashes do: { | s, i | s.set(\rate, i / 6 + 0.1) };
~dashes do: { | s, i | s.set(\rate, i / 7 + 0.1) };
~dashes do: { | s, i | s.set(\rate, i / 8 + 0.1) };
~dashes do: { | s, i | s.set(\rate, i / 9 + 0.1) };
~dashes do: { | s, i | s.set(\rate, i / 10 + 0.1) };
~dashes do: { | s, i | s.set(\rate, i / 11 + 0.1) };
~dashes do: { | s, i | s.set(\rate, i / 12 + 0.1) };
~dashes do: { | s, i | s.set(\rate, i / 13 + 0.1) };
~dashes do: { | s, i | s.set(\rate, i / 14 + 0.1) };
~dashes do: { | s, i | s.set(\rate, i / 15 + 0.1) };
~dashes do: { | s, i | s.set(\rate, i / 20 + 0.1) };
~dashes do: { | s, i | s.set(\rate, i / 30 + 0.1) };
~dashes do: { | s, i | s.set(\rate, i / 50 + 0.1) };
~dashes do: { | s, i | s.set(\rate, i / 70 + 0.1) };
~dashes do: { | s, i | s.set(\rate, i / 90 + 0.1) };
~dashes do: { | s, i | s.set(\rate, i / 100 + 0.1) };
~dashes do: { | s, i | s.set(\rate, i / 200 + 0.1) };
~dashes do: { | s, i | s.set(\rate, i / 300 + 0.1) };
~dashes do: { | s, i | s.set(\rate, i / 400 + 0.1) };
~dashes do: { | s, i | s.set(\rate, i / 1000 + 0.1) };
~dashes do: { | s, i | s.set(\rate, i / 1000 + 0.05) };
~dashes do: { | s, i | s.set(\rate, 0.05) };
~dashes do: { | s, i | s.set(\trig, 0) };
~dashes do: { | s, i | s.set(\trig, 1) };



