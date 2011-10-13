
PrivateBusSynth : SynthResource {
	var <bus;
	
	*new { | key, defName, args, target, addAction=\adTohead,  numChannels = 1, rate = \audio  ... moreArgs |
		^super.new(key, numChannels, rate, target.asTarget, 
			defName ?? { key.asSymbol }, args, addAction, *moreArgs);
	}

	init { | numChannels, rate, target, defName, args |
		bus = Bus.perform(\rate, numChannels);
		this onEnd: { bus.free; bus = nil; };
		ServerReady.addSynth(this, { this.makeObject(target, defName, args ++ [\out, bus.index]); });
	}
}
