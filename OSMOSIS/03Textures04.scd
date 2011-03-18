/* Series of Textures to serve as basic building blocks of Osmosis piece. 
IZ 2011 03 16-17

O.startup; // boot server, load buffers + synthdefs
b = O.buffers.keys.asArray.sort;
[chimes, dashes, dfd, newstar, strich, 
swallowsa, swallowsb, swallowsc, swallowsd, swallowse, 
weddell, weddella, weddellb, weddellc, weddelle
]


O.bplay(\chimes)
O.bplay(\dashes)
O.bplay(\dfd)
O.bplay(\newstar)
O.bplay(\strich)


a = NodeArray({ | i | Synth(\bphasor, [\out, i, \bufnum, O@\swallowsa, \rate, 1, \vol, 0]); });

*/


~swallowsa 		= KDpan([\bphasor, \bufnum, O@\swallowsa, \vol, 0.5, \rate, 1]);
~weddella			= KDpan([\bphasor, \bufnum, O@\weddella, \vol, 0.5, \rate, 1]);

~chimes			= KDpan([\bphasor, \bufnum, O@\chimes, \vol, 0.5, \rate, 1]);
~dashes			= KDpan([\bphasor, \bufnum, O@\dashes, \vol, 0.5, \rate, 1]);
~dfd				= KDpan([\bphasor, \bufnum, O@\dfd, \vol, 0.5, \rate, 1]);
~newstar			= KDpan([\bphasor, \bufnum, O@\newstar, \vol, 0.5, \rate, 1]);
~strich			= KDpan([\bphasor, \bufnum, O@\strich, \vol, 0.5, \rate, 1]);

~allsw			= NodeArray({ | i | Synth(\bphasor, [\out, i, \bufnum, O@\swallowsa, \rate, 1, \vol, 0]); });
~allswd			= KDpanvol(~allsw);
/*
~allswd.fadeIn;
~allswd.wideTo(18, 10);
~allswd.vertWideTo(18, 10);
*/

~allwe			= NodeArray({ | i | Synth(\blfn3, [\out, i, \bufnum, O@\weddelle, \vol, 0]); });
~allwed			= KDpanvol(~allwe);
/*
~allwed.fadeIn;
~allwed.wideTo(18, 10);
~allwed.vertWideTo(18, 10);
~allwed.
*/

~alldf			= NodeArray({ | i | Synth(\bphasor, [\out, i, \bufnum, O@\dfd, \vol, 0]); });
~alldfd			= KDpanvol(~alldf);


~allswd.phrases(40, 
	[\vol, [ [0.2, 1], [3, 3], [7, 2], [10, 7], [5, 0]] * 1.5],
	[\ele, [[0, 0], [1, 1], [10, 0]]],
	[\azi, [[0, 0], [1, 1.01], [0, -1], [1, 0]]]
);



~allswd.setNodes([\ratelag, 10, \rate, 1.5]);


~allswd.setNodes([\bufnum, O@\weddella], 0);
~allswd.setNodes([\bufnum, O@\weddelle], (0..1));

~allswd.setNodes([\bufnum, O@\chimes], 2);

~allwe			= NodeArray({ | i | Synth(\blfn3, [\out, i, \bufnum, O@\weddelle, \vol, 0]); });
~allwe.fadeIn;

~allwed.wideTo(18, 5);
~allwed.setNodes([\rate, 1]);
~allwed.fadeIn;

~allswd.fadeIn;
~allswd.wideTo(18, 10);
~allswd.wideTo(2, 10);
~allswd.goTo
// ~allsw.fadeIn;



~allswd.setLag(\vol, 2, 2);
~allswd.setLag(\width, 18, 2);
~allswd do: 

~allsw.fadeOut;
~allsw do: { | n | n.set(\vollag, 3, \vol, 0); }
~allwe
~allco


~allwed
~allcod

p.phrases(40, 
	[\vol, [ [0.2, 1], [3, 3], [7, 2], [10, 7], [5, 0]] * 1.5],
	[\ele, [[0, 0], [1, 1], [10, 0]]],
	[\azi, [[0, 0], [1, 1.01], [0, -1], [1, 0]]]
);


a = NodeArray({ | i | Synth(\bphasor, [\out, i, \bufnum, O@\swallowsa, \rate,  1 / 100 + 1, \vol, 1]); })

a = NodeArray({ | i | Synth(\blfn3, [\out, i, \bufnum, O@\swallowsa, \rate,  i / 100 + 0.2, \vol, 1]); })

m = { Out.ar(0, Mix.ar(In.ar(0, 43))) }.play(addAction: \addToTail);



p = KDpan([\bphasor, \bufnum, O@\swallowsa, \vol, 0.1, \rate, 1]);

p.set(\azi, 0);
p.set(\vol, 10);

p.phrases(40, 
	[\vol, [ [0.2, 1], [3, 3], [7, 2], [10, 7], [5, 0]] * 1.5],
	[\ele, [[0, 0], [1, 1], [10, 0]]],
	[\azi, [[0, 0], [1, 1.01], [0, -1], [1, 0]]]
);


p.phrases(40, 
	[\vol, [ [0.2, 1], [3, 3], [7, 2], [10, 7], [5, 0]] * 1.5],
	[\ele, [[0, 0], [1, 1], [10, 0]]],
	[\azi, [[0, 0], [1, 1.01], [0, -1], [1, 0]]]
);


a = NodeArray({ | i | Synth(\bphasor, [\out, i, \bufnum, O@\swallowsa, \rate, 1, \vol, 0]); })
a = NodeArray({ | i | Synth(\blfn3, [\out, i, \bufnum, O@\weddelle, \rate, 0.045, \vol, 1]); })
a = NodeArray({ | i | Synth(\blfn3, [\out, i, \bufnum, O@\weddelle, \rate, 0.015, \vol, 1]); })


p = KDpan.new;
p add: { | bus, group | Synth(\blfn3, [\out, bus, \bufnum, O@\weddelle], target: group, addAction: \addToHead) };
p.set(\elelag, 60, \ele, 0.5, \azilag, 60, \azi, 1);


a.setn([\vol, 0.1]);
a.setn([\vol, 0.0]);
a.setn([\vollag, 10, \vol, 0.1]);


a.setn({ | n, i | [\vollag, i * 3 + 10, \vol, 0.5] });


a.set(\vollag, 0.2);


a = NodeArray({ | i | Synth(\blfn3, [\out, i, \bufnum, O@\weddelle, \rate, 0.045, \vol, 1]); })

==========================================


p = KDpan.new;
p add: { | bus, group | Synth(\blfn3, [\out, bus, \bufnum, O@\weddelle], target: group, addAction: \addToHead) };

p.set(\vol, 0.1);
p.set(\vol, 0.1);
p.set(\ele, 1);
p.set(\elelag, 20, \ele, 0.5, \azilag, 20, \azi, 1);
p.set(\elelag, 20, \ele, 0);
p.set(\azilag, 30, \azi, 1);
p.set(\azilag, 120, \azi, 0);
