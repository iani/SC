/* IZ 2012 03 15

(Draft)

OnsetDetector: Detect onsets in an audio buffer loaded from file.
Optionally save the onsets in text format (also as audio file?).



*/

AbstractDetectOnsets {
	var <audioFilePath;
	var >onsetPath;
	var <replyID;		// Used by SendReply.kr to identify detection Synth process to OSCresponderNode
	var <bufName;
	var <responder;	/* responder that receives the onsets from the detection synth
		Proposed to use OSCresponderNode and to check id of the sending node. 
		This will enable running multiple onset detection processes of the same kind concurrently
	*/

	var <onsets;

	*initClass {
		StartUp add: { this.makeUdef }	
	}

	*new { | audioFilePath |
		^super.newCopyArgs(audioFilePath).init;
	}

	init {
		if (audioFilePath.isNil) {
			audioFilePath = PathName("sounds/a*".pathMatch.first).absolutePath;
		}
	}

	responderName { ^this.class.name }

	addOnset { | onset |
		onsets = onsets add: onset;
	}

	*makeUdef {
		Udef(this.synthdefName, this.udefFunc);
	}

	*synthdefName { this.name }

	*udefFunc {
		// return a dummy function here to avoid subclassResponsibility 
		^{ Out.ar(0, WhiteNoise.ar(0.1)) }
	}
	
	detect {
		this.makeResponder;
		this.startSynth;
//		this.class.name.play(args: [\buf, ...])
	}

	makeResponder {
		replyID = UniqueID.next;
		responder = OSCresponderNode(nil, this.responderName, { | t, r, msg |
			if (msg[2] == replyID) { this addOnset: msg[3] };
		}).add;
	}

	fileDescriptor {
		// string identifying the type of analysis for the file name
		^this.class.name.asString;
	}

	ended {
		// triggered when detection synth ends. See method detect
		postf("% detection ended for: %\n", this.fileDescriptor, audioFilePath);
		this.save;
		responder.remove;
	}
	
	save { onsets.writeArchive(this.onsetPath); }

	onsetPath {
		var pname;
		if (onsetPath.isNil) {
			pname = PathName(audioFilePath);
			onsetPath = pname.pathOnly 
				++ pname.fileNameWithoutExtension 
				++ this.fileDescriptor
				++ ".scd";
		};
		^onsetPath;
	}

}

DetectOnsets : AbstractDetectOnsets { // Uses Onsets.kr
	var <>odftype = 'mkl';	// odftype parameter for Onsets.kr (see its help file).
	*makeUDef {
//		UDef(this.name, this.udefFunc);
	}
	
	*udefFunc {
//		^{ | buf, threshold = 0.01, replyID |
	
	
//			
//		}
	}

	fileDescriptor { ^format("%_%", super.fileDescriptor, odftype.asString) }
}

DetectJA : AbstractDetectOnsets { // Uses PV_JensenAndersen UGen 
	
	
}

DetectHF : AbstractDetectOnsets { // Uses PV_HainsworthFoote UGen
	
	
}

