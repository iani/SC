/* IZ Thu 16 August 2012  4:02 PM EEST
See AppModel and Adapter

*/

AppNamelessWidget {
	var <model;
	*new { | ... args | ^this.newCopyArgs(*args).init; }
	addAction { | message, action |
		this.addNotifier(model, message, { | ... args | action.(this, *args) });
	}
}

AppNamelessWindow : AppNamelessWidget {
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
	  	Use .view method for this: 
	  	app.view(StaticText().addAction ...)
	*/
	var <view, onObjectClosed;
	init {
		view.onClose = { this.objectClosed }
	}
	// used to get string values from TextView with button click. Also present in AppView (refactor???)
	makeViewValueGetter { | name | view.action = { model.getViewValue(name) } }
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
	
	// add specialized adapters to your adapter
	mapper { | spec | adapter mapper: spec }
	proxySelector { | proxySpace | adapter proxySelector: proxySpace; }
	proxyState { | proxySelector | adapter proxyState: proxySelector; }
	proxySpecSelector { | proxySelector | adapter proxySpecSelector: proxySelector; }
	proxyControl { | proxySpecSelector | adapter proxyControl: proxySpecSelector; }

}

AppView : AppNamedWidget {
	var <>view;

	init {	
		super.init;
		this.initView;
		this.initActions;
		view onClose: { this.viewClosed; };
	}
	
	initView { /* this.subclassResponsibility(thisMethod) */ }
	initActions { /* this.subclassResponsibility(thisMethod) */ }
	viewClosed { this.objectClosed; } // subclasses like AppWindow add more actions
	// used to get string values from TextView with button click
	makeViewValueGetter { | name | view.action = { model.getViewValue(name) } }
}

AppValueView : AppView {
	var <>viewAction, <>updateAction;
	init {
		super.init;
		this.initView;
		this.initActions;
		view.onClose = { this.objectClosed; }
	}
	
	initActions {
		this.initViewAction(viewAction);
		this.initUpdateAction(updateAction);
	}

	initUpdateAction { | argAction |
		updateAction = argAction ?? { this.defaultUpdateAction };
	}
	
	defaultUpdateAction { ^{ | argView, val | argView.value = val } }

	initViewAction { | argAction |
		view.action = { viewAction.(view, this) };
		viewAction = argAction ?? { this.defaultViewAction };
	}

	defaultViewAction { ^{ | argView | adapter.valueAction = argView.value; } }

	valueArray { | argValues |  updateAction.(view, *argValues) }

}

AppSpecValueView : AppValueView {
	defaultViewAction { ^{ | argView | adapter.adapter map: argView.value; } }
	defaultUpdateAction { ^{ | argView, val | argView.value = adapter.adapter.unmappedValue; } }
}

AppTextValueView : AppValueView { // for StaticText, TextField, TextView
	defaultUpdateAction { ^{ | view, string | view.string = string; } }
}


AppTextView : AppTextValueView {
	initView {
		view = TextView();
		this.addNotifier(adapter, \at, { adapter.valueAction = view.string });
	}
}

AppStaticTextView : AppTextValueView {
	initView { view = StaticText(); }
	defaultViewAction { ^{ | ... args | [ "StaticText view action not implemented", args].postln; } }
}

AppItemSelectView : AppValueView { // for ListView, PopUpMenu 
	
	defaultViewAction { ^{ adapter.valueAction = [view.value, view.items] } }
	defaultUpdateAction { ^{ | v, val, items | v.items = items; v.value = val ? 0; } }
}
