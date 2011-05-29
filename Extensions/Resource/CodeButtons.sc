

CodeButtons : WindowResource {
	var code, snippets;
	*new { | doc, bounds |
		var code;
		doc = doc ?? { Document.current };
		code = Code(doc);
		bounds = bounds ?? { Window.centeredWindowBounds(
			3 * 335, 
			(code.headers.size / 3).ceil.asInteger,
			32)
		};
		^super.new(doc.name.asSymbol, {
			var window;
			window = Window(doc.name, bounds);
			window.addFlowLayout(5@5, 5@5);
			window;
		}, Code(doc)).front;
	}

	init { | windowFunc, code |
		super.init(windowFunc);
		this.initSnippets(code);	
	}

	initSnippets { | argCode |
		var view, rgb, bcolor, tcolor, numbuttons, height, bounds;
		view = object.view;
//		view.background = Color(*Array.rand(3, 0.7, 1.0));
		view.background = Color(*Array.rand(3, 0.07, 0.3));
//		tcolor = Color(1.0, 0.975, 0.735);
		tcolor = Color.red(0.7);
//		bcolor = Color(0.0, 0.45, 0.42);
		bcolor = Color.white;
		code = argCode;
		numbuttons = 0;
		code.headers do: { | h, i |
			if (h[3] != $ ) {
				numbuttons = numbuttons + 1;
				Button(view, Rect(0, 0, 328, 28))
					.states_([[h[3..60], tcolor, bcolor]])
					.font_(Font("ArialNarrow-Bold", 14))
					.action_({ | me | 
						code.performCodeAt(i + 1, \fork, SystemClock) });
			};
		};
		height = (numbuttons / 3).ceil.asInteger * 32 + 30;
		bounds = object.bounds;
		bounds.height = height;
		bounds.left = Window.screenBounds.width - bounds.width;
		bounds.top = Window.screenBounds.height - bounds.height;
		object.bounds = bounds;
	}	
}
