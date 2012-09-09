/* IZ Fri 31 August 2012  2:57 AM EEST

Redo of Adapter idea from scratch, with radical simplification of principle 

See Value.org file for discussion. 

*/

Value {
	var <>model;		// AppModel2 instance that contains me
	var <adapter;	 // contains the object + the adapter? 
	var <inputs;	// Array of MIDIFunc and/or OSCFunc that send me input

	*new { | model, adapter | ^this.newCopyArgs(model).adapter_(adapter) }

	updateListeners { adapter !? { this.notify(adapter.updateMessage); } }

	adapter_ { | argAdapter | 
		argAdapter !? { adapter = argAdapter.container_(this) };
		this.updateListeners;
	}

	// make me get my list from the item of another list
	sublistOf { | superList, getListFunction |
		this.adapter = ListAdapter2();
		 // vary this to get different parts of the item
		getListFunction ?? { getListFunction = { | sublist | sublist }; };
		if (superList isKindOf: Symbol) { superList = model.getValue(superList); };
		this.addNotifier(superList, \list, {
			this.adapter.items_(this, getListFunction.(superList.adapter.item));
		});
		this.addNotifier(superList, \index, {
			this.adapter.items_(this, getListFunction.(superList.adapter.item));
		});
	}

	items_ { | changer, items |
		adapter.items_(changer, items);
	}

	// MIDI and OSC
	enable { inputs do: _.enable }
	disable { inputs do: _.disable }

	setMIDI { |  createMsg = \cc, func ... args |
		// remove any previous MIDIFuncs before adding this one
		this.removeMIDI;	// remove all previous MIDIFuncs from inputs
		this.addMIDI(createMsg, func, *args);
	}

	removeMIDI {	// remove all previous MIDIFuncs from inputs
		inputs.copy do: { | i | if (i isKindOf: MIDIFunc) { inputs remove: i } };
	}

	addMIDI { | createMsg = \cc, func ... args |
		// Create and add a MIDIFunc to my inputs
		inputs = inputs add: MIDIFunc.performList(createMsg, this.makeMIDIAction(func), args);
	}

	makeMIDIAction { | argFunc |
		if (argFunc.isNil) { ^this.defaultMIDIAction } { ^{ | ... args | argFunc.(this, *args) } }
	}

	defaultMIDIAction {
		if (adapter.isNil) {
			^{ | ... args | this.valueAction_(args[0]) }
		}{
			^adapter.defaultMIDIAction;
		}
	}

	addOSC { /* not yet implemented */ }
	
	objectClosed {
		super.objectClosed;
		inputs do: _.free;
		inputs = nil;
	}
}


Widget {
	// NOTE: Model and name are not used in current functionality, but stored for 
	// direct access in possible future applications. 
	// In a "stricter" implementation, model and name variables could be ommitted. 
	var <model;	// the AppModel2 that created me
	var <name;	// the Symbol under which my Value instance is stored in the AppModel
	var <value; 	// the Value instance that I interact with;
	var <view;  	// my view

	*initClass {
		StartUp add: {
			// Allow (proxy-watching) widgets to start and stop watching cmdPeriod notifications
			CmdPeriod add: { CmdPeriod.notify(\cmdPeriod) }
		}
	}

	*new { | model, name, view |
		^this.newCopyArgs(model, name).init(view); // get value and initialize view's onClose action
	}

	init { | argView |
		name !? { value = model.getValue(name); }; // nameless and valueless widgets permitted (?)
		argView !? { this.view = argView; };
	}

	view_ { | argView | // when my view closes, remove all notifications
			view = argView;
			view.onClose = { this.objectClosed }
	}

	do { | action | /* perform a function on self. Used to initialize value etc.
		at creation time, while returning self for further processing */
		action.(this);
	}

	action_ { | action |
		// set my view's action. Pass myself for access to my value etc. 
		view.action = { action.(this) };
	}
	// set my Value's adapter
	adapter_ { | adapter | value.adapter = adapter; }
	
	updateAction { | message, action | // Add a response to a message from my value
		// Add an action to be done when receiving the specified message from my value-adapter.
		// Pass the sender to the action, to avoid updating self if this is a problem.
		// Also make myself available to the action function. 
		this.addNotifier(value, message, { | sender | action.(sender, this) });
	}
	
	updater { | notifier, message, action |
		// add notifier to self. Provides self as argument to function 
		this.addNotifier(notifier, message, { | ... args | action.(this, *args) })
	}

	addValueListener { | listener, message, action |
		value.addListener(listener, message, { action.(value) })
	}

	notifyAction { | message | // set my view's action to make value send notification message with me
		view.action = { value.notify(message, this) };
	}
	
	// Initializing behavior for different types of views and functions

	// Numeric value views: NumberBox, Slider, Knob
	simpleNumber { // For NumberBox
		value.adapter ?? { value.adapter = NumberAdapter(value) };
		view.action = { value.adapter.value_(this, view.value) };
		this.updateAction(\number, { | sender |
			if (sender !== this) { view.value = value.adapter.value }
		});
	}

	mappedNumber { | spec | // For Slider, Knob
		value.adapter ?? { value.adapter = NumberAdapter(value) };
		spec !? { value.adapter.spec_(spec) };
		view.action = { value.adapter.standardizedValue_(this, view.value) };
		this.updateAction(\number, { | sender |
			if (sender !== this) { view.value = value.adapter.standardizedValue }
		});
	}

	// String value views
	text {	// For TextField, StaticText 
		value.adapter ?? { value.adapter = TextAdapter(value) };
		view.action = { value.adapter.string_(this, view.string) };
		this.updateAction(\text, { | sender |
			if (sender !== this) { view.string = value.adapter.string }
		});
	}

	textView { | message = \updateText | // for TextView. 
		// Adds updateAction to update text to adapter via button
		this.text;
		this.updateAction(message, { | sender |
			if (sender !== this) { value.adapter.string_(this, view.string) }
		});
	}

	getText { | message = \updateText |
		// Make a button get the text from a TextView and update the Value adapter
		this.notifyAction(message);
	}

	// Special case: get string from view, for further processing, without updating Value
	makeStringGetter { | message = \getString |
		// get the string from a view attached to your Value, without updating the Value itself
		this.updateAction(message, { | stringRef | stringRef.value = view.string; });
	}

	getString { | message = \getString | 
		// Use getString to get the string of a TextView prepared with makeStringGetter
		var string;
		string = `"";
		value.notify(message, string);
		^string.value;
	}

	// ======== List views: ListView, PopUpMenu =======
	list { | getListAction |
		value.adapter ?? { value.adapter = ListAdapter2(value) };
		getListAction = getListAction ?? { { value.adapter.items collect: _.asString } };
		view.action = { value.adapter.index_(this, view.value) };
		this.updateAction(\list, { // | sender |
				view.items = getListAction.(this);
				view.value = value.adapter.index;
		});
		this.updateAction(\index, { // | sender |
			/* if (sender !== this) { */
			view.value = value.adapter.index
			// }
		});
	}

	items_ { | items | value.adapter.items_(this, items); }
	item_ { | item | value.adapter.item_(this, item); }

	listItem { | getItemFunc | // display currently selected item from a list.
		value.adapter ?? { value.adapter = ListAdapter2() };
		getItemFunc = getItemFunc ?? { { value.adapter.item.asString } };
		this.updateAction(\list, { view.string = getItemFunc.(this) });
		this.updateAction(\index, { view.string = getItemFunc.(this) });
		this.replace;		// default action is replace 
	}	

	sublistOf { | valueName, getListFunction | // make me get my list from the item of another list
		value.sublistOf(valueName, getListFunction);
	}

	// Other things to do with Lists: Insert, delete, replace, append, show index, show size, navigate

	// Actions for adding, deleting, replacing items in a list
	replace { | itemCreationFunc | // replace current item in list with an item you created
		itemCreationFunc = itemCreationFunc ?? { { view.string } };
		view.action = { value.adapter.replace(this, itemCreationFunc.(this)); }
	}
	replaceOn { | itemCreationFunc, message = \replace |
		// upon receiving message, replace current item in list with item you created
		itemCreationFunc = itemCreationFunc ?? { { view.string } };
		this.updateAction(message, {
			value.adapter.replace(this, itemCreationFunc.(this));
		});
	}

	append { | itemCreationFunc | // append to list item that you created
		itemCreationFunc = itemCreationFunc ?? { { view.string } };
		view.action = { value.adapter.append(this, itemCreationFunc.(this)); };
	}

	appendOn { | itemCreationFunc, message = \append | // 
		// upon receiving message, append in list the item you created
		itemCreationFunc = itemCreationFunc ?? { { view.string } };
		this.updateAction(message, {
			value.adapter.append(this, itemCreationFunc.(this));
		});
	}

	insert { | itemCreationFunc | // replace current item in list with an item you created
		itemCreationFunc = itemCreationFunc ?? { { view.string } };
		view.action = { value.adapter.insert(this, itemCreationFunc.(this)); };
	}

	insertOn { | itemCreationFunc, message = \insert |
		// upon receiving message, replace current item in list with item you created
		itemCreationFunc = itemCreationFunc ?? { { view.string } };
		this.updateAction(message, {
			value.adapter.insert(this, itemCreationFunc.(this));
		});
	}

	delete { | message | // mostly for buttons
		view.action = { value.adapter.delete(this); };
	}

	// Getting index of current item and size of list
	listIndex { | startAt = 1 | // NumberBox displaying / setting index of element in list
		value.adapter ?? { value.adapter = ListAdapter2() };
		view.action = {
			view.value = view.value max: startAt min: (value.adapter.size - 1 + startAt);
			value.adapter.index_(this, view.value - startAt);
		};
		this.updateAction(\list, { | sender |
			if (sender !== this) { view.value = value.adapter.index + startAt }
		});
		this.updateAction(\index, { | sender |
			if (sender !== this) { view.value = value.adapter.index + startAt }
		});
	}

	listSize { // NumberBox displaying number of elements in list (list size). 
		value.adapter ?? { value.adapter = ListAdapter2() };
		view.enabled = false;
		this.updateAction(\list, { view.value = value.adapter.size })
	}
	
	// Navigating to different items in list
	firstItem { view.action = { value.adapter.first } }
	lastItem { view.action = { value.adapter.last } }
	previousItem { view.action = { value.adapter.previous } }
	nextItem { view.action = { value.adapter.next } }
	
	// NodeProxy stuff
	
	/* Make a button act as play/stop switch for any proxy chosen by another widget from
	   a proxy space. The button should be created on the same Value item as the choosing widget.
	   The choosing widget is created simply as a listView/popUpmenu on a ProxySpace's proxies. Eg:  
		app.listView(\proxies).items_(proxySpace.proxies).view ...
	   */
	proxyWatcher { | playAction, stopAction |
		playAction ?? { playAction = { this.checkProxy(value.adapter.item.item.play); } };
		stopAction ?? { stopAction = { value.adapter.item.item.stop } };
		view.action = { [stopAction, playAction][view.value].(this) };
		this.addNotifier(CmdPeriod, \cmdPeriod, { view.value = 0 });
		this.updateAction(\list, { this.prStartWatchingProxy(value.adapter.item.item) });
		this.updateAction(\index, { this.prStartWatchingProxy(value.adapter.item.item) });
		this.prStartWatchingProxy(value.adapter.item.item);
	}
	
	prStartWatchingProxy { | proxy |
		// used internally by proxyWatcher method to connect proxy and disconnect previous one
		this.notify(\disconnectProxy);	// remove notifiers to self from previous proxy
		if (proxy.isNil) { view.value = 0; ^this };
		this.addNotifier(proxy, \play, { view.value = 1 });
		this.addNotifier(proxy, \stop, { view.value = 0 });
		proxy.addNotifierOneShot(this, \disconnectProxy, { // prepare this proxy for removal
			this.removeNotifier(proxy, \play);
			this.removeNotifier(proxy, \stop);
		});
		this.checkProxy(proxy);
	}
	
	checkProxy { | proxy | // check if proxy is monitoring and update button state
		if (proxy.notNil and: { proxy.isMonitoring }) { view.value = 1 } { view.value = 0 };
	}
	
	
	//: TODO: Add automatic update of specs via notification from proxy
	proxyControlList { | proxySelector |
		// make a list of proxy control names for the proxy selected by proxySelector
		// These are updated from the ProxyItems specs List through the \list message
		// This updating is currently served by ProxyCode. Question here: 
		// Parse proxy args every time? Would that not create an inconsistency 
		// with proxy specs parsed from snippets via ProxyCode
		// Should proxies parse arguments every time that the source changes? 
		this.sublistOf(proxySelector, { | item |
			if (item.specs.size == 0) { // only parse specs here if not already provided!
				MergeSpecs.parseArguments(item.item);
			};
// always connect to item specs because you need updates: ???? therefore not [['-'], nil]] array?
			if (item.specs.size == 0) { [['-', nil]] } { item.specs };
			// !!!!!!!!!!!!!!!! THE ITEMS SPECS ARE MY LIST:
//			item.specs;	// always connect to item specs because you need updates
		}); // ProxyItem sends updates to me over its specs when updated by MergeSpecs
		this.list({ | me | me.value.adapter.items collect: _[0] });
	}

	// TODO: We need access to the proxy itself to control it.
	// Testing here.
	proxyControl { | proxySelector |
		// for NumberBox, Slider, Knob or other numeric views
		// make my view control a parameter of a NodeProxy, selected by a proxyControl List
		// proxyControl shares value with a ProxyControlList of which it is a kind of listItem
		// we need the proxySelector to get the proxy that we operate on: 
		if (proxySelector isKindOf: Symbol) { proxySelector = model.getValue(proxySelector) };
		this.updateAction(\list, { value.adapter.item[1].postln; });
		this.updateAction(\index, { value.adapter.item[1].postln; });
		view.action_({
			// will proxySelector remain available throughout the lifetime of the Widget? Should be.
			[this, thisMethod.name, "proxy item???", proxySelector.adapter.item].postln;
			value.adapter.item.postln; // print the spec we operate with
		});
	}
	
	
	
	
}

