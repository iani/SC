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


*/

WidgetInterconnect {
	// test class
	var <window;

	*new {
		^super.new.init;
	}

	init {
		window = Window("test interconnecting widgets");
		window.onClose = { this.objectClosed };
		window.layout = VLayout(
			Slider().addModel(this, \slider1, \numberbox1).addMIDI(\noteOn).w,
			NumberBox().addModel(this, \numberbox1, \slider1).w,
			// Alternative coding style: Create new Widgets explicitly: 
			Widget(Slider(), this, \slider2, \numberbox2, spec: \freq.asSpec).addMIDI.w,
			Widget(NumberBox(), this, \numberbox2, \slider2).w
		);
		window.front;
	}
}


/* 
// older test, using "addSelfNotifier"
WidgetInterconnect {
	// test class
	var <window;

	*new {
		^super.new.init;
	}
	
	init {
		window = Window("test interconnecting widgets");
		window.onClose = { this.closed };
		window.layout = VLayout(
			Slider().orientation_(\horizontal)
				.addSelfNotifier(this, \slider, { | value, slider |
					slider.value = value;
				})
				.action_({ | me | this.notify(\numberbox, me.value) }),
			NumberBox()
				.addSelfNotifier(this, \numberbox, { | value, numbox |
					numbox.value = value;
				}) 
				.action_({ | me | this.notify(\slider, me.value) })
		);
		window.front;
	}


	closed { this.objectClosed }
}

*/