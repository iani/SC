/* IZ Fri 31 August 2012  2:57 AM EEST

Redo of Adapter idea from scratch, with radical simplification of principle 

See Value.org file for discussion. 

*/

Value {
	var <>model;		// AppModel instance that contains me
	var <adapter;	 	// My value (object) + adapter interfacing to it
	var <inputs;		// Array of MIDIFunc and/or OSCFunc that send me input

	*new { | model, adapter | ^this.newCopyArgs(model).adapter_(adapter) }

	updateListeners { adapter !? { this.notify(adapter.updateMessage); } }

	adapter_ { | argAdapter | 
		argAdapter !? { adapter = argAdapter.container_(this) };
		this.updateListeners;
	}

	// === Text utilities ===
	getString { | message = \getString | // Get the string of a TextView prepared with makeStringGetter
		var string;
		string = `"";
		this.notify(message, string);
		^string.value;
	}

	// === List utilities ===
	list { | items | this.adapter = ListAdapter(this, items) }
	items_ { | changer, items | adapter.items_(changer, items); }
	items { ^adapter.items }
	item_ { | changer, item | adapter.item_(changer, item); }
	item { ^adapter.item }
	index_ { | changer, index | adapter.index_(changer, index); }
	index { ^adapter.index }

	// make me get my list from the item of another list
	sublistOf { | superList, getListFunction |
		this.adapter = ListAdapter();
		 // vary this to get different parts of the item
		getListFunction ?? { getListFunction = { | sublist | sublist }; };
		if (superList isKindOf: Symbol) { superList = model.getValue(superList); };
		this.addNotifier(superList, \list, {
			this.adapter.items_(this, getListFunction.(superList.adapter.item, this));
		});
		this.addNotifier(superList, \index, {
			this.adapter.items_(this, getListFunction.(superList.adapter.item, this));
		});
	}

	// === MIDI and OSC ===
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

	defaultMIDIAction { // default is for setting value of proxyControl. Other defaults?
		^{ | ... args | this.setProxyParameter(args[0] / 127) }
	}
	
	setProxyParameter { | argValue |
		this.item.adapter.standardizedValue_(this, argValue)
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
	
	/* Set my Value. Remove notification connections from any previous value, and prepare
	   the new value to discnnect if replaced by another one later. Used by prSetControl method.
	   The message notification setup on value must be done separately useing updateAction. */
	value_ { | argValue ... messages |
		this.notify(\disconnect); // Cause previous Value to remove notifications to myself
		value = argValue;
		value.addNotifierOneShot(this, \disconnect, {
			messages do: { | m | this.removeNotifier(value, m); }
		});
	}  
	// set my Value's adapter
	adapter_ { | adapter | value.adapter = adapter; }
	
	updateAction { | message, action | // Add a response to a message from my value
		// Add an action to be done when receiving the specified message from my value-adapter.
		// Pass the sender to the action, to avoid updating self if this is a problem.
		// Also make myself available to the action function. 
		this.addNotifier(value, message, { | sender | action.(sender, this) });
	}
	
	addUpdateAction { | message, action |
		// add action to be performed when receiving message from value.
		// do not replace any previous action. 
		NotificationCenter.registrations.put(value, message, this,
			NotificationCenter.registrations.at(value, message, this) 
				addFunc: { | ... args | action.(this, *args) } // important: provide access to self
		);
	}
	
	updater { | notifier, message, action |
		// add notifier to self. Provides self as argument to function 
		this.addNotifier(notifier, message, { | ... args | action.(this, *args) })
	}

	// Access to views of values, for actions that require them: 
	viewGetter { | name = \view | // name a widget's view for access by getView
		this.updateAction(name, { | ref  | ref.value = view });
	}

	getView { | name = \view | // get the view of a widget named by viewGetter method
		var ref;
		value.notify(name, ref = `nil)
		^ref.value;
	}

	addValueListener { | listener, message, action |
		// make some other object perform an action whenever receiving a message from my Value
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
		// get the string from a view attached to some Value, without updating the Value itself
		this.updateAction(message, { | stringRef | stringRef.value = view.string; });
	}

	getString { | message = \getString | 
		// Use getString to get the string of a TextView prepared with makeStringGetter
		^value.getString(message);
	}
	
	// ======== Shortcuts (suggested by NC at demo in Leeds ==========
	string_ { | string | value.adapter.string_(this, string); }
	string { ^value.adapter.string }
	// TODO: rename value variable and methods of SpecAdapter ????
	// Name issues: number? magnitude? 
	number_ { | number | value.adapter.value_(this, number); }
	number { ^value.adapter.value }
	standardizedNumber_ { | number | value.adapter.standardizedValue_(this, number); }
	standardizedNumber { ^value.adapter.standardizedValue } // again: naming issues. See value above
	
	// ======== List views: ListView, PopUpMenu =======
	list { | getListAction |
		value.adapter ?? { value.adapter = ListAdapter(value) };
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
	items { ^value.adapter.items }
	item_ { | item | value.adapter.item_(this, item); }
	item { ^value.adapter.item }
	index { ^value.adapter.index }
	index_ { | index | value.adapter.index_(this, index) }
	first { value.adapter.first }
	last { value.adapter.last }
	previous { value.adapter.previous }
	next { value.adapter.next }

	listItem { | getItemFunc | // display currently selected item from a list.
		value.adapter ?? { value.adapter = ListAdapter() };
		getItemFunc = getItemFunc ?? { { value.adapter.item.asString } };
		this.updateAction(\list, { view.string = getItemFunc.(this) });
		this.updateAction(\index, { view.string = getItemFunc.(this) });
		this.replace;		// default action is replace item with your content
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

	delete { // mostly for buttons
		view.action = { value.adapter.delete(this); };
	}

	// Getting index of current item and size of list
	listIndex { | startAt = 1 | // NumberBox displaying / setting index of element in list
		value.adapter ?? { value.adapter = ListAdapter() };
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
		value.adapter ?? { value.adapter = ListAdapter() };
		view.enabled = false;
		this.updateAction(\list, { view.value = value.adapter.size })
	}
	
	// Navigating to different items in list
	firstItem { view.action = { value.adapter.first } }
	lastItem { view.action = { value.adapter.last } }
	previousItem { view.action = { value.adapter.previous } }
	nextItem { view.action = { value.adapter.next } }
	
	// NodeProxy stuff
	
	proxyList { | proxySpace | // Auto-updated list for choosing proxy from all proxies in proxySpace
		this.items_((proxySpace ?? { Document.prepareProxySpace }).proxies);
		this.updater(proxySpace, \list, { this.items_(proxySpace.proxies) });
		value.notify(\initProxyControls);	// Initialize proxyWatchers etc. created before me
	}

	/* Make a button act as play/stop switch for any proxy chosen by another widget from
	a proxy space. The button should be created on the same Value item as the choosing widget.
	The choosing widget is created simply as a listView/popUpmenu on a ProxySpace's proxies. Eg:  
		app.listView(\proxies).items_(proxySpace.proxies).view. 
	Shortcut for listView for choosing proxies: proxyList. */
	proxyWatcher { | playAction, stopAction |
		// Initialize myself only AFTER my proxySelector has been created: 
		if (value.adapter.isKindOf(ListAdapter).not) {
			this.addNotifierOneShot(value, \initProxyControls, {
				this.proxyWatcher(playAction, stopAction);
			});
		}{
			playAction ?? { playAction = { this.checkProxy(value.adapter.item.item.play); } };
			stopAction ?? { stopAction = { value.adapter.item.item.stop } };
			view.action = { [stopAction, playAction][view.value].(this) };
			this.addNotifier(CmdPeriod, \cmdPeriod, { view.value = 0 });
			this.updateAction(\list, { this.prStartWatchingProxy(value.adapter.item.item) });
			this.updateAction(\index, { this.prStartWatchingProxy(value.adapter.item.item) });
			this.prStartWatchingProxy(value.adapter.item.item);
		}
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
		^proxy; // for further use if in another expression.
	}
	
	proxyControlList { | proxySelector, autoSelect |
		// make a list of proxy control names for the proxy selected by proxySelector
		// These are updated from the ProxyItems specs List through the \list message
		// Update messages are currently sent by ProxyCode:evalInProxySpace.  
		// Questions: Parse proxy args every time? Would that not create an inconsistency 
		// with proxy specs parsed from snippets via ProxyCode
		// Should proxies parse arguments every time that the source changes? 
		/* If autoSelect is given a positive integer value, then the widget will select
		   the nth parameter, if available whenever the list of parameter changes */
		if (proxySelector isKindOf: Symbol) {
			proxySelector = model.getValue(proxySelector);
		};
		// Initialize myself only AFTER my proxySelector has been created: 
		if (proxySelector.value.adapter.isNil) {
			this.addNotifierOneShot(proxySelector.value, \initProxyControls, {
				this.proxyControlList(proxySelector, autoSelect);
			});
		}{
			this.sublistOf(proxySelector, { | item |
				if (item.specs.size < 2) { // only parse specs here if not already provided!
					MergeSpecs.parseArguments(item.item);
				};
				item.specs; // the specs are Value instances to which widgets connect
			});
			if (autoSelect.isNil) {
				this.list({ | me | me.items collect: { | v | v.adapter.parameter }; });
			}{
				this.list({ | me |
					if (autoSelect < me.items.size) { me.value.adapter.index_(nil, autoSelect); };
					me.items collect: { | v | v.adapter.parameter };
				});			
			};
			value.notify(\initProxyControls);	// Initialize proxyControls created before me
		}
	}

	proxyControl {
		var paramList;	// The list of the proxyControlList from which parameters are chosen. 
		// Initialize myself only AFTER my proxyControlList has been created: 
		if (value.adapter isKindOf: NumberAdapter) {
			this.addNotifierOneShot(value, \initProxyControls, { this.proxyControl });
		}{
			// Later my value inst var will be changed. So I keep the paramList in this closure:
			paramList = value.adapter;
			this.listItem({ | me | me.item !? { me.item.adapter.parameter } });
			this.updateAction(\list, { | sender, list |
				paramList.item !? { this.prSetControl(paramList.item); };
			});
			this.updateAction(\index, { | sender, list |
				paramList.item !? { this.prSetControl(paramList.item); };
			});
		}
	}
	
	prSetControl { | proxyControl |
		this.value_(proxyControl, \number);
		/* provide appropriate view action and updateAction for a numerical value view.
		The view sets these according to its Class.  */
		view.connectToNumberValue(value.adapter, this);
		value.adapter.getValueFromProxy;
	}
	
	// SoundFile stuff

	soundFileView {
		value.adapter = SoundFileAdapter(value);
		this.updateAction(\read, { | soundfile, startframe, frames |
			view.soundfile = soundfile.soundFile;
			view.read(startframe, frames);
			value.notify(\sfViewAction, this);
		});
		view.mouseUpAction = { | view | value.notify(\sfViewAction, this) };
	}
	
	
}