
Graph : Model {
	var <>graph;
	
	*new { ^super.new.init }
	
	init { graph = List.new }

	add { | node |
		graph add: node;
		node.graph = this;
	}

	draw { graph do: _.value }
	
	//TODO

	zoomToFit {}
	
	scroll {}

	zoom {}

}

