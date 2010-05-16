/* iz Saturday; October 25, 2008: 3:27 PM
Utilities for playing Metro. Repeat some function n number of times or iterate over a collection at time intervals determined by dt.

Examples:

10 metro: { | n | postf("% seconds have elapsed\n", n) };

// to repeat forever, use aFunction.metro: 
m = { | n | n.postln; } metro: 0.1;
{ m.stop }.defer(3);		// but you can stop the Metro it any time...

// or you can give the number of repeats as argument: 
(
{ | n |
	{ GUI.window.new(n.asString, Rect(500.rand, 500.rand, 100, 100)).front }.defer;
} metro: 5;
)

{ | n | { SinOsc.ar(n+1 * 300, 0, 0.2 * EnvGen.kr(Env.perc, doneAction: 2)) }.play } metro: 10;

10 metro: { | n | { SinOsc.ar(n+1 * 300, 0, 0.2 * EnvGen.kr(Env.perc, doneAction: 2)) }.play };

20.metro({ | n | { SinOsc.ar(n+1 * 300, 0, 0.2 * EnvGen.kr(Env.perc, doneAction: 2)) }.play }, 0.1);

(10..1) metro: { | l | postf("Countdown %!\n", l) }

m = Pwhite(0, 0.5, 100).metro({ | n | postf("some noise!!! %\n", n) }, 0.05);

*/

+ Pattern {
	metro { | action, dt = 1, startNow = true |
		var metro;
		metro = Metro(action, dt, this);
		if (startNow) { metro.start };
		^metro;
	}
}

+ SequenceableCollection {
	metro { | action, dt = 1, startNow = true |
		var metro;
		^Pseq(this).metro(action, dt, startNow);
	}	
}

+ Integer {
	metro { | action, dt = 1, startNow = true |
		var metro;
		^Pseries(0, 1, this).metro(action, dt, startNow);
	}
}
+ Function {
	metro { | repeats = inf, dt = 1, startNow = true |
		var metro;
		^Pseries(0, 1, repeats).metro(this, dt, startNow);
	}
}



/*
+ Collection {
	metro { | action, dt = 1, startNow = true |
		var metro;
		metro = Metro(action, dt, Pseq(this));
		if (startNow) { metro.start };
		^metro;
	}	
}

+ Integer {
	metro { | action, dt = 1, startNow = true |
		var metro;
		metro = Metro(action, dt, Pcount(this));
		if (startNow) { metro.start };
		^metro;
	}
}
*/