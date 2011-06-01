BusResource : AbstractServerResource {
	*control { | name = \control, numChannels = 1, server |
		^super.new(name.asSymbol, server, numChannels, \control);
	}

	*audio { | name = \audio, numChannels = 1, server |
		^super.new(name.asSymbol, server, numChannels, \audio);
	}

	init { | target, numChannels, rate |
		super.init(target);
		object = Bus.perform(rate ? \control, server, numChannels);
	}

	index { ^object.index }
	
	set { | value | object.set(value) }
	
	get { | action | ^object.get(action); }
	getn { | count, action | ^object.getn(count, action); }
}
