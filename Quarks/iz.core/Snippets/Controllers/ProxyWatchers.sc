/* IZ Thu 09 August 2012 10:20 AM EEST
Perform actions on a widget whenever a node is created, starts or stops, or its source changes in the ProxySpace of a ProxyCode of a Document. 

Code extracted from ProxySourceEditor, in order to be used also in other classes. 

TO DEFINE: 

ProxyNodeSetter -> set the node that another widget watches
ProxyNodeWatcher -> notify widget when a NodeProxy plays or stops playing
ProxySpecWatcher -> notify to update specs (or other stuff???) when the source of a NodeProxy changes
ProxySpaceWatcher -> notify when a ProxySpace creates or removes a NodeProxy 

*/

ProxyNodeSetter {
	/* Note: multiple ProxyNodeSetters can be added to the same widget, to set the nodes
		of multiple targets, such as a start-stop button or a menu of controls */
	var <widget, <targetName, <proxySpace, <action, targetWidget;
	
	*new { | widget, targetName, proxySpace, action, targetWidget |
		^this.newCopyArgs(widget, targetName, proxySpace, action, targetWidget).init;
	}

	init {
		widget.action = action ?? {{ | val, me |
			me.notify(\setNode, proxySpace[me.widget.item]) 
		}};
		this.addNotifier(widget, \setNode, { | argNode |
			this setNode: argNode });
		proxySpace = proxySpace ?? { Document.current.envir };
	}

	setNode { | node |
		this.targetWidget.notify(\setNode, node);
	}

	targetWidget {
		targetWidget ?? { targetWidget = widget.model.widget(targetName) };
		^targetWidget;
	}

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

	var <widget, <action, <node;
	
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
		this.removeNode;		// always remove node (!?)
		argNode !? {
			node = argNode;
			this.addNotifier(node, \proxySpecs, { | ... specs |
				action.(specs, this);
			});
		}
	}

	removeNode {
		node !? {
			this.removeNotifier(node, \proxySpecs);
		};
		node = nil;
	}
	
	setWidgetItems { | specs |
		widget.widget.items = specs.flop.first;
	}
}

ProxySpaceWatcher {
	var <widget, <proxySpace, <>action;
	
	*new { | widget, proxySpace, action |
		^this.newCopyArgs(widget, proxySpace, action).init;
	}
	
	init {
	}
	
}
