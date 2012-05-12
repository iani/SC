
Graph : Model {
	var <>graph;
	
	*new { ^super.new.init }
	
	init { graph = List.new }

	add { | node |
		graph add: node;
		node.graph = this;
	}

	draw { | bounds | graph do: _.(bounds) }
	
	//TODO

	zoomToFit {}
	
	scroll {}

	zoom {}

}

