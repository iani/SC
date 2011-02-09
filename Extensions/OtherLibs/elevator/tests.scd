// Simulation of the elevator paradox

// 2011 02 10. Incomplete 

Server.default = Server.internal;
Server.default.boot;

{ LFTri.kr(10) }.scope(zoom: 32);

OSCresponder(nil, 'tr', { "went above".postln; }).add;

(
{ 
	var elevator, isabove;
	elevator = LFSaw.kr(2.0).range(0, 1);
	isabove = elevator > 0.9;
	SendTrig.kr(isabove, 1, 1);
	[elevator, isabove];
}.scope(zoom: 32);
)

