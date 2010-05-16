/* iz 080905

Controller creating Nodes, where each node is provided with its own DynamicAdapter. 
nodes are stored in variable nodes so that they can be all stopped or otherwise collectively controlled when needed (similar to Group, but here the nodes do not have to be in the same Group on the Server). 
Removes itself from the model + modifies its model with "nodeEnded" message when its node ends. 


There is an overhead in doing the group management in an array on the client, but: 
1. It gives more freedom about creating the Nodes/Synths in any Group one wants 
2. It avoids having to write a mechanism for creating the nodes/synths from parameters passed by the action to the "addNode" message, and this simplifies the code. 
3. To remove the DynamicAdapter one has to add the onEnd to the nodes anyway, so the overhead is limited. 


n = NodeArrayController(\anything, 
	(
		addAsound: { | controller, msg, model |
			[controller, msg, model].postln;
			controller.addNode(Synth("variable_sin", [\freq, 400 rrand: 3000]), model)
		},
		stop: { | controller | controller.freeAll }
	),
	(freq: { | adapter, msg, freq | adapter.target.set(\freq, freq) })
);

\anything.changed(\addAsound, \alpha);
\anything.changed(\addAsound, \beta);
\alpha.changed(\freq, 1500);
\beta.changed(\freq, 1200);
n.nodes;
\anything.changed(\stop);
n.nodes;
n.remove;

*/

NodeArrayController : Controller { 
	var <nodeActions, <nodes; 
	init {
		super.init;
		nodes = List.new;
	}
	update { arg theChanger, what ... moreArgs;
		// pass yourself to the action so that you can be called for addNode or other stuff
		var action;
		action = actions.at(what);
		if (action.notNil, {
			action.valueArray(this, what, moreArgs);
		});
	}
	addNode { | argNode, argModel |
		var adapter; 
		adapter = DynamicAdapter(argModel ? model, nodeActions, argNode);
		nodes add: adapter;
		argNode onEnd: {
			nodes remove: adapter;
			adapter.remove;
		}
	}
	freeAll {
		nodes.copy do: _.free;
	}
}

