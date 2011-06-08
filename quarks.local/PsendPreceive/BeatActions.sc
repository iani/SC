
BeatActions : IdentityDictionary {
	classvar <>verbose = false;
	value { | beatID |
		if (verbose) { postf("beat: %\n", beatID) }; 
//		[this, beatID, this[beatID[0]]].postln;
		this[beatID[0]].value(this);
	}
}


