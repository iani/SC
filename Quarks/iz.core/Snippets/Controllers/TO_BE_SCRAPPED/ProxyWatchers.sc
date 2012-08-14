/* IZ Thu 09 August 2012 10:20 AM EEST

WILL BE REMOVED!!!!!!!


Perform actions on a widget whenever a node is created, starts or stops, or its source changes in the ProxySpace of a ProxyCode of a Document. 

Functionality extracted from ProxySourceEditor, in order to be used also in other classes. 

ProxySpaceWatcher -> notify when a ProxySpace creates or removes a NodeProxy 

AbstractNodeWatcher -> Watch for changes that happen to a selected node
					Can change the node watched
					Abstract superclass for ProxyNodeWatcher, ProxySpecWatcher
ProxyNodeWatcher -> notify widget when a NodeProxy plays or stops playing
ProxySpecWatcher -> notify to update specs when the source of a NodeProxy changes

ProxyNodeSetter -> set the node that another widget watches
ProxySpecSetter -> set the spec of a target widget when the chosen spec changes
	(Actually also takes over the node-setting action of the target widget).

Example: 
(
w = Window.new.front;
w.layout = VLayout(
	PopUpMenu().addModel(w, \nodes)
		.proxySpaceWatcher
		.proxyNodeSetter(\button)
		.proxyNodeSetter(\specmenu).v,
	Button().states_([["start"], ["stop"]])
		.addModel(w, \button).proxyNodeWatcher.v,
	PopUpMenu()
		.addModel(w, \specmenu)
		.proxySpecWatcher
		.proxySpecSetter(\knob).v,
	Knob().addModel(w, \knob, \numbox).v,
	NumberBox().addModel(w, \numbox, \knob).v
);
w.onClose = { w.objectClosed };
)

//==

~out = { WhiteNoise.ar(0.1) };
~out.play;

~out2 = { | freq = 400 | SinOsc.ar(freq, 0, 0.1) };
~out2.play;

~out3 = { GrayNoise.ar(0.1) };
~out3.play;
*/


ProxyNodeSetter {
	/* 	When a new node is seleted by my widget, set it as node in a target widget.
		Note: multiple ProxyNodeSetters can be added to the same widget, to set the nodes
		of multiple targets, such as a start-stop button or a menu of controls */
	var <widget, <targetName, <proxySpace, <action, targetWidget, <node;
	
	*new { | widget, targetName, proxySpace, action, targetWidget |
		// if targetWidget is not provided, it is fetched from targetName
		^this.newCopyArgs(widget, targetName, proxySpace, action, targetWidget).init;
	}

	init {
		proxySpace = proxySpace ?? { Document.current.envir };
		// set my widgets action to notify me to set node:
		widget.action = action ?? {{
			[this, thisMethod.name, "widget view items: ", widget.view.items, 
				"widget view value: ", widget.view.value].postln;
			if (widget.view.value.isNil) {
				"HERE I AVOIDED widget view item".postln;
				"Lets have a look at the contents of the views items".postln;
				widget.view.items.postln;
				widget.view.value = widget.view.items.size - 1; // patch patch patch
			};
			if (widget.view.item === '-') {
				widget.notify(\setNode, nil)
			}{
				widget.notify(\setNode, proxySpace[widget.view.item]) 
			}
			
		}};
		// set me to perform my setNode method when I am notified from my widget
		this.addNotifier(widget, \setNode, { | argNode | this setNode: argNode });
		// make my node available to the widget, and thus everywhere. 
		this.addNotifier(widget, \getNode, { | ref | ref.value = node; });
		// Permit model to perform an update once after all widgets have been created
		widget.model.registerOneShot(\update, widget, { widget.view.doAction });
	} 

	setNode { | argNode |
		node = argNode;
		this.targetWidget.notify(\setNode, node);
	}

	targetWidget {
		targetWidget ?? { targetWidget = widget.model.widget(targetName) };
		^targetWidget;
	}

}

AbstractProxyNodeWatcher {
	var <widget, <node;
	
	init {
		// Make myself to change my widgets node whenever I am notified \setNode
		this.addNotifier(widget, \setNode, { | argNode | this.setNode(argNode) });
		// Make my node available to the widget, and thus everywhere. 
		this.addNotifier(widget, \getNode, { | ref | ref.value = node; });
		/* 	Set the action that I will perform when notified of a change in the node's 
			state which concerns me: */
		this.setAction;
		this.setNode(node);
	}

	setNode { | argNode |
		this.removeNode;		// always remove node (!?)
		argNode !? {
			node = argNode;
			this.addNotifiers;
		};
		this.updateState;
	}

	removeNode {
		node !? { this.removeNotifiers };
		node = nil;
	}

	setAction { this.subclassResponsibility(thisMethod) }
	addNotifiers { this.subclassResponsibility(thisMethod) }
	removeNotifiers { this.subclassResponsibility(thisMethod) }
	updateState { this.subclassResponsibility(thisMethod) }
}

ProxyNodeWatcher : AbstractProxyNodeWatcher {
	/*  Watch a NodeProxy's play / stop status. When the proxy starts, perform playAction, 
		when it stops, perform stopAction
	*/

	var <>playAction, <>stopAction, <setWidgetAction;

	*new { | widget, playAction, stopAction, setWidgetAction = true, node |
		^this.newCopyArgs(widget, node, playAction, stopAction, setWidgetAction).init;
	}

	setAction {
		if (setWidgetAction) { widget.action = { | val | this playOrStopNode: val } };
		playAction = playAction ?? { this.defaultPlayAction };
		stopAction = stopAction ?? { this.defaultStopAction };
	}

	defaultPlayAction { ^{ widget.value = 1 } }
	defaultStopAction { ^{ widget.value = 0 } }

	addNotifiers {
		node !? {
			this.addNotifier(node, \play, { playAction.(this) });
			this.addNotifier(node, \stop, { stopAction.(this) });
		}
	}

	removeNotifiers {
		node !? {
			this.removeNotifier(node, \play);
			this.removeNotifier(node, \stop);
		}
	}

	updateState {
		node !? { if (node.isMonitoring) { playAction.(this) } { stopAction.(this) } };
	}

	playOrStopNode { | value = 1 |
		if (node.isNil) {
			widget.view.value = 0
		}{
			if (value > 0) { node.play; } { node.stop; }
		};
	}
}

ProxySpecWatcher : AbstractProxyNodeWatcher {
	/*  	Watch a NodeProxy's specs status. When the specs of a proxy change, do something with
		them and your widget.
		
		Optionally set the widget's action, to let you make the widget notify the spec it has chosen. 
		This notification is caught by ProxySpecSetter, which sets the spec of 
		and prepares the node-setting function for the target widget.
	*/

	classvar <specCache; /* IdentityDictionary: Stores the most recently generated specs 
		for each node, to access them when a widget switches to a new node */

	var <action; /* Do this when new specs are received. Default: set my widget's items and
				store the specs for access by other widgets that need them */
	var <setWidgetAction = true;	// if false, do not set the widget's action
	var <specs;	// Widgets connected to my widget via ProxySpecSetter are sent these specs

	*initClass { this.clearCache }
	*clearCache { specCache = IdentityDictionary.new }
	
	*cacheSpecs { | argNodeProxy, argSpecs |
		// ProxyCode stores most recent specs nodes here, for access when switching
		// Should updateState also cache new specs? 
		specCache[argNodeProxy] = argSpecs;
	}
	
	*new { | widget, action, setWidgetAction = true, node |
		^this.newCopyArgs(widget, node, action, setWidgetAction).init;
	}

	addNotifiers {
		node !? { this.addNotifier(node, \proxySpecs, { | argSpecs |
			action.(argSpecs, this) });
		}
	}

	removeNotifiers {
		node !? { this.removeNotifier(node, \proxySpecs); }
	}

	setAction {
		if (setWidgetAction) { widget.action = { this.notifyCurrentSpec; }; };
		action = action ?? {{ | specs | this.setWidgetItems(specs) }};
	}

	setWidgetItems { | argSpecs |
		/* Default action to do when receiving new specs: store them and set my widget's items */
		specs = argSpecs;
		widget.view.items = argSpecs.flop.first;
	}
	
	updateState {
		/* 	When switching to a new node, set my widget's items to the cached or parsed 
			specs of that node */
		var cachedSpecs;
		if (node.isNil) {
			cachedSpecs = MergeSpecs.nilSpecs;
		}{
			cachedSpecs = specCache[node];
		}; 
		if (cachedSpecs.isNil) {
// Should specs parsed from the new node be cached also?
/*			cachedSpecs = MergeSpecs(node);
			node !? { specCache[node] = cachedSpecs };
			action.(cachedSpecs, this); 
*/
			action.(MergeSpecs(node), this);
		}{
			action.(cachedSpecs, this);
		};		
	}
	
	notifyCurrentSpec {
		[this, thisMethod.name, widget.name, widget.view.items, specs, node].postln;
		widget.view.items !? {
			widget.notify(\currentSpec, [specs[widget.view.value ? 0], node]);
		}
	}
}

ProxySpecSetter {
	/* 	When a new node control parameter is seleted by my widget, set the specs 
		and the control function for my target widget.
		Note: multiple ProxySpecSetters can be added to the same widget, to set the nodes
		of multiple targets.

		Mechanism: 
		- Start listening to notifications \currentSpec from your widget. 
		  These are emitted by a ProxySpecWatcher, when it receives notification \proxySpecs
		  from the widget. The widget is set to emit those notifications by the ProxySpecWatcher.
		  
		- Hijack the target Widget, by setting its action to your setParameter method.
		- When your widget chooses a different parameter, then do the following: 
			- set your target widget's spec;
			- set your nodeParamSetterFunc depending on the parameter name.
	*/
	var <widget, <targetName, targetWidget, <parameter, <nodeParamSetterFunc, node;
	
	*new { | widget, targetName, targetWidget |
		// if targetWidget is not provided, it is fetched from targetName
		^this.newCopyArgs(widget, targetName, targetWidget).init;
	}

	init {
		// set me to perform my setNode method when I am notified from my widget
		this.addNotifier(widget, \currentSpec, { | spec, argNode |
			node = argNode;
			this.setParameter(spec[0]);
			this.targetWidget.spec = spec[1];
		});
		// Permit model to perform an update once after all widgets have been created
		widget.model.registerOneShot(\update, widget, { widget.view.doAction });
		// make my node available to the widget, and thus everywhere. 
		this.addNotifier(widget, \getNode, { | ref | ref.value = node; });
	} 

	targetWidget {
		targetWidget ?? {
			targetWidget = widget.model.widget(targetName);
			targetWidget.action = { | val | this.setNodeParam(val); };
		};
		^targetWidget;
	}

	setNodeParam { | val | nodeParamSetterFunc.(val); }
	
	setParameter { | paramName |
		parameter = paramName;
		nodeParamSetterFunc = switch ( parameter,
			'-', { {} },
			\vol, { { | val | node.vol = val; } },
			\fadeTime, { { | val | node.fadeTime = val; } },
			{ { | val | node.set(parameter, val); } }
		);

	}
}
