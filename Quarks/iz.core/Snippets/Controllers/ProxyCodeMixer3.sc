/* IZ Mon 27 August 2012  5:38 PM EEST

ProxyCodeMixer with 3 banks of knobs, for UC-33e 

*/

ProxyCodeMixer3 : ProxyCodeMixer {

	*initClass {
		Class.initClassTree(MIDISpecs);
		MIDISpecs.put(this, this.uc33eSpecs);
	}

	init {
		stripWidth = 100;
		numPresets = 10;
		super.init;
	}

	makeStrips {
		strips = { | index | ProxyCodeStrip3(this, index) } ! numStrips;
	}

	initMIDI {
		var specs, knob1, knob2, knob3, slider, strip;
		specs = this.midiSpecs;
		knob1 = specs[\knob1];
		knob2 = specs[\knob2];
		knob3 = specs[\knob3];
		slider = specs[\slider];
		8.min(numStrips) do: { | i |
			strips[i].addMIDI([
				slider: slider.put(3, i), 
				knob1: knob1.put(3, i),
				knob2: knob2.put(3, i),
				knob3: knob3.put(3, i)
			]);
		}

	}

	*uc33eSpecs { // these specs are for M-Audio U-Control UC-33e, 1st program setting
		^(
			knob1: [\cc, nil, 10, 0],
			knob2: [\cc, nil, 12, 0],
			knob3: [\cc, nil, 13, 0],
			slider: [\cc, nil, 7, 0]
/*			startStopButton: [\cc, { | me | me.toggle }, 18, 0],
			prevSnippet: [\cc, { | me | me.action.value }, 19, 0],
			eval: [\cc, { | me | me.action.value }, 20, 0],
			nextSnippet: [\cc, { | me | me.action.value }, 21, 0],
			firstSnippet: [\cc, { | me | me.action.value }, 22, 0],
			add: [\cc, { | me | me.action.value }, 23, 0],
			lastSnippet: [\cc, { | me | me.action.value }, 24, 0],
			resetSpecs: [\cc,  { | me | me.action.value }, 25, 0],
			toggleWindowSize: [\cc,  { | me | me.toggle }, 26, 0],
			delete: [\cc,  { | me | me.action.value }, 27, 0],
*/		)
	}
	
}