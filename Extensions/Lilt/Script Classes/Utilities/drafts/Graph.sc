/* iz 080821
Attempt to model a graph using identity dictionaries, so one can traverse the graph with paths using for example symbols as keys. 


g = Graph.new;
g.add(1, 'one');
g.keysAt(1);
g.nodesAt('one');


*/

GraphNode {
	var <object, <keys;
	*new { | object |
		^this.newCopyArgs(object).init;
	}
	init { keys = Set.new }
	addKey { | key |
		keys.add(key);
	}
}

Graph {
	var <nodes, <keys;
	*new {
		^super.new.init;
	}
	init {
		nodes = IdentityDictionary.new;
		keys = IdentityDictionary.new;
	}
	keysAt { | object |
		object = nodes.at(object);
		if (object.isNil) { ^nil } { ^object.keys };
	}
	nodesAt { | key |
		^keys.at(key);
	}
	add { | object, key |
		var node;
		node = this.prGetNode(object);
		if (key.notNil) {
			this.prAddKeyToNode(key, node);
		}
	}
	prGetNode { | object |
		var node;
		node = nodes.at(object);
		if (node.isNil) {
			node = GraphNode(object);
			nodes.put(object, node);
		};
		^node;
	}
	prAddKeyToNode { | key, node |
		var prKeys;
		node.addKey(key);
		prKeys = keys.at(key);
		if (prKeys.isNil) {
			prKeys = Set.new;
			keys.put(key, prKeys)
		};
		prKeys.add(node);
	}
}
