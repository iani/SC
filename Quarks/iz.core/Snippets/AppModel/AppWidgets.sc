/* IZ Thu 16 August 2012  4:02 PM EEST
See AppModel and Adapter
*/

AppNamelessWidget {
	var <model;
	*new { | ... args | ^this.newCopyArgs(*args).init; }
	widgetDo { | func |
		/* perform func on the widget. Needed to customize the widget at create time,
			while returning the view widget for further processing.
			Useful for adding notifiers to the widget from other sources than its AppModel,
			for example, running processes, other system events. 
			When the the view or window used by this widget closes, the notification is also 
			removed. 
			This is the preferable method for adding notifications from external sources. 
		 */
		func.(this)
	}	
}

AppNamelessWindow : AppNamelessWidget {
	var <windowInitFunc, <window;

	init {
		window = Window();
		windowInitFunc.(window, model);
		window.onClose = {
			window.notify(\windowClosed, this);
			window.objectClosed;
		};
		window.toFrontAction = { window.notify(\windowToFront, this) };
		window.endFrontAction = { window.notify(\windowEndFront, this) };
		window.front;	// Update views next, after window has drawn:
		model.updateListeners;
	}
}

AppStickyWindow : AppNamelessWindow {
	*new { | model, owner, name, windowInitFunc |
		var windowMaker;
		windowMaker = Library.at(owner, name);
		if (windowMaker.notNil) { 
			windowMaker.showWindow;
		}{
			Library.put(owner, name, super.new(model, windowInitFunc));
		}
	}
	showWindow { if (window.isClosed) { this.init } { window.front }; }
}



AppNamelessView : AppNamelessWidget {
	/*	For views that have no Adapter in the apps model, but do want to 
	   	change their state using the notification mechanism
	  	Use .view method for this: 
	  	app.view(StaticText().addAction ...)
	*/
	var <view; // var onObjectClosed;
	init { view.onClose = { this.objectClosed } }
	// used to get string values from TextView with button click. Also present in AppView (refactor???)
//	makeViewValueGetter { | name | view.action = { model.getViewValue(name) } }

	addModel { | object, message, action |
		// make perform action whenever I receive "value"
	}
}

AppNamedWidget : AppNamelessWidget {
	var <adapter, <>name;
	*new { | model, name ... args | 
		^this.newCopyArgs(model, nil, name, *args).init;
	}
	init {
		name !? { adapter = model.getAdapter(name); }; // allow nameless option for convenience
		this.addNotifier(adapter, \value, this); // see AppValueView:valueArray
	}
	
	adapter_ { | action | adapter.adapter = action; }
	action_ { | func | adapter.adapter = func;  } // synonym to adapter_ in analogy to View:action_

	addAction { | func, key | // perform additional action when \value is notified by adapter
		/* WARNING: actions that call value_ on this adapter, must send themselves as senders
		   otherwise infinite loop results. Example of correct call: 
		   aWidget.addAction({ | adapter, func | adapter.value_(0, func) }); 
		*/
		adapter.addAction(func, key);
	}

	removeAction { | key | adapter.removeAction(key) }

	adapterDo { | func |
		/* perform func on the adapter. Needed to customize the adapter at create time,
			while returning the view widget for further processing.
			Useful for adding notifiers to the adapter from other sources than its AppModel,
			for example, running processes, other system events. 
			However, note that notifiers added to the Adapter are not removed automatically
			when the a view or window closes, and must be closed by calling anAppModel.objectClosed
			So it is better to attach notifications to the widget through 
			AppNamelessWidget:widgetDo
		 */
		func.(adapter, this)
	}	
	// add specialized adapters to your adapter
	mapper { | spec | adapter mapper: spec } // knob, slider etc. install spec in adapter
	list { | items | adapter.list(items) }

	// Proxy stuff
	proxySelector { | proxySpace | adapter proxySelector: proxySpace; }
	proxyState { | proxySelector, playFunc, stopFunc |
		adapter.proxyState(proxySelector, playFunc, stopFunc);
	}
	proxySpecSelector { | proxySelector | adapter proxySpecSelector: proxySelector; }
	proxyControl { | proxySpecSelector | adapter proxyControl: proxySpecSelector; }
	proxyHistory { | proxySelector | adapter.proxyHistory(proxySelector); }

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
		view.onClose = { this.viewClosed; };
	}
	
	initView { view.onClose = { this.objectClosed }; }
	initActions { /* this.subclassResponsibility(thisMethod) */ }
	viewClosed { this.objectClosed; }

	// actions for navigating through a list of items
	nextItem { view.action = { adapter.adapter.next; } }
	previousItem { view.action = { adapter.adapter.previous; } }
	firstItem { view.action = { adapter.adapter.first; } }
	lastItem { view.action = { adapter.adapter.last; } }
	
	keyDownAction_ { | func |
		view.keyDownAction_({ | view, char, modifiers, unicode, keycode |
			func.(this, char, modifiers, unicode, keycode);
		})
	}
	
	initValue { | value |
		// initialize a view's value AFTER the initial update has been received at window creation
		view.addNotifierOneShot(adapter, \value, { { view.value = value }.defer(0.01) });
	}
	
	initItems { | items, value |
		view.addNotifierOneShot(adapter, \value, {
			{	// make sure you are after all other itializations
				items !? { view.items = items collect: _.asString };
				value !? { view.value = value};
			}.defer(0.01);
		});
		
	}
}

AppWindow : AppView { // not tested. Use WindowHandler???
	var <windowInitFunc, <onCloseFunc;
	
	init {
		windowInitFunc.(view, model);
		onCloseFunc !? { this.addNotifier(view, \windowClosed, onCloseFunc) };
		view.onClose = {
			view.notify(\windowClosed, this);
			view.objectClosed;
		};
		view.front;
	}
}

AppValueView : AppView {
	var <>viewAction, <>updateAction;

	valueArray { | argSenders |
		if (argSenders[0] !== this) { // do not update if you were the setter
			updateAction.(view, *argSenders);
		}
	}

	initActions {
		this.initViewAction(viewAction);
		this.initUpdateAction(updateAction);
	}

	initUpdateAction { | argAction |
		updateAction = argAction ?? { this.defaultUpdateAction };
	}

	defaultViewAction { ^{ adapter.valueAction_(view.value, this); } }
	defaultUpdateAction { ^{ view.value = adapter.value } }
	
	valueAction_ { | func |
		view.action = { adapter.valueAction_(func.(this), this) }
	}

	listSize { // make updateFunction tell you the size of the list in my list adapter
		this.list;
		updateAction = { view.value = adapter.adapter.items.size };
		view.enabled = false;
		this.doUpdate;
	}

	listItems { | items, updateItems = true |
		this.list(items);
		if (updateItems) {
			updateAction = {
				view.items = adapter.adapter.items collect: _.asString;
				view.value = adapter.value;
			};
		}{
			updateAction = { view.value = adapter.value; };
		};
		this.doUpdate;
	}

	listItem { | mode = \append | // mode can be one of: \append, \replace, \insert
		// Make a text view perform one of 4 actions on the list contained in my adapter
		this.list;
		switch (mode, 
			\append, { viewAction = { adapter.adapter.add(view.string, this) } },
			\replace, { viewAction = { adapter.adapter.replace(view.string, this) } },
			\rename, { viewAction = { adapter.adapter.rename(view.string, this) } },
			\insert, { viewAction = { adapter.adapter.insert(nil, view.string, this) } },
//			\delete, { viewAction = { adapter.adapter.removeAt(nil, this) } }, // does not make sense?
		);
		updateAction = {
			var item;
			item = adapter.adapter.items[adapter.value];
			view.string = if (item.isNil) { "<empty>" } { item.asString };
		}
	}

	items_ { | items | adapter.adapter.items = items }

	// used to get string values from TextView with button click
	getContents {	| widgetName, mode, extraAction |
		// make a button get the text from a TextView.
		// mode is one of \append, \replace, \insert, \delete
		this.viewAction = {};
		this.updateAction = {};
		view.action = { adapter.notify(\getContents, [widgetName, mode, extraAction]); }
	}

	listIndex { | startAt = 1 | 
		viewAction = { 
			adapter.valueAction_(view.value.round(1) - startAt max: 0, this);
			this.doUpdate; 
		};
		updateAction = { 
			view.value = (adapter.value ? 0) + startAt min: adapter.adapter.items.size;
		};
		this.doUpdate;
	}
 
	adapterUpdate { | argAction |
		// TODO: this will become default action, and the present method will be deprecated
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
}

AppSpecValueView : AppValueView {
	defaultViewAction { ^{ adapter.adapter map: view.value; } }
	defaultUpdateAction { ^{ view.value = adapter.adapter.unmappedValue; } }
}

AppTextValueView : AppValueView { // for StaticText, TextField, TextView
	defaultViewAction { ^{ adapter.valueAction_(view.string, this); } }
	defaultUpdateAction { ^{ view.string = adapter.value; } }

	list { | items, replaceItems = true |
		// set viewAction, updateAction to work with ListAdapter in your adapter
		super.list(items);
		updateAction = { view.string = adapter.adapter.item.asString;  };
		if (replaceItems) {
			viewAction = { adapter.adapter.put(nil, view.string) }
		}{
			viewAction = { adapter.adapter.add(view.string); }
		};
		this.initListActions;
		updateAction.value;
	}

	initListActions {
		// add behavior for responding to buttons that make me do something with my contents
		// on a list: 
		this.addNotifier(adapter, \getContents, { | argName, updateAction = \append, extraAction |
			if (argName.isNil or: { argName === name }) {
				var string;
				if (extraAction.notNil) {
					string = extraAction.(view.string)
				}{
					string = view.string
				};
				switch ( updateAction,
					\append, { adapter.adapter.add(string, this) },
					\replace, { adapter.adapter.replace(string, this) },
					\rename, { adapter.adapter.rename(string, this) },
					\insert, { adapter.adapter.insert(nil, string, this) },
					\delete, { adapter.adapter.delete(this) },
					{ updateAction.(this) }
				)
			}
		});
	}
	
	proxyHistory { | proxySelector | 
		this.listItem;
		super.proxyHistory(proxySelector);
	}

	initValue { | string |
		// initialize a view's value AFTER the initial update has been received at window creation
		view.addNotifierOneShot(adapter, \value, {
			{ view.string = string }.defer(0.01); // ensure you initialize after initial updates
		});
	}
}

AppTextView : AppTextValueView {
	initView {
		view = TextView();
		super.initView;
		this.addNotifier(adapter, \getContents, { adapter.valueAction_(view.string, this) })
	}	
}

AppStaticTextView : AppTextValueView {
	initView {
		view = StaticText();
		super.initView;
	}
	defaultViewAction { ^{ /* view has no action */ } }
	noUpdate { updateAction = {} } // for StaticText attached to list view items
}
