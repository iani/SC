/* IZ Thu 16 August 2012  6:28 PM EEST

*/

ProxyCodeMixer : AppModel {
	var <doc, <>numStrips = 8, <numPresets = 8, <proxyCode, <proxySpace, <proxyList, <strips;
	var <stripWidth = 80;
	var <>font;
	var <valueCache;	// fast access to values for storing and restoring presets

	*initClass {
		Class.initClassTree(MIDISpecs);
		MIDISpecs.put(this, this.uc33eSpecs);
	}

	*new { | doc, numStrips = 8, numPresets = 8 | ^super.new(doc, numStrips, numPresets).init; }

	init {
		font = Font.default.size_(9);
		/* PATCH: if proxySpace is provided, then use it and ignore doc */
		if (doc isKindOf: ProxySpace) {
			proxySpace = doc;
		}{
			doc = doc ?? { Document.current };
			proxyCode = ProxyCode(doc);
			proxySpace = doc.envir;
		};
		proxyList = proxySpace.proxies;
		this.makeStrips;
		this.makeWindow;
		this.initPresets;
		this.reloadProxies;
		this.initMIDI;
	}

	makeStrips {
		strips = { | index | ProxyCodeStrip(this, index) } ! numStrips;
	}

	reloadProxies { proxyList.notify(\list, proxyList); }

	makeWindow {
		var winWidth;
		winWidth = stripWidth * numStrips;
		this.window({ | w, app |
			w	.name_("Proxy Code Mixer : " ++ doc.name)
				.bounds_(Rect(Window.screenBounds.width - winWidth, 0, winWidth, 250))
				.layout = HLayout(
					VLayout(
						*(this.radioButtons(
							\presets, 
							{ "just a placeholder" } ! numPresets,
							{ | me | this.setPreset(me.item); },
							{ | me | this.getPreset(me.item); },
						) collect: _.font_(font))
					),
					*(strips collect: _.gui)
				);
			this.addWindowActions(w);
		});
	}

	addWindowActions { | window |
		this.windowClosed(window, {
			this.disable(window);
			this.objectClosed;
		});
		this.windowToFront(window, { this.enable; });
		this.windowEndFront(window, { this.disable; });
		window.addNotifier(this, \colorEnabled, {
			if (window.isClosed.not) { window.view.background = Color(*[0.7, 0.8, 0.9] /* .scramble */); }
		});
		window.addNotifier(this, \colorDisabled, {
			if (window.isClosed.not) { window.view.background = Color(0.8, 0.8, 0.8, 0.5); };
		});
	}	

	enable {
		super.enable(true);
		strips do: _.enable;
		this.notify(\colorEnabled);
	}

	disable {
		super.disable;
		strips do: _.disable;
		this.notify(\colorDisabled);
	}

	// PRESETS

	initPresets {
		var protoPreset;
		valueCache = strips.collect(_.valueCache).flat;
		protoPreset = valueCache collect: _.item;
		this.getValue(\presets).items_(nil, { List.newUsing(protoPreset) } ! numPresets);
	}

	getPreset { | preset | preset.array = valueCache collect: _.item; }

	setPreset { | preset |
		valueCache do: { | v, i | v.item_(nil, preset[i]) };
		{ this.notify(\autoSetProxy); }.defer(0.5); // TODO: use layered views instead of presets
	}

	// MIDI

	initMIDI {
		var specs, knob, slider, strip;
		specs = this.midiSpecs;
		knob = specs[\knob];
		slider = specs[\slider];
		// _mean_ shortcut creating midi specs by setting the channel number for each strip:
		8.min(numStrips) do: { | i |
			strips[i].addMIDI([slider: slider.put(3, i), knob: knob.put(3, i)]);
		}
	}

	*uc33eSpecs { // these specs are for M-Audio U-Control UC-33e, 1st program setting
		^(
			knob: [\cc, nil, 10, 0],
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

