//:Customize Spectrograph background color
Spectrograph.backgroundColor = Color(0.2, 0.5, 0.9);
//:Start Spectrograph with some test tones.
Spectrograph.start;

\test.playFunc({ SinOsc.ar(LFSaw.kr(0.1).range(100, 10000), 0, 0.1) });
\test2.playFunc({ SinOsc.ar(LFSaw.kr(0.01).range(100, 10000), 0, 0.1) });


