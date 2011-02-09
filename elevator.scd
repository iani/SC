/* Simulation of the elevator paradox
http://en.wikipedia.org/wiki/Elevator_paradox

2011 02 10. 

*/

// Simulation 1: Mouse x controls the level at which one is waiting for the elevator
(
OSCresponder(nil, 'tr', { | time, resp, msg | 
	postf("elevator going %\n", ["down", "up"][msg[2]]);
}).add;

{ 
	////////////////			 1. simulation
	var elevator, level, uptrig, downtrig, updowntrig, waittrig, env;
	elevator = LFTri.kr(1).range(0, 1);
	level = MouseX.kr(0, 1);
	uptrig = Slope.kr(elevator > level) > 0;
	downtrig = Slope.kr(elevator < level) > 0;
	updowntrig = uptrig + downtrig;
	waittrig = TDelay.kr(updowntrig, Demand.kr(updowntrig, 0, Dwhite(0.01, 0.99, inf)));
	
	////////////////			 2. sending data
	SendTrig.kr(waittrig, elevator < level, PulseCount.kr(waittrig));
	
	////////////////			 3. sonification 
	env = Env.perc(level: 0.1);
	Mix([
		SinOsc.ar(600, 0, EnvGen.kr(env, gate: uptrig, doneAction: 0)),
		SinOsc.ar(400, 0, EnvGen.kr(env, gate: downtrig, doneAction: 0)),
		SinOsc.ar(800 + (elevator < level * 400), 0, EnvGen.kr(env, gate: waittrig, doneAction: 0)),
		SinOsc.ar(8000, 0, EnvGen.kr(env, gate: updowntrig, timeScale: 0.1, doneAction: 0))
	]);
}.play
)

/* Simulation 2: Gather a statistic of entries to the elevator.
Count separatetly whether the elevator was entered going up or down.
The level at which one is waiting for the elevator is constant, and is defined by variable ~level;
*/
(
var level = 0.9;
~statistics = [(direction: 'down', count: 0), (direction: 'up', count: 0)];
OSCresponder(nil, 'tr', { | time, addr, msg | 
	var upOrDown;
	upOrDown = ~statistics[msg[2]];
	upOrDown[\count] = upOrDown[\count] + 1;
	~statistics.postln;
}).add;

{ 
	////////////////			 1. simulation
	var elevator, uptrig, downtrig, updowntrig, waittrig, env;

	elevator = LFTri.kr(1).range(0, 1);
	uptrig = Slope.kr(elevator > level) > 0;
	downtrig = Slope.kr(elevator < level) > 0;
	updowntrig = uptrig + downtrig;
	waittrig = TDelay.kr(updowntrig, Demand.kr(updowntrig, 0, Dwhite(0.01, 0.99, inf)));
	
	////////////////			 2. sending data
	SendTrig.kr(waittrig, elevator < level, PulseCount.kr(waittrig));
	
	////////////////			 3. sonification 
	env = Env.perc(level: 0.1);
	Mix([
		SinOsc.ar(600, 0, EnvGen.kr(env, gate: uptrig, doneAction: 0)),
		SinOsc.ar(400, 0, EnvGen.kr(env, gate: downtrig, doneAction: 0)),
		SinOsc.ar(800 + (elevator < level * 400), 0, EnvGen.kr(env, gate: waittrig, doneAction: 0)),
		SinOsc.ar(8000, 0, EnvGen.kr(env, gate: updowntrig, timeScale: 0.1, doneAction: 0))
	]);
}.play
)

// Since ~statistics is an environment variable, one can access it after the simulation has stopped: 
~statistics; 

///////////////////////////////////////////////////////////////////////////
          ////////////////////  Older tries /////////////////////
///////////////////////////////////////////////////////////////////////////

/*
--- Algorithm notes ---

=========== older notes: 
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
(
Server.default = Server.internal;
Server.default.boot;

~count = -1;
~statistics = [(total: 0, taken: 0), (total: 0, taken: 0)];

OSCresponder(nil, 'tr', { | time, addr, msg | 
	var upOrDown, count;
	#upOrDown, count = msg[2..];
	upOrDown = ~statistics[upOrDown];
	upOrDown.total = upOrDown.total + 1;
	if (upOrDown.taken < count) { upOrDown.taken = count };
	~statistics.postln;
}).add;
)

(
{
	var updown, takenup, takendown, waitTrig;
	updown = Impulse.kr(1);
	waitTrig = TDelay.kr(updown, /* 0.3 */ Demand.kr(updown, 0, Dwhite(0.01, 0.5)) /* */ );
	takenup = PulseCount.kr(waitTrig);
	SendTrig.kr(updown, 0, takenup);

	// sonification: 	
	Out.ar(0, [SinOsc.ar(400, 0, 0.1 * Decay2.kr(updown)), SinOsc.ar((60 + takenup).midicps, 0, 0.1 * Decay2.kr(waitTrig))]);
}.play;
);

(
{
	var updown, takenup, takendown, waitTrig;
	var up, down, elevator; 
	elevator = LFTri.kr(1).range(0, 1);
	up = elevator > 0.9;
	down = elevator < 0.9; 
//	updown = up + down;
//	updown = Impulse.kr(1);
	waitTrig = TDelay.kr(up, /* 0.3 */ Demand.kr(up, 0, Dwhite(0.01, 0.5)) /* */ );
	takenup = PulseCount.kr(waitTrig);
	SendTrig.kr(up, 0, takenup);
	[WhiteNoise.ar(Decay2.kr(up > 0))];
	// sonification: 	
//	Out.ar(0, [SinOsc.ar(400, 0, 0.1 * Decay2.kr(updown)), SinOsc.ar((60 + takenup).midicps, 0, 0.1 * Decay2.kr(waitTrig))]);
}.play;
);

{ 0.1 * WhiteNoise.ar(EnvGen.kr(Env.perc, gate: Impulse.kr(1), doneAction: 0)) }.play;

(
{ 
	var trig;
	trig = Impulse.kr(1);
	0.1 * WhiteNoise.ar(EnvGen.kr(Env.perc, gate: trig, doneAction: 0));
}.play
)

(
{ 
	var elevator, trig;
	elevator = LFTri.kr(1).range(0, 1);
	trig = elevator > 0.9;
	0.1 * WhiteNoise.ar(EnvGen.kr(Env.perc, gate: trig, doneAction: 0));
}.play
)

(
// Does not work! We have to use Slope to be able to add the two triggers in one.
{ 
	var elevator, uptrig, downtrig;
	elevator = LFTri.kr(1).range(0, 1);
	uptrig = elevator > 0.9;
	downtrig = elevator < 0.9;
	0.1 * WhiteNoise.ar(EnvGen.kr(Env.perc, gate: uptrig + downtrig, doneAction: 0));
}.play
)

(
{ 
	var elevator, uptrig, downtrig;
	elevator = LFTri.kr(1).range(0, 1);
	uptrig = Slope.kr(elevator > 0.9) > 0;
	downtrig = Slope.kr(elevator < 0.9) > 0;
	0.1 * WhiteNoise.ar(EnvGen.kr(Env.perc, gate: uptrig + downtrig, doneAction: 0));
}.play
)





(
{ Slope.ar(LFTri.ar(50).range(0, 1) > 0.9) * 0.1 }.loadToFloatArray(0.05, Server.default, { | a | 
	{ a.asCompileString.postln; }.defer
});
)

(
{ 1 < Slope.ar(LFTri.ar(100).range(0, 1) > 0.9) * 0.1 }.loadToFloatArray(0.05, Server.default, { | a | 
	{ a.asCompileString.postln; }.defer
});
)

(
{
	var updown, takenup, takendown, waitTrig;
	var up, down, elevator; 
	elevator = LFTri.kr(1).range(0, 1);
	up = Slope.kr(elevator > 0.9);
	down = Slope.kr(elevator < 0.9); 
	updown = up + down;
//	updown = Impulse.kr(1);
	waitTrig = TDelay.kr(updown, /* 0.3 */ Demand.kr(updown, 0, Dwhite(0.01, 0.5)) /* */ );
	takenup = PulseCount.kr(waitTrig);
	SendTrig.kr(updown, 0, takenup);
	[WhiteNoise.ar(Decay2.kr(up > 0))];
	// sonification: 	
//	Out.ar(0, [SinOsc.ar(400, 0, 0.1 * Decay2.kr(updown)), SinOsc.ar((60 + takenup).midicps, 0, 0.1 * Decay2.kr(waitTrig))]);
}.play;
);

===========================


{ LFTri.kr(10) }.scope(zoom: 32);

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
// This does not work. We need to introduce a TDelay with random Demand pattern to model the random waiting 
// before trying to take the elevator again, after the elevator has been boarded. 
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

(
// Here trying to introduce a TDelay with random Demand pattern to model the random waiting 
// before trying to take the elevator again, after the elevator has been boarded. 
{
	var elevator;
	var goesuptrig, goesdowntrig, goesupordowntrig, waittime;
	var soundup, sounddown;
	var iswaiting;
	elevator = LFTri.kr(0.2).range(0, 1);
	goesuptrig = Slope.kr(elevator > 0.9) > 0;
	goesdowntrig = Slope.kr(elevator < 0.9) > 0;
	goesupordowntrig = goesuptrig + goesdowntrig;
	waittime = Demand.kr(goesupordowntrig, 0, Dwhite(0.01, 0.5, inf));
	iswaiting = SetResetFF.kr(TDelay.kr(goesupordowntrig, waittime), goesupordowntrig);
	SendTrig.kr(goesuptrig, 1, 1);
	SendTrig.kr(goesdowntrig, 2, -1);
/*	SendTrig.kr(goesuptrig, 10, goesuptrig);
	SendTrig.kr(goesuptrig, 100, goesdowntrig);
	SendTrig.kr(goesdowntrig, 20, goesdowntrig);
	SendTrig.kr(goesdowntrig, 200, goesuptrig);
*/	SendTrig.kr(goesuptrig * iswaiting, 1000, goesuptrig);
	SendTrig.kr(goesdowntrig * iswaiting, 2000, goesdowntrig);
	soundup = SinOsc.ar(600, 0, Decay2.kr(goesuptrig, mul: 0.1));
	sounddown = SinOsc.ar(400, 0, Decay2.kr(goesdowntrig, mul: 0.1));
	Mix([soundup, sounddown, iswaiting * SinOsc.ar(500, 0, 0.1)]);
}.play
)












