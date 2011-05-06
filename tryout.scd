Spectrograph.small; // in case needed ...
//:test sound 
\default.play({ SinOsc.ar(LFNoise0.ar(10).range(100, 10000), 0, 0.05) });

Spectrograph.start(nil, 
		Rect(Window.screenBounds.width - 600, 0, 600, 200),
		server: Server.default
);

//:checking

Group.new;
Server.default.queryAllNodes;

\default.play;
//:pen stuff
Spectrograph.current.penObjects;

Spectrograph.current;

