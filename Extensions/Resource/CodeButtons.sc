

CodeButtons : WindowResource {
	var doc, code, snippets;
	*new { | doc, bounds |
		doc = doc ?? { Document.current };
		bounds = bounds ?? { Window.fullScreenBounds }; // fullScreenBounds centeredWindowBounds
		^super.new(doc.name.asSymbol, {
			var window;
			window = Window(doc.name, bounds);
			window.addFlowLayout(5@5, 5@5);
			window;
		}).initSnippets(doc).front;
	}

	initSnippets { | argDoc |
		var view;
		view = object.view;
		doc = argDoc;
		code = Code(doc);
		code.headers do: { | h, i |
			Button(view, Rect(0, 0, 280, 20))
				.states_([["->" ++ h[2..], Color.black, Color(0.9, 0.7, 0.2)]])
				.action_({ | me | 
					me.value.postln;
					i.postln;
					code.performCodeAt(i + 1, \fork, SystemClock) });
		};
	}	
}
