/*
SpectrographData.new
\test3.playFunc({ LFSaw.ar(LFNoise2.kr(0.1).range(100, 10000), 0, 0.005) });
Array
OF
Aris Bezas Tue, 17 May 2011, 18:19
*/

//:Run lola
s.boot;
2.wait;
PollFFT('of');
PollFFT('of').isPlaying;
SpectrographData.new

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
n.sendMsg();
OF.data