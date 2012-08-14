/* IZ Sun 05 August 2012  8:10 PMEEST

Simplify the task of adding and communicating with several widgets to any object, for representing and control different aspects of that object. 

See also PxMenu, PxControlsMenu, PxKnob, PxSlider, PxNumberBox

** Example

First do this: 
 
(
Document.current.envir = ProxySpace.push; 
w = Window.new;
w.layout = VLayout(
	PopUpMenu().addModel(w, \nodes)
		.watchProxySpace.v,
	Button().states_([["start"], ["stop"]])
		.addModel(w, \button)
		.proxyOnOffButton(\nodes).v,
	PopUpMenu().addModel(w, \specs)
		. (\nodes).v,
	Knob().addModel(w, \knob)
		.getSpecsFrom(\specs).v
);
w.windowHandler(w).front;
)

Then do this: 

//:sample - Run the following two lines. 


~out = { | freq = 400 | SinOsc.ar(freq, 0, 0.1) };
~out.play; // after that, check the first menu of the window above, and select 'out'

Then select controls from the second menu, and use the knob to control selected parameter.

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
	classvar <specCache; /* IdentityDictionary: Stores the most recently generated specs 
		for each node, to access them when a widget switches to a new node */

	var <>view, <model, <name, <>notifyTarget, <>action, <>updateFunc, <>spec;
	var <value = 0;		// the mapped value of the widget
	var <>inputs;		/* MIDIFuncs, MIDIResponders etc.
		Can be enabled or disabled individually or in groups
		See Object:enable and Widget:enableInput */
	var <>proxySpace, <>proxy, <>proxySpecs;

	*cacheSpecs { | argNodeProxy, argSpecs |
		// MergeSpecs stores most recent specs nodes here, for access when switching
		// Should updateState also cache new specs? 
		specCache[argNodeProxy] = argSpecs;
	}

	*initClass {
		all = MultiLevelIdentityDictionary.new;
		enabled = IdentityDictionary.new;
		specCache = IdentityDictionary.new;
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
		^{ | argValue |
			argValue !? {
				view.value = value = spec.unmap(argValue);
				action.(value, this);
			}
		}
	}

	value_ { | argValue |
		// also set the views value
		value = argValue;
		view.value = value;
	}

	setViewValue { | argValue | view.value = argValue } // not used by preset ...
	setValue { | argValue | value = argValue } // used by preset

	valueAction_ { | argValue |
		// set my value and perform my view's action
		value = argValue;
		view.valueAction = value;
	}

	valueArray { | ... args |
		// provide access to self when evaluated by receiving notification from model
		updateFunc.valueArray(args /* add: this */);
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

	// MIDI and OSC input enabling / disabling
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

	// ============= Interacting with proxies and other widgets ========
	
	// Menu for choosing a proxy from a ProxySpace
	watchProxySpace { | argProxySpace, argFunc |
		proxySpace = argProxySpace ?? {
			Document.current.envir ?? {
				proxySpace = ProxySpace.push;
				Document.current.envir = proxySpace;
				proxySpace;
			}
		};
		action = argFunc ?? {{
			this.notify(\setProxy, this.getItemFromMenu({ | symbol | proxySpace[symbol] }));
		}};
		this.addNotifier(proxySpace, \newProxy, { | newProxy |
			{ this.updateProxies; }.defer(0.1);
		});
		this.updateProxies;
	}

	getItemFromMenu { | argFunc |
		if (view.items.size == 0 or: { view.value.isNil } or: { view.item == '-' }) {
			^nil
		}{
			^argFunc.(view.item, view.value);
		}
	}
	
	updateProxies {
		this.updateItemsAndValue(proxySpace.envir.keys.asArray.sort add: '-');
	}
	
	updateItemsAndValue { | newItems, defaultItem = '-' |
		var oldItem;
		if (view.items.notNil and: { view.value.notNil }) {
			oldItem = view.item;
		}{
			oldItem = defaultItem
		};
		value = newItems indexOf: oldItem;
		view.items = newItems;
		view.value = value;
	}

	// === Button for playing or stopping a proxy ===
	proxyOnOffButton { | proxyOrWidgetName |
		action = {
			if (proxy.isNil) {
				view.value = value = 0;
			}{
				if (value > 0) { proxy.play; } { proxy.stop; }
			};
		};
		if (proxyOrWidgetName isKindOf: NodeProxy) {
			proxy = proxyOrWidgetName;
		}{
			this.connectToSetterWidget(proxyOrWidgetName, \setProxy, { | argProxy |
				this onOffButtonSetProxy: argProxy;
			});
		};
		this.updateOnOffStateFromProxy;
	}

	connectToSetterWidget { | widgetName, message, argFunc |
		this doOnUpdate: {
			this.addNotifier(model.widget(widgetName), message, argFunc)
		}
	}

	// maybe should be "doOnEnable ... registerOneShot(\enable ... )
	doOnUpdate { | argFunc | model.registerOneShot(\update, this, argFunc); }

	onOffButtonSetProxy { | argProxy |
		proxy !? {
			this.removeNotifier(proxy, \play);
			this.removeNotifier(proxy, \stop);
		};
		proxy = argProxy;
		proxy !? {
			this.addNotifier(proxy, \play, { view.value = value = 1 });
			this.addNotifier(proxy, \stop, { view.value = value = 0 });
			this.updateOnOffStateFromProxy;
		}
	}

	updateOnOffStateFromProxy {
		if (proxy.notNil and: { proxy.isMonitoring }) {
			view.value = value = 1;
		}{
			view.value = value = 0;			
		}
	}

	// === Menu for choosing the control parameter of a proxy ===
	proxyControlsMenu { | proxyOrWidgetName, argFunc |
		action = argFunc ?? {{ 
			this.chooseAndNotifyControlSpec;
		}};
		if (proxyOrWidgetName isKindOf: NodeProxy) {
			proxy = proxyOrWidgetName;
		}{
			this.connectToSetterWidget(proxyOrWidgetName, \setProxy, { | argProxy |
				this controlMenuSetProxy: argProxy;
			});
		};
		this.getControlSpecsFromProxyAndNotify;
	}

	chooseAndNotifyControlSpec {
		this.notify(\setControlSpec, 
			this.getItemFromMenu({ | name, index |
				proxySpecs[index] add: proxy;
			})
		);
	}

	controlMenuSetProxy { | argProxy |
		proxy !? {
			this.removeNotifier(proxy, \proxySpecs);
		};
		proxy = argProxy;
		if (proxy.notNil) {
			this.addNotifier(proxy, \proxySpecs, { | argSpecs |
				this.setAndNotifyProxySpecs(argSpecs);
			});
		};
		this.getControlSpecsFromProxyAndNotify;
	}

	getControlSpecsFromProxyAndNotify {
		var newSpecs;	// for clarity
		if (proxy.isNil) {
			newSpecs = MergeSpecs.nilSpecs;
		}{
			if ((newSpecs = specCache[proxy]).isNil) {
				newSpecs = MergeSpecs(proxy);
				specCache[proxy] = newSpecs;
			};
		};
		this.setAndNotifyProxySpecs(newSpecs);
	}

	setAndNotifyProxySpecs { | argSpecs |
		proxySpecs = argSpecs;
		this.updateItemsAndValue(proxySpecs.flop.first);
		this.chooseAndNotifyControlSpec;
	}

	// slider knob or other controller for setting NodeProxy parameters
	getSpecsFrom { | argWidgetName |
		this.connectToSetterWidget(argWidgetName, \setControlSpec, { | parameter, argSpec, argProxy |
			parameter = parameter ? '-';
			spec = argSpec;
			proxy = argProxy;
			action = switch ( parameter,
				'-', { {} },
				\vol, { { | val | proxy.vol = val; } },
				\fadeTime, { { | val | proxy.fadeTime = val; } },
				{ { | val | proxy.set(parameter, val); } }
			);
		})
	}

	// storing and restoring presets
	preset { ^WidgetPreset(this); }
	preset_ { | argPreset | argPreset.restore; }

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
	
	takeWidgetSnapshot { ^this.widgets collect: _.preset; }

	restoreWidgetSnapshot { | widgetPresets | widgetPresets do: _.restore; }
}

+ Nil {
	// nil as spec just returns the input argument as is
	map { | value | ^value }
	unmap { | value | ^value }		
}
