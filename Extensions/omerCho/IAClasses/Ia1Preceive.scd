Summary

Posc

Send a score consisting of timed beats and tags to many computers via OSC. 

The score is written like a pbind. The tags and beats are a pattern that is bound to the key 'msg'. 

Psend

Create a phrase that sends tags via Posc. Psend also sends a \start when it starts, and an \end when it has ended. 

Preceive

Bind actions (functions) to tags or to beats. An action bound to a beat or a tag is executed when the beat or the tag is received via osc. 

Examples

Activate / Deactivate posting of OSC input: 

Activate posting of OSC input: 

Preceive.postOSC;
Preceive.stopOSC;

Play some simple scores with Posc: 

Preceive.postOSC;	// activate posting to see what is being sent. 

Posc.play([\a, \b, [\c, 1, 2, 3]]);	// play some simple tags;

// play some tags, some groups of subbeats, and some chords of tags;
Posc.play([\a, \b, Beats(3, \bang), [[\note1, 100], [\note2, 200]]]);	

Posc.play([\a, \b, Beats(3), [[\note1, 100], [\note2, 200]]]);	

// Use some patterns inside the score
Posc.play([\a, Prand([\excellent, \randomize], 10), Pn([[\note1, 100], [\note2, 200]], 3)]);	

Set the OSC destinations to which Posc will send when started with play 

Posc.destinations(NetAddr.localAddr, NetAddr("127.0.0.1", 12345));	// add local host sclang and oF destinations

Creating a score to bind actions to tags or beats

// 1. Define the player that will listen to the score, and activate it
(
Preceive(
	1 -> { { WhiteNoise.ar(EnvGen.kr(Env.perc, doneAction: 2) * 0.1) }.play },
	\a -> { { SinOsc.ar(400, 0, EnvGen.kr(Env.perc, doneAction: 2) * 0.1) }.play },
	\b -> { { SinOsc.ar(1000, 0, EnvGen.kr(Env.perc, doneAction: 2) * 0.1) }.play },
	10 -> { { BrownNoise.ar(EnvGen.kr(Env.perc, doneAction: 2) * 0.1) }.play },
	3 -> { { PinkNoise.ar(EnvGen.kr(Env.perc, doneAction: 2) * 0.1) }.play }
).play;
)

Posc.play;

// 2. Play a score for the player 
Posc.play([Beats(3), 5]);	// play some simple tags;

// 3. adding more patterns in Pbind style!!!

Posc.play(
	[\a, \b, Beats(3), \b, Beats(2), \a, Prand([\b, \a], 5)],
	\dur, 0.1

);	// play some simple tags;
