//:a
Spectrograph.big;

\default.play rsyncs: { loop { 0.25.rand.wait; \default.set(\freq, 100 + 10000.rand) } };

//:x
\default.play;
100 do: { 0.1.wait; \default.set(\freq, 400 + 14000.rand) };

//:y
Spectrograph.current;

//:z
Spectrograph.small;

