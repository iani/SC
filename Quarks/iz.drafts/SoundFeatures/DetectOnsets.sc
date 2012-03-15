/* IZ 2012 03 15

(Draft)

OnsetDetector: Detect onsets in an audio buffer loaded from file.
Optionally save the onsets in text format (also as audio file?).

*/

DetectOnsets : SynthResource {
	var <audioFilePath;
	var <onsetPath;
	var <bufName;
	var <responder;	// OSCresponder that receives the onsets from the detection synth
	
	*initClass {
		StartUp add: { this.makeUDef }	
	}
	
	*makeUDef {
//		UDef(this.name, this.udefFunc);
	}
	
	*udefFunc {
//		^{ | |
//			
//		})
	}
	
	detect {
//		this.class.name.play(args: [\buf, ...])
	}
}

DetectJA : OnsetDetector { // Uses PV_JensenAndersen UGen 
	
	
}

DetectHF : OnsetDetector { // Uses PV_HainsworthFoote UGen
	
	
}

