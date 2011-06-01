Routine and Function:sched based control of synths in the Resource library.

Using, "in order of appearance":

SynthResource:rsync (and: rsynca, rsyncs). 

SynthResource:sched (and: scheda, scheds).

SynthResource:stream (and: streama, streams).

Chain: SEE HELP FOT THIS CLASS

Symbol:stream (EventStream)


=========

Basics: 

The methods rsync, sched and stream are for starting routine-like processes that operate on a synth, and that end when the synth ends. 

rsync -> create a routine from a function, pass that routine the synth of the SynthResource as argument. 

sched -> Schedule the function. See Function:sched. This means looping the function until it returns nil (or non-number). The return function of each call of the function is used as value in seconds for scheduling the next evaluation of the function. 

stream -> Like sched, except that the values for the scheduling are not written as part of the function, but are provided by an expression (a single value, a pattern or stream or a function) that is provided as a separate argument.


==========

//:rsync example: 

{ | freq = 400 | SinOsc.ar(freq, 0, 0.1) }.play
	.rsync({	| s | // here is the function that plays inside a routine					// the actual Synth instance is passed to the function as argument.
		loop {
			s.set(\freq, 400 rrand: 4000);
			0.1.wait;
		}
	})

//:With Udef and Symbol:playDef
\default.playDef
	.rsync({	| s | // here is the function that plays inside a routine					// the actual Synth instance is passed to the function as argument.
		loop {
			s.set(\freq, 400 rrand: 4000);
			0.1.wait;
		}
	})
//:----
\default.playDef
	.dur(5, 5)
	.scheda({	| s | // here is the function that plays inside a routine
		s.set(\freq, 400 rrand: 4000);	// no loop needed: sched is a loop in itself
		0.1;							// no wait needed: last value is wait value of loop
	}, 0.1);


