/*

KDpan(<synthfunc>);

Starts a kdpan Synth, and if synthfunc is given as argument, adds a synth made by synthfunc to it, . 
More synths can be added by aKDpan.add(<synthfunc>);

synthfunc takes the bus number and the group of the KDpan input as argument, 
and must use them to set its output and group (!)

KDpan also implements all control parameters of kdpan synthdef as messages. 

aKDpan.set(\azi, 0.3);

and: 

aKDpan.azi = 0.1;
aKDpan.ele = 0.5;
etc. 

Furthermore it can map the inputs of the kdpan synth instance: 

aKDpan.map(\azi, bus.index);

// arg target, outbus = 0, fadeTime = 0.02, addAction=\addToHead, args;


p = KDpan({ | out, group | { | out | Out.ar(out, WhiteNoise.ar(0.1)) }
	.play(args: [\out, out], target: group, addAction: \addToHead) });

p = KDpan({ | out, group |
	Synth(\bphasor, [\bufnum, O@\swallowsa, \out, out], group, \addToHead);
});


p = KDpan([\bphasor, \bufnum, O@\swallowsa]);
	
	
p = KDpan({ | bus, group | Synth("brd(group, addAction: \addToHead, 
	args: [\out, bus])
});


Phrase(50, \vol, [[0.1, 0.1], [5, 1], [10, 0]]).play(p);

p.phrases(60, 
	[\vol, [[0.1, 0], [5, 5], [10, 0]]],
	[\ele, [[0, 0], [1, 1], [10, 0]]],
	[\azi, [[0, 0], [1, 1.01], [0, -1], [1, 0]]]
);


p.set(\width, 18);
p.set(\vertwidth, 18);
p.set(\width, 2);

p.set(\azilag, 10, \azi, 0);

p.set(\azilag, 0, \azi, 1.01);
p.set(\azilag, 10, \azi, 0);

p.set(\azilag, 10, \azi, 1.02);
p.set(\azilag, 0, \azi, -1);

p.set(\azilag, 10, \azi, 0);


p.set(\width, 50);
p.set(\azi, 1);
p.set(\azi, 0);
p.set(\vol, 0.1);
p.set(\vol, 0.1);
p.set(\ele, 1);

*/


KDpan {
	var <bus;
	var <panner;
	var <synths;
	var <group;

	*new { | synthSpec, azi = 0, ele = 0, width = 2 |
		^super.new.init(synthSpec, azi, ele, width);
	}
	
	init { | synthSpec, azi = 0, ele = 0, width = 2 |
		bus = Bus.audio;
		group = Group(Server.default);
		// *new(defName, args: [ arg1, value1, ... argN, valueN  ], target, addAction)
		panner = Synth("kdpan", [\in, bus.index, \azi, azi, \ele, ele, \width, width], group, \addToTail);
		if (synthSpec.notNil) { this.add(synthSpec) };
	}

	phrases { | duration ... phrases | 
		// play a sequence of parameter settings timed and with lags
		// the phrase is scaled so that all subsequences have total duration equal to "duration"
		phrases do: { | p | Phrase(duration, p[0], p[1]).play(this) };
	}


	add { | synthSpec | synths = synths.add(synthSpec.asSynthFunc.(bus.index, group);) }
	free { group.free }

	set { | ... parlist | panner.set(*parlist); }
	map { | parameter, bus | panner.map(parameter, bus.index); }
	azi { | azi | panner.set(\azi, azi);	}
	ele { | ele | panner.set(\ele, ele); }
	width { | width | panner.set(\width, width); }
	vol { | vol | panner.set(\vol, vol);	}
}

/*

p = KDpan({ | out, group |
	Synth(\bphasor, [\bufnum, O@\swallowsa, \out, out], group, \addToHead);
});
p.set(\azi, 1);
p.set(\azi, 0);

a = NodeArray({ | i | Synth(\blfn3, [\out, i, \bufnum, O@\weddellb, \rate, 0.01, \vol, 0.01]); })
a.set(\vol, 1);
p = KDpanvol(a);
p.set(\azi, 1);

p.set(\width, 18);
p.set(\azi, 0);

*/

KDpanvol : KDpan {

	init { | nodeArray, azi = 0, ele = 0, width = 2 |
		bus = Bus.control(Server.default, 43);
		group = Group(Server.default);
		// *new(defName, args: [ arg1, value1, ... argN, valueN  ], target, addAction)
		panner = Synth("kdpanvol", [\in, bus.index, \azi, azi, \ele, ele, \width, width], group, \addToTail);
		if (nodeArray.notNil) { this.add(nodeArray) };
	}
	add { | nodeArray | 
		nodeArray.moveToTail(group);
		synths = synths.add(nodeArray);
		nodeArray.map(\vol, bus.index);
	}

}

Phrase {
	var <duration, <parameter, 	<durvaluepairs;
	var <duration_ratio, <parameterlag;

	*new { | duration, parameter, durvaluepairs |	
		^this.newCopyArgs(duration, parameter, durvaluepairs).init;
	}
	
	init { 
		duration_ratio = duration / durvaluepairs.flop.first.sum;
		parameterlag = (parameter ++ \lag).asSymbol.postln;
	}
	
	play { | panner |
		{
			durvaluepairs.flat pairsDo: { | dur, val |
				dur = dur * duration_ratio;
				panner.set(parameterlag, dur, parameter, val);
				dur.wait;
			}
		}.fork
	}	
}