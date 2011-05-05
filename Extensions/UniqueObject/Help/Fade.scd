/*
Add release to any synthdef to avoid clipping
*/

//:clips: 
WaitForServer.new;

SynthDef(\test, { Out.ar(0, SinOsc.ar(Rand(400, 4000), 0, 0.2)) }).send(Server.default);

10 do: { 0.1.wait; \test.play; 0.1.wait; \test.free };

//:does not clip: 
WaitForServer.new;

SynthDef(\test, { Out.ar(0, SinOsc.ar(Rand(400, 4000), 0, 0.2 * Fade.kr)) }).send(Server.default);

10 do: { 0.1.wait; \test.play; 0.1.wait; \test.release; };


//:setting the release time: 
WaitForServer.new;

SynthDef(\test, { Out.ar(0, SinOsc.ar(Rand(400, 4000), 0, 0.1 * Fade.kr)) }).send(Server.default);
0.1.wait;

10 do: { \test.play; 0.1.wait; \test.release(exprand(0.01, 0.9)); 1.0.wait; };

//:testing release with default synthdef
WaitForServer.new;

10 do: { \default.play(args: [\freq: rrand(30, 90).midicps]); 0.1.wait; \default.release;  0.5.wait; };

