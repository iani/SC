/* iz Thursday; September 11, 2008: 12:21 PM
Creating sounds from Contours. 

Usage in script:

SimpleContourSound addScript: ~script;
Synth should provide ~freqMap as a BiMap that maps x values between -1 and 1 to an audible frequency range. 

*/

SimpleContourSound : ContourController {
	var <synth;
	init { | argModel |
		super.init(argModel);
		synth = Synth("variable_sin", [\freq, ~freqMap.value(model.x)]);
		~synths add: this;
		synth.envirOnEnd(currentEnvironment, {
			this.remove;
			~synths remove: this;
		}); 
	}
	moved { | contour |
		synth.set(\freq, ~freqMap.value(model.x));
	}

	died {
		super.died;
		synth.free;
	}
}
