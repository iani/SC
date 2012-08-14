/* IZ Fri 10 August 2012  7:33 PM EEST

Version 2: Refactoring using Widget and ProxyWatchers

A GUI emulating the controls of NanoKontrol2, with features: 

Means to map the controls of any NodeProxy in a ProxySpace to a slider or knob. 
Presets, preset history.
Etc. 

n = NanoKontrol2a(ProxySpace.push);

n = NanoKontrol2b();
n.window.bounds;

TODO: Implement auto-proxy-mode, provide MIDI specs for UC-33e, and for Korg NanoKontrol 2. 

*/

NanoKontrol2b {
	classvar <all; // all open NanoKontrol2b instances 

	var <doc, <docName;
	var <proxyCode; 	// ProxyCode instance that created me. Holds history of NodeProxy source code
	var <proxySpace; 
	var <proxies; 	// array of proxies added - to check where to put 
	var <window;		// the main gui window;
	var <strips; 		// 8 strips with slider and knob controls.
	var <presets, <>currentPreset;	// store and restore configurations
	var <proxyPresetCache;  /* array of strip-presets for all proxies of proxySpace.
				For restoring one-strip-per-NodeProxy settings */
	
	var <font;
	
	*new { | doc |
		^this.newCopyArgs(doc).init;
	}

	init {
		doc = doc ?? { Document.current };
		docName = doc.name;
		proxyCode = ProxyCode(doc);
		proxySpace = proxyCode.proxySpace;
		font = Font.default.size_(10);
		this.makeWindow;
//		this.makeProxySpaceWatcher;	// auto-allocate new NodeProxies to free strips
		all = all add: this;
	}

	makeWindow {
		window = Window("NanoK2 " ++ (docName ? "ps"), 
			Rect(Window.screenBounds.width - 630, 0, 630, 290)
		);
		WindowHandler(this, window, 
			{
				strips do: _.objectClosed;
				all remove: this;
			},
			enableAction: { 
				strips do: _.enable;
				if (window.isClosed.not) { 
					window.view.background = Color(*[0.9, 0.8, 0.7].scramble);
				};
			},
			disableAction: {
				strips do: _.disable;
				if (window.isClosed.not) {
					window.view.background = Color(0.9, 0.9, 0.9, 0.5);
				}; 
			}
		);
		presets = (0..9) collect: NanoK2Presetb(_, this);
		currentPreset = presets.first;
		strips = { NanoK2Stripb(this) } ! 8;
		window.layout = HLayout(
			VLayout(*(presets collect: _.gui)),
			*(strips collect: _.gui)
		);
		presets.first.initEmpty;
		window.front
	}

	makeProxySpaceWatcher {
		ProxySpaceWatcher(this, proxySpace, { | newProxy, pSpace |
			{ this.addProxy(newProxy, pSpace); }.defer(0.1);
		});
	}

	addProxy { | newNode, pSpace |
		var nodeName, widget;
		newNode !? {
//			pSpace.envir.findKeyForValue(newNode).dup(50).postln;
			

		}
	}

	takeSnapshot { ^strips collect: _.takeWidgetSnapshot }

	restoreWidgetSnapshot { | widgetPresets | widgetPresets do: _.restore; }

	restoreSnapshot { | argSnapshot, argPresetGroup, autoProxyMode |
		argSnapshot = argSnapshot ?? { argPresetGroup.empty };
		argSnapshot do: { | s, i | strips[i].restoreWidgetSnapshot(s) };
		currentPreset = argPresetGroup;
		this.sendProxiesToStrips;
	}
	
	sendProxiesToStrips {
		
	}
	
	editNodeProxySource { | proxyName |
		// received from NanoK2Strip. Edit the source code of the proxy
		// (and replace source). 
		proxyCode editNodeProxySource: proxyName;
	}
}

