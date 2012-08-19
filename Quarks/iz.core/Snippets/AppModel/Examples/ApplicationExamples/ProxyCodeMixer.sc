/* IZ Thu 16 August 2012  6:28 PM EEST

Redoing the NanoKontrol2b idea as a subclass of AppModel.

ProxyCodeMixer.new;

ProxyCode(Document.current);
*/


ProxyCodeMixer : AppModel {
	var <doc, <>numStrips = 8, <proxyCode, <proxySpace, <strips;
	var <presetHandler;
	var <proxyList;
	
	*new { | doc, numStrips = 8 | ^super.new(doc, numStrips).init; }

	init {
		doc = doc ?? { Document.current };
		proxyCode = ProxyCode(doc);
		proxySpace = doc.envir;
		strips = { ProxyCodeStrip(this) } ! numStrips;
		presetHandler = ProxyCodePresetHandler(this);
		this.makeWindow;
		presetHandler.initPresets;
		{ this.initStrips; }.defer(1); // proxies may take time to load if launched same time as me
	}

	initStrips {
		var proxy, proxyIndex;
		ProxySelector.proxyNames[proxySpace][1..] do: { | pxName, i |
			proxy = proxySpace[pxName];
			proxyIndex = ProxySelector.getProxyIndexForName(proxySpace, pxName) - 1;
			this.addNewProxy(proxy, proxyIndex);
		};
		this.makeNewProxyAction;
	}

	makeNewProxyAction {
		ProxySelector.addProxySelector(proxySpace, \proxyNames, { | pn, proxy |
			this.addNewProxy(proxy, pn.size - 2);
		});
	}

	addNewProxy { | proxy, index |
		var presetNr, stripNr, strip;
		proxyList = proxyList add: proxy;
		stripNr = index % numStrips;
		presetNr = index - stripNr / numStrips;
		if (presetHandler.currentIndex == presetNr) {
			strip = strips[stripNr];
			{
				this.setStripProxy(strip, ProxySelector.getProxyIndex(proxySpace, proxy)); 
			}.defer(0.1); // wait for strip to register in ProsySpace first!
		}
	}

	setStripProxy { | strip, proxyIndex |
		strip.getAdapter(\proxySelector).selectItemAt(proxyIndex);
		this.setDefaultStripControls(strip);
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
		var proxies, proxy, strip;
		proxies = proxyList[(presetIndex * numStrips .. 1 + presetIndex * numStrips - 1)];
		argPreset do: { | preset, i |
			strip = strips[i];
			strip.restorePreset(preset);
			proxy = proxies[i];
			proxy !? { 
				if (preset[\proxySelector][\value][0] == 0) {
					this.setStripProxy(strip, ProxySelector.getProxyIndex(proxySpace, proxy))				}
			}
		}
	} 

}

