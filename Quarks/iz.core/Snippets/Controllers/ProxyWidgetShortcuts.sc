/* IZ Tue 14 August 2012  1:44 AM EEST
Shortcuts for creating commonly used GUI items connected to Proxies and acting on them. 

See Widget, methods watchProxySpace etc

Example: 
(
Document.current.envir = ProxySpace.push;
w = Window.new;
w.layout = VLayout(
	PxMenu(w, \nodes),
	PxButton(w, \button, \nodes).states_([["start"], ["stop"]]),
	PxControlsMenu(w, \specs, \nodes),
	PxKnob(w, \knob, \specs, \numbox),
	PxNumberBox(w, \numbox, \knob)
);
w.windowHandler(w).front;
)
//:sample

~out = { | freq = 400 | SinOsc.ar(freq, 0, 0.1) };
~out.play; // after that, check the first menu of the window above, and select 'out'


*/

PxMenu { 
	*new { | model, name, proxySpace |
		^PopUpMenu()
			.addModel(model, name)
			.watchProxySpace(proxySpace).view;
	}
}

PxControlsMenu {
	*new { | model, name, setterWidgetNameOrProxy |
		^PopUpMenu()
			.addModel(model, name)
			.proxyControlsMenu(setterWidgetNameOrProxy).view;
	}
}

PxButton {
	*new { | model, name, setterWidgetNameOrProxy |
		^Button()
			.addModel(model, name)
			.proxyOnOffButton(setterWidgetNameOrProxy).view;
	}
}


PxSlider {
	*new { | model, name, setterWidgetName, receiverWidgetName |
		^Slider()
			.addModel(model, name, receiverWidgetName)
			.getSpecsFrom(setterWidgetName).view;	
	}
}


PxKnob {
	*new { | model, name, setterWidgetName, receiverWidgetName |
		^Knob()
			.addModel(model, name, receiverWidgetName)
			.getSpecsFrom(setterWidgetName).view;	
	}
}

PxNumberBox {
	*new { | model, name, receiverWidgetName |
		^NumberBox().addModel(model, name, receiverWidgetName).view
	}
}
