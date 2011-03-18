/* Series of Textures to serve as basic building blocks of Osmosis piece. 
IZ 2011 03 16-17

O.startup; // boot server, load buffers + synthdefs

*/

a = NodeArray({ | i | Synth(\bphasor, [\out, i, \bufnum, O@\swallowsa, \rate, 1, \vol, 0]); })

a.setn([\vol, 0.1]);
a.setn([\vol, 0.0]);
a.setn([\vollag, 10, \vol, 0.1]);


a.setn({ | n, i | [\vollag, i * 3 + 10, \vol, 0.5] });


a.set(\vollag, 0.2);

