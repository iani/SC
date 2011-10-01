Polygon {
	var <>origin, <>lines, <graph;
	
	*new { | origin, lines |
		^this.newCopyArgs(origin, lines).init;
	}

	init {
		origin = origin ?? { 0@0 };
		lines = List.new.addAll(lines);	
	}

	value {
		Pen.moveTo(origin);
		lines do: Pen.lineTo(_);
		Pen.stroke;
	}
	
	add { | point |
		lines add: point;
		graph.changed;
	}
	
	graph_ { | argGraph |
		// called by Graph:add
		graph = argGraph;
		graph.changed;
	}
	
	size { ^lines.size }
	
	pos { | index |
		// calculate the position from the index and the lines of the graph
		// index == -1 means position at origin
		^lines.at(index) ? origin;
	}

	
}

+ Function { 
	graph_ { | graph | graph.changed }	
}

