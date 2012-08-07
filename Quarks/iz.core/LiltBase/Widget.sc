/* IZ Sun 05 August 2012  8:10 PMEEST

Simplify the task of adding and communicating with several widgets to any object, for representing and control different aspects of that object. 

(TODO: Use case example description needed here!) 

Example: 
a = WidgetInterconnect.new;

WidgetInterconnect {
	// test class
	var <window;
	*new { ^super.new.init; }
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
*/

Widget {
	classvar <all; /* all widgets stored in a MultiLevelIdentityDictionary under 
		their model and name. Provides access to any widget of any model.
		Models notify their widgets when they are closed */
	classvar <enabled;	/* This variable is used to optionally deactivate MIDI or other 
		input sources of widgets when a new model becomes active. 
		See Widget class methods 'enable' and 'disable'.
		It is a dictionary holding objects
		whose widget inputs are enabled. There is one entry per input type. 
		Common input types are: MIDIFunc, OSCFunc. */

	var <>widget, <model, <name, <>notify, <>action, <>updateFunc, <>spec;
	var <>inputs;		/* MIDIFuncs, MIDIResponders etc.
		Can be enabled or disabled individually or in groups
		See Object:enable and Widget:enableInput */
	var <value;		// the mapped value of the widget

	*initClass {
		all = MultiLevelIdentityDictionary.new;
		enabled = IdentityDictionary.new;
	}

	*removeModel { | object |
		all.removeEmptyAt(object);
		this.disable(object);
	}

	*new { | widget, model, name, notify, action, updateFunc, spec |
		^this.newCopyArgs(widget, model, name, notify, action, updateFunc, spec).init
	}

	init {
		value = widget.value;
		all.put(model, name, this);
		widget.action = { | me |
			value = spec.map(me.value);
			action.(value, this);
			notify !? { model.notify(notify, value, this) };
		};
		updateFunc ?? { updateFunc = this.defaultUpdateFunc };
//		this.addNotifier(model, name, updateFunc);
		this.addNotifier(model, name, this);
		model onObjectClosed: { this.objectClosed };
	}

	defaultUpdateFunc {
		^{ | value | widget.value = value = spec.unmap(value) }
	}

	value_ { | argValue |
		// also set the widgets value
		value = argValue;
		widget.value = value;
	}
	
	valueAction_ { | argValue |
		// set my value and perform my widget's action
		value = argValue;
		widget.valueAction = value;
	}

	valueArray { | ... args |
		// provide access to self when evaluated by receiving notification from model
//		["valueArray method in Widget is calling updateFunc with these args: ", args add: this].postln;
		updateFunc.valueArray(args add: this);
	}
	
	addMIDI { | type = 'cc', num, chan, src, action |
		if (MIDIClient.initialized.not) {
			MIDIIn.connectAll;
		};
		inputs = inputs add: MIDIFunc.perform(type, action ?? {{ | val |
			{ widget.valueAction = val / 127; }.defer;
		}}, num, chan, src);
	}
	
	w { ^widget } // shortcut for accessing the widget, when used in Layouts 
	view { ^widget } // shortcut for accessing the widget, when used in Layouts 

	*enable { | model, inputType, disablePrevious = true |
		if (disablePrevious) { this.disable(enabled, inputType) };
		model.widgets do: _.enableInput(inputType);
		enabled[inputType] = model;	
	}

	*disable { | model, inputType |
		model !? {
			model.widgets do: _.disableInput(inputType);
			if (inputType.isNil) {
				enabled keysValuesDo: { | key, value |
					if (value === model) { enabled[key] = nil };
				};
			}{
				if (enabled[inputType] === model) { enabled[inputType] = nil }
			}
		}
	}
	
	objectClosed {
		super.objectClosed;
	}
}


+ Object {
	// Add a widget such as a Slider to a model under a name, create default actions if needed
	/* Usage example:  
		Slider().addModel(this, \slider1);
	*/
	addModel { | model, name, notify, action, updateFunc, spec, controller, device = \default |
		^Widget(this, model, name, notify, action, updateFunc, spec);
	}

	widget { | name |
		// return the widget registered for this object under name
		^Widget.all.at(this, name);
	}
	widgets {
		// return all widgets of this object 
		var widgets;
		widgets = Widget.all[this];
		widgets !? { widgets = widgets.values };
		^widgets;
	}
	setSpec { | name, spec | this.widget(name).spec = spec; }
	setaction { | name, function | this.widget(name).action = function; }
	setNotify { | name, symbol | this.widget(name).notify = symbol; }
	
	enable { | inputType, disablePrevious = true |
		/* Enable inputs whose class is kind of inputType from all widgets belonging to this object.
		If disablePrevious is true, then the previously enabled object is sent the message disable */

		this.widgets do: _.enableInput(inputType);
	}

	disable { | inputType |
		/* Disable inputs whose class is kind of inputType from all widgets belonging to this object */
		Widget.disable(this, inputType);
	}

}



+ Nil {
	// nil as spec just returns the input argument as is
	map { | value | ^value }
	unmap { | value | ^value }		
}

+ QView {
	/* 	Views can add themselves directly to a MIDIDevice controller. 
		No automatic group switching, activate, deactivate or removal on close available here. 
		
		MAY HAVE TO BE REMOVED VERY SOON
		
	*/
	addMIDI { | controller, device, action |

	}
}
