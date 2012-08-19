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

	adapter_ { | action | adapter.adapter = action; }
	action_ { | func | adapter.adapter = func;  } // synonym to adapter_ in analogy to View:action_
	
	// add specialized adapters to your adapter
	mapper { | spec | adapter mapper: spec }
	proxySelector { | proxySpace | adapter proxySelector: proxySpace; }
	proxyState { | proxySelector | adapter proxyState: proxySelector; }
	proxySpecSelector { | proxySelector | adapter proxySpecSelector: proxySelector; }
	proxyControl { | proxySpecSelector | adapter proxyControl: proxySpecSelector; }
	list { | items | adapter.list(items) }

	// add listeners to notifications from your adapter
	addAdapterListener { | listener, message, action |
		listener.addNotifier(adapter, message, action);
	}
	addSelectionListener { | listener, action | // for ProxySelector, ProxySpecSelector
		this.addAdapterListener(listener, \selection, action);
	}
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
	viewClosed { this.objectClosed; }
	// used to get string values from TextView with button click
	makeViewValueGetter { | name | view.action = { model.getViewValue(name) } }
}

AppValueView : AppView {
	var <>viewAction, <>updateAction;

	initActions {
		this.initViewAction(viewAction);
		this.initUpdateAction(updateAction);
	}

	initUpdateAction { | argAction |
		updateAction = argAction ?? { this.defaultUpdateAction };
	}
	
	defaultUpdateAction { ^{ | argView, val | view.value = val } }
	
	listSize { // make updateFunction tell you the size of the list in my list adapter
		updateAction = { view.value = adapter.adapter.items.size };
		view.enabled = false;
		// below is not needed, since view is disabled
		// viewAction = updateAction.value; // reset back to size of list
	}
	
	adapterUpdate { | argAction |
		// replace the update action with one that passes you the adapter as argument
		updateAction = { | ... args | argAction.(adapter, *args) };
	}
	doUpdate { // perform the update action now. 
		updateAction.(view, adapter.value)
	}

	initViewAction { | argAction |
		view.action = { viewAction.(view, this) };
		viewAction = argAction ?? { this.defaultViewAction };
	}

	defaultViewAction { ^{ | argView | adapter.valueAction = argView.value; } }
	valueArray { | argValues |  updateAction.(view, *argValues) }
}

AppSpecValueView : AppValueView {
	defaultViewAction { ^{ adapter.adapter map: view.value; } }
	defaultUpdateAction { ^{ view.value = adapter.adapter.unmappedValue; } }
}

AppTextValueView : AppValueView { // for StaticText, TextField, TextView
	defaultUpdateAction { ^{ | argView, string | view.string = string; } }
	list { | items, replaceItems = true |
		// set viewAction, updateAction to work with ListAdapter in your adapter
		super.list(items);
		updateAction = { view.string = adapter.adapter.item;  };
		if (replaceItems) {
			viewAction = { adapter.adapter.put(view.string) }
		}{
			viewAction = { adapter.adapter.add(view.string); }
		};
		updateAction.value;
	}
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
