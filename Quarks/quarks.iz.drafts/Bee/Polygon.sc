Polygon {
	var <>origin, <>lines, <graph;
	
	var <left, <right, <top, <bottom;
	
	*new { | origin, lines |
		^this.newCopyArgs(origin, lines).init;
	}

	init {
		origin = origin ?? { 0@0 };
		right = left = origin.x;
		top = bottom = origin.y;
		lines = List.new.addAll(lines);	
	}

	value {
		Pen.moveTo(origin);
		lines do: Pen.lineTo(_);
		Pen.stroke;
	}
	
	add { | point |
		left = point.x min: left;
		right = point.x max: right;
		top = point.y min: top;
		bottom = point.y max: bottom;
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

	bounds { ^Rect(left, top, right - left, bottom - top) }
	
}

+ Function { 
	graph_ { | graph | graph.changed }
}

