/* */

Psend  : FilterPattern {
	var <>name = \beat;
	
	asStream { // | pOsc |
		^Routine({ arg inval; this.embedInStream(inval); });
	}
	embedInStream { arg event;
		event[\posc].broadcast(["start", name]);
		event[\beatCounters] push: name;
//		event[\beatCounters] = event[\beatCounters] add: BeatCounter(name.asString);
		event = pattern.embedInStream(event);
		event[\posc].broadcast(["stop", name]);
		event[\beatCounters].pop;
		^event;
	}
}
