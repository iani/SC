Chain

Inherits from: Object
			
Purpose

Play a pattern producing fuctions whose values are objects that respond to onEnd (SynthResources, EventStreams), by chaining each next element so as to start playing when the previous element ends. 

Chain is a tool for writing algorithms that execute actions at determined points in time. These actions may include creating Synths or the sending of OSC messages to external applications as well as drawing graphics in the same generative process that makes the sound in sync with the sound events. 

Usage

Starting a chain

	*new(pattern, envir) : Create a new instance of Chain and start it
		pattern: A pattern producing functions that when evaluated create objects that respond to onEnd.
		envir: an Environment or Event.
		The functions are evaluated using envir as environment. Default is an empty event. 

A Chain instance "plays" the functions produced by a pattern by evaluating each next function produced by the pattern a certain time interval after the previous one. The time interval is determined by the function itself. In the simplest case, it is the duration of the synth produced by the function.

//:Repeat a Synth-making function 10 times 
(
Chain(
	Pn({ { SinOsc.ar(400 + 400.rand.dup, 0, 0.1) }.play.dur(0.1, 0.1) }, 10) 
);
)
//: End of example

Note that the Chain plays each new element from the Pattern as soon as the previous one has finished. So the timing changes as the duration of each element changes:
 
//:Each next synth starts exactly at the end of the previous one 
(
Chain(
	Pn({ { SinOsc.ar(400 + 400.rand.dup, 0, 0.1) }.play.dur([0.1, 0.2, 0.4].choose, 0.1) }, 10) 
);
)
//: End of example

Stopping a chain

	stop : Stop a playing Chain instance
	
//:Start and stop a chain 
(
{
	var chain;
	chain = Chain(	// Pn repeats infinitely per default
		Pn({ { SinOsc.ar(400 + 400.rand.dup, 0, 0.1) }.play.dur([0.05, 0.01].choose, 0.05) }) 
	);
	rrand(2.0, 3).wait;
	chain.stop;
}.fork
)

//:Start and stop a chain - shorter coding using implicit routine 
// Run this with Command-shift-x or Command-shift-alt-x
var chain;
chain = Chain(
	Pn({ { SinOsc.ar(400 + 400.rand.dup, 0, 0.1) }.play.dur([0.05, 0.01].choose, 0.05) }) 
);
rrand(2.0, 3).wait;
chain.stop;

//: End of examples

Storing a chain as Resource

One can simplify the code even further by storing a chain as a Resource which is accessibe through messages to a symbol. 
To do this, one sends the symbol the message 'chain': 

//:Start and stop a chain as Resource (run with Command-shift-x)
\simple.chain(Pn({ { SinOsc.ar(400 + 400.rand.dup, 0, 0.1) }.play.dur(0.01, 0.05) }) );
1.5.wait;
\simple.chain.stop;
//: End of example

Playing SynthDefs by name instead of Functions 

To create synths from a previously defined SynthDef, send the message play to the symbol corresponding to the name of the SynthDef. The arguments to method play are the same as those for Synth:play minus the name of the SynthDef, which is provided by the Symbol. (For more details, see class SynthResource).

Chain(Pn({ \default.play([\freq, 400 + 400.rand]).dur(0.02, 0.05) }, 25) );

Using Patterns as data generators in a Chain

Chain evaluates all its functions inside an environment which can be provided in the second argument to the new method. Any environment variables that are set in this environment thus become available to all the functions played by the Chain. In this way, streams created from patterns, or other data and data generating objects can be passed to the functions of the chain. 

//:Passing a frequency pattern to the functions in a Chain
Chain(
	Pn({ { SinOsc.ar(~freq.next, 0, 0.1.dup) }.play.dur(0.01, 0.05) }, 25),
	(freq: Pseq([60, 65, 63, 67, 70, 72].midicps, inf).asStream)
);
//: End of example

Using messages to symbol as stream generators

This library defines several methods in Symbol as shortcuts for storing streams created from patterns in an environment. The most general of these is the method stream: 

	aSymbol.stream(pattern) : Create a stream from pattern and store it in the environment variable aSymbol 

Using this, the above example can be written as follows: 

//:Example of Symbol:stream
Chain(
	Pn({ { SinOsc.ar(~freq.next, 0, 0.1.dup) }.play.dur(0.01, 0.05) }, 25),
	() make: { \freq.stream(Pseq([60, 65, 63, 67, 70, 72].midicps, inf)) }
);
//: ---

The code above is longer than in the example before, but it can be shortened by using pseq, one of the specialized methods for binding pattern streams to symbols: 

//:Example of Symbol:pseq
Chain(
	Pn({ { SinOsc.ar(~freq.next, 0, 0.1.dup) }.play.dur(0.01, 0.05) }, 25),
	() make: { \freq.pseq([60, 65, 63, 67, 70, 72].midicps, inf) }
);
//: ---

One may access the stream-storing objects in the environment by sending them the messages 'this' to obtain the current value and 'n' to obtain the next value. Thus the above example becomes: 

//:Accessing a stream's next value in the environment via Symbol:n
Chain(
	Pn({ { SinOsc.ar(\freq.n, 0, 0.1.dup) }.play.dur(0.01, 0.05) }, 25),
	() make: { \freq.stream(Pseq([60, 65, 63, 67, 70, 72].midicps, inf)) }
);
//: ---

Here is an example of accessing the current value and the next value:

//:Accessing a stream's current value in the environment via Symbol:this
Chain(	// call freq.n to initialize the value of the stream when the chain starts
	Pn({ \freq.n; { SinOsc.ar([\freq.this * 2, \freq.n], 0, 0.1) }.play.dur(0.01, 0.2) }, 25),
	() make: { \freq.stream(Pseq([60, 65, 63, 67, 70, 72].midicps, inf)) }
);
//: ---

The convenience of the expression () make: { ... } becomes more evident when defining several patterns for the Chain:

//:A Chain with several streams in its environment
Chain(
	Pn({ { SinOsc.ar([\freq1.n, \freq2.n], 0, 0.1) }.play.dur(\dur.n, 0.05) }, 100),
	() make: {
		\freq1.pseq([60, 65, 63, 67, 70, 72].midicps, inf);
		\freq2.pseq([80, 87, 80, 82, 87, 80, 90].midicps, inf);
		\dur.pseq([1, 1, 2, 4] / 40, inf);
	}
);

//: ---

Here is an example of 3 different synth functions sharing data from the Chain environment

//:3 different synth functions sharing patterns. 
Chain(Pseq([
	{ \default.play([\amp, 0.05, \freq, ~freq.next]).dur(~dur2.next, ~fade.next); },
	{ { Resonz.ar(WhiteNoise.ar(2.5), \freq.n.dup, 0.01) }.play.dur(\dur2.n, \fade.n); },
	{ { SinOsc.ar(\freq.n.dup / 2, 0, 0.07) }.play.dur(\dur2.n, \fade.n); },
], 20),	
() make: {	// store shared patterns in the global environment of the Chain:
	\dur2.pseq([0.1, 0.2], inf);
	\fade.pseq([0.1, 0.2, 1], inf); 
	\freq.pseq([80, 85, 87, 90, 92].midicps, inf) 
});
//: ---


Chaining functions with the chain message

The functions of the Chain do not have to return synths. They can contain any actions. 
To make a function which does not return a synth playable by the Chain, send it the message chain: 

//:Including a non-sound function in a chain
Chain(Pseq([
	{ 	postf("counting start of sequence repetition: %\n", \count.n) }.chain,
	{	// Play a single synth.
		\default.play([\freq, 500 + 1500.rand]).dur(0.1, 0.1)
	},	// The chain continues as soon as the synth finishes playing
	{	// Play another synth.
		{ SinOsc.ar(400 + 400.rand, 0, 0.1) }.play.dur(0.1, 0.1)
	}	// The chain continues as soon as the synth finishes playing
	],
	5	// repeat 5 times
), () make: { \count.pseries(1) }
);
//: ---

If a function needs to wait for an interval before proceeding to the next one,  then this is indicated in the first argument of chain, like this: 

//:Make a function wait for a given time interval
Chain(Pseq([
	{ "waiting for 1 second".postln }.chain(1.once),	// wait 1 second once
	{ \default.play([\freq, 500 + 1500.rand]).dur(0.1, 0.1) },
	{ { SinOsc.ar(400 + 400.rand, 0, 0.1) }.play.dur(0.1, 0.1) }
], 5
));
//: ---

One can pass a pattern as duration argument to Function:chain. In this case, the function will repeat its evaluation with each next duration provided by the patterns stream, until the stream ends.
 
//:Passing a pattern as duration argument to Function-chain

Chain(Pseq([	// play the first function 3 times, with an interval of 0.5 seconds:
	{ postf("waiting % seconds, counting: %\n", ~dur.value, \count.pseries(1).next) }.chain(Pn(0.5, 3)),	
	{ { SinOsc.ar(400 + 400.rand, 0, 0.1) }.play.dur(0.1, 0.1) }
], 5
));

//: --

Functions that are played with the chain message can execute any statements, including the creation of synths. Therefore, one can use Function:chain together with a pattern as duration argument to play a sequence of sounds with the same function. The duration argument can also be a function returning the duration. In this case, the function is evaluated each time that the function is sent the message chain. This means that the funtion can be played with a different pattern each time that it is encountered in the sequence of functions played by Chain. In the example below, this is used to play event sequences of different lengths each time. 

//:Example combining a single synth and a chain of synths.
Chain(Prand([ // choose from the following at random:
	{	// Play a series of events
		\default.mplay([\freq, (50..80).choose.midicps]).dur(0.03, exprand(0.01, 0.3));
		// The number and timing of the events is defined through arguments to the chain message
	}.chain({ Prand([0.06, 0.07, 0.14], 10 rrand: 20) }),
	{	// Play a single synth.
		{ | freq = 400 | SinOsc.ar(freq * [1, 1.2], 0, 0.02) }
			.play(args: [\freq,  \freq.pseries(4).next * 100])
			.dur(0.1 rrand: 1, 0.5 rrand: 2.5) 
	}
], 30
));
//: ---

The next example shows the difference in a more striking manner.  

//:Variation: Insert some staccato chord/clusters, and 30-note fast passages
Chain(Prand([
	{
		\default.mplay([\freq, (50..80).choose.midicps]).dur(0.03, exprand(0.01, 0.3));

	}.chain({ Prand([	// choose at random between chord, fast passage and rhythm
			Prand([0.06, 0.07, 0.14] * 1.5, 10 rrand: 20),
			Pn(0.0, 5 + 5.rand),
			Pn(0.05, 30)
		], 1)
	}),
	{	{ | freq = 400 | SinOsc.ar(freq * [1, 1.2], 0, 0.02) }
		.play(args: [\freq,  \freq.pseries(4).next * 100])
		.dur(0.1 rrand: 1, 0.5 rrand: 2.5) 
	}
	],
	30
));

//: Another chain example
Chain(Prand([
	{
		{ 
			Klang.ar(`[ Array.rand(3, 100.0, 125.0) * \inc.prand((5..7), inf).next]).dup * 0.02 
		}.play.dur(0.1, 0.3);
	}.chain(Pn(0.1, 5)),
	{	{ | freq = 400 | SinOsc.ar(freq * [1, 1.1], 0, 0.1) }
		.play(args: [\freq,  \freq.pseries(4).next * 100])
		.dur(0.01 rrand: 0.1, 0.05 rrand: 0.5) 
	}
	],
	30
));


//:Variant: Introduce occasional clusters and change the range of the Klang synth
Chain(
	Pseq([
		/* 	Restart the frequency series at each repeat */
 		{ \freq.pseries(4).reset.toParent; }.chain,
		Pseq([
			{
				{
					Klang.ar(`[ Array.rand(3, 100.0, 125.0) * 
						\inc.stream(Prand([
							Prand((1..5), inf),
							Prand((7..12), inf),
							Prand((12..20), inf)
						], inf)).next
					]).dup * 0.03 
				}.play.dur(0.1, 0.3);
			}.chain({ Prand([Pn(exprand(0.1, 0.5), 5), Pn(0, 5)]) }),
			{	{ | freq = 400 | Mix(SinOsc.ar(freq * (1, 1.05 + 0.2.rand .. 2), 0, 0.02)).dup }
				.play(args: [\freq,  ~freq.next * 100])
				.dur(0.01 rrand: 0.1, 0.05 rrand: 0.5) 
			}]
			, 50 // can change this
		)
	], inf)
);

//: End second chain example

Using chain to send sequences of OSC messages. 

//:Third chain example
// Activate OSC input posting from the Utils menu of this library
// To see post the OSC messages sent here
Chain(Prand([
	{
		NetAddr.localAddr.sendMsg(*[\test1, \count.pseries(101).next, ~globalcount.next].postln);
		\default.mplay([\freq, 30.rrand(100).midicps]).dur(0.1, 0.1);		
	}.chain(Pn(0.1, 5));,
	{
		NetAddr.localAddr.sendMsg(*[\test2, \count.pseries(1).next * 10, ~globalcount.next].postln);
	}.chain(Pn(1, 3));
	],
	30
), (globalcount: Pseries(1).asStream)
);

//:Chaining single Synth generating functions with multiple synth ones. 
Chain(Pseq([
		// Single synth. When it ends, the next link in the chain starts:
	{ \default.play([\amp, 0.05, \freq, ~freq.next]).dur(~dur2.next, ~fade.next); },
	{	// Multiple synth (a chain of 10 to 20 synths):
		\default.mplay([\freq, (50..80).choose.midicps]).dur(0.03, exprand(0.01, 0.3));
	}.chain({ Prand([0.06, 0.07, 0.14], 10 rrand: 20) }),
	{ \default.play([\freq, ~freq.next]).dur(~dur2.next, ~fade.next); },
	{ { Resonz.ar(WhiteNoise.ar(2.5), \freq.n.dup, 0.01) }.play.dur(\dur2.n, \fade.n); },
	{ { SinOsc.ar(\freq.n.dup / 2, 0, 0.07) }.play.dur(\dur2.n, \fade.n); },
], 20),	
() make: {	// store shared patterns in the global environment of the Chain:
	\dur2.pseq([0.1, 0.2], inf);
	\fade.pseq([0.1, 0.2, 1], inf); 
	\freq.pseq([80, 85, 87, 90, 92].midicps, inf) 
});


//:Getting crazier
Chain(Pseq([
	{ \default.mplay([\amp, 0.02, \freq, ~freq.next]).dur(~dur2.next, ~fade.next); }.chain,
	{ \default.mplay([\amp, 0.02, \freq, ~freq.next]).dur(~dur2.next, ~fade.next); }.chain,
	{
		\default.mplay([\freq, (50..80).choose.midicps]).dur(0.03, exprand(0.01, 0.3));
	}.chain({ Prand([0.06, 0.07, 0.14], 10 rrand: 20) }),
	{ \default.mplay([\freq, ~freq.next]).dur(~dur2.next, ~fade.next); },
	{ { Resonz.ar(WhiteNoise.ar(2.5).dup, \freq.n, 0.01) }.play.dur(\dur2.n, \fade.n); },
	{ { | freq = 400 | SinOsc.ar(freq.dup, 0, 0.1) }.play(args: [\freq, \freq.n]).dur(\dur2.n, \fade.n); },
], 20),	
() make: {
	\dur2.pseq([0.1, 0.2], inf);
	\fade.pseq([0.1, 0.2, 1], inf); 
	\freq.pseq([80, 85, 87, 90, 92].midicps, inf) 
});

//: 


Playing chains in parallel

//:Playing chains in parallel

Chain(Pseq([
		// Single synth. When it ends, the next link in the chain starts:
	{ \default.mplay([\amp, 0.01, \freq, ~freq.next]).dur(~dur2.next, ~fade.next); },
	{ { Resonz.ar(WhiteNoise.ar(2.5).dup, \freq.n, 0.01) }.play.dur(\dur2.n, \fade.n); },
	{ { SinOsc.ar(\freq.n.dup, 0, 0.02) }.play.dur(\dur2.n, \fade.n); },
], 20),	
() make: {	// store shared patterns in the global environment of the Chain:
	\dur2.pseq([0.1, 0.2], inf);
	\fade.pseq([0.1, 0.2, 1], inf); 
	\freq.pseq([80, 85, 87, 90, 92].midicps, inf) 
});
0.2.wait;
Chain(Pseq([
		// Single synth. When it ends, the next link in the chain starts:
	{ \default.mplay([\amp, 0.01, \freq, ~freq.next]).dur(~dur2.next, ~fade.next); },
	{ { Resonz.ar(WhiteNoise.ar(2.5).dup, \freq.n, 0.01) }.play.dur(\dur2.n, \fade.n); },
	{ { SinOsc.ar(\freq.n.dup, 0, 0.02) }.play.dur(\dur2.n, \fade.n); },
], 20),	
() make: {	// store shared patterns in the global environment of the Chain:
	\dur2.pseq([0.1, 0.2], inf);
	\fade.pseq([0.1, 0.2, 1], inf); 
	\freq.pseq([80, 85, 87, 90, 92].midicps * 1.25, inf) 
});

//: ---


Sending the chains out of phase by lengthening the links duration

//:Sending the chains out of phase by lengthening the links

Chain(Pseq([
		// Single synth. When it ends, the next link in the chain starts:
	{ \default.mplay([\amp, 0.01, \freq, ~freq.next]).dur(~dur2.next, ~fade.next); },
	{ { Resonz.ar(WhiteNoise.ar(2.0).dup, \freq.n, 0.01) }.play.dur(\dur2.n, \fade.n + 0.01); },
	{ { SinOsc.ar(\freq.n.dup, 0, 0.02) }.play.dur(\dur2.n, \fade.n); },
], 20),	
() make: {	// store shared patterns in the global environment of the Chain:
	\dur2.pseq([0.1, 0.2], inf);
	\fade.pseq([0.1, 0.2, 1], inf); 
	\freq.pseq([80, 85, 87, 90, 92].midicps, inf) 
});
0.2.wait;
Chain(Pseq([
		// Single synth. When it ends, the next link in the chain starts:
	{ \default.mplay([\amp, 0.01, \freq, ~freq.next]).dur(~dur2.next, ~fade.next); },
	{ { Resonz.ar(WhiteNoise.ar(2.0).dup, \freq.n, 0.01) }.play.dur(\dur2.n, \fade.n); },
	{ { SinOsc.ar(\freq.n.dup, 0, 0.02) }.play.dur(\dur2.n, \fade.n); },
], 20),	
() make: {	// store shared patterns in the global environment of the Chain:
	\dur2.pseq([0.1, 0.2], inf);
	\fade.pseq([0.1, 0.2, 1], inf); 
	\freq.pseq([80, 85, 87, 90, 92].midicps * 1.25, inf) 
});

//:The above example with the Routine explicitly added

{
	Chain(Pseq([
			// Single synth. When it ends, the next link in the chain starts:
		{ \default.mplay([\amp, 0.01, \freq, ~freq.next]).dur(~dur2.next, ~fade.next); },
		{ { Resonz.ar(WhiteNoise.ar(2.0).dup, \freq.n, 0.01) }.play.dur(\dur2.n, \fade.n + 0.01); },
		{ { SinOsc.ar(\freq.n.dup, 0, 0.02) }.play.dur(\dur2.n, \fade.n); },
	], 20),	
	() make: {	// store shared patterns in the global environment of the Chain:
		\dur2.pseq([0.1, 0.2], inf);
		\fade.pseq([0.1, 0.2, 1], inf); 
		\freq.pseq([80, 85, 87, 90, 92].midicps, inf) 
	});
	0.2.wait;
	Chain(Pseq([
			// Single synth. When it ends, the next link in the chain starts:
		{ \default.mplay([\amp, 0.01, \freq, ~freq.next]).dur(~dur2.next, ~fade.next); },
		{ { Resonz.ar(WhiteNoise.ar(2.0).dup, \freq.n, 0.01) }.play.dur(\dur2.n, \fade.n); },
		{ { SinOsc.ar(\freq.n.dup, 0, 0.02) }.play.dur(\dur2.n, \fade.n); },
	], 20),	
	() make: {	// store shared patterns in the global environment of the Chain:
		\dur2.pseq([0.1, 0.2], inf);
		\fade.pseq([0.1, 0.2, 1], inf); 
		\freq.pseq([80, 85, 87, 90, 92].midicps * 1.25, inf) 
	});
}.fork
//: ----

Related Classes

See also: EventStream, SynthResource, ChainLink, SynthLink

Instance Creation

	*new(pattern, envir) : Create a new instance of Chain and start it
		pattern: A pattern producing functions that when evaluated create objects that respond to onEnd.
		envir: an Environment or Event.
		The functions are evaluated using envir as environment. Default is an empty event. 

Class variables

	-- 

Instance variables

	currentFunc : The function that created the currently playing element in the stream chain

	link : The currently playing element in the stream chain. 
		To stop, the Chain sends this link the message stopLink. 

	envir : The 

	pattern : 

	stream

Class methods

	new

Instance methods

	current : 

	envir : 

	isRunning : 

	next : 

	pattern : 

	pattern_ : 

	reset : 

	start : 

	stop : 	Stop the chain from playing, by stopping its currently playing link.
		The chain sends the message stopLink to the current link.
		Any running synths do not free immediately, but run until their currently scheduled duration.
		
	free : 	Stop the chain from playing and free the synth stored in instance variable link.
	
		Currently does not support ChainLink

//:Free the synth of a chain when stopping
\simple.chain(Pn({ { SinOsc.ar(400 + 400.rand.dup, 0, 0.1) }.play.dur(0.01, 0.1) }));
2.wait;
\simple.chain.free;
//: End of Example
		
	release(fadeout) :  Stop the chain from playing and release the synth stored in instance variable link,
			using fadeout as release time. Default value of fadeout is 0.2.
			
		Currently does not support SynthLink and Chainlink.
		Only works if the current synth has not already been released by 'dur'!
		
//:Release the synth of a chain when stopping
\simple.chain(Pn({ { SinOsc.ar(400 + 400.rand.dup, 0, 0.1) }.play.dur(0.1, 0.5) }));
2.wait;
\simple.chain.release(3);
//: End of Example

	stream

Examples


//:An old electronic game and one-armed bandit gone crazy
Chain(Pseq([
	{ postf("sequence repeat no: %, random increment: %\n", \count.enext, \i.enext); }.chain,
	{
		\default.mplay([\freq, (50..80).choose.midicps]).dur(0.03, exprand(0.01, 0.3));
	}.chain({ Prand([0.06, 0.07, 0.14], 3 rrand: 15) }),
	{
		\default.mplay([\freq, [60, 62, 64, 65, 70, 72].choose.midicps])
			.dur(0.03, exprand(0.01, 0.3));
	}.chain({ Pn(0.25, 8.rand) }),
	{	postf("10 counts: %\n", \count2.pseries(1).next);
		\default.mplay([\amp, 0.3, \freq, (\i.this + \f.prand((0..2)).next).midicps])
			.dur(0.03, exprand(0.01, 0.03));
	}.chain(Pn(0.05, 10)),
	{	\default.mplay([\amp, 0.3, \freq, (\i.this + \f.pser([0, 4, 7]).next).midicps])
			.dur(0.03, exprand(0.01, 0.03));
	}.chain({ Pn(0.05, 25.rand) }),
	// Here is where it meditates and tries to transcend reality
	{	{ | freq = 400 | SinOsc.ar(freq * [1, 1.2], 0, 0.2) }
		.play(args: [\freq,  \freq.pseq((4..20), inf).next * 100])
		.dur(1 rrand: 2.5, 1.25 rrand: 3.5) 
	}
	],
	inf
), () make: { \count.pseries(1); \i.pwhite(40, 100) }
);

//:Substituting a new pattern in a stream while the chain is playing

Chain(
	Pseq([
		Pn({ { SinOsc.ar(~freq.next, 0, 0.1.dup) }.play.dur(0.01, 0.05) }, 25),
		{ \freq.replaceStream(Pseq(Array.rand(3 rrand: 10, 50, 100).midicps, inf)); }.chain,
		Pn({ \default.play([\freq, ~freq.next]).dur(0.01, 0.05) }, 25),
	], inf),
	() make: { \freq.stream(Pseq([60, 65, 63, 67, 70, 72].midicps, inf)) }
);

//:The above with a Udef instead of playing a function. 

Udef('ping', { | freq = 400 | Out.ar(0, Fade.kr * SinOsc.ar(freq * [1, 1.01], 0, 0.1)); });

Chain(
	Pseq([
		Pn({ \ping.mplay([\freq, ~freq.next]).dur(0.02, 0.07) }, 25),
		{ \freq.replaceStream(Pseq(Array.rand(3 rrand: 10, 50, 100).midicps, inf)); }.chain,
		Pn({ \default.mplay([\freq, ~freq.next]).dur(0.02, 0.07) }, 25),
	], inf),
	() make: { \freq.stream(Pseq([60, 65, 63, 67, 70, 72].midicps, inf)) }
);

//:Varying the lengths of the patterns of events in the chains 

Udef('ping', { | freq = 400 | Out.ar(0, Fade.kr * SinOsc.ar(freq * [1, 1.01], 0, 0.1)); });

Chain(
	Pseq([
		Plazy({ Pn({ \ping.mplay([\freq, ~freq.next]).dur(0.02, 0.07) }, 1 + 25.rand) }),
		{ \freq.replaceStream(Pseq(Array.rand(3 rrand: 10, 50, 100).midicps, inf)); }.chain,
		Plazy({ Pn({ \default.mplay([\freq, ~freq.next]).dur(0.02, 0.07) }, 1 + 25.rand) })
	], inf),
	() make: { \freq.stream(Pseq([60, 65, 63, 67, 70, 72].midicps, inf)) }
);

//:Letting both Pn patterns have the same length

Udef('ping', { | freq = 400 | Out.ar(0, Fade.kr * SinOsc.ar(freq * [1, 1.01], 0, 0.1)); });

Chain(
	Pseq([
		Plazy({ Pn({ \default.mplay([\freq, ~freq.next / 2]).dur(0.02, 0.07) }, ~numnotes.value) }),
		Plazy({ Pn({ \ping.mplay([\freq, ~freq.next * 2]).dur(0.02, 0.07) }, ~numnotes.value) }),
		{ \freq.replaceStream(Pseq(Array.rand(~numnotes.next, 70, 82).midicps, inf)); }
			.chain(0.125.once),		// leave some silence before the next repeat
	], inf),
	() make: {
		\numnotes.pser((1..16), inf);
		\freq.stream(Pseq([60, 65, 63, 67, 70, 72].midicps, inf));
	}
);

//:Also mirroring the rhythm of the patterns

Udef('ping', { | freq = 400 | Out.ar(0, Fade.kr * SinOsc.ar(freq * [1, 1.01], 0, 0.1)); });

Chain(
	Pseq([
		Plazy({ Pn({ \default.mplay([\freq, ~freq.next / 2]).dur(0.02, ~d.next) },
			~numnotes.value) }),
		Plazy({ Pn({ \ping.mplay([\freq, ~freq.next * 2]).dur(0.02, ~d.next) },
			~numnotes.value) }),
		{
			\freq.replaceStream(Pseq(Array.rand(~numnotes.next, 70, 82).midicps, inf)); 
			\d.replaceStream(Pseq({ [0.035, 0.07, 0.14, 0.28].choose } ! ~numnotes.value, inf)); 
		}.chain(0.125.once),		// leave some silence before the next repeat
	], inf),
	() make: {
		\numnotes.pser((1..16), inf);
		\freq.stream(Pseq([60, 65, 63, 67, 70, 72].midicps, inf));
		\d.pseq([0.07], inf);
	}
);

//:Using wchoose to bias short values for the rhythm

Udef('ping', { | freq = 400 | Out.ar(0, Fade.kr * SinOsc.ar(freq * [1, 1.01], 0, 0.1)); });

Chain(
	Pseq([
		Plazy({ Pn({ \default.mplay([\freq, ~freq.next / 2]).dur(0.02, ~d.next) },
			~numnotes.value) }),	// diminish duration range in the repetition:
		Plazy({ Pn({ \ping.mplay([\freq, ~freq.next * 2]).dur(0.02, (~d.next + 1).log10 ** 0.25) },
			~numnotes.value) }),
		{
			\freq.replaceStream(Pseq(
				[0.5, 0.75, 1, 1.25, 2].choose * Array.rand(~numnotes.next, 70, 82).midicps, inf
			)); 
			\d.replaceStream(Pseq(
				{ [0.035, 0.07, 0.14, 0.28].wchoose([0.5, 0.125, 0.125, 0.25]) } ! ~numnotes.value
				* [1, 1.25, 1.5, 2, 4].choose, 
				inf)
			); 	// leave some silence before the next repeat
		}.chain(Pfuncn({ [0.125, 0.25, 0.5, 1, 2].choose }, 1)),
	], inf),
	() make: {
		\numnotes.pser((1..16), inf);
		\freq.stream(Pseq([60, 65, 63, 67, 70, 72].midicps, inf));
		\d.pseq([0.07], inf);
	}
);

//:Using a non-chromatic pitch set

Udef('ping', { | freq = 400 | Out.ar(0, Fade.kr * SinOsc.ar(freq * [1, 1.01], 0, 0.1)); });

Chain(
	Pseq([
		Plazy({ Pn({ \default.mplay([\freq, ~freq.next / 2]).dur(0.02, ~d.next) },
			~numnotes.value) }),
		Plazy({ Pn({ \ping.mplay([\freq, ~freq.next * 2]).dur(0.02, ~d.next) },
			~numnotes.value) }),
		{
			\freq.replaceStream(
				Pseq(({
					([63, 65, 70, 72, 73, 75, 78, 79, 80]) 
						.wchoose([1, 2, 3, 4, 5, 4, 3, 2, 1].normalizeSum); 
				} ! ~numnotes.next + [-12, -7, -5, 0, 2, 5].choose).midicps, inf)); 
			\d.replaceStream(Pseq(
				{ [0.035, 0.07, 0.14, 0.28].wchoose([0.5, 0.125, 0.125, 0.25]) } ! ~numnotes.value
				* [1, 1.25, 1.5, 2, 4].choose, 
				inf)
			); 		// leave some silence before the next repeat:
		}.chain(Pfuncn({ [0.125, 0.25, 0.5, 1, 2].choose }, 1)),
	], inf),
	() make: {
		\numnotes.pser((1..16), inf);
		\freq.stream(Pseq([60, 65, 63, 67, 70, 72].midicps, inf));
		\d.pseq([0.07], inf);
	}
);

//:Note: When starting many chains together, use mplay instead of play
//Generally: Synths will not be started twice if a SynthResource is already bound to a symbol.

Udef('ping', { | freq = 400 | Out.ar(0, Fade.kr * SinOsc.ar(freq * [1, 1.01], 0, 0.1)); });

Chain(
	Pseq([
		Pn({ \ping.mplay([\freq, ~freq.next]).dur(0.02, 0.07) }, 25),
		{ \freq.replaceStream(Pseq(Array.rand(3 rrand: 10, 50, 100).midicps, inf)); }.chain,
		Pn({ \default.mplay([\freq, ~freq.next]).dur(0.02, 0.07) }, 25),
	], inf),
	() make: { \freq.stream(Pseq([60, 65, 63, 67, 70, 72].midicps, inf)) }
);
Chain(
	Pseq([
		Pn({ \ping.mplay([\freq, ~freq.next]).dur(0.02, 0.07) }, 25),
		{ \freq.replaceStream(Pseq(Array.rand(3 rrand: 10, 50, 100).midicps, inf)); }.chain,
		Pn({ \default.mplay([\freq, ~freq.next]).dur(0.02, 0.07) }, 25),
		{}.chain((0.09 / 8).once) // going out of phase
	], inf),
	() make: { \freq.stream(Pseq([60, 65, 63, 67, 70, 72].midicps, inf)) }
);

