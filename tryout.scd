//:a 
Spectrograph.backgroundColor = Color(0.5, 0.5, 0.5);
//:b
Spectrograph.start;

\test.playFunc({ SinOsc.ar(LFSaw.kr(0.1).range(100, 10000), 0, 0.1) });
\test2.playFunc({ SinOsc.ar(LFSaw.kr(0.01).range(100, 10000), 0, 0.1) });

//:x

w = Window.new.front;
w.alpha = 0.5;
w.view.background = Color.clear;


