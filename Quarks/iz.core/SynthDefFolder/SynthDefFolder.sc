


SynthDefFolder {

	*initClass {
		StartUp add: {
			(this.filenameSymbol.asString.dirname +/+ "*.scd").pathMatch do: _.load;
		}	
	}
	
}

Stereo : Group {
	var <synths;
	*new { | defName, args, target, addAction=\addToHead |
		^super.new(target, addAction).initSynths(defName, args);
		
	}

	initSynths { | defName, args |
		synths = { | i | Synth(defName, args ++ [\out, i]) } ! 2;
	}

}

OUT { *new { | source, out = 0 | ^Out.ar(\out.kr(out), source); } }

