/* iz Tue 18 December 2012 11:56 AM EET
*/



+ Node {
	mapf { | controlName, busName, source |
		var bus;
		bus = (busName ? controlName).bus(this.server);
		this.map(controlName, bus);
		source !? {
			source.play(this.server, bus);
		};
	}
}

+ Symbol {
	bus { | server, rate = \control, numChannels = 1 |
		var bus;
		server = server.asTarget.server;
		bus = Library.at(\Busses, server, this);
		bus ?? {
			bus = Bus.alloc(rate, server, numChannels);
			Library.put(\Busses, server, this, bus)
		};
		^bus;
	}
}