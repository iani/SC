/* IZ Wed 15 August 2012  9:14 PM EEST
Trying whole different approach to the whole widget / value notification thing.

This file contains a very early form which shows the bare basics. 
It may be useful as example for undestanding the basic principles of this approach.

User acts on view, setting a value 
	view maps value (using an adapter = spec or other stuff, possibly function);
	view sends app this message: app.setValue(key, value);


Widgets that need to add extra actions like setting their specs, changing their state, etc., 
can do it like this: 

widget.addNotifier(model, <commandname>, action);

So any widget can ask to be specifically notified when it needs to change something 
different than its value. It needs not and should not store itself in the AppModel.

First example: 

//:
AppModel().window({ | w, app |
	w.view.layout = VLayout(
		app.knob(\test).spec_(\freq).view,
		app.numberBox(\test).view
	)
});
//:

*/

AppModel {

	var <values;  /* IdentityDictionary: Adapters holding my values per name */
	
	*new { ^this.newCopyArgs(IdentityDictionary.new, IdentityDictionary.new); }

	getValue { | name | ^values[name].value; }
	getAdapter { | name |
		var adapter;
		adapter = values[name];
		if (adapter.isNil) {
			adapter = Adapter.new;
			values[name] = adapter;
		};
		^adapter;
	}
	setValue { | name, value |
		var adapter;
		[thisMethod.name, name, value, values[name]].postln;
		adapter = values[name];
		adapter !? { adapter.value = value; }
	}


	// =========== Adding views and windows ============
	window { | windowInitFunc, onCloseFunc |
		var window;
		window = Window();
		windowInitFunc.(window, this);
		onCloseFunc !? { this.addNotifier(window, \windowClosed, onCloseFunc) };
		window onClose: {
			window.notify(\windowClosed, window, this);
			window.objectClosed;
		};
		window.front;
	}

	numberBox { | name |
		^AppView(this, name, NumberBox());
	}
	knob { | name |
		^AppView(this, name, Knob());
	}
	slider { | name |
		^AppView(this, name, Slider());
	}
}

Adapter {
	var <value, <>action;
	
	value_ { | argValue |
		[thisMethod.name, argValue].postln;
		value = argValue;
		action.(this);
		this.notify(\value, value);
	}	
}


AppWidget {
	var <adapter, <model, <name;
	*new { | ... args | ^this.newCopyArgs(nil, *args).init; }
	init {
		adapter = model.getAdapter(name);
		this.addNotifier(adapter, \value, this);
	}
}

AppView : AppWidget {
	var <>view, <>updateAction, <>viewAction;

	init {
		super.init;
		this.initView;
		this.initActions;
		view onClose: { this.objectClosed; }
	}

	initView { /* this.subclassResponsibility(thisMethod) */ }
	
	initActions {
		this.initUpdateAction(updateAction);
		this.initViewAction(viewAction);
	}

	initUpdateAction { | argAction |
		updateAction = argAction ?? {{ | argView, val | argView.value = val }};
	}

	initViewAction { | argAction |
		view.action = { viewAction.(view, this) };
		viewAction = argAction ?? {{ | argView, myself |
			myself.model.setValue(name, argView.value);
		}};
	}

	valueArray { | argValues |  updateAction.(view, *argValues) }
	
	spec_ { | spec |
		spec = spec.asSpec;
		updateAction = { | argView, val | argView.value = spec.unmap(val) };
		viewAction =  { | argView, myself |
			myself.model.setValue(name, spec.map(argView.value));
		}
	}
	
}

