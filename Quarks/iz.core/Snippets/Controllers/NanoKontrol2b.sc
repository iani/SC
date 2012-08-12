/* IZ Fri 10 August 2012  7:33 PM EEST

Version 2: Refactoring using Widget and ProxyWatchers

A GUI emulating the controls of NanoKontrol2, with features: 

Means to map the controls of any NodeProxy in a ProxySpace to a slider or knob. 
Etc. 

n = NanoKontrol2a(ProxySpace.push);

n = NanoKontrol2b();
n.window.bounds;

TODO: Lose the proxyCode. ???

*/

NanoKontrol2b {
	classvar <all; // all open NanoKontrol2b instances 

	var <doc, <docName;
	var <proxyCode; // ProxyCode instance that created me. Holds history of NodeProxy source code
	var <proxySpace; 
	var <proxies; // array of proxy names used for proxy selection menus
	var <window;	// the main gui window;
	var <presets, <currentPreset;	// store proxy, control, and spec configurations
	var <strips; 	// 8 strips with slider and knob controls.

	var font;
	
	*new { | doc |
		^this.newCopyArgs(doc).init;
	}

	init {
		doc = doc ?? { Document.current };
		docName = doc.name;
		proxyCode = ProxyCode(doc);
		proxySpace = proxyCode.proxySpace;
		font = Font.default.size_(10);
		presets = (0..9) collect: NanoK2Preset(_, this);
		currentPreset = presets.first;
		this.makeWindow;	
		all = all add: this;
	}

	makeWindow {
		window = Window("NanoK2 " ++ (docName ? "ps"), 
			Rect(Window.screenBounds.width - 630, 0, 630, 330)
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
		strips = { NanoK2Stripb(this) } ! 8;
		window.layout = HLayout(
			VLayout(*(presets collect: _.button)),
			*(strips collect: _.gui)
		);
		window.front
	}

	loadPreset { | preset | currentPreset = preset ? currentPreset; }
	savePreset { | preset | currentPreset = preset ? currentPreset; }
	
	editNodeProxySource { | proxyName |
		// received from NanoK2Strip. Edit the source code of the proxy
		// (and replace source). 
		proxyCode editNodeProxySource: proxyName;
	}
}
