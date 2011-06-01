
/* 

Store synthdefs in a global pool and make them available to synths on any server by sending them to the server at boot time, before starting any synth.

Does not store separate synthdef copies for different servers, just one global pool. 

SynthDefs created while the server is running are sent immediately. ServerReady ensures that they will be sent before starting any further instance of SynthResource. 

*/

//:test 1
Udef(\test1, { Out.ar(0, WhiteNoise.ar(0.1)) });
SynthResource(\test1)

//:test 2
Udef(\test1, { Out.ar(0, SinOsc.ar(2000, 0, 0.1)) });
Udef(\test2, { Out.ar(0, LFPulse.ar(100, 0, 0.5, 0.1)) });
Udef(\test3, { Out.ar(0, LFNoise2.ar(1000, 0.1)) });

\test1.play;
\test2.play;
\test3.play;

//:Works also when rebooting the server>

Server.default.quit;

1.wait;

\test1.play;
\test2.play;
\test3.play;
