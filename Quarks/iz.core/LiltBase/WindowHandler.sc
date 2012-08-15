/* IZ Fri 10 August 2012 11:23 PM EEST

Encapsulate actions to do when a window comes to front, goes to back or closes. 
This kind of behavior may be useful to objects belonging to different classes, and is therefore implemented here as a separate class. Also notify \update when a window opens the first time, necessary to interconnect any Widgets that depend on each other. 

This is a draft made after ProxySourceEditor and NanoKontrol2 of the Snippets quark. 

Use of WindowHandler is illustrated by class WHExample. Try: 

(
{ 	
	var window;
	window = Window("WH Example", Rect(*({ 100 rrand: 400 } ! 4)));
	window.windowHandler(window, 
		enableAction: { 
			if (window.isClosed.not) { window.view.background = (Color.red) };
		},
		disableAction: { 
			if (window.isClosed.not) { window.view.background = (Color(*(0.5.dup(4)))) }; 
		}
	);
	window.front;
} ! 2
)

// Showing how update works when the window opens. Used by window interconnect method
// doOnUpdate for connectToSetterWidget 
// Direct use is not recommended. Used by proxyControlsMenu, proxyOnOffButton getSpecsFrom. 
( 
var window;
window = Window.new;
\test.windowHandler(window);
\test.registerOneShot(\update, \something, { "update received".postln; });
window.front;
)

*/

WindowHandler {
	
	var <model, <window, <>onClose, <>toFrontAction, <>endFrontAction;
	var <>enableAction, <>disableAction;
	
	*new { | model, window, onClose, toFrontAction, endFrontAction, enableAction, disableAction |
		^this.newCopyArgs(model, window, onClose, toFrontAction, endFrontAction,
			enableAction, disableAction
		).init;
	}

	init {
		window.onClose = { this.doOnClose };
		window.endFrontAction = { this.doEndFrontAction };
		window.toFrontAction = { this.doToFrontAction };
		enableAction !? { this.addNotifier(model, \enable, enableAction); };
		disableAction !? { this.addNotifier(model, \disable, disableAction); };
	}

	doOnClose {
		onClose.(this); // before disabling
		model.disable;
		model.objectClosed;
	}
	
	doToFrontAction {
		model.notify(\update);	// widgets that need to connect to other widgets use this
		model.enable;
		toFrontAction.(this); // after enabling
	}

	doEndFrontAction { endFrontAction.(this) } // only extras 

}

+ Object {
	windowHandler { | window, onClose, toFrontAction, endFrontAction, enableAction, disableAction |
		WindowHandler(
			this, window, onClose, toFrontAction, endFrontAction, enableAction, disableAction
		)
	}
}
