/* IZ Mon 27 August 2012 12:31 PM EEST
Keep MIDI specifications for any object, to be accessed when that object needs to create MIDI inputs
*/

MIDISpecs {
	classvar <all;
	classvar <>default;

	*initClass { all = IdentityDictionary.new }

	*put { | key, specs | all[key] = specs; }
	
	*at { | key | ^all[key] }

}

+ Object { 
	
	midiSpecs { | key |
		var specs;
		specs = MIDISpecs.at(key ? this);
		specs ?? { specs = MIDISpecs.at(this.class) };
		specs ?? { specs = MIDISpecs.default };
		^specs;
	}

}