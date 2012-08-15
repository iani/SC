/* IZ Sun 05 August 2012  6:46 PMEEST

Testing how to interconnect gui elements without creating a variable for each element, by using NotificationCenter with the Lilt extension methods: addNotifier, objectClosed etc. 

a = WidgetInterconnect.new;
// access a widget
a.widget(\slider);

// change the spec of a widget: 
a.setSpec(\slider, \freq.asSpec);

MIDIFunc.noteOn = { | ... args | args.postln });

MIDIIn.control = { | ... args | args.postln; };
MIDIIn.noteOn = { | ... args | args.postln; };

w = Window.new.front;
w.layout = VLayout(
	PopUpMenu().items_([\out0, \out1])
		.addModel(w, \nodemenu)
			.proxyNodeSetter(\button)
			.proxyNodeSetter(\specmenu).w,
	Button().states_([["start"], ["stop"]])
		.addModel(w, \button).proxyNodeWatcher.w,
	PopUpMenu()
		.addModel(w, \specmenu).proxySpecWatcher.w

);




*/

WidgetInterconnect {
	// test class
	var <window;
	var <proxySpace, <node;

	*new {
		^super.new.init;
	}

	init {
		proxySpace = ProxySpace.push;
		window = Window("test interconnecting widgets");
		window.onClose = { this.objectClosed };
		window.layout = VLayout(
			Slider().addModel(this, \slider1, \numberbox1)
//				.addMIDI(\noteOn)
				.v,
			NumberBox().addModel(this, \numberbox1, \slider1).v,
			// Alternative coding style: Create new Widgets explicitly: 
			Widget(Slider(), this, \slider2, \numberbox2, spec: \freq.asSpec)
//				.addMIDI
				.v,
			Widget(NumberBox(), this, \numberbox2, \slider2).v,
			// Tests for Proxy Watchers: 
			Button()
				.states_([["make NodeProxy"]])
				.action_({
					var pn, pa1, pa2;
					#pn, pa1, pa2 = { "asdfghjklmnxoprs".scramble[..(1 rrand: 4)].asSymbol } ! 3;
					proxySpace[pn] = format("{
						| % = %, % = 0.1 | %.ar(%, 0, %);
					}", pa1, 300 rrand: 1000, 
						pa2, ["SinOsc", "LFTri", "LFSaw"].choose, pa1, pa2
					).interpret;
					proxySpace[pn].play;
				}),
			PopUpMenu().addModel(this, \nodeSelector)
				.proxySpaceWatcher(proxySpace)
				.proxyNodeSetter(\nodeSpecs, proxySpace)
				.v,
			PopUpMenu().addModel(this, \nodeSpecs)
				.proxySpecWatcher
				.proxySpecSetter(\knob1).v,
			Knob().addModel(this, \knob1).v
		);
		window.front;
		this.notify(\update);	// update widgets that depend on others
	}
}
