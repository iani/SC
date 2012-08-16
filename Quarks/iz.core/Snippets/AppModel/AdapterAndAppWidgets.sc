/* IZ Thu 16 August 2012  4:02 PM EEST
See AppModel

*/

Adapter {
	var <>action, <value;
	
	value_ { | argValue |
		value = argValue;
		action.(this);
		this.notify(\value, value);
	}
}

AppNamelessWidget {
	var <model;
	*new { | ... args | ^this.newCopyArgs(*args).init; }
	addAction { | message, action |
		this.addNotifier(model, message, { | ... args | action.(this, *args) });
	}
}

AppWindow : AppNamelessWidget {
	var <windowInitFunc, <onCloseFunc, <window;
	
	init {
		window = Window();
		windowInitFunc.(window, model);
		onCloseFunc !? { this.addNotifier(window, \windowClosed, onCloseFunc) };
		window.onClose = {
			window.notify(\windowClosed, this);
			window.objectClosed;
		};
		window.front;
	}
}

AppNamelessView : AppNamelessWidget {
	/*	For views that have no Adapter in the apps model, but do want to 
	   	change their state using the notification mechanism
	   	Such a view would do, for example: 
	  	AppNamelessView(app, StaticText()).addAction(app, \setLabel, { | me, string |
		  	me.view.string = string
	  	});
	  	
	  	Use .view method for this: 
	  	app.view(StaticText().addAction ...
	  
	*/
	var <view, onObjectClosed;
	init {
		view.onClose = { this.objectClosed }
	}
	
}

AppNamedWidget : AppNamelessWidget {
	var <adapter, <name;
	*new { | model, name ... args | 
		^this.newCopyArgs(model, nil, name, *args).init;
	}
	init {
		name !? { adapter = model.getAdapter(name); }; // allow nameless option for convenience
		this.addNotifier(adapter, \value, this);
	}
	
	adapterAction { | action | adapter.action = action; }
}

AppView : AppNamedWidget {
	var <>view;

	init {	
		super.init;
		view onClose: { this.viewClosed; };
	}
	
	viewClosed { this.objectClosed; } // subclasses like AppWindow add more actions

}

AppValueView : AppView {
	var <>viewAction, <>updateAction;
	init {
		super.init;
		this.initView;
		this.initActions;
		view.onClose = { this.objectClosed; }
	}

	initView { /* this.subclassResponsibility(thisMethod) */ }
	
	initActions {
		this.initViewAction(viewAction);
		this.initUpdateAction(updateAction);
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
