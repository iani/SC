/* IZ Sat 18 August 2012 10:25 PM EEST

a = ProxyCodeMixer(nil, 8);

Stores and restores proxy and spec selections for a ProxyCodeMixer.

*/

ProxyCodePreset : AppModel {
	var <>handler, <>index = 0, <>preset, <>font;
	
	*new { | ... args | ^super.new(*args).init; }

	init {
		font = Font.default.size_(10);
	}

	store { preset = handler.proxyCodeMixer.makePreset; }

	restore {
		if (preset.isNil) {
			handler.proxyCodeMixer.strips do: { | s |
				s.getAdapter(\proxySelector).selectItemAt(0);
			}
		}{
			handler.proxyCodeMixer.restorePreset(preset, index)
		}
	}

	gui {
		var label;
		label = (index + 1).asString;
		^this.button(\preset)
			.adapter_({ handler.restorePreset(this) })
			.view.states_([[label], [label, nil, Color.yellow]])
			.font_(font);
	}
	
	setActive { this.getAdapter(\preset).value = 1 }
	setInactive { this.getAdapter(\preset).value = 0 }
}

ProxyCodePresetHandler {

	var <>proxyCodeMixer, <>presets, <>currentPreset;

	*new { | mixer | ^this.newCopyArgs(mixer).init }

	init {
		presets = { | i | ProxyCodePreset(this, i) } ! 8;
		currentPreset = presets.first;		
	}
	
	initPresets {
		var inited;
		inited = presets[0].store;
		inited.preset do: this.initializePreset(_);
		inited = inited.preset;
		presets do: { | p | p.preset = inited.copy };
	}
	
	initializePreset { | argPreset |
		argPreset use: {
			~proxySelector[\value] = [0, ['-']];
			~knobSpecs[\value] = nil;
			~sliderSpecs[\value] = nil;
		}
	}

	gui { ^presets collect: _.gui; }
	
	restorePreset { | argPreset |
		if (currentPreset === argPreset) {
			argPreset.setActive;
		}{
			currentPreset.store;
			currentPreset.setInactive;
			argPreset.restore;
			argPreset.setActive;
			currentPreset = argPreset;
		}
	}
	
	currentIndex { ^currentPreset.index }
}