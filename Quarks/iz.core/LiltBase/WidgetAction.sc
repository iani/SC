/* IZ Sun 05 August 2012  7:34 PMEEST

For GUI widgets or other items that need to set themselves when notified via NotificationCenter, using Object:addSelfNotifier.

This is experimental. See also Widget class, which develops this further and may make WidgetAction obsolete. 

Example: 

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

WidgetAction {
	var <widget, <action;
	
	*new { | widget, action | ^this.newCopyArgs(widget, action) }
	
	valueArray { | ... args |
		action.valueArray(args add: widget);
	}
}
