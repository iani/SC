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
		whose widget inputs are enabled. There is one entry per widget group. 
		*/

	var <>view, <model, <name, <>notifyTarget, <>action, <>updateFunc, <>spec;
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

	*new { | view, model, name, notifyTarget, action, updateFunc, spec |
		^this.newCopyArgs(view, model, name, notifyTarget, action, updateFunc, spec).init
	}

	init {
		value = view.value;
		all.put(model, name, this);
		view.action = { | me |
			value = spec.map(me.value);
			action.(value, this);
			notifyTarget !? { model.notify(notifyTarget, value, this) };
		};
		updateFunc = updateFunc ?? { this.defaultUpdateFunc };
		this.addNotifier(model, name, this);
		model onObjectClosed: { this.objectClosed };
	}

	defaultUpdateFunc {
		^{ | value |
			view.value = value = spec.unmap(value);
			action.(value, this);
		}
	}

	value_ { | argValue |
		// also set the views value
		value = argValue;
		view.value = value;
	}
	
	valueAction_ { | argValue |
		// set my value and perform my view's action
		value = argValue;
		view.valueAction = value;
	}

	valueArray { | ... args |
		// provide access to self when evaluated by receiving notification from model
		updateFunc.valueArray(args add: this);
	}
	
	addMIDI { | type = 'cc', num, chan, src, argAction |
		if (MIDIClient.initialized.not) {
			MIDIIn.connectAll;
		};
		argAction = argAction ?? {{ | me, val |
			view.valueAction = val / 127;
		}}; 
		inputs = inputs add: MIDIFunc.perform(type, 
			{ | val, num, chan, src |  // See MIDIFunc help: number of args varies by MIDI msg type
				{ argAction.(this, val, num, chan, src) }.defer;
			}, num, chan, src);
	}

	// shortcut for accessing the view, when used in Layouts: 
	v { ^view }

	*enable { | model, group, inputType, disablePrevious = true |
		var previous;
		group = group ?? { this getGroup: model };
		if (disablePrevious and: { (previous = enabled[group]).notNil }) {
			previous.disable(group, inputType);
		};
		model.widgets do: _.enableInput(inputType);
		enabled[group] = model;	
		model.notify(\enable);
	}

	*getGroup { | model |
		var group;
		/* if no group is given, then try to get the group from the object or from its class */
		(group = Library.at(\widgetGroups, model)) !? { ^group };
		(group = Library.at(\widgetGroups, model.class)) !? { ^group };
		^\global;
	}

	*disable { | model, group, inputType |
		group = group ?? { this getGroup: model };
		model = model ?? { enabled[group] };
		model !? {
			model.widgets do: _.disableInput(inputType);
			enabled[group] = nil;
			model.notify(\disable);
		}
	}

	enableInput { | inputType |
		this.selectInputType(inputType) do: _.enable;
	}

	disableInput { | inputType |
		this.selectInputType(inputType) do: _.disable;
	}

	selectInputType { | inputType |
		if (inputType.isNil) {
			^inputs
		}{
			^inputs select: { | i | i isKindOf: inputType }
		}
	}

	objectClosed {
		super.objectClosed;
		this.disableInput;
	}
	
	toggle { | onval = 1 | this.valueAction = onval - value; }
	increment { | inc = 1, limit = inf |
		this.valueAction = value = value + 1 min: limit;
	}
	decrement { | inc = 1, limit = 0 |
		this.valueAction = value = value - 1 max: limit;
	}
	
	proxySpaceWatcher { | space, argAction |
		// update state when a NodeProxy is added or removed from a ProxySpace
		ProxySpaceWatcher(this, space, argAction);
	}

	proxyNodeSetter { | targetName, proxySpace, argAction, targetWidget |
		// Set the node of targetWidget when the node chosen by my widget change
		// if targetWidget is not provided, it is fetched from targetName
		ProxyNodeSetter(this, targetName, proxySpace, argAction, targetWidget);
	}

	proxyNodeWatcher { | playAction, stopAction, setWidgetAction = true, node |
		// update state when a NodeProxy starts or stops playing
		ProxyNodeWatcher(this, playAction, stopAction, setWidgetAction, node);
	}

	proxySpecWatcher { | argAction, node |
		// update specs when the source of a NodeProxy changes
		ProxySpecWatcher(this, argAction, node);
	}
	
	proxySpecSetter { | targetName, argAction, targetWidget |
		// set the specs of targetWidget when the specs of the parameter chosen by this widget change
		// if targetWidget is not provided, it is fetched from targetName
		ProxySpecSetter(this, targetName, argAction, targetWidget);
	}

}


+ Object {
	// Add a view such as a Slider to a model under a name, create default actions if needed
	/* Usage example:  
		Slider().addModel(this, \slider1);
	*/
	addModel { | model, name, notifyTarget, action, updateFunc, spec, controller, device = \default |
		^Widget(this, model, name, notifyTarget, action, updateFunc, spec);
	}

	widget { | name |
		// return the widget registered for this object under name
		^Widget.all.at(this, name);
	}
	
	widgetValue { | name | 
		// return the value of the widget registered for this object under name
		^this.widget(name).value;
	}
	
	widgets {
		// return all widgets of this object
		var widgets;
		widgets = Widget.all[this];
		widgets !? { widgets = widgets.values };
		^widgets;
	}
	setSpec { | name, spec | this.widget(name).spec = spec; }
	setAction { | name, function | this.widget(name).action = function; }
	setValue { | name, value | this.widget(name).value = value; }
	setValueAction { | name, value | this.widget(name).valueAction = value; }
	setNotify { | name, symbol | this.widget(name).notify = symbol; }
	
	enable { | group, inputType, disablePrevious = true |
		/* Enable inputs whose class is kind of inputType from all widgets belonging to this object.
		If disablePrevious is true, then the previously enabled object is sent the message disable */
		Widget.enable(this, group, inputType, disablePrevious);
	}

	disable { | group, inputType |
		/* Disable inputs whose class is kind of inputType from all widgets belonging to this object */
		Widget.disable(this, group, inputType);
	}

	addMIDI { | specs |
		/* specs is a Dictionary or an Array of form [key: value, ... ] 
		Add midi to any widget whose name is included in a key, constructing the MIDIFunc from the 
		specs. Specs must be of the form [\miditype, ... other specs], corresponding to the 
		arguments required by Widget:addMIDI, which are: type = 'cc', num, chan, src, action.
		For example, see ProxySourceEditor:makeWindow
		*/
		var widgets;
		widgets = Widget.all[this];
		widgets !? {
			specs keysValuesDo: { | widget, specs |
				widget = widgets[widget];
				widget !? { widget.addMIDI(*specs) }
			}
		};
	}
}

+ Nil {
	// nil as spec just returns the input argument as is
	map { | value | ^value }
	unmap { | value | ^value }		
}
