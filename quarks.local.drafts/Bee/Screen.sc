

Screen {
	var <graph, <>view, <>window;
	var <x = 0, <y = 0, <xScale = 1, <yScale = 1;
	
	*new { | graph, name, bounds |
		^super.new.init(graph, name, bounds);
	}

	init { | argGraph, name, bounds |
		window = Window(name ? "graphics", bounds ?? { Rect(680 * 2, 100, 300, 300) });
		view = window.view;
		view.background = Color.white;
		this.initViewFunc;
		this.graph = argGraph;
		window.front;
	}
	
	initViewFunc {
		window.drawHook = {
			this.setView;
			graph.draw;
		}
	}

	setView {
		Pen.translate(x, y);
		Pen.scale(xScale, yScale);
	}

	graph_ { | argGraph |
		graph = argGraph ?? { Graph.new };
		graph addDependant: this;
		this.update;
	}

	add { | node | graph add: node }

	update { this.refresh }
	
	refresh { { window.refresh }.defer }

	// Scrolling and zooming 
	
	x_ { | newx | 
		x = newx;
		this.update;
	}

	y_ { | newy | 
		y = newy;
		this.update;
	}

	xScale_ { | newxScale | 
		xScale = newxScale;
		this.update;
	}

	yScale_ { | newyScale | 
		yScale = newyScale;
		this.update;
	}

	//TODO

	zoomToFit {}
	
	scroll {}

	zoom {}

}

/*

zoomToFit: 
Fitting the rectangle of the entire drawing in the rectangle of of the view of the Screen window: 
Let sbounds be the screen window bounds, and dbounds be the bounds of the drawing. 

... 


*/
