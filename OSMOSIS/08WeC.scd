/* Series of Textures to serve as basic building blocks of Osmosis piece. 

... Draft 1 ...

IZ 2011 03 16 

*/

// Prepare the piece with this to configure the server, boot it, and load synthdefs and buffers: 

O.startup;

// A delicate texture to start, pianissimo: 

// ~weddellc1000 = { | i | Synth(\bphasor, [\out, i.postln, \bufnum, O@\swallowsa, \rate, i / 1000 + 0.01]); } ! 43;

// But should be prefaced by a single swallow? 

~weddellc = { | i | Synth(\blfn1, [\out, i, \bufnum, O@\weddellc]); } ! 43;

// fadein 
~weddellc do: { | s, i | s.set(\vollag, i + 1 * 10, \vol, 0.1) };

~weddellc[10..13] do: { | s, i | s.set(\vollag, i + 1 * 3, \vol, 0.5) };

~weddellc[10..13] do: { | s, i | s.set(\vollag, i + 1 * 3, \vol, 0.0) };

~weddellc[41..42] do: { | s, i | s.set(\vollag, i + 1 * 3, \vol, 0.2) };

~weddellc[41..42] do: { | s, i | s.set(\vollag, i + 1 * 3, \vol, 0.0) };

~weddellc[0..3] do: { | s, i | s.set(\vollag, i + 1 * 3, \vol, 0.0) };

~weddellc do: { | s, i | s.set(\vollag, 10, \vol, 0.0) };


// fadeout;
~swallowsc do: { | s, i | s.set(\vollag, i + 30 % 43 + 1 * 2, \vol, 0.0) };

// rate
~swallowsc do: { | s, i | s.set(\ratelag, i + 1 * 5, \rate, i + 1 / 100) };


~weddellc do: { | s, i | s.set(\rate, i / 1000 + 0.01) };
~weddellc do: { | s, i | s.set(\rate, i / 100 + 0.01) };
~weddellc do: { | s, i | s.set(\rate, i / 10 + 0.01) };
~weddellc do: { | s, i | s.set(\rate, i / 9 + 0.01) };
~weddellc do: { | s, i | s.set(\rate, i / 8 + 0.01) };
~weddellc do: { | s, i | s.set(\rate, i / 7 + 0.01) };
~weddellc do: { | s, i | s.set(\rate, i / 6 + 0.01) };
~weddellc do: { | s, i | s.set(\rate, i / 5 + 0.01) };
~weddellc do: { | s, i | s.set(\rate, i / 4 + 0.01) };
~weddellc do: { | s, i | s.set(\rate, i / 3 + 0.01) };
~weddellc do: { | s, i | s.set(\rate, i / 2 + 0.01) };
~weddellc do: { | s, i | s.set(\rate, i / 1 + 0.01) };
~weddellc do: { | s, i | s.set(\rate, i / 0.9 + 0.01) };
~weddellc do: { | s, i | s.set(\rate, i / 0.5 + 0.01) };
~weddellc do: { | s, i | s.set(\rate, i / 0.5 + 0.9) };
~weddellc do: { | s, i | s.set(\rate, i / 0.5 + 0.8) };
~weddellc do: { | s, i | s.set(\rate, i / 0.5 + 0.7) };
~weddellc do: { | s, i | s.set(\rate, i / 0.5 + 0.6) };
~weddellc do: { | s, i | s.set(\rate, i / 0.5 + 0.5) };
~weddellc do: { | s, i | s.set(\rate, i / 0.5 + 0.4) };
~weddellc do: { | s, i | s.set(\rate, i / 0.5 + 0.3) };
~weddellc do: { | s, i | s.set(\rate, i / 0.5 + 0.2) };
~weddellc do: { | s, i | s.set(\rate, i / 0.5 + 0.1) };
~weddellc do: { | s, i | s.set(\rate, i / 0.6 + 0.1) };
~weddellc do: { | s, i | s.set(\rate, i / 0.7 + 0.1) };
~weddellc do: { | s, i | s.set(\rate, i / 0.8 + 0.1) };
~weddellc do: { | s, i | s.set(\rate, i / 0.9 + 0.1) };
~weddellc do: { | s, i | s.set(\rate, i / 1 + 0.1) };
~weddellc do: { | s, i | s.set(\rate, i / 2 + 0.1) };
~weddellc do: { | s, i | s.set(\rate, i / 3 + 0.1) };
~weddellc do: { | s, i | s.set(\rate, i / 4 + 0.1) };
~weddellc do: { | s, i | s.set(\rate, i / 5 + 0.1) };
~weddellc do: { | s, i | s.set(\rate, i / 6 + 0.1) };
~weddellc do: { | s, i | s.set(\rate, i / 7 + 0.1) };
~weddellc do: { | s, i | s.set(\rate, i / 8 + 0.1) };
~weddellc do: { | s, i | s.set(\rate, i / 9 + 0.1) };
~weddellc do: { | s, i | s.set(\rate, i / 10 + 0.1) };
~weddellc do: { | s, i | s.set(\rate, i / 11 + 0.1) };
~weddellc do: { | s, i | s.set(\rate, i / 12 + 0.1) };
~weddellc do: { | s, i | s.set(\rate, i / 13 + 0.1) };
~weddellc do: { | s, i | s.set(\rate, i / 14 + 0.1) };
~weddellc do: { | s, i | s.set(\rate, i / 15 + 0.1) };
~weddellc do: { | s, i | s.set(\rate, i / 20 + 0.1) };
~weddellc do: { | s, i | s.set(\rate, i / 30 + 0.1) };
~weddellc do: { | s, i | s.set(\rate, i / 50 + 0.1) };
~weddellc do: { | s, i | s.set(\rate, i / 70 + 0.1) };
~weddellc do: { | s, i | s.set(\rate, i / 90 + 0.1) };
~weddellc do: { | s, i | s.set(\rate, i / 100 + 0.1) };
~weddellc do: { | s, i | s.set(\rate, i / 200 + 0.1) };
~weddellc do: { | s, i | s.set(\rate, i / 300 + 0.1) };
~weddellc do: { | s, i | s.set(\rate, i / 400 + 0.1) };
~weddellc do: { | s, i | s.set(\rate, i / 1000 + 0.1) };
~weddellc do: { | s, i | s.set(\rate, i / 1000 + 0.05) };
~weddellc do: { | s, i | s.set(\rate, 0.05) };
~weddellc do: { | s, i | s.set(\trig, 0) };
~weddellc do: { | s, i | s.set(\trig, 1) };



