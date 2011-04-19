/*

UniqueSpectrogram {
	classvar all;

	*all {
		if (all.isNil) { all = IdentityDictionary.new };
		^all;	
	}

	*new { | bounds |
		var new;
		new = this.all.at(Server.default);
		if (new.isNil) {
			^this.makeNewFor(server, bounds);	
		}{
			new.start;
		};
		^new;
	}

	
}

*/