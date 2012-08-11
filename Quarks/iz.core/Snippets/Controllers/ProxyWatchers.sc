/* IZ Thu 09 August 2012 10:20 AM EEST
Perform actions on a widget whenever a node is created, starts or stops, or its source changes in the ProxySpace of a ProxyCode of a Document. 

Code extracted from ProxySourceEditor, in order to be used also in other classes. 

TO DEFINE: 


ProxySpaceWatcher -> notify when a ProxySpace creates or removes a NodeProxy 


AbstractNodeWatcher -> Watch for changes that happen to a selected node
					Can change the node watched
					Abstract superclass for ProxyNodeWatcher, ProxySpecWatcher
ProxyNodeWatcher -> notify widget when a NodeProxy plays or stops playing
ProxySpecWatcher -> notify to update specs when the source of a NodeProxy changes

ProxyNodeSetter -> set the node that another widget watches
ProxySpecSetter -> set the spec of another widget when the chosen spec changes

*/


ProxySpaceWatcher {
	var <widget, <proxySpace, <>action;

	*new { | widget, proxySpace, action |
		^this.newCopyArgs(widget, proxySpace, action).init;
	}

	init {
		proxySpace = proxySpace ?? {
			Document.current.envir ?? { ProxySpace.push }
		};
		action = action ?? {{
			// Wait for proxyspace to install the new proxy. Why does this take so long?
			{ this.updateNodeProxyList; }.defer(0.1);
		}};
		widget.addNotifier(proxySpace, \newProxy, { action.(this) });
	}
	
	updateNodeProxyList {
		var currentItem, items, index;
		widget.view.items !? { currentItem = widget.view.item; };
		items = proxySpace.envir.keys.asArray.sort add: '-';
		widget.view.items = items;
		widget.view.value = items.indexOf(currentItem) ?? { items.size - 1 };
	}
}

ProxyNodeSetter {
	/* 	When a new node is seleted by my widget, set it as node in a target widget.
		Note: multiple ProxyNodeSetters can be added to the same widget, to set the nodes
		of multiple targets, such as a start-stop button or a menu of controls */
	var <widget, <targetName, <proxySpace, <action, targetWidget;
	
	*new { | widget, targetName, proxySpace, action, targetWidget |
		// if targetWidget is not provided, it is fetched from targetName
		^this.newCopyArgs(widget, targetName, proxySpace, action, targetWidget).init;
	}

	init {
		widget.action = action ?? {{ | val, me |
			me.notify(\setNode, proxySpace[me.view.item]) 
		}};
		this.addNotifier(widget, \setNode, { | argNode |
			this setNode: argNode });
		proxySpace = proxySpace ?? { Document.current.envir };
		// Permit model to perform an update once after all widgets have been created
		widget.model.registerOneShot(\update, widget, { widget.view.doAction });
	} 

	setNode { | node |
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
		this.addNotifier(widget, \setNode, { | argNode | this.setNode(argNode) });
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
	updateState { this.subclassResponsibility(thisMethod) }
}

ProxyNodeWatcher {
	/*  Watch a NodeProxy's play / stop status. When the proxy starts, perform playAction, 
		when it stops, perform stopAction
	*/

	var <widget, <>playAction, <>stopAction, <setWidgetAction, <node;

	*new { | widget, playAction, stopAction, setWidgetAction = true, node |
		^this.newCopyArgs(widget, playAction, stopAction, setWidgetAction, node).init;
	}
	
	init {
		this.addNotifier(widget, \setNode, { | argNode |
			this.setNode(argNode)
		});
		if (setWidgetAction) { widget.action = { | val | this playOrStopNode: val } };
		playAction = playAction ?? { this.defaultPlayAction };
		stopAction = stopAction ?? { this.defaultStopAction };
		this.setNode(node);
	}

	defaultPlayAction { ^{ widget.value = 1 } }
	defaultStopAction { ^{ widget.value = 0 } }

	setNode { | argNode |
		this.removeNode;		// always remove node (!?)
		argNode !? {
			node = argNode;
			this.addNotifier(node, \play, { playAction.(this) });
			this.addNotifier(node, \stop, { stopAction.(this) });
			if (node.isMonitoring) { playAction.(this) } { stopAction.(this) };
		}
	}

	removeNode {
		node !? { 
			this.removeNotifier(node, \play);
			this.removeNotifier(node, \stop);
		};
		node = nil;
	}
	
	playOrStopNode { | value = 1 |
		node !? {
			if (value > 0) {
				node.play;
			}{
				node.stop;
			}
		}
	}
}

ProxySpecWatcher {
	/*  	Watch a NodeProxy's specs status. When the specs of a proxy change, do something with
		them and your widget
	*/

	classvar <specCache; // Store the most recently generated specs for access when switching

	var <widget, <action, <node;

	*initClass {
		specCache = IdentityDictionary.new;
	}
	
	*cacheSpecs { | argNodeProxy, argSpecs |
		// ProxyCode stores most recent specs for all nodes here, for access when switching
		specCache[argNodeProxy] = argSpecs;
	}
	
	*new { | widget, action, node |
		^this.newCopyArgs(widget, action, node).init;
	}

	init {
		this.addNotifier(widget, \setNode, { | argNode | this.setNode(argNode) });
		action = action ?? { this.defaultAction };
		this.setNode(node);
	}

	defaultAction { ^{ | specs | this.setWidgetItems(specs) } }

	setNode { | argNode |
		var cachedSpecs;
		this.removeNode;		// always remove node (!?)
		argNode !? {
			node = argNode;
			this.addNotifier(node, \proxySpecs, { | ... specs |
				action.(specs, this);
			});
			cachedSpecs = specCache[node];
			if (cachedSpecs.notNil) {
				action.(cachedSpecs, this);
			}{
				action.(MergeSpecs(node), this);
			}
		}
	}

	removeNode {
		node !? {
			this.removeNotifier(node, \proxySpecs);
		};
		node = nil;
	}
	
	setWidgetItems { | specs |
		widget.view.items = specs.flop.first;
	}
}

ProxySpecSetter {
	
}
