//:---
ServerReady(Server.default).addListener(\test, \started, { "started".postln; });



ServerReady(Server.default).addAction({ \default.play });
ServerReady(Server.default).addObjectAction(\test, { { \default.play }.defer(1) });

Spectrograph.start;

ServerReady(Server.default);

NotificationCenter.registrations.atPath([ServerReady(Server.default)]);

NetAddr.localAddr.sendMsg('/serverReady');

\default.release;

\test2.objectClosed;

//:---
Server.default.boot;
//:---



Server.default = Server.internal;

Server.internal.boot;
Stethoscope(Server.internal).window;

\default.play;


Spectrograph.small;

Server.default.queryAllNodes;

Server.default.serverRunning;

