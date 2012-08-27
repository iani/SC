/* IZ Thu 16 August 2012  6:28 PM EEST

Redoing the NanoKontrol2b idea as a subclass of AppModel.

ProxyCodeMixer.new;

ProxyCode(Document.current);
*/


ProxyCodeMixer : AppModel {
	var <doc, <>numStrips = 8, <proxyCode, <proxySpace, <strips;
	var <presetHandler;

	*initClass {
		Class.initClassTree(MIDISpecs);
		MIDISpecs.put(this, this.uc33eSpecs);
	}

	
	*new { | doc, numStrips = 8 | ^super.new(doc, numStrips).init; }

	init {
		doc = doc ?? { Document.current };
		proxyCode = ProxyCode(doc);
		proxySpace = doc.envir;
		strips = { | index | ProxyCodeStrip(this, index) } ! numStrips;
		presetHandler = ProxyCodePresetHandler(this);
		this.makeWindow;
		this.reloadProxies;
		presetHandler.initPresets;
		this.initMIDI;
	}

	reloadProxies { proxySpace.notify(\proxyNames, [ProxySelector.proxyNames[proxySpace]]); }

	makeWindow {
		var stripWidth = 80, winWidth;
		winWidth = stripWidth * numStrips;
		this.window({ | w, app |
			w	.name_("Proxy Code Mixer : " ++ doc.name)
				.bounds_(Rect(Window.screenBounds.width - winWidth, 0, winWidth, 250))
				.layout = HLayout(
					VLayout(*(presetHandler.gui)),
					*(strips collect: _.gui)
				);
			WindowHandler(this, w, 
				{ /* all[proxyName] = nil; */},
				enableAction: { 
					if (w.isClosed.not) { 
						w.view.background = Color(*[0.9, 0.8, 0.7].scramble);
					};
				},
				disableAction: { 
					if (w.isClosed.not) {
						w.view.background = Color(0.9, 0.9, 0.9, 0.5);
					};
				}
			);
		})
	}

	setDefaultStripControls { | strip |
		strip.getAdapter(\sliderSpecs).selectItemAt(1);
		strip.getAdapter(\knobSpecs).selectItemAt(2);
	}

	makePreset { ^strips collect: _.makePreset; }

	restorePreset { | argPreset, presetIndex |
		argPreset do: { | preset, i |
			strips[i].restorePreset(preset);
		};
		{ this.reloadProxies; }.defer(1);
	}

	initMIDI {
		var specs, knob, slider, strip;
		specs = this.midiSpecs;
		knob = specs[\knob];
		slider = specs[\slider];
		8.min(numStrips) do: { | i |
			strips[i].addMIDI([slider: slider.add(i), knob: knob.add(i)]);
		}
	}

	*uc33eSpecs { // these specs are for M-Audio U-Control UC-33e, 1st program setting
		^(
			knob: [\cc, nil, 10],
			slider: [\cc, nil, 7]
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

