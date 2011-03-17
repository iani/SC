/* 

~swallows = { | i | Synth(\bphasor, [\out, i.postln, \bufnum, O@\swallowsa, \rate, 1]); } ! 43;
~volbus = Bus.control(numChannels: 43);
~swallows do: { | s, i | s.map(\vol, ~volbus.index + i) };
~swallows do: { | s, i | s.map(\vol, ~volbus.index + i) };
~volbus;
~panner = Synth("kdpanvol", [\out, ~volbus.index]);
~panner.set(\width, 2);
~panner.set(\ele, 0.6); 
~panner.set(\ele, 0.1); 

~panner.set(\width, 18);


~dfd = { | i | Synth(\bphasor, [\out, (i * 3).postln, \bufnum, O@\dfd, \rate, 1]); } ! 3;

~b1 = Bus.control;
~b2 = Bus.control;
~b3 = Bus.control;

~dfd[0].map(\vol, ~b1);
~b1.set(0);
~dfd[1].map(\vol, ~b2);
~b2.set(0);
~dfd[2].map(\vol, ~b3);
~b3.set(0);


*/
