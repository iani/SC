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

p = KDpan.new;
p.add({ | bus, group | { Out.ar(bus, WhiteNoise.ar(0.1)) }.play(group, addAction: \addToHead, 
	args: [\out, bus])
});

p.set(\vol, 0.1);
*/


KDpan {
	var <bus;
	var <panner;
	var <synths;
	var <group;
	
	*new { | synthFunc, azi = 0, ele = 0, width = 2 |
		^super.new.init(synthFunc, azi, ele, width);
	}
	
	init { | synthFunc, azi = 0, ele = 0, width = 2 |
		bus = Bus.audio;
		group = Group(Server.default);
		// *new(defName, args: [ arg1, value1, ... argN, valueN  ], target, addAction)
		panner = Synth("kdpan", [\in, bus.index, \azi, azi, \ele, ele, \width, width], group, \addToTail);
		if (synthFunc.notNil) { this.add(synthFunc) };
	}

	add { | synthFunc |
		synths = synths.add(synthFunc.(bus.index, group);)
	}
	
	free { group.free }
	
	set { | ... parlist |
		panner.set(*parlist);
	}
	
	map { | parameter, bus |
		panner.map(parameter, bus.index);	
	}
	
	azi { | azi |
		panner.set(\azi, azi);	
	}

	ele { | ele |
		panner.set(\ele, ele);	
	}

	width { | width |
		panner.set(\width, width);	
	}

	vol { | vol |
		panner.set(\vol, vol);	
	}
	
}