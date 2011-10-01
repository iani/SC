

Screen {
	var <graph, <>view, <>window;
	
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
	
	initViewFunc { window.drawHook = { graph.draw } }

	graph_ { | argGraph |
		graph = argGraph ?? { Graph.new };
		graph addDependant: this;
		this.update;
	}

	add { | node | graph add: node }

	update { this.refresh }
	
	refresh { { window.refresh }.defer }

}

