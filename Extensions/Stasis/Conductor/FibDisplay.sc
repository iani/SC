/* IZ May 22, 2010
Graphic display of the progress of the fibonacci structure. 

d = FibDisplay.new;
d.show;
d.addBranch([0, 144, 1]);
d.addBranch([55, 89, 2]);

d = FibDisplay(5);
d.numLevels;

*/


FibDisplay {
	var <numBeats = 144;	// number of total beats contained in the Fibonacci structures to be played
	var <oscMessage;		// the osc message which gives the data for each new branch
	var <>bounds;
	var <numLevels;
	var <xScale, <yScale;
	var <window;
	var <fibData;
	var <scaledFibData;
	var <margin = 5;

	*new { | numBeats = 144, bounds |
		^this.newCopyArgs(numBeats).init(bounds);
	}

	init { | argBounds |
		numLevels = ({ | i = 1 |
			var a = 0, b = 1, c = 0;
			i do: { 
				c = a + b;
				a = b;
				b = c;
			};
			c;
		} ! 30);
		numLevels = numLevels.indexOf(numLevels detect: (_ >= numBeats));
		this.rescale(argBounds ?? { this.defaultWindowBounds});
	}

	defaultWindowBounds { ^Rect(0, 0, 1200, 300) }

	addBranch { | branch |
		fibData = fibData add: branch;
		scaledFibData = scaledFibData add: this.scaleBranch(branch);
		window.refresh;
	}

	scaleBranch { | branch |
		var pos, size, level;
		#pos, size, level = branch;
		^Rect(pos * xScale + margin, margin, size * xScale, numLevels - level * yScale);
	}

	show {
		if (window.isNil) { this.makeWindow };
		window.front;
	}

	makeWindow {
		window = Window("Fib", bounds);
		window.view.background = Color.white;
		window.onClose = { window = nil };
		window.drawHook = { this.draw };
	}

	draw {
		this rescale: window.view.bounds;
		scaledFibData do: { | r | Pen.addRect(r) };
		Pen.stroke;
	}

	drawBranch { | pos = 0, size = 144, level = 1 |
		Pen.addRect(Rect(pos * xScale + margin, margin, size * xScale, numLevels - level * yScale));
	}

	rescale { | newBounds |
		if (newBounds == bounds) { ^this };
		bounds = newBounds;
		xScale = bounds.width - (margin * 2) / numBeats;
		yScale = bounds.height - (margin * 2) / numLevels;
		scaledFibData = fibData collect: (this.scaleBranch(_));
	}
}