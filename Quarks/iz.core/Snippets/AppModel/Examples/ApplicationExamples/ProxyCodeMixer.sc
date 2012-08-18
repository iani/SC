/* IZ Thu 16 August 2012  6:28 PM EEST

Redoing the NanoKontrol2b idea as a subclass of AppModel.

ProxyCodeMixer.new;

ProxyCode(Document.current);
*/


ProxyCodeMixer : AppModel {
	var <doc, <>numStrips = 8, <proxyCode, <proxySpace, <strips;
	var <presetHandler;
	
	*new { | doc, numStrips = 8 | ^super.new(doc, numStrips).init; }

	init {
		doc = doc ?? { Document.current };
		proxyCode = ProxyCode(doc);
		proxySpace = doc.envir;
		strips = { ProxyCodeStrip(this) } ! numStrips;
		presetHandler = ProxyCodePresetHandler(this);
		this.makeNewProxyAction;
		this.makeWindow;
		presetHandler.initPresets;
		{ this.initStrips; }.defer(1); // proxies may take time to load if launched same time as me
	}

	makeNewProxyAction {
		ProxySelector.addProxySelector(proxySpace, \proxyNames, { | pn, proxy |
			this.addNewProxy(proxy, pn.size - 2);
		});
	}

	addNewProxy { | proxy, index |
		var presetNr, stripNr, strip;
		stripNr = index % numStrips;
		presetNr = index - stripNr / numStrips;
		if (presetHandler.currentIndex == presetNr) {
			strip = strips[stripNr];
			{
				this.setStripProxy(strip, ProxySelector.getProxyIndex(proxySpace, proxy)); 
			}.defer(0.2); // wait for strip to update specs from proxy first
		}{
			
		};
	}

	makeWindow {
		var stripWidth = 80, winWidth;
		winWidth = stripWidth * numStrips;
		this.window({ | w, app |
			w	.name_("Proxy Code Mixer : " ++ doc.name)
				.bounds_(Rect(Window.screenBounds.width - winWidth, 0, winWidth, 250))
				.layout = HLayout(
					VLayout(*(presetHandler.gui)),
					*(strips collect: _.gui)
				)
		})
	}

	initStrips {
		var strip;
		proxySpace.envir.size do: { | i |
			strip = strips[i];
			strip !? { this.setStripProxy(strip, i) };
		};
	}

	setStripProxy { | strip, proxyIndex |
		strip.getAdapter(\proxySelector).selectItemAt(proxyIndex + 1);
		this.setDefaultStripControls(strip);
	}

	setDefaultStripControls { | strip |
		strip.getAdapter(\sliderSpecs).selectItemAt(1);
		strip.getAdapter(\knobSpecs).selectItemAt(2);
	}

	makePreset { ^strips collect: _.makePreset; }

	restorePreset { | argPreset | argPreset do: { | p, i | strips[i].restorePreset(p) } } 

}

