Spectrograph.start; // in case needed ...

\test.play({ SinOsc.ar(LFSaw.kr(0.1).range(100, 10000), 0, 0.001) });
\test2.play({ SinOsc.ar(LFSaw.kr(0.01).range(100, 10000), 0, 0.001) });
\default.play rsyncs: { loop { 0.25.rand.wait; \default.set(\freq, 100 + 10000.rand) } };

//:checking

Group.new;
Server.default.queryAllNodes;

\default.play;
