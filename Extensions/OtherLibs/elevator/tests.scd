// Simulation of the elevator paradox

// 2011 02 10. Incomplete 

/*
--- Algorithm notes ---

elevator = LFTri.kr(...).range(0, 1);
floor_level = 0.9;

Need to send 2 different triggers (using SendTrig): 
1. When the elevator has been observed coming up, send 1.
2. When the elevator has been observed going down, send -1. 

Therefore, use 2 SendTrig instances: 

1. SendTrig 1 is triggered when Schmidt.kr becomes 1, or with: elevator > floor_level
2. SendTrig 2 is triggered when Schmidt.kr becomes 0. or with: elevator < floor_level

Use Gate or SetResetFF with multiplication (*) to trigger activation of the SendTrigs with Dust.

--- UGens needed / explored: ---

1. Tested
LFTri
> 		(greater than operator)
SendTrig

2. To work in
Schmidt
½Dust
PulseCount

Following may not be needed: 
ToggleFF
Gate
SetResetFF


*/

Server.default = Server.internal;
Server.default.boot;

{ LFTri.kr(10) }.scope(zoom: 32);

OSCresponder(nil, 'tr', { | time, addr, msg | postf("went above bzw. below %\n", msg[2]); }).add;

(
{ 
	var elevator, isabove;
	elevator = LFTri.kr(2.0).range(0, 1);
	isabove = elevator > 0.9;
	SendTrig.kr(isabove, 1, 1);
	[elevator, isabove];
}.scope(zoom: 32);
)

(
{
	var elevator, isabove;
	elevator = LFTri.kr(2.0).range(0, 1);
	isabove = elevator > 0.9;
	Decay2.ar(PinkNoise.ar(Slope.kr(isabove) > 0), mul: 0.5);
}.play;
)

(
{
	var elevator, isabove, isbelow, floor_level = 0.9;
	elevator = LFTri.kr(1).range(0, 1);
	isabove = elevator > floor_level;
	isbelow = elevator < floor_level;
	SendTrig.kr(isabove, 1);
	SendTrig.kr(isbelow, -1);
	Decay2.ar(PinkNoise.ar(Slope.kr(isbelow)), mul: 0.001);
}.play;
)






