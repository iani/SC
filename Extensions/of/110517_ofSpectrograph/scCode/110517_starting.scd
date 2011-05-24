/*
SpectrographData.new
\test3.playFunc({ LFSaw.ar(LFNoise2.kr(0.1).range(100, 10000), 0, 0.005) });
Array
OF
Aris Bezas Tue, 17 May 2011, 18:19
*/




//:Use new stuff
s.boot;
2.wait;
PollFFT('of') addDependant: SendSpectrogramData.new;
0.1.wait;
// Synth for clarinet, using pure In.ar to take it out to the Out.ar
SynthDef("pure_clarinet",	
	{
		var in;
		in = In.ar(8);
		Out.ar(0, in);
	}		
).send(s);
0.1.wait;
c = Synth("pure_clarinet");
OF.int("textureAlpha",255);
OF.int("textureRed",255);
"ok".postln;

//:foubuf v.003
SynthDef(\foubuf, {| out = 0, bufnum = 0, rate = 1, trigger = 1, loop = 1, pos = 0, level = 1, windowSize = 0.5, pitchRatio = 1 |
        Out.ar(out,
                Pan2.ar( 
                        PitchShift.ar(                  
                                PlayBuf.ar(1, 
                                        bufnum, 
                                        rate, 
                                        trigger, 
                                        0, 
                                        loop
                                ),
                                windowSize,
                                pitchRatio
                        ),
                        pos,
                        level
                )
        )
}).send(s);


//:Play
c = Synth("pure_clarinet");
OF.int("textureColor",255);
//k = Synth(\foubuf).play

//:Set
k.set(\bufnum, 7)

//:Define and Play Synths
\foubuf_01.playDef(\foubuf);

//:Other sound
\test.playFunc({ LFSaw.ar(LFNoise2.kr(0.5).range(100, 1000), 0, 0.1) });
OF.backTrue
n = NetAddr("127.0.0.1", 57120); // the url should be the one of computer of app 2 (or nil)
o = OSCresponderNode(n, '/b_setn', { |t, r, msg| ("time:" + t).postln; msg[1].postln }).add;


//:Run lola
s.boot;
2.wait;
PollFFT('of');
PollFFT('of').isPlaying;
SpectrographData.new
Spectrograph.new
//: Try to understand the folling code at SendSpectrogramData
makeColors {
	colors = (1..colorSize).pow(colorScaleExponent).normalize;
		collect: { | blendFactor |
			//Image colorToPixel: background.blend(binColor, blendFactor);
			//blendFactor.postln;
		};
	}
	
	
(1..10).collect({|n| n.squared }); // receiver syntax	

	
PollFFT('of') addDependant: { | ... data | OF.spectro(data);}

//:Print data
PollFFT('of') addDependant: { | ... data | data.postln; }

//:Other stuff
n = NetAddr("127.0.0.1", 57120);
OSCresponder

n = NetAddr("127.0.0.1", 57120); // the url should be the one of computer of app 2 (or nil)
o = OSCresponderNode(n, '/chat', { |t, r, msg| ("time:" + t).postln; msg[1].postln }).add;

n.sendMsg();
OF.data