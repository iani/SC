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


p = KDpan([\bphasor, \bufnum, );

p.phrases(20, 
	[\vol, [[0.1, 0], [5, 5], [10, 0]]],
	[\ele, [[0, 0], [1, 1], [10, 0]]],
	[\azi, [[0, 0], [1, 1.01], [0, -1], [1, 0]]]
);


p = KDpan({ | out, group |
	Synth(\bphasor, [\bufnum, O@\swallowsa, \out, out], group, \addToHead);
});



p = KDpan({ | out, group |
	Synth(\bphasor, [\bufnum, O@\swallowsa, \out, out], group, \addToHead);
});

p = KDpan([\bphasor, \bufnum, O@\swallowsa]);
p.fadeIn(2, 12);	
p.fadeIn(2, 0);


p = KDpan([\bphasor, \bufnum, O@\swallowsa, \vol, 0.0, \rate, 1]);
p.fadeIn(2, 12);	


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

	*new { | synthSpec, azi = 0, ele = 0, width = 2, vol = 0 |
		^super.new.init(synthSpec, azi, ele, width, vol);
	}
	
	init { | synthSpec, azi = 0, ele = 0, width = 2, vol = 0 |
		bus = Bus.audio;
		group = Group(Server.default);
		// *new(defName, args: [ arg1, value1, ... argN, valueN  ], target, addAction)
		panner = Synth("kdpan", [\in, bus.index, \azi, azi, \ele, ele, \width, width, \vol, vol], group, \addToTail);
		if (synthSpec.notNil) { this.add(synthSpec) };
	}

	add { | synthSpec | synths = synths.add(synthSpec.asSynthFunc.(bus.index, group);) }

	phrases { | duration ... phrases | 
		// play a sequence of parameter settings timed and with lags
		// the phrase is scaled so that all subsequences have total duration equal to "duration"
		phrases do: { | p | Phrase(duration, p[0], p[1]).play(this) };
	}

	fadeTo { | time = 3, value = 1 |
		panner.set(\vollag, time, \vol, value);	
	}

	fadeIn { | time = 3, value = 1 |
		this.fadeTo(time, value);		
	}
		
	fadeOut { | time = 3 |
		this.fadeTo(time, 0);
	}

	panTo { | azi = 0, ele = 0, time = 5 |
		panner.set(\vollag, time, \azi, azi, \elelag, time, \ele, ele);
	}

	// just a synonym to help ... : 
	gotTo { | azi = 0, ele = 0, time = 5 |
		this.panTo(azi, ele, time);
	}
	

	wideTo { | width = 2, time = 5 |
		panner.set(\widthlag, time, \width, width);
	} 

	vertWideTo { | width = 2, time = 5 |
		panner.set(\vertwidthlag, time, \vertwidth, width);
	}

	setLag { | param, val, time = 5 |
		panner.set((param ++ \lag).asSymbol, time, param, val);
	}
	
	setLagSynths { | param, val, time = 5 |
		synths do: { | s | s.set((param ++ \lag).asSymbol, time, param, val); }
	}

	fadeSynths { | val = 0.5, time = 5 | this.setLagSynths(\vol, val, time); }

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

	init { | nodeArray, azi = 0, ele = 0, width = 2, vol = 0 |
		bus = Bus.control(Server.default, 43);
		group = Group(Server.default);
		// *new(defName, args: [ arg1, value1, ... argN, valueN  ], target, addAction)
		panner = Synth("kdpanvol", [\in, bus.index, \azi, azi, \ele, ele, \width, width, \vol, vol], group, \addToTail);
		if (nodeArray.notNil) { this.add(nodeArray) };
	}
	add { | nodeArray | 
		nodeArray.moveToTail(group);
		synths = nodeArray; // synths.add(nodeArray);
		nodeArray.map(\vol, bus.index); // should become; this.remap; // but after the performance
	}
	
	setNodes { | parFunc, ids |
		ids = ids ?? { (0 .. synths.nodes.size) };
		ids = ids.asArray;
//		synths do: { | s | s.nodes[ids] do: { | n, i | n.set(*parFunc.(ids[i], i)) } };
		synths.nodes[ids] do: { | n, i | n.set(*parFunc.(ids[i], i)) } 
	} 

	fadeOutAndEnd { | time |
		this.fadeOut(time);
		{ 
			synths do: _.free;
			panner.free;
		}.defer(time + 0.1); 		
	}
	
	remap { synths.map(\vol, bus.index); }
	
	randPhrase { | duration = 20, numPoints = 5 |
		this.phrases(duration, 
			[\vol, [[0.2, 1], [3, 3], [7, 2], [10, 7], [5, 0]] * 1.5],
			[\ele, [[0, 0], [1, 1], [10, 0]]],
			[\azi, [[0, 0], [1, 1.01], [0, -1], [1, 0]]]
		);		
	}

	randWidePhrase { | duration = 20, numPoints = 5, maxVol = 1.0, durVariation = 1 |
		this.phrases(duration,
			[\vol, [[0.2, 1], [3, 3], [7, 2], [10, 7], [5, 0]] * 1.5],
			[\ele, [[0, 0], [1, 1], [10, 0]]],
			[\azi, [[0, 0], [1, 1.01], [0, -1], [1, 0]]]
		);		
	}
	
	makeDurations { | numPoints, durVariation |
		Array.rand(numPoints, 1.0, 1 + durVariation);
	}
	
	makeVolDurations { | numPoints, durVariation |
		Array.rand(numPoints + 2, 1.0, 1 + durVariation);
	}

	makeValues { | numPoints, minVal, maxVal |
		Array.rand(numPoints, minVal, maxVal)
	}

	makeVolValues { | numPoints, minVal, maxVal |
		[0] ++ Array.rand(numPoints, minVal, maxVal) ++ [0];
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
		parameterlag = (parameter ++ \lag).asSymbol; // .postln;
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