/* iz Wednesday; September 10, 2008: 1:36 PM
a ModelWithController that starts, controls and stops nodes. Each node is bound to its own model with an Adapter whose actions are defined through a common nodeActions dictionary. 

See also ContourController and SimpleContourSound
*/


NodeController : ModelWithController {
	var <>nodeActions, <nodes;

	init { | argModel, argActions, argNodeActions |
		super.init(argModel, argActions);
		nodeActions = argNodeActions;
		nodes = List.new;
	}
	addNode { | argNode, argModel |
		var adapter; 
		adapter = Adapter(argModel ? actions.model, nodeActions, argNode);
		nodes add: adapter;
		argNode onEnd: {
			nodes remove: adapter;
			adapter.remove;
		}
	}
	freeAll {
		nodes.copy do: _.free;
	}
	deactivate {
		this.freeAll;
		super.deactivate;
	}

}

