/*
Basically Visual stuff

Aris Bezas Tue, 17 May 2011, 18:19
*/

//:Use new stuff
s.boot;
2.wait;
SendSpectrogramData.new
0.1.wait;
\SoundIn.playDef(\pure_clarinet)

//:OF change variables
OF.int("textureAlpha",255);
OF.int("textureRed",255);
OF.reverse;

//:Set
k.set(\bufnum, 7)

//:Define and Play Synths
\foubuf_01.playDef(\foubuf);

//:Other sound
\test.playFunc({ LFSaw.ar(LFNoise2.kr(0.5).range(100, 1000), 0, 0.1) });
n = NetAddr("127.0.0.1", 57120); // the url should be the one of computer of app 2 (or nil)
o = OSCresponderNode(n, '/b_setn', { |t, r, msg| ("time:" + t).postln; msg[1].postln }).add;

