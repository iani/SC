/* Parameter that binds an input to an audio bus via which a synth can received audio signal input
Note: 
Kr input is done for any parameter, via mapping, and is done automatically from any kr output bus.
Tr (Trigger) input is done via dragging from a TrOutput. 
*/

ArInput : Parameter {
	inputRate { ^\audio }
	busLinkClass { ^ArBusLink }
	acceptableWriterClass { ^ArOutput }
	labelBackground { ^Color.red.alpha_(0.1) }
	setScriptAttributes {	// set flag for script label color
		script.hasAudioInput = true;
	}
	resetInputBusIndex { | argIndex |
		this.set(argIndex)
	}
	muteInput {
		MuteBus.muteInput(this, script.session.server);
	}
	unmuteInput {
		MuteBus.unmuteInput(this, script.session.server);
	}
	setPreset { // disabled for the moment - ArInputs dont change 
		// their numeric value directly but should be implemented as 
		// re-linking to a different script (hairy ...)
	}
	add2MIDIList {
		// called by Script:defaultMIDIobjects
		// add self to the array of objects controllable by MIDI for your Script.
		// IO Parameters skip this because they are not controllable by MIDI
	}
}
