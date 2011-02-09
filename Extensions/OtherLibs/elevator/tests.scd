/* Simulation of the elevator paradox
http://en.wikipedia.org/wiki/Elevator_paradox

2011 02 10. Incomplete 

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

OSCresponder(nil, 'tr', { | time, addr, msg | postf("went above bzw. below %\n", msg[2..3]); }).add;

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
	var elevator, isabove, isbelow, iswaiting, floor_level = 0.9;
	elevator = LFTri.kr(0.5).range(0, 1);
	isabove = elevator > floor_level;
	isbelow = elevator < floor_level;
	iswaiting = SetResetFF.kr(Impulse.kr(1), Slope.kr(isbelow));
	SendTrig.kr(isabove * iswaiting, 1, elevator);
	SendTrig.kr(isbelow * iswaiting, 2, elevator);
//	SendTrig.kr(isbelow + iswaiting, -1);
	Out.ar(0, SinOsc.ar(400, 0, 0.1) * iswaiting);
//	Decay2.ar(PinkNoise.ar(Slope.kr(isbelow)), mul: 0.001);
	[iswaiting, isabove, isbelow, isabove * iswaiting, isbelow * iswaiting];
}.play // .scope(zoom: 32);
)

(
{
	var goesup, goesdown;
	var soundup, sounddown;
	goesup = Impulse.kr(2);
	goesdown = Impulse.kr(3);
	soundup = SinOsc.ar(400, 0, Decay2.kr(goesup, mul: 0.1));
	sounddown = SinOsc.ar(800, 0, Decay2.kr(goesdown, mul: 0.1));
	[soundup, sounddown];
}.play
)

(
{
	var elevator;
	var goesup, goesdown;
	var soundup, sounddown;
	elevator = LFTri.kr(0.9).range(0, 1);
	goesup = Slope.kr(elevator > 0.9) > 0; // Impulse.kr(2);
	goesdown = Slope.kr(elevator < 0.9) > 0;
	SendTrig.kr(goesup, 1, 1);
	SendTrig.kr(goesdown, 2, -1);
	
	soundup = SinOsc.ar(800, 0, Decay2.kr(goesup, mul: 0.1));
	sounddown = SinOsc.ar(400, 0, Decay2.kr(goesdown, mul: 0.1));
	[soundup, sounddown];
}.play
)

(
{
	var elevator;
	var goesup, goesdown;
	var soundup, sounddown;
	var iswaiting;
	elevator = LFTri.kr(0.2).range(0, 1);
	goesup = Slope.kr(elevator > 0.9) > 0; // Impulse.kr(2);
	goesdown = Slope.kr(elevator < 0.9) > 0;
	iswaiting = SetResetFF.kr(Dust.kr(10), goesup + goesdown);
	SendTrig.kr(goesup, 1, 1);
	SendTrig.kr(goesdown, 2, -1);
	SendTrig.kr(goesup, 10, goesup);
	SendTrig.kr(goesup, 100, goesdown);
	SendTrig.kr(goesdown, 20, goesdown);
	SendTrig.kr(goesdown, 200, goesup);
	SendTrig.kr(goesup * iswaiting, 1000, goesup);
	SendTrig.kr(goesdown * iswaiting, 2000, goesdown);
	soundup = SinOsc.ar(600, 0, Decay2.kr(goesup, mul: 0.1));
	sounddown = SinOsc.ar(400, 0, Decay2.kr(goesdown, mul: 0.1));
	Mix([soundup, sounddown, iswaiting * SinOsc.ar(500, 0, 0.1)]);
}.play
)












