/* IZ Tue 14 August 2012  7:43 PM EEST

MainProxyMixer.new;

Cloned from NanoKontrol2b

To become a mixer with fixed proxy allocation per strip. Proxies are allocated in the order that they are created or found when parsed from a document. 

16 channels total. When more nodes than 16 are created, these are allocated to the next preset. Presets thus function as banks. 

============= IDEAS ============

Substitute the top knob-node-selection menu with a drag item for mapping the output of a proxy to the parameter or input of another proxy. This is to be dragged to an input drag-sink on the ProxySourceEditor (next version, to be done). 

Mappings may be saved as code-snippet? 

What about making Snippets that are marked by beginning with //:- like this: 
//:- 
Be non-proxy snippets, and thus excepted from being allocated in the proxy mixer's channels? 
These could then be shown in a different type of GUI that just allows to apply them. 
This would be a snippet-button type of gui like CodeButtons, developed a while ago.

In the ProxySourceEditor interface, the drag-sink field can be set to accept outputs from other proxies for mapping, or other objects such as numbers, for setting, or patterns, for streaming with asStream, (for NodeProxies playing Pbinds, etc. 


===============================================================

MainProxyMixer.new;

*/

MainProxyMixer {
	classvar <all; // all open instances

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
		window = Window(this.class.name ++ " : " ++ (docName ? "ps"), 
			Rect(Window.screenBounds.width - 1000, 0, 1200, 290)
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
		strips = { NanoK2Stripb(this) } ! 16;
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

